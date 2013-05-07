package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;

public class ClientOutputThread extends Thread {
	private Socket socket;
	private ClientMonitor cm;
	private OutputStream os;

	public ClientOutputThread(Socket socket, ClientMonitor cm) {
		this.socket = socket;
		this.cm = cm;
		try {
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (!socket.isClosed()) {
			try {
				cm.waitForEvent();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			getMovements();
			getMessages();
			//getGameName();
		}
		System.out.println("ClientOutputThread stopped");
		return;
	}

	public void getGameName() {
		try {
			os.write(Conversions.intToByteArray(Constants.SENDGAMENAME));
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void getMessages() {
		ArrayList<String> messages = cm.getChatMessagesToSend();
		for (int i = 0; i < messages.size(); i++) {
			String message = messages.get(i);
			byte[] command = Conversions.intToByteArray(Constants.SENDMESSAGE);
			//System.out.println("opthread::sending command: "+Conversions.ByteArrayToInt(command));
			byte[] length = Conversions.intToByteArray(message.length());
			byte[] text = message.getBytes();
			try {
				//System.out.println("opthread::writing, length: "+message.length());
				os.write(command);
				os.write(length);
				os.write(text);
				os.flush();
			} catch (IOException e) {
				// e.printStackTrace();
				closeConnection();
			}
		}

	}

	private void getMovements() {
		ArrayList<Byte> movements = cm.getMovements();
		for (int i = 0; i < movements.size(); i++) {
			if (movements.get(i) != -1) {
				try {
					os.write(movements.get(i));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean closeConnection() {
		if (socket.isClosed())
			return true;
		try {
			socket.getOutputStream().write(Constants.LEAVEGAME);
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void startNewGame(String gameName, int port) {
		try {
			os.write(Constants.STARTGAME);
			os.write(Conversions.intToByteArray(port));
			os.write(Conversions.intToByteArray(gameName.length()));
			os.write(gameName.getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refresh() {
		byte[] send = Conversions.intToByteArray(Constants.GAMELISTING);
		try {
			os.write(send);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

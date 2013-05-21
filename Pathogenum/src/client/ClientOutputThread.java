package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;
import utils.Misc;

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
			sendMovements();
			getMessages();
			getReady();
		}
		return;
	}

	public void getGameName() {
		if(!socket.isClosed()){
			try {
				os.write(Conversions.intToByteArray(Constants.SENDGAMENAME));
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
	}

	private void getMessages() {
		ArrayList<String> messages = cm.getChatMessagesToSend();
		for (int i = 0; i < messages.size(); i++) {
			String message = messages.get(i);
			byte[] command = Conversions.intToByteArray(Constants.SENDMESSAGE);
			byte[] length = Conversions.intToByteArray(message.length());
			byte[] text = message.getBytes();
			try {
				os.write(command);
				os.write(length);
				os.write(text);
				os.flush();
			} catch (IOException e) {
				 e.printStackTrace();
				closeConnection();
			}
		}

	}

	private void getReady() {
		boolean isReady = cm.isReady();
		if(isReady){
			byte[] command = Conversions.intToByteArray(Constants.SETREADY);
			try {
				os.write(command);
				os.flush();
			} catch (IOException e) {
				closeConnection();
			}
		}
	}

	private void sendMovements() {
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
			os.write(Constants.ADDGAME);
			os.write(Conversions.intToByteArray(port));
			os.write(Conversions.intToByteArray(gameName.length()));
			os.write(gameName.getBytes());
			os.flush();
		} catch (IOException e) {
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

package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;
import utils.misc;

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
			//System.out.println("after movements before messages");
			getMessages();
			//System.out.println("after messages before ready");
			//getGameName();
			getReady();
			//System.out.println("after ready");
		}
		System.out.println("ClientOutputThread stopped");
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
	
	private void getReady() {
		boolean isReady = cm.isReady();
		if(isReady){
			byte[] command = Conversions.intToByteArray(Constants.SETREADY);
			//System.out.println("Sending \"I AM READY BIATCH\"");
			try {
				os.write(command);
				os.flush();
			} catch (IOException e) {
				closeConnection();
			}
		}
	}

	private void sendMovements() {
		//System.out.println("--------------------------I'm here");
		ArrayList<Byte> movements = cm.getMovements();
		for (int i = 0; i < movements.size(); i++) {
			if (movements.get(i) != -1) {
				try {
					//System.out.println("Sending movement throug cyberspyder: " + movements.get(i));
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
			System.out.println("SENDINGSTARTNEWGAME from COT");
			os.write(Constants.ADDGAME);
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
		System.out.println("Sendind refresh to Hub");
		byte[] send = Conversions.intToByteArray(Constants.GAMELISTING);
		try {
			os.write(send);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

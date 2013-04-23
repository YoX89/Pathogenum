package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import utils.Conversions;

/**
 * Class for handling the connection between clients and server.
 * 
 * @author BigFarmor
 * 
 */
public class ClientConnectionHandler {
	private Socket sock;
	private OutputStream os;
	private InputThread iThread;
	public static final int SENDMESSAGE = 100, STARTGAME = 101,
			LEAVEGAME = 102, JOINGAME = 103, SETREADY = 104;

	public ClientConnectionHandler(InetAddress host, int port) {
		try {
			sock = new Socket(host, port);
			os = sock.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		iThread = new InputThread(sock);
		iThread.start();
	}

	/**
	 * Sends a message via the protocol SENDMESSAGE,N(LENGTH OF MESSAGE),MESSAGE
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		byte[] command = Conversions.intToByteArray(SENDMESSAGE);
		byte[] length = Conversions.intToByteArray(message.length());
		byte[] text = message.getBytes();
		try {
			os.write(command);
			os.write(length);
			os.write(text);
		} catch (IOException e) {
			// e.printStackTrace();
			closeConnection();
		}
	}

	/**
	 * Closes the connection between the CCH and the server.
	 * 
	 * @return
	 */
	public boolean closeConnection() {
		if (sock.isClosed())
			return true;
		try {
			sock.getOutputStream().write(LEAVEGAME);
			sock.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves the messages from the client.InputThread
	 * 
	 * @return
	 */
	public ArrayList<String> getMessage() {
		ArrayList<String> list = iThread.getChatMessages();
		return list;
	}

}

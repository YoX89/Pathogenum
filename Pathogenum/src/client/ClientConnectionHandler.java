package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import utils.Conversions;

public class ClientConnectionHandler {
	private Socket sock;
	private InputStream is;
	private OutputStream os;
	private InputThread iThread;
	public static final int SENDMESSAGE = 100, STARTGAME = 101, LEAVEGAME = 102, JOINGAME = 103, SETREADY = 104;
	public ClientConnectionHandler(InetAddress host, int port){
		try {
			sock = new Socket(host , port);
			is = sock.getInputStream();
			os = sock.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		iThread = new InputThread(sock);
		iThread.start();
	}
	public void sendMessage(String message){
		byte[] command = Conversions.intToByteArray(SENDMESSAGE);
		byte[] length = Conversions.intToByteArray(message.length());
		byte[] text = message.getBytes();
		try {
			os.write(command);
			os.write(length);
			os.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void closeConnection() {
		try {
			sock.getOutputStream().write(LEAVEGAME);
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	public ArrayList<String> getMessage(){
		ArrayList<String> list = iThread.getChatMessages();
		return list;
	}
	
}

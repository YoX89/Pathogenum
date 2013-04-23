package GamesLobby;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import utils.Conversions;

public class HubComServer extends Thread{
	
	public final static byte ADD = 1, REM = 2, LIST = 3;
	public static final int SENDMESSAGE = 100;
	Socket connection;
	GamesMonitor gm;
	InputStream is;
	OutputStream os;
	ChatMonitor cm;
	int ok;
	
	public HubComServer(Socket connection,GamesMonitor gm, ChatMonitor cm){
		this.connection = connection;
		this.gm = gm;
		this.cm = cm;
		try {
			is = connection.getInputStream();
			os = connection.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		byte[] command = new byte[4];
		
		ok = 0;
		while(ok != -1){
			try{
				ok = is.read(command);
				int com = Conversions.ByteArrayToInt(command);
				System.out.println("Command: " + command[0]);
				switch(com){
					case ADD:
						addCommand(connection,is);
						break;
					case REM: 
						removeCommand(connection, is);
						break;
					case LIST:
						listCommand(os);
						break;
					case SENDMESSAGE:
						fetchMessage();
						break;
				}
			}catch(IOException ie){
				ok = -1;
			}
		}
		cm.notifyAll();
		return;
	}

	private void listCommand(OutputStream os2) {
		ArrayList<GameAddress> addresses = gm.getGameAddresses();
		int gameNbr = addresses.size();
		try {
			os2.write(Conversions.intToByteArray(gameNbr));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(GameAddress ga: addresses){
			String host = ga.getHost();
			String gameName = ga.getGameName();
			int port = ga.getPort();
			int hostLength = (host.getBytes()).length;
			int gameNameLength = (gameName.getBytes()).length;
			try {
				os2.write(Conversions.intToByteArray(hostLength));
				os2.write(host.getBytes());
				os2.write(Conversions.intToByteArray(gameNameLength));
				os2.write(gameName.getBytes());
				os2.write(Conversions.intToByteArray(port));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void fetchMessage() throws IOException{
		byte[] buff = new byte[4];
			ok = is.read(buff);
		int mLength = Conversions.ByteArrayToInt(buff);
		buff = new byte[mLength];
			ok = is.read(buff);
		String message = new String(buff);
		cm.putMessage(connection.getInetAddress().getHostAddress(),message);
	}
	
	private void removeCommand(Socket connection2, InputStream is2) {
		InetAddress ia = connection2.getInetAddress();
		String ip = ia.getHostAddress();
		int port = connection2.getPort();
		byte[] nameLength = new byte[4];
		try {
			is2.read(nameLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int nameL = Conversions.ByteArrayToInt(nameLength);
		byte[] name = new byte[nameL];
		try {
			is2.read(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String gameName = new String(name);
		GameAddress ga = new GameAddress(gameName, ip, port);
		gm.removeGame(ga);
		//skicka conf kanske?
	}

	private void addCommand(Socket connection2,InputStream is2) {
		InetAddress ia = connection2.getInetAddress();
		String ip = ia.getHostAddress();
		int port = connection2.getPort();
		byte[] nameLength = new byte[4];
		try {
			is2.read(nameLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int nameL = Conversions.ByteArrayToInt(nameLength);
		byte[] name = new byte[nameL];
		try {
			is2.read(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String gameName = new String(name);
		GameAddress ga = new GameAddress(gameName, ip, port);
		gm.addGame(ga);
		//skicka conf h�r med maybe?
	}

}

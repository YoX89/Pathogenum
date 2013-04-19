package GamesLobby;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class HubComServer extends Thread{
	
	final static byte ADD = 1, REM = 2, LIST = 3;
	Socket connection;
	GamesMonitor gm;
	InputStream is;
	
	public HubComServer(Socket connection,GamesMonitor gm){
		this.connection = connection;
		this.gm = gm;
		try {
			is = connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		byte[] command = new byte[1];
		boolean isConnected = true;
		while(isConnected){
			try{
				is.read(command);
				switch(command[0]){
					case ADD:
						addCommand(is);
						break;
					case REM: 
						removeCommand(is);
						break;
					case LIST:
						listCommand(is);
						break;
				}
			}catch(IOException ie){
				isConnected = false;
			}
		}
	}

	private void listCommand(InputStream is2) {
		StringBuilder sb = new StringBuilder();
		ArrayList<GameAddress> addresses = gm.getGameAddresses();
		int gameNbr = addresses.size();
		sb.append(gameNbr);
		for(GameAddress ga: addresses){
			String host = ga.getHost();
			String gameName = ga.getGameName();
			int port = ga.getPort();
			int hostLength = (host.getBytes()).length;
			int gameNameLength = (gameName.getBytes()).length;
			sb.append(hostLength);
			sb.append(host.getBytes());
			sb.append(gameNameLength);
			sb.append(gameName.getBytes());
			sb.append(port);
		}
	}

	private void removeCommand(InputStream is2) {
		// TODO Auto-generated method stub
		
	}

	private void addCommand(InputStream is2) {
		// TODO Auto-generated method stub
		
	}

}

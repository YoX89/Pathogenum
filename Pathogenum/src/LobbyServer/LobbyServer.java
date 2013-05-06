package LobbyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A server representing the lobby that starts a corresponding input and output
 * server
 * 
 * @author Mardrey
 * 
 */
public class LobbyServer extends Thread {

	private ServerSocket s;
	private LobbyMonitor lm;
	private String name;
	private ArrayList<Socket> clients;

	public LobbyServer(String name, int port) {
		this.name = name;
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lm = new LobbyMonitor();
		
		clients = new ArrayList<Socket>();
		
		new ConnectionListener().start();
		
	}
	
	
	@Override
	public void run(){
		while(true){
			try {
				lm.waitForEvent();
				boolean isReady = true;
				for(int i = 0; i < clients.size(); ++i){
					if(!lm.getReady(clients.get(i).getInetAddress().getHostAddress())){
						isReady = false;
						break;
					}
				}
				
				//start gameserver
				
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			
		}
	}

	

	public String getGameName() {
		return name;
	}
	
	
	private class ConnectionListener extends Thread {
		
		public void run() {
			System.out.println("LobbyServer started");
			while (true) {// �ndra till while(!gamestarted)
				try {
					Socket conn = s.accept();
					System.out.println("Client connects");
					if (lm.getRegister().keySet().size() >= 4) {
						System.out.println("Maximum size of game");
						conn.close();
					} else {
						clients.add(conn);
						LobbyComOutputServer lcos = new LobbyComOutputServer(conn,
								lm);
						LobbyComInputServer lcis = new LobbyComInputServer(conn, lm);
						lcos.start();
						lcis.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}

package LobbyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server representing the lobby that starts a corresponding input and output
 * server
 * 
 * @author Mardrey
 * 
 */
public class LobbyServer extends Thread {

	ServerSocket s;
	LobbyMonitor lm;
	String name = "This is wrong";

	public LobbyServer(String name, int port) {
		this.name = name;
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lm = new LobbyMonitor(name);
	}

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
					LobbyComOutputServer lcos = new LobbyComOutputServer(conn,
							lm);
					LobbyComInputServer lcis = new LobbyComInputServer(conn, lm);
					lcos.start();
					lcis.start();
					while(!lcos.runs){
						
					}
					lm.notifyWaiters();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getGameName() {
		return name;
	}
}

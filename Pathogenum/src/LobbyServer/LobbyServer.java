package LobbyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import GameServer.GameServer;

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
	private ConnectionListener conListener;

	public LobbyServer(String name, int port) {
		this.name = name;
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lm = new LobbyMonitor(name);
		
		clients = new ArrayList<Socket>();

		conListener = new ConnectionListener();
		conListener.start();
	}

	@Override
	public void run() {

		boolean notReady = true;
		while (notReady) {
			try {
				lm.waitForEvent();
				for (int i = 0; i < clients.size(); ++i) {
					String hostname = clients.get(i).getInetAddress()
							.getHostName();
					System.out.println("Hostname: " + hostname);
					boolean rdy = lm.getReady(hostname);
					if (rdy) {
						notReady = false;
						break;
					}

				}
				// start gameserver

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		conListener.interrupt();
		GameServer gs = new GameServer(clients);
		gs.start();
	}

	public String getGameName() {
		return name;
	}

	private class ConnectionListener extends Thread {

		public void run() {
			System.out.println("LobbyServer started");
			boolean notInterupted = true;
			while (notInterupted) {// �ndra till while(!gamestarted)
				try {
					Socket conn = s.accept();
					System.out.println("Client connects");
					if (lm.getRegister().keySet().size() >= 4) {
						System.out.println("Maximum size of game");
						conn.close();
					} else {
						clients.add(conn);
						LobbyComOutputServer lcos = new LobbyComOutputServer(
								conn, lm);
						LobbyComInputServer lcis = new LobbyComInputServer(
								conn, lm);
						lcos.start();
						lcis.start();
						while (!lcos.runs) {

						}
						System.out.println("Set Hostname: "
								+ conn.getInetAddress().getHostName());
						lm.setReady(conn.getInetAddress().getHostName(), false);
						lm.notifyWaiters();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (this.isInterrupted()) {
					notInterupted = false;
					// TODO
					// Clean up

				}
			}
		}

	}
}

package lobbyserver;

import gameserver.GameServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;


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
	private Socket hubSocket;
	private InetAddress hubAddress;
	private int hubPort;
	private int gamePort;
	
	public LobbyServer(String name, int port, InetAddress hubInetAddress, int hubPort) {
		gamePort = port;
		this.name = name;
		try {
			s = new ServerSocket(gamePort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		hubAddress = hubInetAddress;
		this.hubPort = hubPort;
		try {
			hubSocket = new Socket(hubAddress, hubPort);
		} catch (IOException e) {
			System.out.println("Could not connect to hubserver in gamelobbyserver");
			//e.printStackTrace();
		}
		lm = new LobbyMonitor(name);
		
		clients = new ArrayList<Socket>();
		
		conListener = new ConnectionListener();
		conListener.start();
	}

	@Override
	public void run() {

		boolean allReady = false;
		while (!allReady) {
			try {
				lm.waitForEvent();
				allReady = true;
				for (int i = 0; i < clients.size(); ++i) {
					String hostname = clients.get(i).getInetAddress()
							.getHostName();
					if(!lm.getReady(hostname))
						allReady = false;
					
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			conListener.interrupt();
			s.close(); //interrupt s.accept in conlistener
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		writeDeRegLobby();
		GameServer gs = new GameServer(clients);
		
		gs.start();
	}

	private void writeDeRegLobby(){
		OutputStream os;
		try {
			os = hubSocket.getOutputStream();
		byte[] com = Conversions.intToByteArray(Constants.LEAVEGAME);
		os.write(com);
		int nameL = name.length();
		os.write(Conversions.intToByteArray(nameL));
		os.write(name.getBytes());
		os.write(Conversions.intToByteArray(gamePort));
		} catch (IOException e) {
			System.out.println("Could not write DeregisterLobbyMessage in lobby");
			//e.printStackTrace();
		}
		
	}
	
	public String getGameName() {
		return name;
	}

	private class ConnectionListener extends Thread {

		ArrayList<LobbyInputThread> lcisList;
		ArrayList<LobbyOutputThread> lcosList;
		
		public void run() {
			lcisList = new ArrayList<LobbyInputThread>();
			lcosList = new ArrayList<LobbyOutputThread>();
			boolean notInterupted = true;
			while (notInterupted) {// �ndra till while(!gamestarted)
				try {
					Socket conn = s.accept();
					if (lm.getRegister().keySet().size() >= 4) {
						System.out.println("Maximum size of game");
						conn.close();
					} else {
						clients.add(conn);
						LobbyOutputThread lcos = new LobbyOutputThread(
								conn, lm);
						LobbyInputThread lcis = new LobbyInputThread(
								conn, lm);
						lcisList.add(lcis);
						lcosList.add(lcos);
						lcos.start();
						lcis.start();
						while (!lcos.runs) {

						}
						lm.setReady(conn.getInetAddress().getHostName(), false);
						lm.notifyWaiters();
					}
				} catch (IOException e) {
					System.out.println("Serversocket in lobby closed, as an interrupt.");
					//e.printStackTrace();
				}

				if (this.isInterrupted()) {
					notInterupted = false;
				}
			}
			for(LobbyOutputThread lcos: lcosList){
				lcos.interrupt();
			}
			for(LobbyInputThread lcis: lcisList){
				lcis.interrupt();
			}
			return;
		}

	}
}

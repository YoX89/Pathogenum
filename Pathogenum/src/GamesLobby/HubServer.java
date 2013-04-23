package GamesLobby;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class HubServer extends Thread{
private static ArrayList<GameAddress> gameList = new ArrayList<GameAddress>();
ServerSocket Ssock;
GamesMonitor gm;
ChatMonitor cm;
public HubServer(int port){
	gm = new GamesMonitor();
	cm = new ChatMonitor();
	try {
		Ssock = new ServerSocket(port);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

//for testing purposes
public GamesMonitor getGM(){
	return gm;
}

public void run(){
		System.out.println("HubServer started on: " + Ssock.getInetAddress().getHostAddress() + Ssock.getLocalPort());
		while(true){
			try{
				Socket conn = Ssock.accept();
				HubComOutputServer hcos = new HubComOutputServer(conn, cm);
				HubComServer HCS = new HubComServer(conn, gm, cm);
				hcos.start();
				HCS.start();
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Shutting down Hub");
				return;
			}
		}
		
	}
}			

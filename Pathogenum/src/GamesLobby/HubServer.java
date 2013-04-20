package GamesLobby;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class HubServer extends Thread{
private static ArrayList<GameAddress> gameList = new ArrayList<GameAddress>();
ServerSocket Ssock;
GamesMonitor gm;
public HubServer(int port){
	gm = new GamesMonitor();
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
		while(true){
			try{
				Socket conn = Ssock.accept();
				HubComServer HCS = new HubComServer(conn, gm);
				HCS.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}			

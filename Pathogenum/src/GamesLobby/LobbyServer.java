package GamesLobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LobbyServer extends Thread{
	
	public static final int SENDMESSAGE = 100, STARTGAME = 101, LEAVEGAME = 102, JOINGAME = 103, SETREADY = 104;
	ServerSocket s;
	LobbyMonitor lm;
	
	public LobbyServer(int port){
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lm = new LobbyMonitor();
	}
	
	public void run(){
		System.out.println("LobbyServer started");
		while(true){//ändra till while(!gamestarted)
			try {
				Socket conn = s.accept();
				System.out.println("Client connects");
				LobbyComInputServer lcis = new LobbyComInputServer(conn, lm);
				LobbyComOutputServer lcos = new LobbyComOutputServer(conn, lm);
				lcis.start();
				lcos.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

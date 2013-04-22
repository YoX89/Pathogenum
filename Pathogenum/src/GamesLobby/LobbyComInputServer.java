package GamesLobby;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import utils.Conversions;

public class LobbyComInputServer extends Thread{
	
	InputStream is;
	Socket conn;
	LobbyMonitor lm;
	
	public LobbyComInputServer(Socket s, LobbyMonitor lm){
		conn = s;
		try {
			is = conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.lm = lm;
	}
	
	public void run(){
		System.out.println("LobbyComInputServer started");
		int ok = 0;
		while(ok != -1){ // �ndra t while(!gamestarted)
			byte[] com = new byte[4];
			try {
				ok = is.read(com);
				System.out.println("Input command read");
			} catch (IOException e) {
				e.printStackTrace();
				try {
					if(!conn.isClosed()){
						conn.close();
						lm.notifyWaiters();
					}
					return;	
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			switch(com[0]){
			case LobbyServer.SENDMESSAGE:
				try {
					System.out.println("\tCommand was SENDMESSAGE");
					fetchMessage();
				} catch (IOException e) {
					e.printStackTrace();
					if(!conn.isClosed()){
						try {
							conn.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						lm.notifyWaiters();
					}else{
						lm.notifyWaiters();
					}
					System.out.println("LobbyComInputServer stopped1");
					return;
				}
			break;
			case LobbyServer.LEAVEGAME:
				try {
					conn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				lm.notifyWaiters();
				ok = -1;
			break;
			}
		}
		System.out.println("LobbyComInputServer stopped2");
		return;
	}

	private void fetchMessage() throws IOException{
		byte[] buff = new byte[4];
			is.read(buff);
		int mLength = Conversions.ByteArrayToInt(buff);
		buff = new byte[mLength];
			is.read(buff);
		String message = new String(buff);
		lm.putMessage(conn.getInetAddress().getHostAddress(),message);
	}
}

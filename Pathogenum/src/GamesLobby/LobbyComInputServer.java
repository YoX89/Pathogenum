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
	int ok = 0;
	
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
		while(ok != -1){
			byte[] com = new byte[4];
			try {
				ok = is.read(com);
			}catch(IOException e){
				e.printStackTrace();
			}
			
			switch(com[0]){
			case LobbyServer.SENDMESSAGE:
				try {
					fetchMessage();
				} catch (IOException e) {
					ok = -1;
				}
			break;
			case LobbyServer.LEAVEGAME:
				ok = -1;
			break;
			}
		}
		try {
			if(!conn.isClosed())
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("LobbyComInputServer stopped2");
		lm.notifyWaiters();
		return;
	}

	private void fetchMessage() throws IOException{
		byte[] buff = new byte[4];
			ok = is.read(buff);
		int mLength = Conversions.ByteArrayToInt(buff);
		buff = new byte[mLength];
			ok = is.read(buff);
		String message = new String(buff);
		lm.putMessage(conn.getInetAddress().getHostAddress(),message);
	}
}

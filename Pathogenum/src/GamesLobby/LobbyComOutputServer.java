package GamesLobby;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import utils.Conversions;

public class LobbyComOutputServer extends Thread {

	OutputStream os;
	Socket conn;
	LobbyMonitor lm;

	public LobbyComOutputServer(Socket s, LobbyMonitor lm) {
		conn = s;
		try {
			os = conn.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.lm = lm;
		lm.registerOT(this);
	}

	public void run() {
		System.out.println("LobbyComOutputServer started");
		while (true) { // �ndra t while(!gamestarted)
			try {
				lm.waitForEvent(); // wait for event
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				readAndPrintMsg();
				if(conn.isClosed()){
					System.out.println("LobbyComOutputServer stopped");
					return;
				}
			} catch (IOException e1) {
				if(!conn.isClosed()){
					try {
						conn.close();
						System.out.println("LobbyComOutputServer stopped");
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}else{
					System.out.println("LobbyComOutputServer stopped");
					return;
				}
			}
			
			if(conn.isClosed()){
				System.out.println("LobbyComOutputServer stopped");
				return;
			}
			System.out.println("new turn");
		}
	}

	private void readAndPrintMsg() throws IOException{
		String msg = lm.getMessage(this);
		if (msg != null) {
			byte[] com = Conversions.intToByteArray(LobbyServer.SENDMESSAGE);
				os.write(com);
				com = Conversions.intToByteArray(msg.getBytes().length);
				os.write(com);
				os.write(msg.getBytes());
		}
	}
}

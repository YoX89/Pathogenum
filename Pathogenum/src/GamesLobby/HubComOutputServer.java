package GamesLobby;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import utils.Conversions;

public class HubComOutputServer extends Thread{
	OutputStream os;
	Socket conn;
	ChatMonitor lm;
	int ok = 0;
	
	public HubComOutputServer(Socket s, ChatMonitor lm) {
		conn = s;
		try {
			os = conn.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.lm = lm;
		lm.registerOT(this);
	}

	public void run(){
		System.out.println("HubComOutputServer started");
		while(ok != -1){
			try {
				lm.waitForEvent();
				if(conn.isClosed()){
					ok = -1;
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				readAndPrintMsg();
			} catch (IOException e) {
				//e.printStackTrace();
				ok = -1;
			}
		}
		System.out.println("HubComOutputServer stopped");
		lm.deRegister(this);
		return;
	}
	
	private void readAndPrintMsg() throws IOException{
		String msg = lm.getMessage(this);
		if (msg != null) {
			byte[] com = Conversions.intToByteArray(LobbyServer.SENDMESSAGE);
				os.write(com);
				com = Conversions.intToByteArray(msg.getBytes().length);
				os.write(com);
				os.write(msg.getBytes());
				System.out.println("Sending message: " + msg);
		}
	}
}

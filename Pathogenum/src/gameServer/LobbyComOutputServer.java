package gameServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import utils.Conversions;

/**A server that writes output from the monitor and writes it to the clients
 * @author Mardrey
 *
 */
public class LobbyComOutputServer extends Thread {

	OutputStream os;
	Socket conn;
	LobbyMonitor lm;
	int ok = 0;
	
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

	public void run(){
		System.out.println("LobbyComOutputServer started");
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
		System.out.println("LobbyComOutputServer stopped");
		lm.deRegister(this);
		return;
	}
	
	/**
	 * Reads message from monitor and writes to client
	 * @throws IOException
	 */
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

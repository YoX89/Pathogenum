package HubServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import publicMonitors.ChatMonitor;

import utils.Conversions;

/**
 * A server that handles output from hub to client
 * @author Mardrey
 *
 */
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
	
	/**
	 * Reads input from monitor and writes to clients 
	 * @throws IOException
	 */
	private void readAndPrintMsg() throws IOException{
		String msg = lm.getMessage(this);
		if (msg != null) {
			byte[] com = Conversions.intToByteArray(HubServer.SENDMESSAGE);
				os.write(com);
				com = Conversions.intToByteArray(msg.getBytes().length);
				os.write(com);
				os.write(msg.getBytes());
				System.out.println("Sending message: " + msg);
		}
	}
}

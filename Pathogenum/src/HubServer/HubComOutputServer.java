package HubServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

import publicMonitors.ChatMonitor;

import utils.Conversions;
import utils.GameAddress;

/**
 * A server that handles output from hub to client
 * 
 * @author Mardrey
 * 
 */
public class HubComOutputServer extends Thread {
	OutputStream os;
	Socket conn;
	GamesMonitor gm;
	int ok = 0;

	public HubComOutputServer(Socket s, ChatMonitor lm, GamesMonitor gm) {
		conn = s;
		try {
			os = conn.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gm = gm;
		lm.registerOT(this);
	}

	public void run() {
		System.out.println("HubComOutputServer started");
		while (ok != -1) {
			try {
				gm.waitForEvent(); //Bl� linus vad jobbig du �r...
				if(conn.isClosed()){
					ok = -1;	
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				readAndPrintMsg();
			} catch (IOException e) {
				// e.printStackTrace();
				ok = -1;
			}
		}
		System.out.println("HubComOutputServer stopped");
		gm.deRegister(this);
		return;
	}

	



	/**
	 * Reads input from monitor and writes to clients
	 * 
	 * @throws IOException
	 */

	private void readAndPrintMsg() throws IOException{
		String msg = gm.getMessage(this);

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

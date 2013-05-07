package LobbyServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import utils.Constants;
import utils.Conversions;

/**
 * A server that writes output from the monitor and writes it to the clients
 * 
 * @author Mardrey
 * 
 */
public class LobbyComOutputServer extends Thread {

	OutputStream os;
	Socket conn;
	LobbyMonitor lm;
	int ok = 0;
	boolean runs;

	public LobbyComOutputServer(Socket s, LobbyMonitor lm) {
		runs = false;
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
		while (ok != -1) {
			try {
				runs = true;
				lm.waitForEvent();
				if (conn.isClosed()) {
					ok = -1;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				if (ok != -1) {
					readAndPrintMsg();
					getAndPrintConnected();
					writeGameName();
					os.flush();
				}
			} catch (IOException e) {
				// e.printStackTrace();
				ok = -1;
			}
		}
		System.out.println("LobbyComOutputServer stopped");
		lm.deRegister(this);
		runs = false;
		return;
	}

	private void writeGameName() {
		System.out.println("LCOS:WRITEGAMENAME");
		String name = lm.writeGameName();
		if(name!=null){
			try {
				System.out.println("\tLCOS:WRITINGGAMENAMETOSTREAM");
				os.write(Conversions.intToByteArray(Constants.SENDGAMENAME));
				os.write(Conversions.intToByteArray(name.length()));
				os.write(name.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void getAndPrintConnected() throws IOException {
		System.out.println("Printing Connected");
		byte[] com = new byte[4];
		HashMap<Thread, Boolean> reg = lm.getRegister();
		com = Conversions.intToByteArray(Constants.SENDCONNECTED);
		os.write(com);
		int size = reg.keySet().size();
		com = Conversions.intToByteArray(size);
		os.write(com);
		for (Thread t : reg.keySet()) {
			LobbyComOutputServer lcos = (LobbyComOutputServer) t;
			InetAddress i = lcos.conn.getInetAddress();
			String in = i.getHostAddress();
			System.out.println(" -sending " + in);
			int l = in.length();
			com = Conversions.intToByteArray(l);
			os.write(com);
			os.write(in.getBytes());
		}
	}

	/**
	 * Reads message from monitor and writes to client
	 * 
	 * @throws IOException
	 */
	private void readAndPrintMsg() throws IOException {
		String msg = lm.getMessage(this);
		if (msg != null) {
			byte[] com = Conversions.intToByteArray(Constants.SENDMESSAGE);
			os.write(com);
			com = Conversions.intToByteArray(msg.getBytes().length);
			os.write(com);
			os.write(msg.getBytes());
			System.out.println("Sending message: " + msg);
		}
	}
}

package lobbyserver;

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
public class LobbyOutputThread extends Thread {

	OutputStream os;
	Socket conn;
	LobbyMonitor lm;
	int ok = 0;
	boolean runs;

	public LobbyOutputThread(Socket s, LobbyMonitor lm) {
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
		while (ok != -1) {
			try {
				runs = true;
				lm.waitForEvent();
				if (conn.isClosed()) {
					ok = -1;
				}
			} catch (InterruptedException e) {
				interrupt();
				ok = -1;
			}
			try {
				if (ok != -1) {
					readAndPrintMsg();
					getAndPrintConnected();
					ok = writeGameName();
					os.flush();
				}
			} catch (IOException e) {
				// e.printStackTrace();
				ok = -1;
			}
			if (this.isInterrupted()) {
				return; // keep socket
			}
		}
		runs = false;
		lm.deRegister(this);
		return;
	}

	private int writeGameName() {
		if (runs) {
			String name = lm.writeGameName();
			if (name != null) {
				try {
					os.write(Conversions.intToByteArray(Constants.SENDGAMENAME));
					os.write(Conversions.intToByteArray(name.length()));
					os.write(name.getBytes());
					return 1;
				} catch (IOException e) {
					return -1;
				}
			}
			return 1;
		}
		return -1;
	}

	private void getAndPrintConnected() throws IOException {
		if (runs) {
			byte[] com = new byte[4];
			HashMap<Thread, Boolean> reg = (HashMap<Thread, Boolean>) lm
					.getRegister().clone();
			com = Conversions.intToByteArray(Constants.SENDCONNECTED);
			os.write(com);
			int size = reg.keySet().size();
			com = Conversions.intToByteArray(size);
			os.write(com);
			for (Thread t : reg.keySet()) {
				LobbyOutputThread lcos = (LobbyOutputThread) t;
				InetAddress i = lcos.conn.getInetAddress();
				String in = i.getHostAddress();
				int l = in.length();
				com = Conversions.intToByteArray(l);
				os.write(com);
				os.write(in.getBytes());
			}
		}
	}

	/**
	 * Reads message from monitor and writes to client
	 * 
	 * @throws IOException
	 */
	private void readAndPrintMsg() throws IOException {
		if (runs) {
			String msg = lm.getMessage(this);
			if (msg != null) {
				byte[] com = Conversions.intToByteArray(Constants.SENDMESSAGE);
				os.write(com);
				com = Conversions.intToByteArray(msg.getBytes().length);
				os.write(com);
				os.write(msg.getBytes());
			}
		}
	}
}

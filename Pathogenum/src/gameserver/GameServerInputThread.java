package gameserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class GameServerInputThread extends Thread {

	private InputStream is;
	private GameMonitor gm;
	private int player;
	Socket sock;

	public GameServerInputThread(Socket s, GameMonitor gm, int playerNumber) {
		sock = s;
		try {
			this.is = s.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gm = gm;
		this.player = playerNumber;

	}

	@Override
	public void run() {
		boolean notClosed = true;
		while (notClosed) {
			byte b = 0x00;
			try {
				b = (byte) is.read();
				 gm.addIncomingCommand(b, player);
			} catch (IOException e) {
				e.printStackTrace();
				if (!sock.isClosed()) {
					try {
						sock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					notClosed = false;
				}
			}
		}	
		return;
	}

}

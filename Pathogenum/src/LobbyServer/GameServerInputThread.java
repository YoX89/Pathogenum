package LobbyServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class GameServerInputThread extends Thread {

	private InputStream is;
	private GameMonitor gm;
	private int player;
	public GameServerInputThread(Socket s, GameMonitor gm, int playerNumber){
		try {
			this.is = s.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.gm = gm;
		this.player = playerNumber;

	}
	
	@Override
	public void run(){
		while(true){
			byte b = 0x00;
			try {
				b = (byte) is.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gm.addIncomingCommand(b, player);
			
		}
	}

}

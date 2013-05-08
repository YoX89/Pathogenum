package GameServer;

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
				//System.out.println("Server waiting to read from player " + player);
				b = (byte) is.read();
				//System.out.println("Getting movement on gameserverinput: " + (int)b);
				gm.addIncomingCommand(b, player);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}

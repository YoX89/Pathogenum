package GameServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


import utils.Conversions;

public class GameServerOutputThread extends Thread {
	private OutputStream os;
	private GameMonitor gm;
	private long frameID = 0;
	
	public GameServerOutputThread(Socket s, GameMonitor gm){
		try {
			this.os = s.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gm = gm;

	}
	
	@Override
	public void run(){
		try {
			os.write(Conversions.intToByteArray(gm.getNbrPlayers()));
		while(true){
			byte[] b = gm.getOutGoingCommand(frameID);
				os.write(b);
		}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}

package GameServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


import utils.Constants;
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
				byte[] movements = gm.getOutGoingCommand(frameID);
				byte[] command = Conversions.intToByteArray(Constants.SENDMOVEMENT);
				System.out.println("Writing command from GameServer OutputStream");
				os.write(command);
				os.write(movements);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}

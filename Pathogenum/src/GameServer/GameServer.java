package GameServer;

import java.net.Socket;
import java.util.Random;


public class GameServer extends Thread{

	private long frameID = 0;
	private long seed;
	private long time;
	private GameMonitor gm;
	private int nbrOfPlayers;
	
	public GameServer(Socket[] s){
		nbrOfPlayers = s.length;
		Random rand = new Random();
		
		seed = rand.nextLong(); 
		gm = new GameMonitor(nbrOfPlayers);
		for(int i = 0; i < s.length; ++i){
			GameServerInputThread git = new GameServerInputThread(s[i], gm, nbrOfPlayers);
			GameServerOutputThread got = new GameServerOutputThread(s[i], gm);
			got.start();
			git.start();
			
		}
		time = System.currentTimeMillis();

	}

	@Override
	public void run() {
		
		
		long delta = 17;
		while(true) {
			Byte[] commands = new Byte[nbrOfPlayers];
			//Check movements from clients
			for(int i = 0; i < nbrOfPlayers; ++i){
				commands[i] = gm.getIncomingCommand(i);
			}
			

			//Send movements and frame ID
			gm.setOutgoingCommands(commands, frameID++);



			time = System.currentTimeMillis();
			try {
				Thread.sleep(17 - (delta - 17));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			delta = System.currentTimeMillis() - time;
		}
	}
}
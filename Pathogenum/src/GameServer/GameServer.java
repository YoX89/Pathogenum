package GameServer;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import utils.misc;


public class GameServer extends Thread{

	private long frameID = 0;
	private long seed;
	private long time;
	private GameMonitor gm;
	private int nbrOfPlayers;
	
	public GameServer(ArrayList<Socket> clients){
		nbrOfPlayers = clients.size();
		Random rand = new Random();
		
		seed = rand.nextLong(); 
		gm = new GameMonitor(nbrOfPlayers);
		for(int i = 0; i < nbrOfPlayers; ++i){
			GameServerInputThread git = new GameServerInputThread(clients.get(i), gm, nbrOfPlayers);
			GameServerOutputThread got = new GameServerOutputThread(clients.get(i), gm);
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

			//System.out.println("Putting command with framID: " + frameID + " in the monitor");

			time = System.currentTimeMillis();
			try {
				long sleepingTime = 17 - (delta -17);
				if(sleepingTime > 0){
					Thread.sleep(sleepingTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			delta = System.currentTimeMillis() - time;
		}
	}
}
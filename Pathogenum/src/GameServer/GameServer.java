package GameServer;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import utils.Conversions;
import utils.misc;


public class GameServer extends Thread{

	private long frameID = 0;
	private long seed;
	private long time;
	private GameMonitor gm;
	private int nbrOfPlayers;
	ArrayList<GameServerOutputThread> gots; 

	public GameServer(ArrayList<Socket> clients){
		nbrOfPlayers = clients.size();
		Random rand = new Random();
		gots = new ArrayList<GameServerOutputThread>();
		seed = rand.nextLong(); 
		gm = new GameMonitor(nbrOfPlayers);
		for(int i = 0; i < nbrOfPlayers; ++i){
			GameServerInputThread git = new GameServerInputThread(clients.get(i), gm, i);
			GameServerOutputThread got = new GameServerOutputThread(clients.get(i), gm);
			gots.add(got);
			//			got.start();
			git.start();

		}


		time = System.currentTimeMillis();

	}

	@Override
	public void run() {
		//Send initmessage

		for(int i = 0; i < gots.size(); i++){
			gots.get(i).sendInit(seed, nbrOfPlayers, i);
		}


		for(int i = 0; i < gots.size(); i++){
			gots.get(i).start();
		}



		long sleeptime = 17;
		long delta = 0;
		while(true) {
			long diff = delta - sleeptime;
			if(diff < 0){
				diff = 0;
			}
			Byte[] commands = new Byte[nbrOfPlayers];
			//Check movements from clients
			for(int i = 0; i < nbrOfPlayers; ++i){
				commands[i] = gm.getIncomingCommand(i);
				System.out.println("The command from player " + i + " is: " + commands[i]);
			}

			//Send movements and frame ID
			gm.setOutgoingCommands(commands, frameID++);

			//System.out.println("Putting command with framID: " + frameID + " in the monitor");

			time = System.currentTimeMillis();
			try {
				long sleepingTime = sleeptime - diff;
				//System.out.println("SLEEPINGTIME: " + sleepingTime);
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
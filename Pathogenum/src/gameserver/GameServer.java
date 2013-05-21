package gameserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import utils.Constants;
import utils.Conversions;
import utils.Misc;


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
			GameServerOutputThread got = new GameServerOutputThread(clients.get(i), gm, i);
			gots.add(got);
			got.start();
			git.start();

		}
		time = System.currentTimeMillis();

	}

	@Override
	public void run() {
		for(int i = 0; i < gots.size(); i++){
			if(!gots.get(i).sendInit(seed, nbrOfPlayers, i)){
				System.out.println("SENDING INIT FAILED");
			}
		}
		long desiredSleep = Constants.GAME_SPEED;
		long diff = 0;
		while(true) {
			Byte[] commands = new Byte[nbrOfPlayers];
			for(int i = 0; i < nbrOfPlayers; ++i){
				commands[i] = gm.getIncomingCommand(i);
			}

			//Send movements and frame ID
			gm.setOutgoingCommands(commands, frameID++);
			time = System.currentTimeMillis();
			try {
				long sleepingTime = desiredSleep - diff;
				if(sleepingTime > 0){
					Thread.sleep(sleepingTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			diff = (System.currentTimeMillis() - time) - desiredSleep;
		}
	}


}
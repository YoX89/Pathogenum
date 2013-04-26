package LobbyServer;

import java.util.Random;

public class GameServer extends Thread{

	private long frameID = 0;
	private long seed;
	private long time;
	private GameMonitor gm;
	private int nbrOfPlayers;

	public GameServer(int nbrOfPlayers){
		this.nbrOfPlayers = nbrOfPlayers;
		Random rand = new Random();

		seed = rand.nextLong(); 
		gm = new GameMonitor(nbrOfPlayers);
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

package HubServer;

import java.net.InetAddress;
import java.util.ArrayList;

import utils.GameAddress;
/**
 * a monitor used to store known games for the HubServer
 * @author Mardrey
 *
 */
public class GamesMonitor {
	
	ArrayList<GameAddress> games;
	//int sentSize = 0;
	public GamesMonitor(){
		games = new ArrayList<GameAddress>();
		games.add(new GameAddress("testgame","localhost",12345));
	}
	/**
	 * add a game address to the known list
	 * @param ga
	 */
	public synchronized void addGame(GameAddress ga){
		games.add(ga);
	}
	/**
	 * remove a game from the known list of games
	 * @param ga
	 */
	public synchronized void removeGame(GameAddress ga){
		games.remove(ga);
	}
	/**
	 * returns the list of known games
	 * @return
	 */
	public synchronized ArrayList<GameAddress> getGameAddresses(){
		System.out.println("gets game addresses");
//		if(sentSize != games.size()){
//			sentSize = games.size();
//			return games;
//		}
		return games;
		//return null;
	}
}

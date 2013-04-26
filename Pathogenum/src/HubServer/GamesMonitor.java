package HubServer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;

import publicMonitors.ChatMonitor;

import utils.GameAddress;
/**
 * a monitor used to store known games for the HubServer
 * @author Mardrey
 *
 */
public class GamesMonitor extends ChatMonitor{
	

	LinkedList<GameAddress> games;


	public GamesMonitor(){
		games = new LinkedList<GameAddress>();
		addGame(new GameAddress("testgame","localhost",12345));
	}
	/**
	 * add a game address to the known list
	 * @param ga
	 */
	public synchronized void addGame(GameAddress ga){
		games.offer(ga);
		notifyWaiters();
	}
	/**
	 * remove a game from the known list of games
	 * @param ga
	 */
	public synchronized void removeGame(GameAddress ga){
		games.remove(ga);
		notifyWaiters();
	}
	/**
	 * returns the list of known games
	 * @return
	 */
	public synchronized LinkedList<GameAddress> getGameAddresses(){
			return games;
	//	}
	//	return null;
	}
}

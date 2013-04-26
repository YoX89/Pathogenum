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
	boolean gamesHaveChanged = false;
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
		gamesHaveChanged = true;
		notifyWaiters();
	}
	/**
	 * remove a game from the known list of games
	 * @param ga
	 */
	public synchronized void removeGame(GameAddress ga){
		games.remove(ga);
		gamesHaveChanged = true;
		notifyWaiters();
	}
	/**
	 * returns the list of known games
	 * @return
	 */
	public synchronized LinkedList<GameAddress> getGameAddresses(){
		System.out.println("gets game addresses");
		if(gamesHaveChanged){
			gamesHaveChanged = false;
			return games;
		}
		return null;
	}
}

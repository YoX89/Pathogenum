package HubServer;

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
	}
	/**
	 * add a game address to the known list
	 * @param ga
	 */
	public synchronized void addGame(GameAddress ga){
		if(!games.contains(ga) && ga.getPort() > 1024){
			games.offer(ga);
			notifyAll();
		}
	}
	/**
	 * remove a game from the known list of games
	 * @param ga
	 */
	public synchronized void removeGame(GameAddress ga){
		System.out.println("Should remove: " + ga);
		boolean rem = games.remove(ga);
		System.out.println("Did remove? " + rem);
		notifyAll();
	}
	/**
	 * returns the list of known games
	 * @return
	 */
	public synchronized LinkedList<GameAddress> getGameAddresses(){
			return games;
	}
}

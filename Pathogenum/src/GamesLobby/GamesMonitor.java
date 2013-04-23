package GamesLobby;

import java.util.ArrayList;
/**
 * a monitor used to store known games for the HubServer
 * @author Mardrey
 *
 */
public class GamesMonitor {
	
	ArrayList<GameAddress> games;
	
	public GamesMonitor(){
		games = new ArrayList<GameAddress>();
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
		return games;
	}
}

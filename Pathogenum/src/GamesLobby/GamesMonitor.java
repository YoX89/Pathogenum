package GamesLobby;

import java.util.ArrayList;

public class GamesMonitor {
	
	ArrayList<GameAddress> games;
	
	public GamesMonitor(){
		games = new ArrayList<GameAddress>();
	}
	
	public synchronized void addGame(GameAddress ga){
		games.add(ga);
	}
	
	public synchronized void removeGame(GameAddress ga){
		games.remove(ga);
	}
	
	public synchronized ArrayList<GameAddress> getGameAddresses(){
		return games;
	}
}

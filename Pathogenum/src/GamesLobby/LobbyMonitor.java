package GamesLobby;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * A monitor used by the lobby servers, extends chatmonitor with functionality regarding players in the lobby
 * @author Mardrey
 *
 */
public class LobbyMonitor extends ChatMonitor{
	
	LinkedList<String> messageQueue;
	HashMap<String, Boolean> ready;
	HashMap<LobbyComOutputServer, Boolean> register;
	//HashSet<LobbyComOutputServer> register;
	
	public LobbyMonitor(){
		super();
		System.out.println("LobbyMonitor created");
	}

	
	/**
	 * Changes the ready state of a player to true or false
	 * @param ip
	 * @param rdy
	 */
	public synchronized void setReady(String ip, boolean rdy){
		ready.put(ip, rdy);
		setChanged();
		notifyAll();
	}
	
	/**
	 * Returns the ready state of a player
	 * @param ip
	 * @return
	 */
	public synchronized boolean getReady(String ip){
		return ready.get(ip);
	}
}

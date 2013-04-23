package GamesLobby;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LobbyMonitor extends ChatMonitor{
	
	LinkedList<String> messageQueue;
	HashMap<String, Boolean> ready;
	HashMap<LobbyComOutputServer, Boolean> register;
	//HashSet<LobbyComOutputServer> register;
	
	public LobbyMonitor(){
		messageQueue = new LinkedList<String>();
		ready = new HashMap<String, Boolean>();
		//register = new HashSet<LobbyComOutputServer>();
		register = new HashMap<LobbyComOutputServer, Boolean>();
		System.out.println("LobbyMonitor created");
	}

	
	public synchronized void setReady(String ip, boolean rdy){
		ready.put(ip, rdy);
		setChanged();
		notifyAll();
	}
	
	public synchronized boolean getReady(String ip){
		return ready.get(ip);
	}
}

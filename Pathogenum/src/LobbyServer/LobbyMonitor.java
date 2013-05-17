package LobbyServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import publicMonitors.ChatMonitor;

/**
 * A monitor used by the lobby servers, extends chatmonitor with functionality regarding players in the lobby
 * @author Mardrey
 *
 */
public class LobbyMonitor extends ChatMonitor{
	
	HashMap<String, Boolean> ready;
	ArrayList<String> connectedHosts;
	String gameName;
	boolean shouldSendGameName = false;
	//HashSet<LobbyComOutputServer> register;
	
	public LobbyMonitor(String gameName){
		super();
		System.out.println("LobbyMonitor created");
		this.gameName = gameName;
		ready = new HashMap<String, Boolean>();
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
		if(ready.get(ip) == null){
			return false;
		}
		return ready.get(ip);
	}
	public synchronized void addHost(String host){
		connectedHosts.add(host);
	}
	public synchronized void removeHost(String host){
		connectedHosts.remove(host);
	}
	public synchronized ArrayList<String>  getHosts(){
		return connectedHosts;
	}


	public synchronized void shouldSendGameName() {
		System.out.println("\tLMONITOR:SHOULDSENDGAME");
		shouldSendGameName = true;
		notifyAll();
	}


	public String writeGameName() {
		System.out.println("\tLMONITOR:WRITEGAMENAME");
		if(shouldSendGameName){
			System.out.println("\tLMONITOR:WRITINGGAMENAME");
			shouldSendGameName = false;
			return gameName;
		}
		return null;
	}
}

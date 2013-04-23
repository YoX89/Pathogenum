package GamesLobby;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LobbyMonitor {
	
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

	public void registerOT(LobbyComOutputServer lcos){
		register.put(lcos, true);
	}
	
	public void deRegister(LobbyComOutputServer lcos){
		register.remove(lcos);
	}
	
	public synchronized void putMessage(String hostAddress, String message) {
		messageQueue.offer(hostAddress + ": " + message);
		setChanged();
		notifyAll();
	}
	
	public synchronized String getMessage(LobbyComOutputServer lcos){
		String msg = messageQueue.peek();
		register.put(lcos, true);
		if(msg == null){
			return null;
		}
		if(allSent()){
			messageQueue.pop();
			return msg;
		}
		return msg;
	}
	
	public synchronized void setReady(String ip, boolean rdy){
		ready.put(ip, rdy);
		setChanged();
		notifyAll();
	}
	
	public synchronized boolean getReady(String ip){
		return ready.get(ip);
	}
	
	public synchronized void waitForEvent() throws InterruptedException{
			wait();	
	}
	
	private void setChanged(){
		for(LobbyComOutputServer lcos: register.keySet()){
			register.put(lcos, false);
		}
	}
	
	private boolean allSent(){
		for(LobbyComOutputServer lcos: register.keySet()){
			if(register.get(lcos) == false){
				return false;
			}
		}
		return true;
	}
	
	public synchronized void notifyWaiters(){
		notifyAll();
	}
}

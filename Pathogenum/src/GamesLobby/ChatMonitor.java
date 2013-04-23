package GamesLobby;

import java.util.HashMap;
import java.util.LinkedList;

public class ChatMonitor {

	LinkedList<String> messageQueue;
	HashMap<String, Boolean> ready;
	HashMap<Thread, Boolean> register;

	// HashSet<LobbyComOutputServer> register;

	public ChatMonitor() {
		messageQueue = new LinkedList<String>();
		ready = new HashMap<String, Boolean>();
		// register = new HashSet<LobbyComOutputServer>();
		register = new HashMap<Thread, Boolean>();
		System.out.println("LobbyMonitor created");
	}

	public void registerOT(Thread lcos) {
		register.put(lcos, true);
	}

	public void deRegister(Thread lcos) {
		register.remove(lcos);
	}

	public synchronized void putMessage(String hostAddress, String message) {
		messageQueue.offer(hostAddress + ": " + message);
		setChanged();
		notifyAll();
	}

	public synchronized String getMessage(Thread lcos) {
		String msg = messageQueue.peek();
		register.put(lcos, true);
		if (msg == null) {
			return null;
		}
		if (allSent()) {
			messageQueue.pop();
			return msg;
		}
		return msg;
	}

	public synchronized void waitForEvent() throws InterruptedException {
		wait();
	}

	protected void setChanged() {
		for (Thread lcos : register.keySet()) {
			register.put(lcos, false);
		}
	}

	protected boolean allSent() {
		for (Thread lcos : register.keySet()) {
			if (register.get(lcos) == false) {
				return false;
			}
		}
		return true;
	}

	public synchronized void notifyWaiters() {
		notifyAll();
	}

}

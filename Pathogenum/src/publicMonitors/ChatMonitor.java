package publicMonitors;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Monitor used by Server threads that read and write chat messages
 * @author Mardrey
 *
 */

public class ChatMonitor {
	
	protected LinkedList<String> messageQueue;

	protected HashMap<Thread, Boolean> register;

	// HashSet<LobbyComOutputServer> register;

	public HashMap<Thread, Boolean> getRegister(){
		return register;
	}
	
	public ChatMonitor() {
		messageQueue = new LinkedList<String>();
		// register = new HashSet<LobbyComOutputServer>();
		register = new HashMap<Thread, Boolean>();
		System.out.println("ChatMonitor created");
	}

	/**
	 * Adds a thread to known threads
	 * @param lcos
	 */
	public synchronized void registerOT(Thread lcos) {
		register.put(lcos, true);
		notifyAll();
	}

	/**
	 * Removes a thread from known threads
	 * @param lcos
	 */
	public synchronized void deRegister(Thread lcos) {
		register.remove(lcos);
		notifyAll();
	}

	/**
	 * Adds a message in queue
	 * @param hostAddress
	 * @param message
	 */
	public synchronized void putMessage(String hostAddress, String message) {
		System.out.println("doing putMessage");
		messageQueue.offer(hostAddress + ": " + message);
		setChanged();
		notifyAll();
	}

	/**
	 * Returns the first string in the queue, removes it if all servers have read it
	 * @param lcos
	 * @return msg
	 */
	public synchronized String getMessage(Thread lcos) {
		String msg = messageQueue.peek();
		System.out.println("doing getMessage");
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
	/**
	 * wait for an event, for example a message being put in the buffer
	 * @throws InterruptedException
	 */
	public synchronized void waitForEvent() throws InterruptedException {
		wait();
	}

	/**
	 * Maps a boolean to all threads that signify an unread change
	 */
	protected void setChanged() {
		for (Thread lcos : register.keySet()) {
			register.put(lcos, false);
		}
	}

	/**
	 * Checks if all threads have read a change
	 * @return
	 */
	protected boolean allSent() {
		for (Thread lcos : register.keySet()) {
			if (!register.get(lcos)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * notifies all waiting threads
	 */
	public synchronized void notifyWaiters() {
		notifyAll();
	}

}

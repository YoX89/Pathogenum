package client;

import java.util.ArrayList;
import java.util.LinkedList;

import utils.Conversions;
import utils.GameAddress;

public class ClientMonitor {
	private ArrayList<String> messagesToSend;
	private ArrayList<String> recievedMessages;
	private ArrayList<Byte> movementsToSend;
	private LinkedList<Byte[]> recievedMovements;
	private ArrayList<GameAddress> currentGames;
	private int nbrOfPlayers;
	public ClientMonitor(){
		messagesToSend = new ArrayList<String>();
		recievedMessages = new ArrayList<String>();
		movementsToSend = new ArrayList<Byte>();
		recievedMovements  = new LinkedList<Byte[]>();
		currentGames = new ArrayList<GameAddress>();
	}
	
	public synchronized void addMessage(String message) {
		System.out.println("clientmonitor::addMessage");
		messagesToSend.add(message);
		notifyAll();
	}
	public synchronized void waitForEvent() throws InterruptedException {
		wait();
		System.out.println("notify'd");
	}
	public synchronized ArrayList<String> getMessages(){
		System.out.println("clientmonitor::getMessages");
		ArrayList<String> temp = (ArrayList<String>)messagesToSend.clone();
		messagesToSend.clear();
		return temp;
	}

	public synchronized ArrayList<String> getChatMessages() {
		System.out.println("clientMonitor::addChatMessages");
		ArrayList<String> temp = (ArrayList<String>)recievedMessages.clone();
		recievedMessages.clear();
		return temp;
	}

	public synchronized void addRecievedMessage(String text) {
		System.out.println("clientMonitor::addRecievedMessage");
		recievedMessages.add(text);
	}

	public synchronized void addMovement(byte[] command) {
		movementsToSend.add(new Byte(command[0]));	
		notifyAll();
	}
/*
 * gets movements from client to output thread
 */
	public synchronized ArrayList<Byte> getMovements() {		
		ArrayList<Byte> temp = (ArrayList<Byte>)movementsToSend.clone();
		movementsToSend.clear();
		return temp;
	}

	public synchronized void setNbrOfPlayers(int players) {
		nbrOfPlayers  = players;	
	}
	public synchronized int getNbrOfPlayers(){
		return nbrOfPlayers;
	}
	/*
	 * gets movements from input thread to client
	 */
	public synchronized byte[] recieveMovements(byte[] buff) {
		Byte[] oldestMovement = recievedMovements.pop();
		byte[] primOm = Conversions.ObjectByteArrayToPrimitiveByteArray(oldestMovement);
		return primOm;
	}

	public synchronized ArrayList<GameAddress> getGames() {
		ArrayList<GameAddress> temp = (ArrayList<GameAddress>)currentGames.clone();
		currentGames.clear();
		return temp;
	}

	public synchronized ArrayList<String> getNames() {
		
		return null;
	}

	public synchronized void addGame(GameAddress gameAddress) {
		currentGames.add(gameAddress);		
	}
	
	public synchronized void recieveMovement(Byte[] movement){
		recievedMovements.offer(movement);
	}
}

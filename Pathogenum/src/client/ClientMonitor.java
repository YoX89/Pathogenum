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
	private String gameName;
	private ArrayList<String> connectedPlayers;
	private boolean isReady = false;

	public ClientMonitor(){
		messagesToSend = new ArrayList<String>();
		recievedMessages = new ArrayList<String>();
		movementsToSend = new ArrayList<Byte>();
		recievedMovements  = new LinkedList<Byte[]>();
		currentGames = new ArrayList<GameAddress>();
		gameName = "";
		connectedPlayers = new ArrayList<String>();
	}
	
	public synchronized void addMessage(String message) {
		System.out.println("Message to be sent in monitor: " + message);
		messagesToSend.add(message);
		notifyAll();
	}
	public synchronized void waitForEvent() throws InterruptedException {
		wait();
	}
	public synchronized ArrayList<String> getChatMessagesToSend(){
		System.out.println("Message gotten to be sent from oThread");
		ArrayList<String> temp = (ArrayList<String>)messagesToSend.clone();
		messagesToSend.clear();
		return temp;
	}

	public synchronized ArrayList<String> getRecievedChatMessages() {
		ArrayList<String> temp = new ArrayList<String>();
		if(recievedMessages.size() != 0){
			temp = (ArrayList<String>)recievedMessages.clone(); //G�rs v�ldigt ofta...
			recievedMessages.clear();
		}
		return temp;
	}

	public synchronized void addRecievedMessage(String text) {
		System.out.println("clientmonitor::addrecievedmessage: " + text);
		recievedMessages.add(text);
		notifyAll();
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
		if(recievedMovements.isEmpty()){
			return buff;
		}
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
		return connectedPlayers;
	}

	public synchronized void addGame(GameAddress gameAddress) {
		currentGames.add(gameAddress);		
	}
	
	public synchronized void recieveMovement(Byte[] movement){
		recievedMovements.offer(movement);
	}

	public synchronized String getGameName() {
		return gameName;	
	}
	public synchronized void setGameName(String name){
		System.out.println("CMONITOR:SETTINGGAMENAME");
		gameName = name;
	}


	public void setconnectedPlayers(ArrayList<String> connectedPlayers) {
		this.connectedPlayers = connectedPlayers;
}
	public synchronized void setReady(boolean b) {
		isReady = b;
		notifyAll();
	}

	public synchronized boolean isReady() {
		return isReady;
	}
}

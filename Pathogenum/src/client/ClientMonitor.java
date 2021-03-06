package client;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import javax.sql.rowset.spi.SyncResolver;

import utils.Conversions;
import utils.GameAddress;

public class ClientMonitor {
	private ArrayList<String> messagesToSend;
	private ArrayList<String> recievedMessages;
	private ArrayList<Byte> movementsToSend;
	private LinkedList<Byte[]> recievedMovements;
	private ArrayList<GameAddress> currentGames;
	private int nbrOfPlayers = -1;
	private String gameName;
	private ArrayList<String> connectedPlayers;
	private ArrayList<Integer> dropedPlayers;
	private boolean isReady = false;
	private long seed = -1;
	private int mIndex = -1;

	public ClientMonitor(){
		messagesToSend = new ArrayList<String>();
		recievedMessages = new ArrayList<String>();
		movementsToSend = new ArrayList<Byte>();
		recievedMovements  = new LinkedList<Byte[]>();
		currentGames = new ArrayList<GameAddress>();
		gameName = "";
		connectedPlayers = new ArrayList<String>();
		dropedPlayers = new ArrayList<Integer>();

	}

	public synchronized void addMessage(String message) {
		messagesToSend.add(message);
		notifyAll();
	}

	public synchronized void waitForEvent() throws InterruptedException {
		wait();
	}
	public synchronized ArrayList<String> getChatMessagesToSend(){
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
		recievedMessages.add(text);
		notifyAll();
	}

	public synchronized void addMovementToSend(byte[] command) {
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


	/*
	 * gets movements from input thread to client
	 */
	public synchronized byte[] recieveMovements(byte[] buff) {
		while(recievedMovements.isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

	public synchronized void addMovementToBuffer(byte[] movement){
		Byte[] mov = new Byte[movement.length];
		for(int i=0; i<movement.length;i++){
			mov[i] = new Byte(movement[i]);
		}
		recievedMovements.offer(mov);
		notifyAll();
	}

	public synchronized String getGameName() {
		return gameName;	
	}
	public synchronized void setGameName(String name){
		gameName = name;
	}


	public synchronized void setconnectedPlayers(ArrayList<String> connectedPlayers) {
		this.connectedPlayers = connectedPlayers;
	}
	public synchronized void setReady(boolean b) {
		isReady = b;
		notifyAll();
	}

	public synchronized boolean isReady() {
		return isReady;
	}

	public synchronized void setSeed(long seed) {
		this.seed = seed; 
		notifyAll();
	}

	public synchronized long getSeed() throws InterruptedException {
		while(seed == -1){
			wait();
		}
		return seed;

	}

	public synchronized void setMyIndex(int myIndex) {
		mIndex = myIndex;
		notifyAll();
	}

	public synchronized int getMyIndex() throws InterruptedException {
		while(mIndex == -1){
			wait();
		}
		return mIndex;
	}

	public synchronized void setNbrOfPlayers(int players) {
		nbrOfPlayers  = players;	
		notifyAll();
	}
	public synchronized int getNbrOfPlayers() throws InterruptedException{
		while(nbrOfPlayers == -1){
			wait();
		}
		return nbrOfPlayers;
	}

	public synchronized ArrayList<Integer> getDroppedPlayers() {
		ArrayList<Integer> drop = (ArrayList<Integer>) dropedPlayers.clone();
		dropedPlayers.clear();
		return drop;
	}

	public synchronized void setDroppedPlayer(int index) {
		dropedPlayers.add(index);
	}
}

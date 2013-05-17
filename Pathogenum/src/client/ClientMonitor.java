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
	private long seed;
	private int mIndex;

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
		//System.out.println("Message to be sent in monitor: " + message);
		messagesToSend.add(message);
		notifyAll();
	}
	public synchronized void waitForEvent() throws InterruptedException {
		wait();
	}
	public synchronized ArrayList<String> getChatMessagesToSend(){
		//System.out.println("Message gotten to be sent from oThread");
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
		//System.out.println("clientmonitor::addrecievedmessage: " + text);
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
		while(recievedMovements.isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Byte[] oldestMovement = recievedMovements.pop();
		//System.out.println("OLDESTMOVEMENT: " + misc.printByte(oldestMovement));
		byte[] primOm = Conversions.ObjectByteArrayToPrimitiveByteArray(oldestMovement);
		//System.out.println("OLDESTMOVEMENTPRIMITIV: " + misc.printByte(primOm));
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
		System.out.println("Movement to buffer is: ");
		for(int i = 0; i < movement.length; i++) {
			System.out.print(movement[i] + " ");
		}
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
		//System.out.println("CMONITOR:SETTINGGAMENAME");
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
	}

	public synchronized long getSeed() {
		return seed;
		
	}

	public synchronized void setMyIndex(int myIndex) {
		mIndex = myIndex;
	}
	
	public synchronized int getMyIndex() {
		return mIndex;
	}
}

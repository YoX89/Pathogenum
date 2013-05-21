package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;
import utils.GameAddress;

/**
 * an inputthread responsible for getting input to the client from the server.
 * 
 * @author BigFarmor, Mardrey
 * 
 */
public class ClientInputThread extends Thread {
	Socket sock;
	ClientMonitor cm;
	InputStream is;
	ArrayList<GameAddress> gameList;
	ArrayList<String> connectedPlayers;
	boolean ok = true;

	public ClientInputThread(Socket sock, ClientMonitor cm) {
		this.sock = sock;
		this.cm = cm;
		gameList = new ArrayList<GameAddress>();
		try {
			is = sock.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connectedPlayers = new ArrayList<String>();
	}


	public void run() {

		while (ok) {		
			byte[] command = new byte[4];
			try {				
				int okInt = is.read(command);
				if(okInt == -1){
					ok = false;
				}
				int intCommand = Conversions.ByteArrayToInt(command);
				switch (intCommand) {
				case Constants.SENDMESSAGE:
					readMessage();
					break;
				case Constants.GAMELISTING:
					gameListing();
					break;
				case Constants.SENDCONNECTED:
					connectedListing();
					break;
				case Constants.SENDGAMENAME:
					setGameName();
					break;
				case Constants.SENDMOVEMENT:
					setMovements();
					break;
				case Constants.INITGAME:
					initGame();
					break;
				case Constants.DROPPED:
					setDroppedPlayer();
					break;
				default:
					System.exit(1);
					break;
				}

			} catch (IOException e) {
				//e.printStackTrace();
				ok = false;
			}
		}
		return;
	}

	private void setDroppedPlayer() {
		byte[] buff = new byte[4];
		try {
			is.read(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int index = Conversions.ByteArrayToInt(buff);
		cm.setDroppedPlayer(index);
	}


	private void initGame() {
		long seed = -1;
		int myIndex = -1;
		int players = -1;
		byte[] seedB = new byte[8];
		byte[] myIndexB = new byte[4];
		byte[] playersB = new byte[4];
		int ok = 1;
		try{
			ok = is.read(seedB);
			if(ok < 0){
				System.out.println("Error in reading");
			}
			seed = Conversions.byteArrayToLong(seedB);
			ok = is.read(myIndexB);
			if(ok < 0){
				System.out.println("Error in reading");
			}
			myIndex = Conversions.ByteArrayToInt(myIndexB);
			ok = is.read(playersB);
			if(ok < 0){
				System.out.println("Error in reading");
			}
			players = Conversions.ByteArrayToInt(playersB);
		}catch(IOException ioe){
			ioe.printStackTrace();	
		}
		cm.setSeed(seed);
		cm.setMyIndex(myIndex);
		cm.setNbrOfPlayers(players);
	}


	private void setMovements() {
		byte[] longBuff = new byte[8];

		try {
			is.read(longBuff);
			long frame = Conversions.byteArrayToLong(longBuff);

			byte[] movements = null;
			movements = new byte[connectedPlayers.size()];
			is.read(movements);
			byte[] totaltInfo = new byte[longBuff.length + movements.length];
			for(int i = 0; i < longBuff.length; i++){
				totaltInfo[i] = longBuff[i];
			}
			for(int i = 0; i < movements.length; i++){
				totaltInfo[i+longBuff.length] = movements[i];
			}
			cm.addMovementToBuffer(totaltInfo);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void setGameName() {
		byte[] lengthArray = new byte[4];
		try {
			is.read(lengthArray);
			int length = Conversions.ByteArrayToInt(lengthArray);
			byte[] gameNameArray = new byte[length];
			is.read(gameNameArray);
			String gameName = new String(gameNameArray);
			cm.setGameName(gameName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void connectedListing() throws IOException {
		connectedPlayers = new ArrayList<String>();
		byte[] com = new byte[4];
		is.read(com);
		int nbrPlayers = Conversions.ByteArrayToInt(com);
		for(int i = 0; i < nbrPlayers; i++){
			com = new byte[4];
			is.read(com);
			int sl = Conversions.ByteArrayToInt(com);
			com = new byte[sl];
			is.read(com);
			String playName = new String(com);
			connectedPlayers.add(playName);
		}	
		cm.setconnectedPlayers(connectedPlayers);
	}

	/**
	 * check to see if the input could be read, otherwise close socket
	 * 
	 * @param check
	 * @return
	 */
	private boolean checkInput(int check) {
		if (check == -1) {
			try {
				if (sock.isClosed())
					return true;
				sock.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * returns the available chatmessages.
	 * 
	 * @return
	 */


	public ArrayList<String> getNames() {
		return connectedPlayers;
	}

	private void gameListing() throws IOException{
		byte[] command = new byte[4];
		int check2 = is.read(command);
		if (checkInput(check2))
			return;
		int nbrGames = Conversions.ByteArrayToInt(command);
		for (int i = 0; i < nbrGames; i++) {
			check2 = is.read(command);
			if (checkInput(check2))
				return;
			int nameLength = Conversions.ByteArrayToInt(command);
			byte[] nameArray = new byte[nameLength];
			check2 = is.read(nameArray);
			if (checkInput(check2))
				return;
			String name = new String(nameArray);
			check2 = is.read(command);
			if (checkInput(check2))
				return;
			int hostLength = Conversions.ByteArrayToInt(command);
			byte[] hostArray = new byte[hostLength];
			check2 = is.read(hostArray);
			if (checkInput(check2))
				return;
			String host = new String(hostArray);
			check2 = is.read(command);
			if (checkInput(check2))
				return;
			int port = Conversions.ByteArrayToInt(command);
			cm.addGame(new GameAddress(name, host, port));
		}
	}

	private void readMessage() throws IOException{
		int intCommand = 0;
		byte[] command = new byte[4];
		int check = is.read(command);
		if (checkInput(check))
			return;
		intCommand = Conversions.ByteArrayToInt(command);
		byte[] input = new byte[intCommand];
		check = is.read(input);
		if (checkInput(check))
			return;
		String text = new String(input);
		cm.addRecievedMessage(text);
	}

}

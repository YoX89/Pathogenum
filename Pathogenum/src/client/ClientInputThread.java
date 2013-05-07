package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

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
		
	//	byte[] pl = new byte[4];
	//	try {
	//		is.read(pl);
	//	} catch (IOException e1) {
	//		e1.printStackTrace();
	//	}
	//	int players = Conversions.ByteArrayToInt(pl);
	//	cm.setNbrOfPlayers(players);
		while (ok) {
			System.out.println("ipthread::startofloop");
			byte[] command = new byte[4];
			try {
				System.out.println("ipthread::trying...");
				int okInt = is.read(command);
				System.out.println("ipthread::don isread, command: "+Conversions.ByteArrayToInt(command));
				if(okInt == -1){
					ok = false;
				}
				int intCommand = Conversions.ByteArrayToInt(command);
				switch (intCommand) {
				case Constants.SENDMESSAGE:
					System.out.println("IPthread::doin readmessage");
					readMessage();
					break;
				case Constants.GAMELISTING:
					System.out.println("IPthread::doin gamelisting");
					gameListing();
					break;
				case Constants.SENDCONNECTED:
					System.out.println("IPthread::doin connectedlisting");
					connectedListing();
				break;
				case Constants.SENDGAMENAME:
					setGameName();
					break;
				default:
					break;
				}

			} catch (IOException e) {
				ok = false;
			}
		}
		System.out.println("IThread stopped");
		return;
	}

	private void setGameName() {
		byte[] lengthArray = new byte[4];
		try {
			System.out.println("ClientInputThread::readGameName");
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


	

	public byte[] recieveMovements(byte[] buff) {
		try {
			is.read(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buff;
		
	}

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
		System.out.println("recieved: "+text);
		cm.addRecievedMessage(text);
	}

}

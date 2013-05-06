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
	InputStream is;
	LinkedList<String> chatBuffer = new LinkedList<String>();
	ArrayList<GameAddress> gameList;
	ArrayList<String> connectedPlayers;
	boolean ok = true;
	int players;

	public ClientInputThread(Socket sock) {
		this.sock = sock;
		gameList = new ArrayList<GameAddress>();
		try {
			is = sock.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connectedPlayers = new ArrayList<String>();
	}

	public int getPlayers(){
		return players;
	}
	
	public void run() {
		
		byte[] pl = new byte[4];
		try {
			is.read(pl);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		players = Conversions.ByteArrayToInt(pl);
		
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
					sendMessage();
					break;
				case Constants.GAMELISTING:
					gameListing();
					break;
				case Constants.SENDCONNECTED:
					connectedListing();
				break;
				}

			} catch (IOException e) {
				ok = false;
			}
		}
		System.out.println("IThread stopped");
		return;
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
	public ArrayList<String> getChatMessages() {

		ArrayList<String> list = new ArrayList<String>();
		int size = chatBuffer.size();
		for (int i = 0; i < size; i++) {
			list.add(chatBuffer.pop());
		}
		return list;
	}

	public ArrayList<GameAddress> getGames() {
		return gameList;
	}

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
		gameList = new ArrayList<GameAddress>();
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
			gameList.add(new GameAddress(name, host, port));
		}
	}
	
	private void sendMessage() throws IOException{
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
		chatBuffer.offer(text);
	}

}

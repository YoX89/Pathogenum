package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;
import utils.GameAddress;
import LobbyServer.LobbyServer;

/**
 * Class for handling the connection between clients and server.
 * 
 * @author BigFarmor
 * 
 */
public class ClientConnectionHandler {
	private static ClientConnectionHandler myCCH = null;
	private Socket socket;
	private ClientInputThread iThread;
	private ClientOutputThread oThread;
	private int nbrOfPlayers;
	private InetAddress gameHost;
	private int gamePort;
	private ClientMonitor cm;
	LobbyServer ls;

	public static ClientConnectionHandler getCCH(InetAddress hubHost, int hubPort){
		if(myCCH==null){
			myCCH = new ClientConnectionHandler(hubHost, hubPort);
		}
		return myCCH;
	}
	private ClientConnectionHandler(InetAddress hubHost, int hubPort) {
		try {
			socket = new Socket(hubHost, hubPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cm = new ClientMonitor();
		iThread = new ClientInputThread(socket,cm);
		iThread.start();
		oThread =  new ClientOutputThread(socket,cm);
		oThread.start();
	}

	/**
	 * Sends a message via the protocol SENDMESSAGE,N(LENGTH OF MESSAGE),MESSAGE
	 * 
	 * @param message
	 */


	public void sendMessage(String message) {
		cm.addMessage(message);
	}


	/**
	 * Retrieves the messages from the client.InputThread
	 * 
	 * @return
	 */
	public ArrayList<String> getMessage() {
		ArrayList<String> list = cm.getChatMessages();
		return list;
	}

	/**Sends movement from client to server using UDP
	 * @param acc
	 */
	public void sendMovement(int[] acc) {
		byte[] command = {getCommandFromMovementKey(acc)};
		cm.addMovement(command);
	}

	/**Translates key pressings to 1-byte commands
	 * @param acc
	 * @return
	 */
	private byte getCommandFromMovementKey(int[] acc) {
		if(acc[0]==1){
			if(acc[2]==1){
				return Constants.NORTHWEST;
			}
			else if(acc[3]==1){
				return Constants.NORTHEAST;
			}else{
				return Constants.NORTH;
			}
		}
		if(acc[1]==1){
			if(acc[2]==1){
				return Constants.SOUTHWEST;
			}
			else if(acc[3]==1){
				return Constants.SOUTHEAST;
			}else{
				return Constants.SOUTH;
			}
		}
		if(acc[2]==1){
			return Constants.WEST;
		}
		if(acc[3]==1){
			return Constants.EAST;
		}
		return -1;
	}

	public byte[] receiveMovements() {
		nbrOfPlayers = cm.getNbrOfPlayers();
		byte[] buff = new byte[1*nbrOfPlayers + 8];
		buff = cm.recieveMovements(buff);
		return buff;
	}
	/**
	 * creates a new lobby and binds its own socket an output/input to this socket (which in turn is bound to the lobby)
	 * @param gameName
	 * @param port
	 */
	public void createNewLobby(String gameName, int port) {
		if(port != -1){
			try {
				ls = new LobbyServer(gameName, port);
				ls.start();
				oThread.startNewGame(gameName, port);
				socket.close();
				socket = new Socket(InetAddress.getLocalHost(),port);
				iThread = new ClientInputThread(socket,cm);
				iThread.start();
				oThread = new ClientOutputThread(socket,cm);
				oThread.start();
				System.out.println("New lobby created @: " + gameName + ":" + port);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public ArrayList<GameAddress> getGames() {
		ArrayList<GameAddress> list = cm.getGames();
		return list;
	}

	public void refreshGames() {
		oThread.refresh();

	}
	public String getGameName() {
		String re = ls.getGameName();
		if(re != null){
			return re;
		}else{
			return "";
		}
	}
	public ArrayList<String> getNames() {	
		return cm.getNames();
	}
	public void connectToGame(InetAddress host, int port) {
		gameHost  = host;
		gamePort = port;
		
	}
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}
}

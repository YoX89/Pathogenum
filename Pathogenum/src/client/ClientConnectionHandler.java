package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import LobbyServer.LobbyServer;

import utils.Conversions;
import utils.GameAddress;

/**
 * Class for handling the connection between clients and server.
 * 
 * @author BigFarmor
 * 
 */
public class ClientConnectionHandler {
	private Socket hubSocket;
	private DatagramSocket udpSocket;
	private OutputStream os;
	private InputThread iThread;
	private InetAddress gameHost;
	private int gamePort;
	public static final int SENDMESSAGE = 100, STARTGAME = 101,
			LEAVEGAME = 102, JOINGAME = 103, SETREADY = 104, GAMELISTING = 105;
	public static final byte SOUTH = 1, NORTH = 2, EAST = 3, WEST = 4, SOUTHEAST = 13, SOUTHWEST = 14, NORTHEAST = 23, NORTHWEST = 24;
	public ClientConnectionHandler(InetAddress hubHost, int hubPort) {

		try {
			hubSocket = new Socket(hubHost, hubPort);
			udpSocket = new DatagramSocket();
			os = hubSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		iThread = new InputThread(hubSocket,udpSocket);
		iThread.start();
	}

	/**
	 * Sends a message via the protocol SENDMESSAGE,N(LENGTH OF MESSAGE),MESSAGE
	 * 
	 * @param message
	 */
	
	public void connectToGame(InetAddress host, int port){
		gameHost = host;
		gamePort = port;
	}
	
	public void sendMessage(String message) {
		byte[] command = Conversions.intToByteArray(SENDMESSAGE);
		byte[] length = Conversions.intToByteArray(message.length());
		byte[] text = message.getBytes();
		try {
			os.write(command);
			os.write(length);
			os.write(text);
		} catch (IOException e) {
			// e.printStackTrace();
			closeConnection();
		}
	}

	/**
	 * Closes the connection between the CCH and the server.
	 * 
	 * @return
	 */
	public boolean closeConnection() {
		if (hubSocket.isClosed())
			return true;
		try {
			hubSocket.getOutputStream().write(LEAVEGAME);
			hubSocket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves the messages from the client.InputThread
	 * 
	 * @return
	 */
	public ArrayList<String> getMessage() {
		ArrayList<String> list = iThread.getChatMessages();
		return list;
	}

	/**Sends movement from client to server using UDP
	 * @param acc
	 */
	public void sendMovement(int[] acc) {
		byte[] command = {getCommandFromMovementKey(acc)};
		if(command[0]!=-1){
			DatagramPacket movementPacket = new DatagramPacket(command,1,gameHost,gamePort);
			try {
				udpSocket.send(movementPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**Translates key pressings to 1-byte commands
	 * @param acc
	 * @return
	 */
	private byte getCommandFromMovementKey(int[] acc) {
		if(acc[0]==1){
			if(acc[2]==1){
				return NORTHWEST;
			}
			else if(acc[3]==1){
				return NORTHEAST;
			}else{
				return NORTH;
			}
		}
		if(acc[1]==1){
			if(acc[2]==1){
				return SOUTHWEST;
			}
			else if(acc[3]==1){
				return SOUTHEAST;
			}else{
				return SOUTH;
			}
		}
		if(acc[2]==1){
			return WEST;
		}
		if(acc[3]==1){
			return EAST;
		}
		return -1;
	}

	public byte[] receiveMovements() {
		//IMPLEMENT
		return null;
	}

	public void createNewGame(String gameName, int port) {
		if(port != -1){
		try {
			LobbyServer ls = new LobbyServer(gameName, port);
			ls.start();
			os.write(STARTGAME);
			os.write(Conversions.intToByteArray(port));
			os.write(Conversions.intToByteArray(gameName.length()));
			os.write(gameName.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		}
	}

	public ArrayList<GameAddress> getGames() {
		ArrayList<GameAddress> list = iThread.getGames();
		return list;
	}
}

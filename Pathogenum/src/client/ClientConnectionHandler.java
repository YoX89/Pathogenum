package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

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
	private OutputStream os;
	private InputThread iThread;
	private InetAddress gameHost;
	private int gamePort;
	private int players;
	public static final int SENDMESSAGE = 100, STARTGAME = 101,
			LEAVEGAME = 102, JOINGAME = 103, SETREADY = 104, GAMELISTING = 105;
	public static final byte SOUTH = 1, NORTH = 2, EAST = 3, WEST = 4, SOUTHEAST = 13, SOUTHWEST = 14, NORTHEAST = 23, NORTHWEST = 24;
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
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		iThread = new InputThread(socket);
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
		System.out.println("message is: "+message);
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
		if (socket.isClosed())
			return true;
		try {
			socket.getOutputStream().write(LEAVEGAME);
			socket.close();
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
//		System.out.println("Sending movement " + acc[0] + " " + acc[1]+ " " + acc[2]+ " " + acc[3]);
		byte[] command = {getCommandFromMovementKey(acc)};
//		System.out.println("Which is command : " + command[0]);
		if(command[0]!=-1){
			DatagramPacket movementPacket = new DatagramPacket(command,1,gameHost,gamePort);
			try {
				os.write(command);
				
				//TODO WHAT IS THIS???
//				udpSocket.send(movementPacket);
//				System.out.println("Sending packet");
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
		players = iThread.getPlayers();
		byte[] buff = new byte[1*players + 8];
		buff = iThread.recieveMovements(buff);
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
				os.write(STARTGAME);
				os.write(Conversions.intToByteArray(port));
				os.write(Conversions.intToByteArray(gameName.length()));
				os.write(gameName.getBytes());
				socket.close();
				socket = new Socket(InetAddress.getLocalHost(),port);
				os = socket.getOutputStream();
				iThread = new InputThread(socket);
				iThread.start();
				System.out.println("New lobby created @: " + gameName + ":" + port);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public ArrayList<GameAddress> getGames() {
		ArrayList<GameAddress> list = iThread.getGames();
		return list;
	}

	public void refreshGames() {
		byte[] send = Conversions.intToByteArray(GAMELISTING);
		try {
			os.write(send);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public String getGameName() {
		String re = ls.getGameName();
		if(re != null){
			return re;
		}else{
			return "";
		}
	}
}

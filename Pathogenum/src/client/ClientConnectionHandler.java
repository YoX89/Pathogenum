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
	private OutputStream os;
	private ClientInputThread iThread;
	private int players;
	private InetAddress gameHost;
	private int gamePort;
	
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

		iThread = new ClientInputThread(socket);
		iThread.start();
	}

	/**
	 * Sends a message via the protocol SENDMESSAGE,N(LENGTH OF MESSAGE),MESSAGE
	 * 
	 * @param message
	 */


	public void sendMessage(String message) {
		System.out.println("message is: "+message);
		byte[] command = Conversions.intToByteArray(Constants.SENDMESSAGE);
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
			socket.getOutputStream().write(Constants.LEAVEGAME);
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
			try {
				os.write(command);
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
				os.write(Constants.STARTGAME);
				os.write(Conversions.intToByteArray(port));
				os.write(Conversions.intToByteArray(gameName.length()));
				os.write(gameName.getBytes());
				socket.close();
				socket = new Socket(InetAddress.getLocalHost(),port);
				os = socket.getOutputStream();
				iThread = new ClientInputThread(socket);
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
		byte[] send = Conversions.intToByteArray(Constants.GAMELISTING);
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
	public ArrayList<String> getNames() {	
		return iThread.getNames();
	}
	public void connectToGame(InetAddress host, int port) {
		gameHost  = host;
		gamePort = port;
		
	}
}

package HubServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import utils.Constants;
import utils.Conversions;
import utils.GameAddress;

/**
 * A server that sends and receives data related to the hub
 * 
 * @author Mardrey
 * 
 */
public class HubComInputServer extends Thread {

	Socket connection;
	GamesMonitor gm;
	InputStream is;
	OutputStream os;
	int ok;

	public HubComInputServer(Socket connection, GamesMonitor gm) {
		this.connection = connection;
		this.gm = gm;
		try {
			is = connection.getInputStream();
			os = connection.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		byte[] command = new byte[4];
		System.out.println("HubcomInputServer started");
		ok = 0;
		while (ok != -1) {
			try {
				ok = is.read(command);
				int com = Conversions.ByteArrayToInt(command);
				System.out.println("Command: " + command[0]);
				switch (com) {
				case Constants.ADDGAME:
					addCommand(connection, is);
					break;
				case Constants.GAMELISTING:
					sendGames(os);
					break;
				case Constants.SENDMESSAGE:
					fetchMessage();
					break;
				case Constants.SENDGAMENAME:
					break;
				case Constants.LEAVEGAME:
					deRegisterGame(connection, is);
					break;
				case Constants.STARTGAME:
					System.out.println("Started game sent");
				break;
				default:
					connection.close();
					ok = -1;
					break;
				}
			} catch (IOException ie) {
				ok = -1;
			}
		}
		System.out.println("HubComInputServer stopped");
		gm.notifyWaiters();
		return;
	}

	private void deRegisterGame(Socket conn2, InputStream is2) {
		byte[] gameNameL = new byte[4];
		byte[] gameName;
		int ok = 1;
		try {
			ok = is2.read(gameNameL);
			gameName = new byte[Conversions.ByteArrayToInt(gameNameL)];
			ok = is2.read(gameName);
			String gName = new String(gameName);
			byte[] port = new byte[4];
			ok = is2.read(port);
			int prt = Conversions.ByteArrayToInt(port);
			GameAddress removeGa = new GameAddress(gName, connection.getInetAddress().getHostAddress(), prt);
			gm.removeGame(removeGa);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void sendGames(OutputStream os2) {
		LinkedList<GameAddress> games = gm.getGameAddresses();
		System.out.println("GameSizeInHubIThread: " + games.size());
		if (games != null) {
			try {
				os2.write(Conversions.intToByteArray(Constants.GAMELISTING));
				os2.write(Conversions.intToByteArray(games.size()));
				for (GameAddress address : games) {
					os2.write(Conversions.intToByteArray(address.getGameName()
							.length()));
					os2.write(address.getGameName().getBytes());
					os2.write(Conversions.intToByteArray(address.getHost()
							.length()));
					os2.write(address.getHost().getBytes());
					os2.write(Conversions.intToByteArray(address.getPort()));
				}
			} catch (IOException e) {
				//e.printStackTrace();
				ok = -1;
			}
		}
	}

	/**
	 * Reads a message and puts it in the chat monitor
	 * 
	 * @throws IOException
	 */
	private void fetchMessage() throws IOException {
		byte[] buff = new byte[4];
		ok = is.read(buff);
		int mLength = Conversions.ByteArrayToInt(buff);
		buff = new byte[mLength];
		ok = is.read(buff);
		String message = new String(buff);
		gm.putMessage(connection.getInetAddress().getHostAddress(), message);
	}

	/**
	 * Reads a game address and removes the corresponding game from currently
	 * available games
	 * 
	 * @param connection2
	 * @param is2
	 */

	/**
	 * Reads a game address and adds the corresponding game to currently
	 * available games
	 * 
	 * @param connection2
	 * @param is2
	 */
	private void addCommand(Socket connection2, InputStream is2) {
		InetAddress ia = connection2.getInetAddress();
		String ip = ia.getHostAddress();
		byte[] prt = new byte[4];
		try {
			is2.read(prt);
		} catch (IOException e1) {
			e1.printStackTrace();
			ok = -1;
		}
		int port = Conversions.ByteArrayToInt(prt);
		// int port = connection2.getPort();

		byte[] nameLength = new byte[4];
		try {
			is2.read(nameLength);
		} catch (IOException e) {
			e.printStackTrace();
			ok = -1;
		}
		int nameL = Conversions.ByteArrayToInt(nameLength);
		byte[] name = new byte[nameL];
		try {
			is2.read(name);
		} catch (IOException e) {
			e.printStackTrace();
			ok = -1;
		}
		String gameName = new String(name);
		GameAddress ga = new GameAddress(gameName, ip, port);
		gm.addGame(ga);
		System.out.println("Games after addGame: " + gm.games.size());
		// skicka conf h�r med maybe?
	}

}

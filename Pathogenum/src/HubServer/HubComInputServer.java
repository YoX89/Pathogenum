package HubServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;

import publicMonitors.ChatMonitor;

import utils.Conversions;
import utils.GameAddress;

/**
 * A server that sends and receives data related to the hub
 * 
 * @author Mardrey
 * 
 */
public class HubComInputServer extends Thread {

	public final static byte ADD = 1, REM = 2, LIST = 3;
	public static final int SENDMESSAGE = 100;
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
				case HubServer.STARTGAME:
					addCommand(connection, is);
					break;
				case HubServer.GAMELISTING:
					sendGames(os);
					break;
				case REM:
					removeCommand(connection, is);
					break;
				case LIST:
					listCommand(os);
					break;
				case SENDMESSAGE:
					fetchMessage();
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

	private void sendGames(OutputStream os2) {
		LinkedList<GameAddress> games = gm.getGameAddresses();
		if (games != null) {
			try {
				os2.write(Conversions.intToByteArray(HubServer.GAMELISTING));
				os2.write(Conversions.intToByteArray(games.size()));
				int i = 0;
				for (GameAddress address : games) {
					i++;
					os2.write(Conversions.intToByteArray(address.getGameName()
							.length()));
					os2.write(address.getGameName().getBytes());
					os2.write(Conversions.intToByteArray(address.getHost()
							.length()));
					os2.write(address.getHost().getBytes());
					os2.write(Conversions.intToByteArray(address.getPort()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lists the name, host and port of all games currently available
	 * 
	 * @param os2
	 */
	private void listCommand(OutputStream os2) {
		LinkedList<GameAddress> addresses = gm.getGameAddresses();
		int gameNbr = addresses.size();
		try {
			os2.write(Conversions.intToByteArray(gameNbr));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (GameAddress ga : addresses) {
			String host = ga.getHost();
			String gameName = ga.getGameName();
			int port = ga.getPort();
			int hostLength = (host.getBytes()).length;
			int gameNameLength = (gameName.getBytes()).length;
			try {
				os2.write(Conversions.intToByteArray(hostLength));
				os2.write(host.getBytes());
				os2.write(Conversions.intToByteArray(gameNameLength));
				os2.write(gameName.getBytes());
				os2.write(Conversions.intToByteArray(port));
			} catch (IOException e) {
				e.printStackTrace();
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
	private void removeCommand(Socket connection2, InputStream is2) {
		InetAddress ia = connection2.getInetAddress();
		String ip = ia.getHostAddress();
		int port = connection2.getPort();
		byte[] nameLength = new byte[4];
		try {
			is2.read(nameLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int nameL = Conversions.ByteArrayToInt(nameLength);
		byte[] name = new byte[nameL];
		try {
			is2.read(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String gameName = new String(name);
		GameAddress ga = new GameAddress(gameName, ip, port);
		gm.removeGame(ga);
		// skicka conf kanske?
	}

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
		}
		int port = Conversions.ByteArrayToInt(prt);
		// int port = connection2.getPort();

		byte[] nameLength = new byte[4];
		try {
			is2.read(nameLength);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int nameL = Conversions.ByteArrayToInt(nameLength);
		byte[] name = new byte[nameL];
		try {
			is2.read(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String gameName = new String(name);
		GameAddress ga = new GameAddress(gameName, ip, port);
		gm.addGame(ga);
		// skicka conf h�r med maybe?
	}

}

package hubserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import publicmonitors.ChatMonitor;
import utils.GameAddress;

/**
 * A server representing the hub that starts a corresponding input and output
 * server
 * 
 * @author Mardrey
 * 
 */
public class HubServer extends Thread {
	ServerSocket Ssock;
	GamesMonitor gm;

	public HubServer(int port) {
		gm = new GamesMonitor();
		try {
			Ssock = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// for testing purposes
	public GamesMonitor getGM() {
		return gm;
	}

	public void run() {
		System.out.println("HubServer started on: "
				+ Ssock.getInetAddress().getHostAddress()
				+ Ssock.getLocalPort());
		while (true) {
			try {
				Socket conn = Ssock.accept();
				HubOutputThread hcos = new HubOutputThread(conn, gm);
				HubInputThread HCS = new HubInputThread(conn, gm);
				hcos.start();
				HCS.start();
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("Shutting down Hub");
				return;
			}
		}

	}
}

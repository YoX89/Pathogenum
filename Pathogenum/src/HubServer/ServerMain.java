package HubServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import LobbyServer.GameServer;


/**A class containing a main method that starts a hub server
 * @author Mardrey
 *
 */
public class ServerMain {
	public static void main(String[] args) throws IOException{
		//LobbyServer ls = new LobbyServer(Integer.parseInt(args[0]));
		if(args.length != 1){
			System.out.println("Invalid number of parameters.\nStart program like \"java -jar ServerMain.jar [Port]\"");
			return;
		}
		HubServer server = new HubServer(Integer.parseInt(args[0]));
		server.start();
	}
}

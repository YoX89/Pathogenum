package HubServer;


/**A class containing a main method that starts a hub server
 * @author Mardrey
 *
 */
public class ServerMain {
	public static void main(String[] args){
		//LobbyServer ls = new LobbyServer(Integer.parseInt(args[0]));
		HubServer server = new HubServer(Integer.parseInt(args[0]));
		server.start();
	}
}

package GamesLobby;


public class ServerMain {
	public static void main(String[] args){
		HubServer server = new HubServer(Integer.parseInt(args[0]));
		server.start();
	}
}

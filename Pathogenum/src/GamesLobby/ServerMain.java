package GamesLobby;


public class ServerMain {
	public static void main(String[] args){
		LobbyServer ls = new LobbyServer(Integer.parseInt(args[0]));
	//	HubServer server = new HubServer();
		ls.start();
	}
}

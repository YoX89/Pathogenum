package GamesLobby;

public class GameAddress {
	private String gameName;
	private String host;
	private int port;
	public GameAddress(String gameName, String host, int port){
		this.gameName = gameName;
		this.host = host;
		this.port = port;
	}
	
	public int getPort(){
		return port;
	}
	
	public String getHost(){
		return host;
	}
	
	public String getGameName(){
		return gameName;
	}
}

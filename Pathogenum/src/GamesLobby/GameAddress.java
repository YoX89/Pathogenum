package GamesLobby;

public class GameAddress implements Comparable{
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

	@Override
	public int compareTo(Object arg0) {
		GameAddress ga = (GameAddress)arg0;
		if(gameName.compareTo(ga.gameName) < 0){
			return -1;
		}else if(gameName.compareTo(ga.gameName) > 0){
			return 1;
		}else{
			if(host.compareTo(ga.host) < 0){
				return -1;
			}else if(host.compareTo(ga.host) > 0){
				return 1;
			}else{
				if(port > ga.port){
					return 1;
				}else if(port < ga.port){
					return -1;
				}else{
					return 0;
				}
			}
		}
	}
	
	public boolean equals(Object arg0){
		return (compareTo(arg0) == 0);
	}
	
	public String toString(){
		return host + ":" + port + "/" + gameName;
	}
}

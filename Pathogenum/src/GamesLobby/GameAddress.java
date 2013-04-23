package GamesLobby;

/**
 * A class that maps a game name, a host address and a port number to each other 
 * @author Mardrey, BigFarmor
 *
 */
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

	/**
	 * Compares GameAddresses, returns 0 if equal, -1 if name host or port is smaller than specified object, 1 otherwise
	 *
	 */
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
	
	/**
	 * Compares GameAddresses, returns true if equal, false otherwise
	 *
	 */
	public boolean equals(Object arg0){
		return (compareTo(arg0) == 0);
	}
	
	public String toString(){
		return host + ":" + port + "/" + gameName;
	}
}

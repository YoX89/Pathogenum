package client;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Client extends StateBasedGame {
	
	static AppGameContainer agc;
	String host;
	int port;
	BasicGameState bgs;
	
	public Client(String title, String host, String port) {
		super(title);
		this.host = host;
		this.port = Integer.parseInt(port);
		
	}

	public String getHost(){
		return host;
	}
	public int getPort(){
		return port;
	}
	
	// args0 = hostname, args1 = port
	public static void main(String args[]) {

		try {

			Client client = new Client("client", args[0], args[1]);
			agc = null;
			try {
				agc = new AppGameContainer(client);
			} catch (SlickException e1) {
				e1.printStackTrace();
			}
			try {
				agc.setDisplayMode(1024, 768, false);
			} catch (SlickException e) {
			}
			agc.setTargetFrameRate(60);
			try {
				agc.start();
			} catch (SlickException e) {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	@Override
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		bgs.render(arg0, this, arg1);

	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		bgs.update(arg0,this,arg1);
	}

	

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		BasicGameState Hub = new ClientHubState();
		BasicGameState Lobby = new ClientLobbyState();
		BasicGameState Game = new ClientGameState();
		
		addState(Hub);
		addState(Lobby);
		addState(Game);
		bgs = Hub;
		
	}
	
	@Override
	public boolean closeRequested() {
		((ClientHubState) bgs).closeConnection();
		agc.exit();
		return false;
	}
	
}

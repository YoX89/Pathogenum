package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Class that starts the client program and initializes the states.
 * @author Mardrey, BigFarmor
 */
public class Client extends StateBasedGame {

	static AppGameContainer agc;
	String host;
	int port;
	BasicGameState Hub;
	BasicGameState Lobby;
	BasicGameState game;
	BasicGameState bgs;
	ClientConnectionHandler cch;
	private boolean init = false;

	public Client(String title, String host, String port) {
		super(title);
		this.host = host;
		this.port = Integer.parseInt(port);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	// args0 = hostname, args1 = port
	/**
	 * Main method starting client
	 * @param args
	 */
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
			agc.setAlwaysRender(true);
			try {
				agc.start();
			} catch (SlickException e) {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		if (init)
			bgs.render(arg0, this, arg1);

	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		if (init)
			bgs.update(arg0, this, arg1);
	}

	/**
	 * Initiates state list by adding the states that have been constructed in
	 * the constructor to the built in list, this runst the states init(). (also
	 * set bgs to be the first state)
	 */
	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		
		Hub = null;
		try {
			
			if(host.equals("localhost")){
				host = InetAddress.getLocalHost().getHostAddress();
			}
			InetAddress ia = InetAddress.getByName(host);
			cch = ClientConnectionHandler.getCCH(ia, port);
			Hub = new ClientHubState(ia,port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		Lobby = new ClientLobbyState();
		addState(Hub);
		addState(Lobby);
		bgs = Hub;
		init = true;
	}

	/**
	 * When the window is about to close, this method is run, it tells the
	 * connection handler that it should close the connection with the server.
	 * The server is not able to detect this on its own yet, should be fixed
	 * hopefully.
	 */
	@Override
	public boolean closeRequested() {
		agc.exit();
		System.out.println("CLOSIÖNG");
		return false;
	}

	/**
	 * To be able to switch states, I had to override this method, if you find
	 * a better way. Let me know.
	 */
	@Override
	public void enterState(int id) {
		try {
			if(id == 2) {
				game = new ClientGameState(cch);
				addState(game);
			}
			GameState gs = getState(id);
			gs.init(agc, this);
			bgs = (BasicGameState) gs;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}

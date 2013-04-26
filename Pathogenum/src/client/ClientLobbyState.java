package client;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
/**
 * A gamestate representing the lobby state
 * @author Mardrey
 *
 */
public class ClientLobbyState extends BasicGameState{

	
	Image sendButton;
	TextField inputText;
	TextField outputText;
	TextField nameText;
	String[] chatMessages;
	ClientConnectionHandler cch = ClientConnectionHandler.getCCH(ClientHubState.getHost(), ClientHubState.getPort());
	public static final int ID = 1;
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		chatMessages = new String[5];
		sendButton = new Image("resources/gfx/SendButton.png");
		nameText = new TextField(arg0, arg0.getDefaultFont(), 400, 100, sendButton.getWidth(), 30);
		inputText = new TextField(arg0, arg0.getDefaultFont(), 100, 250, sendButton.getWidth(), 30);
		inputText.setBackgroundColor(new Color(0, 0, 0));
		if(inputText.isAcceptingInput()){
			inputText.setText("true");
		}
		else{
			inputText.setText("false");
		}
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		arg2.drawImage(sendButton, 100, 300);
		inputText.render(arg0, arg2);
		nameText.render(arg0, arg2);
		nameText.setText(cch.getGameName());
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
	
		
	}
	/**
	 * returns the id of this gamestate (must not have a duplicate)
	 */
	@Override
	public int getID() {
		return ID;
	}

}

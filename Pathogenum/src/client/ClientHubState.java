package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class ClientHubState extends BasicGameState{

	public static final int ID = 0;
	
	Image sendButton;
	Image newgameButton;
	Image joingameButton;
	TextField inputText;
	TextField outputText;
	String[] chatMessages;
	ClientConnectionHandler cch;
	
	boolean pressedSend = false;
	boolean pressedJoin = false;
	boolean pressedNew = false;
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		try {
			Client c = (Client)(arg1);
			cch = new ClientConnectionHandler(InetAddress.getByName(c.getHost()),
					c.getPort());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		chatMessages = new String[5];
		sendButton = new Image("resources/gfx/SendButton.png");
		joingameButton = new Image("resources/gfx/JoingameButton.png");
		newgameButton = new Image("resources/gfx/NewgameButton.png");
		inputText = new TextField(arg0, arg0.getDefaultFont(), 400, 250, sendButton.getWidth(), 30);
		outputText = new TextField(arg0, arg0.getDefaultFont(), 400, 100, 500,
				100);
		outputText.setAcceptingInput(false);
		inputText.setBackgroundColor(new Color(0, 0, 0));
		outputText.setBackgroundColor(new Color(0, 0, 0));
		Music bgMusic = new Music("resources/audio/Invincible.ogg");
		bgMusic.loop();
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {

		arg2.drawImage(sendButton, 400, 300);
		arg2.drawImage(newgameButton, 200, 300);
		arg2.drawImage(joingameButton, 600, 300);
		inputText.render(arg0, arg2);
		outputText.render(arg0, arg2);
		
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {

		/*
		 * Sends chat message to server
		 */
		if(pressedSend && !Mouse.isButtonDown(0)){
			pressedSend = false;
		}
		if(pressedNew && !Mouse.isButtonDown(0)){
			pressedNew = false;
		}
		if(pressedJoin && !Mouse.isButtonDown(0)){
			pressedJoin = false;
		}
		
		MouseOverArea moa = new MouseOverArea(arg0, sendButton, 400, 300,
				sendButton.getWidth(), sendButton.getHeight());
		if (moa.isMouseOver() && Mouse.isButtonDown(0)
				&& !(inputText.getText().equals("")) && !pressedSend) {
			System.out.println("PRESSED! Message is: " + inputText.getText());
			pressedSend = true;
			cch.sendMessage(inputText.getText());
			inputText.setText("");
		}
		/*
		 * Creates new game in server
		 */
		moa = new MouseOverArea(arg0, newgameButton, 200, 300,
				newgameButton.getWidth(), newgameButton.getHeight());
		
		if (moa.isMouseOver() && Mouse.isButtonDown(0)&& !pressedNew) { // Enters several	times...
			System.out.println("PRESSED! NEW GAME");
			pressedNew = true;
			arg1.enterState(1); //LobbyGameState
			// IMPLEMENT
		}
		/*
		 * Joins existing game
		 */
		moa = new MouseOverArea(arg0, joingameButton, 600, 300,
				joingameButton.getWidth(), joingameButton.getHeight());
		if (moa.isMouseOver() && Mouse.isButtonDown(0)&& !pressedJoin) { // Enters several
															// times...
			System.out.println("PRESSED! JOIN GAME");
			pressedJoin = true;
			// IMPLEMENT
		}
		ArrayList<String> chatList = cch.getMessage();
		popMessages(chatList);
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}
	
	private void popMessages(ArrayList<String> chatList) {
		
		int clSize = chatList.size();
		for (int t = 0; t < clSize; t++) {
			
			for(int i = chatMessages.length-1; i > 0; i--){
				chatMessages[i] = chatMessages[i-1];
			}
			chatMessages[0] = chatList.get(t);
		}
		String messages = "";
		
			for (int i = 0; i < chatMessages.length; i++) {
				if (chatMessages[i] != null) {
					messages += chatMessages[i];
					messages += "\n";
				}
			}
		System.out.println(messages);
		outputText.setText(messages);
	}

	public void closeConnection() {
		cch.closeConnection();
	}
}

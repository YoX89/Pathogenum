package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.InputImplementation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import utils.GameAddress;
/**
 * A gamestate representing the Hub menu, where the list of lobbys will be displayed amongst a global chat window.
 * @author Mardrey, BigFarmor
 *
 */
public class ClientHubState extends BasicGameState{

	public static final int ID = 0;
	
	Image sendButton;
	Image newgameButton;
	Image joingameButton;
	TextField inputText;
	TextField outputText;
	TextField gamesField;
	TextField newGameNameField;
	TextField newGamePortField;
	String[] chatMessages;
	ArrayList<GameAddress> gamesList;
	ClientConnectionHandler cch;
	
	boolean pressedSend = false;
	boolean pressedJoin = false;
	boolean pressedNew = false;
	/**
	 * creates a clientConnectionHandler for handling connections to the server.. durr
	 * @param ia
	 * @param port
	 */
	public ClientHubState(InetAddress ia, int port){
		try {
			cch = new ClientConnectionHandler(ia,port);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		chatMessages = new String[5];
		gamesList = new ArrayList<GameAddress>();
		sendButton = new Image("resources/gfx/SendButton.png");
		joingameButton = new Image("resources/gfx/JoingameButton.png");
		newgameButton = new Image("resources/gfx/NewgameButton.png");
		inputText = new TextField(arg0, arg0.getDefaultFont(), 400, 250, sendButton.getWidth(), 30);
		outputText = new TextField(arg0, arg0.getDefaultFont(), 400, 100, 500,
				100);
		gamesField = new TextField(arg0, arg0.getDefaultFont(), 200, 450, 500,
				100);
		newGameNameField =  new TextField(arg0, arg0.getDefaultFont(), 200, 200, newgameButton.getWidth(), 30);
		newGamePortField =  new TextField(arg0, arg0.getDefaultFont(), 200, 250, newgameButton.getWidth(), 30);
		newGameNameField.setText("Game Name");
		newGamePortField.setText("Game Port");
		outputText.setAcceptingInput(false);
		gamesField.setAcceptingInput(false);
		inputText.setBackgroundColor(new Color(0, 0, 0));
		outputText.setBackgroundColor(new Color(0, 0, 0));
		gamesField.setBackgroundColor(new Color(0, 0, 0));
		//Music bgMusic = new Music("resources/audio/Invincible.ogg"); //:(
		//bgMusic.loop();
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {

		arg2.drawImage(sendButton, 400, 300);
		arg2.drawImage(newgameButton, 200, 300);
		arg2.drawImage(joingameButton, 600, 300);
		inputText.render(arg0, arg2);
		outputText.render(arg0, arg2);
		gamesField.render(arg0,arg2);
		newGameNameField.render(arg0,arg2);
		newGamePortField.render(arg0,arg2);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		/*
		 * Sends chat message to server
		 */
		if(pressedSend && !Mouse.isButtonDown(0)){ //Prevents clicks being counted several times
			pressedSend = false;
		}
		if(pressedNew && !Mouse.isButtonDown(0)){ // --||--
			pressedNew = false;
		}
		if(pressedJoin && !Mouse.isButtonDown(0)){// --||--
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
		
		if (moa.isMouseOver() && Mouse.isButtonDown(0)&& !pressedNew) { 
			System.out.println("PRESSED! NEW GAME");
			pressedNew = true;
			int port = checkPortValidity();
			
			if(!newGameNameField.getText().equals("") && !newGamePortField.getText().equals("") && port!=-1){
				cch.createNewGame(newGameNameField.getText(),port);
				arg1.enterState(ClientLobbyState.ID);
				return;
			}
			//LobbyGameState
			// IMPLEMENT
		}
		/*
		 * Joins existing game
		 */
		moa = new MouseOverArea(arg0, joingameButton, 600, 300,
				joingameButton.getWidth(), joingameButton.getHeight());
		if (moa.isMouseOver() && Mouse.isButtonDown(0)&& !pressedJoin) { 
			
			pressedJoin = true;
			System.out.println("PRESSED! JOIN GAME");
			arg1.enterState(TemporaryGameState.ID);
			// IMPLEMENT
		}
		ArrayList<String> chatList = cch.getMessage();
		popMessages(chatList);
		gamesList = cch.getGames();
		printGames(gamesList);
		
	}
	private void printGames(ArrayList<GameAddress> list) {
		String text = "";
		for(GameAddress address : list){
			text+=address.getGameName();
			text+="  :  ";
			text+=address.getHost();
			text+="  :  ";
			text+=address.getPort();
			text+="\n";
		}
		gamesField.setText(text);
	}

	private int checkPortValidity() {
		int port = 0;
		try{
			port = Integer.parseInt(newGamePortField.getText());
			if(port<1024 || port>65535){
				return -1;
			}
		}catch(NumberFormatException e){
			return -1;
		}
		return port;
	}

	/**
	 * returns the id of this gamestate (must not have a duplicate)
	 */
	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}
	/**
	 * outputs the text returned by the inputthread to the chat window, pushes older messages down the list
	 * @param chatList
	 */
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
		//System.out.println(messages);
		outputText.setText(messages);
	}
	/**
	 * tells the CCH to close the connection 
	 */
	public void closeConnection() {
		cch.closeConnection();
	}
	

}

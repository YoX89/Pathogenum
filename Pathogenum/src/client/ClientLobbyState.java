package client;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A gamestate representing the lobby state
 * 
 * @author Mardrey
 * 
 */
public class ClientLobbyState extends BasicGameState {

	Image sendButton;
	TextField inputText;
	TextField outputText;
	TextField nameText;
	TextField[] connectedClients;
	String[] chatMessages;
	boolean pressedSend = false;
	ClientConnectionHandler cch = ClientConnectionHandler.getCCH(
			ClientHubState.getHost(), ClientHubState.getPort());
	public static final int ID = 1;

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {

		chatMessages = new String[10];
		sendButton = new Image("resources/gfx/SendButton.png");
		nameText = new TextField(arg0, arg0.getDefaultFont(), 400, 100, 150, 30);
		inputText = new TextField(arg0, arg0.getDefaultFont(), 100, 250,
				sendButton.getWidth(), 30);
		inputText.setBackgroundColor(new Color(0, 0, 0));

		outputText = new TextField(arg0, arg0.getDefaultFont(), 500, 250, 300,
				200);
		outputText.setBackgroundColor(new Color(0, 0, 0));
		connectedClients = new TextField[4];
		for (int i = 0; i < connectedClients.length; i++) {
			connectedClients[i] = new TextField(arg0, arg0.getDefaultFont(),
					100, 500 + i * 50, sendButton.getWidth() * 2, 30);
			connectedClients[i].setAcceptingInput(false);
		}
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		if (!inputText.hasFocus()) { // Vad ledande forskare kallar
										// "ett fulhack"
			inputText.setFocus(true);
		}

		arg2.drawImage(sendButton, 100, 300);
		inputText.render(arg0, arg2);
		outputText.render(arg0, arg2);
		nameText.render(arg0, arg2);
		// nameText.setText(cch.getGameName());
		ArrayList<String> names = cch.getNames();
		// System.out.println("NSIZE: " + names.size());
		if (names != null) {
			System.out.println("NAMES != NULL");
			for (int i = 0; i < names.size(); i++) {
				String text = names.get(i);
				if (text == null) {
					text = "";
				}
				connectedClients[i].setText(text);
			}
		}
		for (int i = 0; i < connectedClients.length; i++) {
			connectedClients[i].render(arg0, arg2);
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {

		if (pressedSend && !Mouse.isButtonDown(0)) {
			pressedSend = false;
		}
		/*
		 * Sends chat message
		 */
		MouseOverArea moa = new MouseOverArea(arg0, sendButton, 100, 300,
				sendButton.getWidth(), sendButton.getHeight());
		if (moa.isMouseOver() && Mouse.isButtonDown(0)
				&& !(inputText.getText().equals("")) && !pressedSend) {
			System.out.println("PRESSED! Message is: " + inputText.getText());
			pressedSend = true;
			cch.sendMessage(inputText.getText());
			inputText.setText("");
		}

		ArrayList<String> chatList = cch.getMessage();
		addNewLines(chatList);
		popMessages(chatList);
		// for(int i = 0; i < chatMessages.length; i++){
		// System.out.println("chatMessages::"+chatMessages[i]);
		// }
	}

	private void addNewLines(ArrayList<String> chatList) {
		for (int i = 0; i < chatList.size(); i++) {
			String s = chatList.get(i);
			if (s.length() > 32) {
				String firstString = s.substring(0, 31);
				String lastString = s.substring(31, s.length());
				String totString = firstString + "\n" + lastString;
				chatList.set(i, totString);
			}
		}

	}

	private void popMessages(ArrayList<String> chatList) {

		int clSize = chatList.size();
		for (int t = 0; t < clSize; t++) {

			for (int i = chatMessages.length - 1; i > 0; i--) {
				chatMessages[i] = chatMessages[i - 1];
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
		// System.out.println(messages);
		outputText.setText(messages);
	}

	/**
	 * returns the id of this gamestate (must not have a duplicate)
	 */
	@Override
	public int getID() {
		return ID;
	}

}

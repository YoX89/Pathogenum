package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;

import utils.Conversions;

public class Client extends BasicGame {
	Image sendButton;
	TextField txt;
	public static final int SENDMESSAGE = 100, STARTGAME = 101, LEAVEGAME = 102, JOINGAME = 103, SETREADY = 104;
	static Socket sock = null;
	static InputStream is = null;
	static OutputStream os = null;
	public Client(String title) {
		super(title);
	}
	
	public static void main(String args[]){
		
		try {
			sock = new Socket(InetAddress.getByName(args[0]) , Integer.parseInt(args[1]));
			is = sock.getInputStream();
			os = sock.getOutputStream();
			Client client = new Client("client");
			AppGameContainer agc = null;
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
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}

	@Override
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
	
		arg1.drawImage(sendButton, 400, 300);
		txt.render(arg0, arg1);
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		sendButton = new Image("resources/gfx/SendButton.png");

		txt  = new TextField(arg0, arg0.getDefaultFont() , 400, 250, 100, 15); //DONT WORK RIGHT!
		txt.setTextColor(new Color(0,0,0));
		txt.setText("This is input text");
		Music bgMusic = new Music("resources/audio/Invincible.ogg");
		bgMusic.loop();
		
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		
		MouseOverArea moa = new MouseOverArea(arg0, sendButton, 400, 300, 131, 65);
		if(moa.isMouseOver()&&Mouse.isButtonDown(0)){
			System.out.println("PRESSED! Message is: "+ txt.getText());
			byte[] command = Conversions.intToByteArray(SENDMESSAGE);
			byte[] length = Conversions.intToByteArray(txt.getText().length());
			byte[] message = txt.getText().getBytes();
			try {
				os.write(command);
				os.write(length);
				os.write(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}

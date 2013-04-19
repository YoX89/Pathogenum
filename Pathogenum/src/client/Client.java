package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class Client extends BasicGame {
	public Client(String title) {
		super(title);
	}
	
	public static void main(String args[]){
		try {
			Socket sock = new Socket(InetAddress.getByName(args[0]) , Integer.parseInt(args[1]));
			InputStream is = sock.getInputStream();
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
	
		
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		Music bgMusic = new Music("resources/audio/Invincible.ogg");
		bgMusic.loop();
		
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}
}

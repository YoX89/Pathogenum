import java.io.File;
import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class Pathogenum extends BasicGame{

	ArrayList<Image> images;
	
	public Pathogenum(String title) {
		super(title);
		images = new ArrayList<Image>();
	}

	@Override
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		for(Image img: images){
			img.draw();
		}
		
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		readImages("resources/");
		
	}

	private void readImages(String dir) throws SlickException {
		File res_dir = new File(dir);
		String[] files = res_dir.list();
		for(String s: files){
			Image im = new Image(dir+s);
			images.add(im);
		}
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 * @throws SlickException 
	 */
	public static void main(String[] args) throws SlickException {
		Pathogenum petriDish = new Pathogenum("Pathogenum");
		AppGameContainer agc = new AppGameContainer(petriDish);
		agc.setTargetFrameRate(60);
		agc.start();
	}

}

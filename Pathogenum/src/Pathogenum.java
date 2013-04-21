import java.io.File;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import utils.Dimensions;
import Entities.Entity;
import Entities.NPC;
import Entities.Player;
import Entities.Wall;
import Physics.PathogenumWorld;


public class Pathogenum extends BasicGame{

	ArrayList<Image> images;
	ArrayList<Shape> shapes;
	ArrayList<Entity> entities;
	Player play;
	PathogenumWorld world;
	int[] keys = new int[4];

	public Pathogenum(String title) {
		super(title);
		images = new ArrayList<Image>();
		shapes = new ArrayList<Shape>();
		entities = new ArrayList<Entity>();
		world = new PathogenumWorld(new Vec2(0f,0f));
		createWalls();
	}

	private void createWalls() {
		Rectangle topWall = new Rectangle(Dimensions.meterToPixel(0.1f), Dimensions.meterToPixel(0.1f),
				Dimensions.SCREEN_WIDTH - Dimensions.meterToPixel(0.2f),
				Dimensions.meterToPixel(0.1f));

		Rectangle leftWall = new Rectangle(Dimensions.meterToPixel(0.1f), Dimensions.meterToPixel(0.1f),
				Dimensions.meterToPixel(0.1f),
				Dimensions.SCREEN_HEIGHT - Dimensions.meterToPixel(0.2f));

		Rectangle rightWall = new Rectangle(Dimensions.SCREEN_WIDTH - Dimensions.meterToPixel(0.1f), Dimensions.meterToPixel(0.1f),
				Dimensions.meterToPixel(0.1f),
				Dimensions.SCREEN_HEIGHT - Dimensions.meterToPixel(0.2f));

		Rectangle bottomWall = new Rectangle(Dimensions.meterToPixel(0.1f), Dimensions.SCREEN_HEIGHT - Dimensions.meterToPixel(0.1f),
				Dimensions.SCREEN_WIDTH - Dimensions.meterToPixel(0.2f),
				Dimensions.meterToPixel(0.1f));

		entities.add(new Wall(topWall, world));	
		entities.add(new Wall(leftWall, world));
		entities.add(new Wall(rightWall, world));
		entities.add(new Wall(bottomWall, world));

	}

	@Override
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		//for(Shape s: shapes){
			//	arg1.draw(s);
		//}
		for(Entity e: entities){
			e.draw(arg1);
		}
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		readImages("resources/gfx/");

		Circle circle = new Circle(100, 100, Dimensions.meterToPixel(0.5f));
		shapes.add(circle);
		play = new Player("Player1",circle, 100, world,0.5f);
		//play = new Player(0,0,"Player1",images.get(0), 100, world);
		entities.add(play);
		entities.add(new NPC("Stand still",new Circle(200, 200, Dimensions.meterToPixel(0.4f)), 100, world,0.4f));
		entities.add(new NPC("Stand still2",new Circle(400, 200, Dimensions.meterToPixel(0.5f)), 100, world,0.5f));
		entities.add(new NPC("Stand still3",new Circle(200, 500, Dimensions.meterToPixel(0.6f)), 100, world,0.6f));
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
		int[] acc = checkMovementKey();
		world.step(arg1, 3, 3);
		for (int i = 3; i<entities.size();++i){
			entities.get(i).addForce(acc, arg1);
		}
//		play.addForce(acc, arg1);
		bodyChange();
	}

	private void bodyChange(){
		ArrayList<Body> rmBodys = world.getRemoveBodys();
		if(!rmBodys.isEmpty()){
			for(int i =0; i< rmBodys.size();++i){
				Body b = rmBodys.get(i);
				for(int j =3; j< entities.size();++j){
					if(b.getUserData().equals((entities.get(j).getName()))){
						entities.remove(j);
						break;
					}
				}
				world.destroyBody(b);				
			}
			rmBodys.clear();
		}		
	}

	/**
	 * @param args
	 * @throws SlickException 
	 */
	public static void main(String[] args) throws SlickException {
		Pathogenum petriDish = new Pathogenum("Pathogenum");
		AppGameContainer agc = new AppGameContainer(petriDish);
		agc.setDisplayMode(Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT, false);
		agc.setTargetFrameRate(60);
		agc.start();
	}
	//Needs to be changed entirely to allow many keys, maybe manipulate key-events with on_press and on_release?
	private int[] checkMovementKey(){
		Input i = new Input(0);
		if(i.isKeyDown(Input.KEY_UP)){
			//			System.out.println("UP");
			keys[0] = 1;
		} else {
			keys[0] = 0; 
		}

		if(i.isKeyDown(Input.KEY_DOWN)){
			//			System.out.println("DOWN");
			keys[1] = 1;
		} else {
			keys[1] = 0; 
		}
		if(i.isKeyDown(Input.KEY_LEFT)){
			//			System.out.println("LEFT");
			keys[2] = 1;
		} else {
			keys[2] = 0; 
		}
		if(i.isKeyDown(Input.KEY_RIGHT)){
			//			System.out.println("RIGHT");
			keys[3] = 1;
		} else {
			keys[3] = 0; 
		}
		return keys;
	}

}

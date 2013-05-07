package client;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import utils.Constants;
import utils.Conversions;
import utils.Dimensions;
import Entities.Entity;
import Entities.NPC;
import Entities.NpcFactory;
import Entities.Player;
import Entities.Wall;
import Physics.PathogenumWorld;
/**
 * A gamestate representing the main game window
 * @author Mardrey, Mynta90, YoX89, BigFarmor
 * 
 * OBS!!! Should be divided and put in ClientGameState and GameServer
 */
public class ClientGameState extends BasicGameState{

	ArrayList<Image> images;
	ArrayList<Shape> shapes;
	ArrayList<Entity> entities;
	Player play;
	World world;
	float[] boundPoints;
	int[] keys = new int[4];
	public static final int ID = 2;
	int FSAE;
	int FSTBAOE;
	NpcFactory fac;

	ClientConnectionHandler cch;
	public ClientGameState(ClientConnectionHandler cch){
		this.cch = cch;
	}
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		int scale = 2;
		FSTBAOE = 60;
		boundPoints = new float[4];
		boundPoints[0] = Dimensions.meterToPixel(0.1f); //x coordinate for left corners
		boundPoints[1] = boundPoints[0] + Dimensions.SCREEN_WIDTH*scale - Dimensions.meterToPixel(0.2f); //x coordinate for right corners
		boundPoints[2] = Dimensions.meterToPixel(0.1f); //y coordinate for upper corners
		boundPoints[3] = Dimensions.meterToPixel(0.1f) + Dimensions.SCREEN_HEIGHT*scale - Dimensions.meterToPixel(0.2f); // y coordinate for lower corners
		//System.out.println("HERE");
		//leave(arg0, arg1);
		images = new ArrayList<Image>();
		shapes = new ArrayList<Shape>();
		entities = new ArrayList<Entity>();
		//		world = new PathogenumWorld(new Vec2(0f,9.82f));
		world = new PathogenumWorld(new Vec2(0f,0f));
		createWalls(boundPoints);

		readImages("resources/gfx/");
		Random rand = new Random();
		fac = new NpcFactory(rand.nextLong(), (PathogenumWorld)world, boundPoints);
		Circle circle = new Circle(100, 100, Dimensions.meterToPixel(0.5f));
		shapes.add(circle);
		play = new Player("Player1",circle, 100, world,0.5f);
		//play = new Player(0,0,"Player1",images.get(0), 100, world);
		entities.add(play);
		for(int i = 0; i < 10 + rand.nextInt(40); i++){
			entities.add(fac.getNpc());
		}
		//try {
			//cch.joinGame(InetAddress.getLocalHost().getCanonicalHostName(), 30000);
		//} catch (UnknownHostException e) {
		//	e.printStackTrace();
		//}
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		float px = play.getPos().x;
		float py = play.getPos().y;
		float rad = ((Circle) play.getShape()).getRadius();
		float scale = 15.625f/rad;

		if(scale*Dimensions.pixelToMeter(arg0.getScreenWidth()) < Dimensions.pixelToMeter(Dimensions.SCREEN_WIDTH)){
			scale = 0.5f;
		}
		arg2.scale(scale,scale);
		arg2.translate(Dimensions.meterToPixel(-px*scale) + (Dimensions.SCREEN_WIDTH/2), Dimensions.meterToPixel(-py*scale) + (Dimensions.SCREEN_HEIGHT/2));
		//		System.out.println("Scale:  " + scale +"   scale*:  " +scale*Dimensions.pixelToMeter(arg0.getScreenWidth()) + "   Screen:  " + Dimensions.pixelToMeter(Dimensions.SCREEN_WIDTH));
		for(Entity e: entities){
			e.draw(arg2);
		}
		arg2.resetTransform();

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
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		System.out.println("Updating!!");
		int[] acc = checkMovementKey();
		cch.sendMovement(acc);
		byte[] movements = cch.receiveMovements();
		doMovements(movements, arg2);
		if(FSAE > FSTBAOE){
			entities.add(fac.getNpc());
			FSAE = 0;
		}else{			
			FSAE++;
		}
		removeBodies();	
	}



	private void removeBodies(){
		ArrayList<Body> rmBodys = ((PathogenumWorld)world).getRemoveBodys();
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
	 * returns the id of this gamestate (must not have a duplicate)
	 */
	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}

	private void createWalls(float[] bp) {
		Rectangle topWall = new Rectangle(bp[0], bp[2],
				bp[1] - bp[0],
				Dimensions.meterToPixel(0.1f));

		Rectangle leftWall = new Rectangle(bp[0], bp[2],
				Dimensions.meterToPixel(0.1f),
				bp[3] - bp[2]);

		Rectangle rightWall = new Rectangle(bp[1]-bp[0], bp[2],
				Dimensions.meterToPixel(0.1f),
				bp[3] - bp[2]);

		Rectangle bottomWall = new Rectangle(bp[0], bp[3] - bp[2],
				bp[1] - bp[0],
				Dimensions.meterToPixel(0.1f));
		entities.add(new Wall(topWall, world));	
		entities.add(new Wall(leftWall, world));
		entities.add(new Wall(rightWall, world));
		entities.add(new Wall(bottomWall, world));

	}

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

	private void doMovements(byte[] b, int s){
		long frame = extractID(b);
		int acc[] = extractMovements(b);
		world.step(s * 0.001f, 8, 3);
		for (int i = 4; i<entities.size();++i){
			entities.get(i).addForce(acc, s);
		}
	}

	private long extractID(byte[] b){
		byte[] id = new byte[8];
		for(int i = 0; i < 8; ++i){
			id[i] = b[i];
		}

		return Conversions.byteArrayToLong(id);
	}

	private int[] extractMovements(byte[] b){
		int[][] movs = new int[b.length-8][4];
		for(int i = 0; i < b.length-8; ++i){
			switch (b[i]) {
			case Constants.EAST:
				movs[i][3] = 1;
				break;
			case Constants.NORTH:
				movs[i][0] = 1;
				break;
			case Constants.NORTHEAST:
				movs[i][3] = 1;
				movs[i][0] = 1;
				break;
			case Constants.NORTHWEST:
				movs[i][0] = 1;
				movs[i][2] = 1;
				break;
			case Constants.SOUTH:
				movs[i][1] = 1;
				break;
			case Constants.SOUTHEAST:
				movs[i][1] = 1;
				movs[i][3] = 1;
				break;
			case Constants.SOUTHWEST:
				movs[i][1] = 1;
				movs[i][2] = 1;
				break;
			case Constants.WEST:
				movs[i][2] = 1;
				break;

			default:
				break;
			}
		}

		//TODO Enable more players
		return movs[0];

	}
}

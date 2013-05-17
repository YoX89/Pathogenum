package client;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import utils.Constants;
import utils.Conversions;
import utils.Dimensions;
import utils.misc;
import Entities.Entity;
import Entities.NPC;
import Entities.NpcFactory;
import Entities.Player;
import Entities.Wall;
import Physics.PathogenumWorld;

/**
 * A gamestate representing the main game window
 * 
 * @author Mardrey, Mynta90, YoX89, BigFarmor
 * 
 *         OBS!!! Should be divided and put in ClientGameState and GameServer
 */
public class ClientGameState extends BasicGameState {

	ArrayList<Image> images;
	ArrayList<Wall> walls;
	ArrayList<NPC> npcs;
	Player[] players;
	World world;
	float[] boundPoints;
	int[] keys = new int[4];
	public static final int ID = 2;
	int FSAE;
	int FSTBAOE;
	NpcFactory fac;
	Music bgMusic;
	long seed = -1;
	int mIndex= -1;
	int numberOfPlayers=-1;
	boolean initDone = false;
	boolean constructDone = false;
	boolean musicPlays = false;
	ClientConnectionHandler cch;

	public ClientGameState(ClientConnectionHandler cch) {
		this.cch = cch;

		seed = cch.getSeed();
		mIndex = cch.getIndex();
		numberOfPlayers = cch.getNbrOfPlayers();
		constructDone = true;
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		initDone = false;
		int scale = 2;
		FSTBAOE = 60;
		boundPoints = new float[4];
		boundPoints[0] = Dimensions.meterToPixel(0.1f); // x coordinate for left
		// corners
		boundPoints[1] = boundPoints[0] + Dimensions.SCREEN_WIDTH * scale
				- Dimensions.meterToPixel(0.2f); // x coordinate for right
		// corners
		boundPoints[2] = Dimensions.meterToPixel(0.1f); // y coordinate for
		// upper corners
		boundPoints[3] = Dimensions.meterToPixel(0.1f)
				+ Dimensions.SCREEN_HEIGHT * scale
				- Dimensions.meterToPixel(0.2f); // y coordinate for lower
		// corners
		// System.out.println("HERE");
		// leave(arg0, arg1);
		images = new ArrayList<Image>();
		walls = new ArrayList<Wall>();
		npcs = new ArrayList<NPC>();
		players = new Player[numberOfPlayers];
		// world = new PathogenumWorld(new Vec2(0f,9.82f));
		world = new PathogenumWorld(new Vec2(0f, 0f));
		createWalls(boundPoints);

		readImages("resources/gfx/");
		Random rand = new Random(seed);
		fac = new NpcFactory(0, (PathogenumWorld) world,
				boundPoints);

		for(int i =0; i<numberOfPlayers; i++){
			Circle circle = new Circle(100+i*500,100+i*400, Dimensions.meterToPixel(0.5f));	
			Player play = new Player(""+i, circle, 100, world, 0.5f);
			players[i]=play;
		}


		for (int i = 0; i < 10 + rand.nextInt(40); i++) {
			npcs.add(fac.getNpc());
		}

		cch.cm.setReady(false);
		//bgMusic = new Music("resources/audio/Invincible.ogg"); //:(

		// try {
		// cch.joinGame(InetAddress.getLocalHost().getCanonicalHostName(),
		// 30000);
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// }

		initDone = true;
		System.out.println("INIT IS NOW DOOOOOOOOOO=0=NE!");
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		if(initDone && constructDone){
		Player play = players[mIndex];
		if(play !=null){
			float px = play.getPos().x;
			float py = play.getPos().y;
			float rad = ((Circle) play.getShape()).getRadius();
			float scale = 15.625f / rad;

			if (scale * Dimensions.pixelToMeter(arg0.getScreenWidth()) < Dimensions
					.pixelToMeter(Dimensions.SCREEN_WIDTH)) {
				scale = 0.5f;
			}
			arg2.scale(scale, scale);
			arg2.translate(Dimensions.meterToPixel(-px * scale)
					+ (Dimensions.SCREEN_WIDTH / 2),
					Dimensions.meterToPixel(-py * scale)
					+ (Dimensions.SCREEN_HEIGHT / 2));
			// System.out.println("Scale:  " + scale +"   scale*:  "
			// +scale*Dimensions.pixelToMeter(arg0.getScreenWidth()) +
			// "   Screen:  " + Dimensions.pixelToMeter(Dimensions.SCREEN_WIDTH));
		}
		for(int i = 0; i< players.length;i++){
			Player player = players[i];
			if(player != null){
				player.draw(arg2);
			}
		}
		for (Entity e : npcs) {
			e.draw(arg2);
		}
		for(Wall w : walls){
			w.draw(arg2);
		}

		arg2.resetTransform();
		}
	}

	private void readImages(String dir) throws SlickException {
		File res_dir = new File(dir);
		String[] files = res_dir.list();
		for (String s : files) {
			Image im = new Image(dir + s);
			images.add(im);
		}
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {

		if(initDone && constructDone){
			// System.out.println("Updating!!");
//			if(!musicPlays){
//				bgMusic.loop();
//				musicPlays = true;
//			}
			int[] acc = checkMovementKey();
			cch.sendMovement(acc);
			byte[] movements = cch.receiveMovements();
			doMovements(movements, arg2);
			if (FSAE > FSTBAOE) {
				npcs.add(fac.getNpc());
				FSAE = 0;
			} else {
				FSAE++;
			}
			removeBodies();
		}
	}



	private void removeBodies() {
		ArrayList<Body> rmBodys = ((PathogenumWorld) world).getRemoveBodys();
		if (!rmBodys.isEmpty()) {
			System.out.println("size of rmBodys " + rmBodys.size());
			for (int i = 0; i < rmBodys.size(); ++i) {
				Body b = rmBodys.get(i);
				boolean find = false;
				for(int j = 0; j<players.length; j++){
					Player play = players[j];
					if(play!=null){
						if (b.getUserData().equals((play.getName()))) {							
							players[j] = null;
							find = true;				
							break;
						}
					}
				}
				if(!find){
					for (int j = 0; j < npcs.size(); ++j) {
						if (b.getUserData().equals((npcs.get(j).getName()))) {
							npcs.remove(j);
							break;
						}
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
		Rectangle topWall = new Rectangle(bp[0], bp[2], bp[1] - bp[0],
				Dimensions.meterToPixel(0.1f));

		Rectangle leftWall = new Rectangle(bp[0], bp[2],
				Dimensions.meterToPixel(0.1f), bp[3] - bp[2]);

		Rectangle rightWall = new Rectangle(bp[1] - bp[0], bp[2],
				Dimensions.meterToPixel(0.1f), bp[3] - bp[2]);

		Rectangle bottomWall = new Rectangle(bp[0], bp[3] - bp[2], bp[1]
				- bp[0], Dimensions.meterToPixel(0.1f));
		walls.add(new Wall(topWall, world));
		walls.add(new Wall(leftWall, world));
		walls.add(new Wall(rightWall, world));
		walls.add(new Wall(bottomWall, world));

	}

	private int[] checkMovementKey() {
		Input i = new Input(0);
		if (i.isKeyDown(Input.KEY_UP)) {
			//System.out.println("UP");
			keys[0] = 1;
		} else {
			keys[0] = 0;
		}

		if (i.isKeyDown(Input.KEY_DOWN)) {
			// System.out.println("DOWN");
			keys[1] = 1;
		} else {
			keys[1] = 0;
		}
		if (i.isKeyDown(Input.KEY_LEFT)) {
			// System.out.println("LEFT");
			keys[2] = 1;
		} else {
			keys[2] = 0;
		}
		if (i.isKeyDown(Input.KEY_RIGHT)) {
			// System.out.println("RIGHT");
			keys[3] = 1;
		} else {
			keys[3] = 0;
		}
		return keys;
	}

	private void doMovements(byte[] b, int s) {
		if (b != null) {
			//			System.out.println("Movements in client: " + misc.printByte(b));
			long frame = extractID(b);
			int acc[][] = extractMovements(b);
			//			System.out.println("ACC: [" + acc[0] +  "] [" + acc[1] +  "] [" + acc[2] +  "] [" + acc[3] +  "]");
			world.step(s * 0.001f, 8, 3);
			for(int i = 0; i< acc.length; i++){
				Player play = players[i];
				if(play != null){
					play.addForce(acc[i], s);
				}
			}
			for (int i = 0; i < npcs.size(); ++i) {
				npcs.get(i).addForce(s);
			}
		}
	}

	private long extractID(byte[] b) {
		byte[] id = new byte[8];
		for (int i = 0; i < 8; ++i) {
			id[i] = b[i];
		}

		return Conversions.byteArrayToLong(id);
	}

	private int[][] extractMovements(byte[] b) {
		int[][] movs = new int[b.length - 8][4];
		for (int i = 0; i < b.length - 8; ++i) {
			switch (b[i+8]) {
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

		// TODO Enable more players
		return movs;

	}
}

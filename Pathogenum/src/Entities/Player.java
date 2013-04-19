package Entities;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;

public class Player extends Entity{
	
	String name;
	Image img;
	Shape sh;
	
	public Player(int x, int y, String name, Image rep, int speed, World world){
		super(x,y, name, rep, world);
		this.name = name;
		img = rep;
	}
	
	public Player(int x, int y, String name, Shape sh, int speed, World world){
		super(x,y, name, sh, world);
		this.name = name;
		this.sh = sh;
	}

}

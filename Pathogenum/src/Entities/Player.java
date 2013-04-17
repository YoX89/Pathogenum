package Entities;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.Image;

public class Player extends Entity{
	
	String name;
	Image img;
	
	public Player(int x, int y, String name, Image rep, int speed, World world){
		super(x,y, name, rep, world);
		this.name = name;
		img = rep;
	}

}

package Entities;

import org.newdawn.slick.Image;

public class Player extends Entity{
	
	String name;
	int speed;
	Image img;
	
	public Player(int x, int y, String name, Image rep, int speed){
		super(x,y, name, rep, speed);
		this.speed = speed;
		this.name = name;
		img = rep;
	}

}

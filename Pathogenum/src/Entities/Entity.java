package Entities;

import org.newdawn.slick.Image;

public class Entity {
	
	public final static int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	int posX;
	int posY;
	double[] acc;
	int speed;
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public double[] getAcc() {
		return acc;
	}

	public void setAcc(double[] acc) {
		this.acc = acc;
	}

	Image img;
	String name;
	
	public Entity(int x, int y, String name, Image img,int speed){
		this.speed = speed;
		acc = new double[4];
		posX = x;
		posY = y;
		this.name = name;
		this.img = img;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void draw(){
		img.draw(posX,posY);
	}
	
	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
	
	public void move(){
		posX += acc[3];
		posX -= acc[2];
		posY += acc[1];
		posY -= acc[0];
	}
	
	public void addInc(int direction, double accInc){
		acc[direction] += accInc;
	}
	
	public void runInertia(){
		for(int i = 0; i < 4; i++){
			if(acc[i] > 0){
				acc[i] = acc[i]-0.03;
				System.out.println("ACC["+i+"] = " + acc[i]);
			}
			if(acc[i] < 0){
				acc[i] = 0;
			}
		}
	}
	
}

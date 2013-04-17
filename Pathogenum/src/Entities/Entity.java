package Entities;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Image;

public class Entity {
	
	public final static int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	private Body body;
	

	Image img;
	String name;
	
	public Entity(int x, int y, String name, Image img, World world){

		this.name = name;
		this.img = img;
		BodyDef bd = new BodyDef();
		bd.position = new Vec2(x+50, y+50);
		
		bd.type = BodyType.DYNAMIC;
		CircleShape cs = new CircleShape();
		cs.m_radius = 0.5f;
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 0.5f;
		fd.friction = 0.3f;        
		fd.restitution = 0.5f;
		
//		body = new Body(bd, world);
		body = world.createBody(bd);
		body.createFixture(fd);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void draw(){
		Vec2 pos = body.getPosition();
		img.draw(pos.x , pos.y);
	}
	
	public Vec2 getPos() {
		return body.getPosition();
	}
	
	public void addForce(int[] acc){
		body.applyForce(new Vec2( 100000000 * (acc[3]-acc[2]), 100000000 * (acc[1] - acc[0])), getPos());
	}




/*	
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
	*/
	
}

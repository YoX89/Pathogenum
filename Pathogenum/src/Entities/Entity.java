package Entities;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import utils.Dimensions;
import Physics.PathogenumWorld;

public class Entity {
	
	public final static int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	protected Body body;
	protected float force = 0.0003f;
	

	protected Image img;
	protected Shape sh;
	protected String name;
	
//	public Entity(int x, int y, String name, Image img, PathogenumWorld world){
//		this.name = name;
//		this.img = img;
//		BodyDef bd = new BodyDef();
//		bd.position = new Vec2(x, y);
//		
//		bd.type = BodyType.DYNAMIC;
//		CircleShape cs = new CircleShape();
//		cs.m_radius = 0.5f;
//		FixtureDef fd = new FixtureDef();
//		fd.shape = cs;
//		fd.density = 0.5f;
//		fd.friction = 0.3f;        
//		fd.restitution = 0.5f;
//		
//		body = world.createBody(bd);
//		body.createFixture(fd);
//		body.setLinearDamping(0.0015f);
//	}
	public Entity(String name, Shape sh, PathogenumWorld world){
		this.name = name;
		this.sh = sh;
		
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void draw(Graphics arg1){
		Vec2 pos = body.getPosition();
		if(img != null){
			System.out.println("img draw");
			img.draw(Dimensions.meterToPixel(pos.x) , Dimensions.meterToPixel(pos.y));
		}else if(sh != null){
			if(sh instanceof Rectangle){
//				System.out.println("The world width is " + Dimensions.pixelToMeter(Dimensions.SCREEN_WIDTH) + "x" +
//						Dimensions.pixelToMeter(Dimensions.SCREEN_HEIGHT));
			}
			sh.setLocation(Dimensions.meterToPixel(pos.x), Dimensions.meterToPixel(pos.y));
			arg1.draw(sh);
		}
		
	}
	
	public Vec2 getPos() {
		return body.getPosition();
	}
	
	public void addForce(int[] acc, int ms){
		//System.out.println(acc[0] + " " + acc[1] + " " + acc[2] + " " + acc[3]);
		//System.out.println(body.getMass() + " ");
		Vec2 f = new Vec2( (force * ms) * (acc[3]-acc[2]), (force* ms) * (acc[1] - acc[0]));
//		System.out.println("x: " + f.x + " y: " + f.y);
		
		Vec2 curr_vec = body.getLinearVelocity();
		Vec2 new_vec = new Vec2(curr_vec.x + (f.x) , curr_vec.y + (f.y));
		
		body.setLinearVelocity(new_vec);
//		System.out.println(curr_vec.x + " " + new_vec.x);
//		System.out.println("The difference in velocity is = x: " + (new_vec.x - curr_vec.x) + " and y: " + (new_vec.y - curr_vec.y));
	}
	
}

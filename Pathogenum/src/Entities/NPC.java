package Entities;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

import utils.Dimensions;

import Physics.PathogenumWorld;

public class NPC extends Entity {
	Random rand = new Random();
	int forceCount =0;
	int [] acc = new int [4];

	public NPC(String name, Shape sh, int speed, PathogenumWorld world, float radius) {
		super(name, sh, world);


		BodyDef bd = new BodyDef();
		bd.position = new Vec2(Dimensions.pixelToMeter(sh.getX()), Dimensions.pixelToMeter(sh.getY()));

		bd.type = BodyType.DYNAMIC;
		CircleShape cs = new CircleShape();
		cs.m_radius = radius;
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 0.5f;
		fd.friction = 0.3f;        
		fd.restitution = 0.5f;

		body = world.createBody(bd);
		body.createFixture(fd);
		body.setLinearDamping(0.0015f);
		body.setUserData(name);

	}


	public void draw(Graphics arg1){
		Vec2 pos = body.getPosition();
		sh.setLocation(Dimensions.meterToPixel(pos.x) - sh.getWidth()/2, Dimensions.meterToPixel(pos.y) - sh.getHeight()/2);
		arg1.draw(sh);
	}


	public void addForce(int[] ac, int ms){
		if(forceCount == -1){
			forceCount = rand.nextInt(20);
			for(int i =0; i<4;++i){
				acc[i] = rand.nextInt(2);
			}
		} 
		changeShapeSize();
		Vec2 f = new Vec2( (force * ms) * (acc[3]-acc[2]), (force* ms) * (acc[1] - acc[0]));
		Vec2 curr_vec = body.getLinearVelocity();
		Vec2 new_vec = new Vec2(curr_vec.x + (f.x) , curr_vec.y + (f.y));		
		body.setLinearVelocity(new_vec);
		forceCount--;

	}
	
	public void changeShapeSize(){
		Circle c = (Circle) sh;
		float r = body.getFixtureList().getShape().getRadius();
		c.setRadius(1000*Dimensions.pixelToMeter(r));
	}



}

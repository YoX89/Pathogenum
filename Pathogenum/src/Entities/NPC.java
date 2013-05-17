package Entities;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;

import utils.Dimensions;

public class NPC extends Entity {
	Random rand;
	int forceCount =0;
	int [] acc = new int [4];
	long seed;
	
	public NPC(String name, Shape sh, int speed, World world, float radius, long seed) {
		super(name, sh, world);
		this.seed = seed;
		BodyDef bd = new BodyDef();
		bd.position = new Vec2(Dimensions.pixelToMeter(sh.getX()), Dimensions.pixelToMeter(sh.getY()));

		bd.type = BodyType.DYNAMIC;
		CircleShape cs = new CircleShape();
		cs.m_radius = radius;
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1f;
		fd.friction = 0.3f;        
		fd.restitution = 0.5f;
		force = 0.04f;
		body = world.createBody(bd);
		body.createFixture(fd);
		body.setLinearDamping(0.0015f);
		body.setUserData(name);
		rand = new Random(seed);
	}
	
	public NPC(String name, Shape sh, int speed, World world, float radius) {
		super(name, sh, world);
		this.seed = seed;
		BodyDef bd = new BodyDef();
		bd.position = new Vec2(Dimensions.pixelToMeter(sh.getX()), Dimensions.pixelToMeter(sh.getY()));

		bd.type = BodyType.DYNAMIC;
		CircleShape cs = new CircleShape();
		cs.m_radius = radius;
		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1f;
		fd.friction = 0.3f;        
		fd.restitution = 0.5f;
		force = 0.04f;
		body = world.createBody(bd);
		body.createFixture(fd);
		body.setLinearDamping(0.0015f);
		body.setUserData(name);
		rand = new Random();

	}


	public void draw(Graphics arg1){
		Vec2 pos = body.getPosition();
		changeShapeSize();
		sh.setLocation(Dimensions.meterToPixel(pos.x) - sh.getWidth()/2, Dimensions.meterToPixel(pos.y) - sh.getHeight()/2);
		arg1.setColor(Color.green);
		arg1.draw(sh);
	}


	public void addForce(int ms){
		if(forceCount == -1){
			forceCount = rand.nextInt(20);
			for(int i =0; i<4;++i){
				acc[i] = rand.nextInt(2);
			}	
		}
		super.addForce(acc, ms);
		forceCount--;

	}

}

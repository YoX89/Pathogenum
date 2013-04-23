package Entities;

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

public class Player extends Entity{
	

//	public Player(int x, int y, String name, Image rep, int speed, PathogenumWorld world){
//		super(x,y, name, rep, world);
//		this.name = name;
//		img = rep;
//	}
	
	public Player( String name, Shape sh, int speed, PathogenumWorld world, float radius){
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
		changeShapeSize();
		sh.setLocation(Dimensions.meterToPixel(pos.x) - sh.getWidth()/2, Dimensions.meterToPixel(pos.y) - sh.getHeight()/2);
		arg1.draw(sh);
	}

}

package Entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;

import utils.Dimensions;

import Physics.PathogenumWorld;

public class Wall extends Entity{

	public Wall(Shape sh, PathogenumWorld world) {
		super("wall", sh, world);
	
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;		
		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;
		
		PolygonShape rect = new PolygonShape();

		fd.shape = rect;
		
		bd.position = new Vec2(Dimensions.pixelToMeter(sh.getX() + sh.getWidth()/2), Dimensions.pixelToMeter(sh.getY() + sh.getHeight()/2));
		rect.setAsBox(Dimensions.pixelToMeter(sh.getWidth()/2), Dimensions.pixelToMeter(sh.getHeight()/2));
		body = world.createBody(bd);
		
		body.createFixture(fd);		
	}
	
	public void draw(Graphics arg1){
		arg1.draw(sh);
	}

}

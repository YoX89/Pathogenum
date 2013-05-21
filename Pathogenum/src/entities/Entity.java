package entities;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import utils.Dimensions;

public class Entity {

	public final static int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	protected Body body;
	protected float force = 0.0001f;

	protected Image img;
	protected Shape sh;
	protected String name;

	public Entity(String name, Shape sh, World world){
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
			img.draw(Dimensions.meterToPixel(pos.x) , Dimensions.meterToPixel(pos.y));
		}else if(sh != null){
			if(sh instanceof Rectangle){
			}
			sh.setLocation(Dimensions.meterToPixel(pos.x), Dimensions.meterToPixel(pos.y));
			arg1.draw(sh);
		}

	}

	public void changeShapeSize(){
		Circle c = (Circle) sh;
		float r = body.getFixtureList().getShape().getRadius();
		c.setRadius(1000*Dimensions.pixelToMeter(r));
	}

	public Vec2 getPos() {
		return body.getPosition();
	}

	public Shape getShape(){
		return sh;
	}

	public Body getBody(){
		return body;
	}

	public void addForce(int[] acc, int ms){
		Vec2 f = new Vec2((acc[3]-acc[2]),(acc[1] - acc[0]));
		f.normalize();
		f = f.mul((force));
		body.applyLinearImpulse(f, getPos());
	}

}

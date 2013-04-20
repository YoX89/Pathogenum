package Physics;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import utils.Dimensions;


public class PathogenumWorld extends World {

	public PathogenumWorld(Vec2 gravity) {
		super(gravity);
		
		createWalls();
		
		
	}
	
	public Body[] createWalls(){
		
		Body[] bodies = new Body[4];
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;		
		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;
		
		PolygonShape rect = new PolygonShape();

		fd.shape = rect;
		
		
		//left
		bd.position = new Vec2(0.1f, 0.1f);
		rect.setAsBox(0.1f, Dimensions.pixelToMeter(Dimensions.SCREEN_HEIGHT) -0.2f);
		bodies[0] = this.createBody(bd);
		
		bodies[0].toString();
		bodies[0].createFixture(fd);

		rect.setAsBox(Dimensions.pixelToMeter(Dimensions.SCREEN_WIDTH)-0.2f, 0.1f);
		bodies[1] = this.createBody(bd);
		bodies[1].createFixture(fd);
//
//		bd.position = new Vec2(Dimensions.pixelToMeter(Dimensions.SCREEN_WIDTH) - 0.1f ,Dimensions.pixelToMeter(Dimensions.SCREEN_HEIGHT) - 0.1f);
//		rect.setAsBox(0.1f, Dimensions.pixelToMeter(1024) -0.2f);
//		wall = this.createBody(bd);
//		wall.createFixture(fd);
//
//		bd.position = new Vec2(0.1f, 0.1f);
//		rect.setAsBox(0.1f, Dimensions.pixelToMeter(1024) -0.2f);
//		wall = this.createBody(bd);
//		wall.createFixture(fd);
		
		
		return bodies;
	}


}

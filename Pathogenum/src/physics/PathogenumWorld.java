package physics;

import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;


public class PathogenumWorld extends World {
	private ArrayList<Body> removeBodys = new ArrayList<Body>();

	public PathogenumWorld(Vec2 gravity) {
		super(gravity);
		setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endContact(Contact con) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beginContact(Contact con) {
				Fixture a = con.getFixtureA();
				Fixture b = con.getFixtureB();
				Body ab = a.getBody();
				Body bb = b.getBody();
				if(a.getShape().getType().equals(ShapeType.POLYGON) || b.getShape().getType().equals(ShapeType.POLYGON)){
				}else {
					float ra = a.getShape().getRadius();
					float rb = b.getShape().getRadius();
					if(ra==rb){
					}else if(ra<rb){
						
						b.getShape().setRadius((float) (Math.sqrt(Math.pow(rb, 2)+Math.pow(ra, 2))));
						bb.resetMassData();
						removeBodys.add(a.getBody());
					}else{
						a.getShape().setRadius((float) (Math.sqrt(Math.pow(ra, 2)+Math.pow(rb, 2))));
						ab.resetMassData();
						removeBodys.add(b.getBody());	
					}
				}
			}
		});
	}

	public ArrayList<Body> getRemoveBodys(){
		return removeBodys;
	}

}

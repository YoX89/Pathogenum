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
			//	System.out.println("----------------end contackt--------------");
				// TODO Auto-generated method stub
			}

			@Override
			public void beginContact(Contact con) {
				//System.out.println("----------------begin contackt--------------");
				Fixture a = con.getFixtureA();
				Fixture b = con.getFixtureB();
				Body ab = a.getBody();
				Body bb = b.getBody();
				//System.out.println("AB: " + ab.getLinearVelocity());
				//System.out.println("BB: " + bb.getLinearVelocity());
				if(a.getShape().getType().equals(ShapeType.POLYGON) || b.getShape().getType().equals(ShapeType.POLYGON)){
				//	System.out.println("wall");
					//System.out.println("LinVeclX: " + locV.x + ", LinVecY: " + locV.y);
				}else {
					//System.out.println("a name " + a.getBody().getUserData());
					//System.out.println("b name " + b.getBody().getUserData());
					float ra = a.getShape().getRadius();
					float rb = b.getShape().getRadius();
					if(ra==rb){
//						System.out.println("samma storlek, vad händer????");
					}else if(ra<rb){
	//					System.out.println("B �r st�rst: ra = " + ra + " rb = " +rb);
						
						b.getShape().setRadius((float) (Math.sqrt(Math.pow(rb, 2)+Math.pow(ra, 2))));
						bb.resetMassData();
		//				System.out.println("Efter �k: ra = " + a.getShape().getRadius() + " rb = " + b.getShape().getRadius());
						removeBodys.add(a.getBody());
					}else{
				//		System.out.println("a �r st�rst: ra = " + ra + " rb = " +rb);
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

package Physics;

import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
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
				System.out.println("----------------end contackt--------------");
				// TODO Auto-generated method stub
			}

			@Override
			public void beginContact(Contact con) {
				System.out.println("----------------begin contackt--------------");
				Fixture a = con.getFixtureA();
				Fixture b = con.getFixtureB();
				if(a.getShape().getType().equals(ShapeType.POLYGON) || b.getShape().getType().equals(ShapeType.POLYGON)){
					System.out.println("wall");
				}else {
					System.out.println("a name " + a.getBody().getUserData());
					System.out.println("b name " + b.getBody().getUserData());
					float ra = a.getShape().getRadius();
					float rb = b.getShape().getRadius();
					if(ra==rb){
						System.out.println("samma storlek????");
					}else if(ra<rb){
						System.out.println("B är störst: ra = " + ra + " rb = " +rb);
						b.getShape().setRadius((float) (rb + Math.pow(rb, 2)-Math.pow(ra, 2)));
						System.out.println("Efter ök: ra = " + a.getShape().getRadius() + " rb = " + b.getShape().getRadius());
						removeBodys.add(a.getBody());
					}else{
						System.out.println("a är störst: ra = " + ra + " rb = " +rb);
						a.getShape().setRadius((float) (ra + Math.pow(ra, 2)-Math.pow(rb, 2)));
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

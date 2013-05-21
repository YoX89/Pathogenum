package entities;

import java.util.Random;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.geom.Circle;

import physics.PathogenumWorld;


import utils.Dimensions;

public class NpcFactory {
	
	Random rand;
	int id;
	PathogenumWorld world;
	float[] bp;
	
	public NpcFactory(long seed, PathogenumWorld world, float[] bp){
		rand = new Random(seed);
		id = 0;
		this.world = world;
		this.bp = bp;
	}
	
	public NPC getNpc(){
		long seed2 = rand.nextLong();
		String name = "NPC"+id;
		
		float radius = 0;
		while(radius < 0.1){
			radius = rand.nextFloat();
		}
		Circle c = new Circle(bp[0]+Dimensions.meterToPixel(0.2f) + rand.nextFloat()*(bp[1] - bp[0]),bp[2]+Dimensions.meterToPixel(0.2f) + rand.nextFloat()*(bp[3]-bp[2]), Dimensions.meterToPixel(radius));
		NPC ent = new NPC(name, c, 100, world, radius, seed2);
		id++;
		return ent;
	}
}

package utils;

public class Dimensions {
	

	private static final float PTM_RATIO = 32;
	
	public static final int SCREEN_WIDTH = 1024;
	public static final int SCREEN_HEIGHT = 768;
	
	
	public static float pixelToMeter(float p){
		return (p/PTM_RATIO);
	}
	
	public static float meterToPixel(float m){
		return (m * PTM_RATIO);
	}

}

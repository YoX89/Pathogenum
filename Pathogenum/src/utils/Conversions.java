package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Conversions {
	public static int ByteArrayToInt(byte[] arr){
		 final ByteBuffer bb = ByteBuffer.wrap(arr);
		 bb.order(ByteOrder.LITTLE_ENDIAN);
		 return bb.getInt();
	}
	
	public static byte[] intToByteArray(int t){
		 final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
		 bb.order(ByteOrder.LITTLE_ENDIAN);
		 bb.putInt(t);
		 return bb.array();
	}
}
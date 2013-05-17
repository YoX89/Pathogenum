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
	
	public static byte[] intToByteArray(int t, ByteOrder bo){
		 final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
		 bb.order(bo);
		 bb.putInt(t);
		 return bb.array();
	}
	
	public static int ByteArrayToInt(byte[] arr, ByteOrder bo){
		 final ByteBuffer bb = ByteBuffer.wrap(arr);
		 bb.order(bo);
		 return bb.getInt();
	}
	
	public static long byteArrayToLong(byte[] arr){
		final ByteBuffer bb = ByteBuffer.wrap(arr);
		 bb.order(ByteOrder.LITTLE_ENDIAN);
		 return bb.getLong();
	}
	
	public static byte[] longToByteArray(long l){
		 final ByteBuffer bb = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
		 bb.order(ByteOrder.LITTLE_ENDIAN);
		 bb.putLong(l);
		 return bb.array();
	}

	public static byte[] ObjectByteArrayToPrimitiveByteArray(
			Byte[] array) {
		byte[] ret = new byte[array.length];
		for(int i = 0; i < array.length; i++){
			ret[i] = array[i];
		}
		return ret;
	}
	
	public static Byte[] PrimitiveByteArrayToObjectByteArray(byte[] array){
		Byte[] ret = new Byte[array.length];
		for(int i = 0; i < array.length; i++){
			ret[i] = array[i];
		}
		return ret;
	}
}

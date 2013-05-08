package utils;

public class misc {
	public static String printByte(byte[] b){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < b.length; i++){
			sb.append(b[i]);
		}
		return sb.toString();
	}
	
	public static String printByte(Byte[] b){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < b.length; i++){
			sb.append(b[i]);
		}
		return sb.toString();
	}
}

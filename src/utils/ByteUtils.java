package utils;

public abstract class ByteUtils {
	
	public static int make8(byte a){
		return a & 0xFF;
	}
	
	public static int make16(byte a, byte b){
		return ((a & 0xFF) << 8) 
				| (b & 0xFF);
	}

	public static int make32(byte a, byte b, byte c, byte d){
		return ((a & 0xFF) << 24)
				| ((b & 0xFF) << 16)
				| ((c & 0xFF) << 8)
				| (d & 0xFF);
	}
}

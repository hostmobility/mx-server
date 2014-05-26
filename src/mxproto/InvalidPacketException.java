package mxproto;

public class InvalidPacketException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7631021409217109401L;

	public InvalidPacketException(String message) {
        super(message);
    }

    public InvalidPacketException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

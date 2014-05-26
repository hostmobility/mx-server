package core;

/**
 * Representation of packets used by the server.
 * @author jay
 *
 */
public abstract class ProtocolPacket {
	public abstract byte[] getPacketBytes();
}

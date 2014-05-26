package mxproto;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import core.ProtocolPacket;

/**
 * MX packet implementation.
 * @author jay
 *
 */
public class MxPacket extends ProtocolPacket {
	
	private byte[] packet;
	
	public MxPacket(){
		
	}
	
	/**
	 * Creates a packet from the specified number of bytes from the given array.
	 * @param packet Array to copy from.
	 * @param bytecount Number of bytes to copy.
	 */
	public MxPacket(byte[] packet, int bytecount){
		this.packet = Arrays.copyOf(packet, bytecount);
	}
	
	/**
	 * Creates a packet with the given header and data.
	 * @param LOM Length of message
	 * @param FLAGS Flags
	 * @param SEQ Sequence number
	 * @param SRC Source endpoint
	 * @param DST Destination endpoint
	 * @param CMD_ID Command type
	 * @param data Data section
	 * @throws InvalidPacketException
	 */
	public MxPacket(int LOM, int FLAGS, int SEQ, int SRC, int DST, int CMD_ID, byte[] data)
		throws InvalidPacketException {
			this(LOM, FLAGS, SEQ, SRC, DST, CMD_ID, data, 
					data.length);
	}
	
	/**
	 * Creates a packet with the given header, and the specified number of bytes
	 * from the given data array as data section.
	 * @param LOM Length of message
	 * @param FLAGS Flags & sequence number
	 * @param SEQ Sequence number
	 * @param SRC Source endpoint
	 * @param DST Destination endpoint
	 * @param CMD_ID Command type
	 * @param data Data array to copy data section from
	 * @param databytecount Length of data section to copy
	 * @throws InvalidPacketException
	 */
	public MxPacket(int LOM, int FLAGS, int SEQ, int SRC, int DST, int CMD_ID, 
			byte[] data, int databytecount)
		throws InvalidPacketException {
			if(LOM > MxDefs.PACKET_MAX_SIZE)
				throw new InvalidPacketException("Packet size too large.");
			if(LOM != databytecount + MxDefs.HEADER_SIZE + 1) // +1 from CMD_ID
				throw new InvalidPacketException("LOM and data length mismatch.");
			packet = new byte[LOM];
			packet[MxDefs.LOM_HIGH_OFFSET] = 0;
			packet[MxDefs.LOM_LOW_OFFSET] = (byte) LOM;
			packet[MxDefs.FLAGS_SEQ_OFFSET] = 
					(byte) ((FLAGS << 4) | SEQ);
			packet[MxDefs.SRC_OFFSET] = (byte) SRC;
			packet[MxDefs.DST_OFFSET] = (byte) DST;
			packet[MxDefs.CMD_OFFSET] = (byte) CMD_ID;
			for(int i=0;i<databytecount;i++){
				packet[MxDefs.DATA_OFFSET + i] = data[i];
			}
			packet[MxDefs.CRC_OFFSET] = (byte) calculateCRC(LOM, packet);
	}
	
	/**
	 * Returns the packet's higher order LOM byte. (Unused in default implementation)
	 * @return Higher order LOM byte.
	 */
	public int getLOMHigh() {
		return packet[MxDefs.LOM_HIGH_OFFSET] & 0xFF;
	}
	
	/**
	 * Returns the packet's lower order LOM byte.
	 * @return Lower order LOM byte.
	 */
	public int getLOMLow() {
		return packet[MxDefs.LOM_LOW_OFFSET] & 0xFF;
	}
	
	/**
	 * Returns the packet's flags.
	 * @return Flags.
	 */
	public int getFlags() {
		return (packet[MxDefs.FLAGS_SEQ_OFFSET] & 0xF0) >>> 4;
	}
	
	/**
	 * Returns the packet's sequence number.
	 * @return Sequence number.
	 */
	public int getSeq() {
		return packet[MxDefs.FLAGS_SEQ_OFFSET] & 0x0F;
	}
	
	/**
	 * Returns the packet's source endpoint.
	 * @return Source endpoint.
	 */
	public int getSrc() {
		return packet[MxDefs.SRC_OFFSET] & 0xFF;
	}
	
	/**
	 * Returns the packet's 
	 * @return
	 */
	public int getDst() {
		return packet[MxDefs.DST_OFFSET] & 0xFF;
	}
	
	/**
	 * Returns the packet's CRC.
	 * @return CRC.
	 */
	public int getCRC() {
		return packet[MxDefs.CRC_OFFSET] & 0xFF;
	}
	
	/**
	 * Returns the packet's command ID.
	 * @return Command ID.
	 */
	public int getCmdId() {
		return packet[MxDefs.CMD_OFFSET] & 0xFF;
	}
	
	/**
	 * Returns the packet's data section as byte array.
	 * @return Data section byte array.
	 */
	public byte[] getData() {
		return Arrays.copyOfRange(packet, MxDefs.DATA_OFFSET, packet.length-1);
	}
	
	/**
	 * Returns a byte array representation of the entire packet.
	 */
	public byte[] getPacketBytes() {
		return packet;
	}
	
	/**
	 * Returns a description of the packet's header.
	 * @return Detailed header description.
	 */
	public String headerString() {
		return String.format("********\n" + 
							"LOM: 0x%X\n" +
							"FLG: 0x%X\n" + 
							"SEQ: 0x%X\n" +
							"SRC: 0x%X\n" + 
							"DST: 0x%X\n" +
							"CRC: %d\n" +
							"CMD: 0x%X\n" +
							"********", 
							getLOMLow(),
							getFlags(),
							getSeq(),
							getSrc(),
							getDst(),
							getCRC(),
							getCmdId());
	}
	
	/**
	 * Returns the data section as a string (only applicable for printable characters).
	 * @return Data section string representation.
	 */
	public String dataString() {
		try{
			return String.format("DATA: [%s]", 
					new String(packet, MxDefs.DATA_OFFSET, 
							getLOMLow() - MxDefs.DATA_OFFSET, "US-ASCII"));
		}
		catch(UnsupportedEncodingException uee){
			return "";
		}
	}
	
	/**
	 * Returns a detailed string representation of the header and data.
	 */
	public String toString() {
		return headerString() + "\n" + dataString();
	}
	
	/**
	 * Creates an ACK packet for a given packet.
	 * @param ackFor The packet to ack.
	 * @return ACK packet for the source packet.
	 */
	public static MxPacket createACKPacket(MxPacket ackFor){
		MxPacket ackpack = new MxPacket();
		int ackpacklen = MxDefs.HEADER_SIZE + 1 + 2; // CMD_ID & 2 bytes SEQ and CRC
		ackpack.packet = new byte[ackpacklen];
		ackpack.packet[MxDefs.LOM_HIGH_OFFSET] = 0;
		ackpack.packet[MxDefs.LOM_LOW_OFFSET] = (byte) ackpacklen; 
		/* FLAGS, SRC, DST meaningless for ACK, skip */
		ackpack.packet[MxDefs.CMD_OFFSET] = MxDefs.CMD_ACK;
		/* ACKs match against original SEQ and CRC */
		ackpack.packet[MxDefs.DATA_OFFSET] = ackFor.packet[MxDefs.FLAGS_SEQ_OFFSET];
		ackpack.packet[MxDefs.DATA_OFFSET+1] = ackFor.packet[MxDefs.CRC_OFFSET];
		ackpack.packet[MxDefs.CRC_OFFSET] = 
				(byte) calculateCRC(ackpacklen, ackpack.packet);
		return ackpack;
	}
	
	/**
	 * Checks if the packet is of type ACK.
	 * @return True if the packet is an ACK packet.
	 */
	public boolean isAck(){
		return getCmdId() == MxDefs.CMD_ACK;
	}
	
	/**
	 * Calculates CRC for the packet by adding all bytes except for the CRC itself,
	 * modulo 256.
	 * @param LOM Length of packet.
	 * @param packet The packet to calculate CRC for.
	 * @return The CRC value.
	 */
	public static int calculateCRC(int LOM, byte[] packet) {
		int CRC = 0;
		for(int i=0;i<LOM;i++){
			if(i!=MxDefs.CRC_OFFSET) // Don't add the CRC to itself
				CRC += packet[i] & 0xFF; // Squish sign bits
		}
		return CRC % 256;
	}
	
	/**
	 * Attempts to parse a MX packet from the given byte array and length.
	 * @param packet Byte array representation of packet.
	 * @param bytecount Length of packet in bytes.
	 * @return MxPacket representation.
	 * @throws InvalidPacketException
	 */
	public static MxPacket parsePacket(byte[] packet, int bytecount) throws InvalidPacketException {
		if(bytecount < MxDefs.PACKET_MIN_SIZE)
			throw new InvalidPacketException("Packet size too small.");
		int LOM = packet[MxDefs.LOM_LOW_OFFSET] & 0xFF;
		int CRC = packet[MxDefs.CRC_OFFSET] & 0xFF; 
		if(LOM > MxDefs.PACKET_MAX_SIZE)
			throw new InvalidPacketException("Packet size too long.");
		if(bytecount != LOM)
			throw new InvalidPacketException(
					String.format("LOM (%d) and data length (%d) mismatch.",
							LOM, bytecount));
		MxPacket mxpacket = new MxPacket(packet, bytecount);
		if(mxpacket.getCRC() != CRC)
			throw new InvalidPacketException("CRC mismatch.");
		return mxpacket;
	}
}
package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

import logger.LogLevel;
import logger.Logger;
import mxproto.InvalidPacketException;
import mxproto.MxDefs;
import mxproto.MxPacket;
import mxproto.MxParser;

/**
 * What a single thread serving a specific connection will run.
 * @author jay
 *
 */
public class ClientSocket implements Runnable {
	
	/* Check for incoming data every [ms] */
	private static final int DEFAULT_READ_TIMEOUT = 666;
	
	/* Maximum time to wait for ACK before resend */
	private static final int DEFAULT_RESEND_TIME = 5*1000*3;
	
	/* Number of resends - disable by using 0 */
	private static final int MAX_RESENDS = 2;
	
	/* Close socket after 5 minutes of inactivity */
	private static final int DEFAULT_CLOSE_TIMEOUT = 5*60*1000;
	
	/* Allocate 256 bytes per connection for incoming data */
	private static final int BUF_SIZE = 256;
	
	private Socket socket;
	private DataInputStream ins;
	private DataOutputStream outs;
	private int readTimeout;
	private int closeTimeout;
	private int resendTime;
	private int resendCount;
	private int ackWaitTime;
	private boolean ackPending;
	private long inactiveTime;
	private int id;
	private int sequenceNr;
	private byte[] buf;
	
	/* Currently using the MX protocol */
	private LinkedList<ProtocolPacket> outQueue;
	
	public Logger logger;
	
	/**
	 * Create a client socket with default timeout between socket reads.
	 * @param socket Base TCP socket.
	 * @param id Numerical ID of socket.
	 * @param logger Logger to use.
	 */
	public ClientSocket(Socket socket, int id, Logger logger){
		this(socket,DEFAULT_READ_TIMEOUT, id, logger);
	}
	
	/**
	 * Create a client socket with custom timeout.
	 * @param socket Base TCP socket.
	 * @param timeoutMillis Timeout between reads.
	 * @param id Numerical ID of socket-
	 * @param logger Logger to use.
	 */
	public ClientSocket(Socket socket, int timeoutMillis, int id, Logger logger){
		this.socket = socket;
		this.readTimeout = timeoutMillis;
		this.closeTimeout = DEFAULT_CLOSE_TIMEOUT;
		this.resendTime = DEFAULT_RESEND_TIME;
		this.resendCount = 0;
		this.ackWaitTime = 0;
		this.ackPending = false;
		this.id = id;
		this.logger = logger;
		this.inactiveTime = 0;
		this.sequenceNr = 0;
		this.outQueue = new LinkedList<ProtocolPacket>();
	}
	
	/**
	 * Initialize streams and buffer.
	 * @return True if successful.
	 */
	public boolean init(){
		try{
			this.ins = new DataInputStream(socket.getInputStream());
			this.outs = new DataOutputStream(socket.getOutputStream());
			socket.setSoTimeout(readTimeout);
			buf = new byte[BUF_SIZE];
		}
		catch(IOException ioe){
			logger.log(LogLevel.WARNING, this.toString() + 
					": error initializing socket streams.");
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "ClientSocket #" + id;
	}
	
	/**
	 * Circular four byte sequence number. Unique for each clientSocket.
	 * @return
	 */
	public int getSequence(){
		return sequenceNr++ % 16;
	}
	
	@Override
	public void run(){
		logger.log(LogLevel.DEBUG, this.toString() + ": up.");
		
		long prevTime = System.currentTimeMillis();
		
		/***************************************************************/
		/* Construct and send a single example packet                  */
		/***************************************************************/
		
			byte[] dataBytes = null;				
			
			/* Data section varies per MX packet
			 * Typically the first two bytes represent a "type"
			 */
			dataBytes = new byte[2];
			dataBytes[0] = 0;
			dataBytes[1] = MxDefs.ANALOG_LI_ION_VOLTAGE;
			//testBytes[2] = 4; // CONTINUOUS_PULSE;
			//testBytes[3] = 20; // Pulse time
			//testBytes[4] = 20; // Pulse time
			
			if(dataBytes != null)
			try{
				MxPacket packet = new MxPacket(
						/* Length: DATA length + header size + CMD_ID */
						dataBytes.length + MxDefs.HEADER_SIZE + 1, 
						0, // Flags
						getSequence(), // Sequence number
						MxDefs.FUNC_APPLICATION, // Source, as good as any atm
						MxDefs.FUNC_ANALOG, // Destination
						MxDefs.CMD_READ, // Command ID
						dataBytes); // Data section
				
				logInfo("Adding packet with DST " + packet.getDst());
				
				/* Queue the packet - packets are sent main loop */
				outQueue.add(packet);
			}
			catch(InvalidPacketException ipe){
				logger.log(LogLevel.WARNING, 
						this.toString() + ": Tried to build invalid packet.");
			}
		
		/***************************************************************/
		/* End of example packet                                       */
		/***************************************************************/
		
		/* Main socket loop */
		while(true){
			try{			
				/* Read incoming */
				int len = ins.read(buf, 0, BUF_SIZE);
				if(len > 0){
					inactiveTime = 0;
					/* Based on MX packets, thus must have a LoM on position 2. 
					 * Might have read in multiple packets into the buffer, 
					 * so loop until everything is read. */
					while(len >= 2){
						MxPacket packet = null;
						int pktLen = buf[1] & 0xFF;
						try{
							packet = MxPacket.parsePacket(buf, len);
						}
						catch(InvalidPacketException ipe){
							logger.log(LogLevel.WARNING, 
									this.toString() + ": " + ipe.getMessage());
							packet = null;
						}
						if(packet!=null){
							/* Received a protocol layer ACK */
							if(packet.isAck()){
								matchQueue(packet);
							}
							/* Received something else - ACK and handle */
							else{
								MxPacket ackpack = MxPacket.createACKPacket(packet);
								outs.write(ackpack.getPacketBytes());
								outs.flush();
								logInfo("Acked packet");
							}
							handlePacket(packet);
						}
						
						/* A bugged packet may have been sent.. avoid infinite loop! */
						if(pktLen == 0) 
							pktLen = Math.min(MxDefs.HEADER_SIZE, len);
						
						for(int i=0;i<pktLen;i++){
							buf[i] = buf[i+pktLen];
						}
						/* This many bytes were actually handled */
						len -= pktLen;
					}
				}
				else{
					logger.log(LogLevel.DEBUG, this.toString() + 
							": stream ended.");
					break;
				}
			}
			/* Single read timed out, no problem */
			catch(SocketTimeoutException ste){
				if(inactiveTime > closeTimeout){
					logger.log(LogLevel.INFO, 
						String.format("%s: reached inactivity timeout (%ds).",
						this.toString(), inactiveTime/1000));
					break;
				}
			}
			/* Connection failure */
			catch(IOException ioe){
				logger.log(LogLevel.WARNING, this.toString() + 
						": failed to read from socket.");
				break;
			}
			
			/* Send outgoing */
			if(!outQueue.isEmpty()){
				if(ackPending){
					if(resendCount >= MAX_RESENDS){
						if(MAX_RESENDS > 0)
							logger.log(LogLevel.DEBUG, 
								this.toString() + ": Resend limit reached.");
						outQueue.poll();
						ackPending = false;
					}
					else if(ackWaitTime > resendTime){
						resendCount++;
						ackWaitTime = 0;
						logger.log(LogLevel.DEBUG, 
								this.toString() + ": Resending.");
						
						ProtocolPacket packet = outQueue.peek();
						try{
							outs.write(packet.getPacketBytes());
							outs.flush();
						}
						catch(IOException ioe){
							// Swallow
						}
					}
				}
				else{
					logger.log(LogLevel.DEBUG, 
							this.toString() + ": Sending.");
					
					ProtocolPacket packet = outQueue.peek();
					try{
						outs.write(packet.getPacketBytes());
						outs.flush();
						ackPending = true;
						resendCount = 0;
						ackWaitTime = 0;
					}
					catch(IOException ioe){
						// Swallow
					}
				}
			}
			
			long currTime = System.currentTimeMillis();
			inactiveTime += currTime - prevTime;
			ackWaitTime += currTime - prevTime;
			prevTime = currTime;
		}
		
		closeSocket();
	}
	
	/**
	 * Gracefully closes the socket with its streams.
	 */
	public void closeSocket(){
		try{
			ins.close();
		}
		catch(IOException ioe){ 
			// Swallow
		}

		try{
			outs.close();
		}
		catch(IOException ioe){ 
			// Swallow
		}
		
		try{
			socket.close();
		}
		catch(IOException ioe){ 
			// Swallow
		}
		
		logInfo("Terminated");
	}
	
	// TODO: implement all useful (?) calls
	public void handlePacket(MxPacket packet){
		switch(packet.getCmdId()){
			case MxDefs.CMD_EVENT:
				logInfo("Received CMD_EVENT");
				switch(packet.getSrc()){
					case MxDefs.FUNC_GPS:
						// 3 bytes aux before GPS data
						MxParser.handleGpsData(this, packet.getData(), 3);
						break;
					default:
						logInfo("Unknown source: "+packet.getSrc());
						break;
				}
				break;
			case MxDefs.CMD_READ_REPLY:
				logInfo("Received CMD_READ_REPLY");
				switch(packet.getSrc()){
					case MxDefs.FUNC_GPS:
						// 4 bytes aux before GPS data
						MxParser.handleGpsData(this, packet.getData(), 4);
						break;
					default:
						logInfo("Unknown source: "+packet.getSrc());
						break;
				}
				break;
			case MxDefs.CMD_WRITE_REPLY:
				logInfo("Received CMD_WRITE_REPLY");
				break;
			case MxDefs.CMD_SUBSCRIBE_REPLY:
				logInfo("Received CMD_SUBSCRIBE_REPLY");
				break;
			case MxDefs.CMD_UNSUBSCRIBE_REPLY:
				logInfo("Received CMD_UNSUBSCRIBE_REPLY");
				break;
			case MxDefs.CMD_FUNCTION_REPLY:
				logInfo("Received CMD_FUNCTION_REPLY");
				break;
			case MxDefs.CMD_ERROR:
				logInfo("Received CMD_ERROR");
				break;
			case MxDefs.CMD_ACK:
				/* Already handled */
				break;
			default:
				logInfo("Received unknown packet: CmdId " + packet.getCmdId());
				break;
		}
	}
	
	public void matchQueue(MxPacket ack){
		if(!outQueue.isEmpty()){
			int crcToCheck = ((MxPacket) outQueue.peek()).getCRC();
			int seqToCheck = ((MxPacket) outQueue.peek()).getSeq();
			
			int ackCrc = ack.getData()[0] & 0xFF;
			int ackSeq = ack.getData()[1] & 0x0F;
			
			if(crcToCheck == ackCrc && seqToCheck == ackSeq){
				logInfo("Ack match.");
				outQueue.poll();
				ackPending = false;
			}
			else
				logInfo("Ack without match");
		}
	}
	
	public void logInfo(String str){
		logger.log(LogLevel.INFO, this.toString() + ": " + str + ".");
	}
}
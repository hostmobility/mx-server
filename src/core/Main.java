package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import logger.LogLevel;
import logger.Logger;
import database.DBHandler;

/**
 * The main functionality of the server lies here.
 * @author jay
 *
 */
public class Main {
	
	/* Used to identify sockets */
	private static int connectionCount = 0;
	
	private static final int MAX_CONNECTIONS = 5000;
	private static final int portNumber = 40002;
	
	/* Serve at most this many simultaneous connections */
	private ExecutorService clientService = Executors.newFixedThreadPool(MAX_CONNECTIONS);
	private Logger logger;
	private DBHandler dbhandler;
	
	public static void main(String[] args){
		Main main = new Main();
		main.listen();
	}
	
	private Main(){
		init();
	}
	
	private void init(){
		connectionCount = 0;
		
		/* Log to a file
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd__HH-mm");
		String logFile = "server_log_" + dateFormat.format(date);
		logger = new Logger(LogLevel.INFO, logFile, true); 
		*/
		
		/* Log to stdout/stderr */
		logger = new Logger(LogLevel.DEBUG, "", true);
		
		/* Provides db clearing / rebuilding functions */
		dbhandler = new DBHandler(logger);
		// dbhandler.clearDB();
		// dbhandler.createDB();
	}
	
	private void listen(){
		ServerSocket ss = null;
		try{
			ss = new ServerSocket(portNumber);
			logger.log(LogLevel.INFO, "Listening...");
			while(true){
				Socket clientConnection = ss.accept();
				ClientSocket clientSocket = 
						new ClientSocket(clientConnection, connectionCount, logger);
				if(clientSocket.init()){
					try{
						clientService.submit(clientSocket);
						logger.log(LogLevel.DEBUG, "Accepted connection #"+
								connectionCount++ + ".");
					}
					catch(RejectedExecutionException ree){
						logger.log(LogLevel.WARNING, ree.getMessage());
						clientSocket.closeSocket();
					}
				}
			}
		}
		catch(IOException ioe){
			logger.log(LogLevel.FATAL, "IOException opening listener socket.");
		}	
		finally{
			logger.log(LogLevel.INFO, "Shutting down.");
			clientService.shutdown();
			try{
				if(ss!=null) ss.close();
			}
			catch(IOException ioe){
				logger.log(LogLevel.FATAL, "IOException closing listener socket.");
			}
		}
	}
}

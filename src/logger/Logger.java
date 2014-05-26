package logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logs activity to a file or standard system streams.
 * @author jay
 *
 */
public class Logger {
	
	public LogLevel level;
	public String filepath;
	
	private PrintWriter print;
	private boolean careful;
	
	/**
	 * Creates a logger with the specified LogLevel threshold and file to write to.
	 * @param level Logging threshold.
	 * @param filepath File to write to.
	 * @param careful Whether to keep re-initializing the writer (inefficient).
	 */
	public Logger(LogLevel level, String filepath, boolean careful){
		this.level = level;
		this.filepath = filepath;
		if(filepath==null) this.filepath = "";
		this.careful = careful;
		
		/* Only initiate PrintWriter once for efficiency. */
		if(!careful && !filepath.equals("")){
			try{
				print = new PrintWriter(
						new BufferedWriter(new FileWriter(filepath, true)));
			}
			catch(IOException ioe){
				System.err.println("WARNING: Failed to initialize logger.");
			}
		}
	}
	
	/**
	 * Creates a file-less logger with the specified LogLevel threshold.
	 * @param level Logging threshold.
	 * @param careful Whether to keep re-initializing the write (inefficient).
	 */
	public Logger(LogLevel level, boolean careful){
		this(level,"",true);
	}
	
	/**
	 * Creates a file-less logger with a LogLevel.INFO threshold.
	 * @param careful Whether to keep re-initializing the write (inefficient).
	 */
	public Logger(boolean careful){
		this(LogLevel.INFO,"",careful);
	}
	
	/**
	 * Creates a file-less logger with a LogLevel.INFO threshold.
	 */
	public Logger(){
		this(LogLevel.INFO,"",true);
	}
	
	/**
	 * Logs a message if its importance matches of exceeds the
	 * current logging threshold. Falls back to system streams if 
	 * writing to file fails.
	 * @param level The message's category.
	 * @param message The message to log.
	 */
	public synchronized void log(LogLevel level, String message){
		if(level.ordinal() >= this.level.ordinal()){
			String format = String.format("%s: %s", level.toString(),message);
			if(!filepath.equals("")){
				try{
					if(careful) print = new PrintWriter(
							new BufferedWriter(new FileWriter(filepath, true)));
					print.println(format);
					if(careful) print.close();
				}
				catch(IOException ioe){
					if(level.ordinal() >= LogLevel.WARNING.ordinal())
						System.err.println(format);
					else
						System.out.println(format);
				}
			}
			else{
				if(level.ordinal() >= LogLevel.WARNING.ordinal())
					System.err.println(format);
				else
					System.out.println(format);
			}
		}
	}
	
	/**
	 * SHALL be called upon end of use if careful is set to false. 
	 * Careful is true by default.
	 */
	public void dispose(){
		if(print!=null){
			print.close();
		}
	}
}

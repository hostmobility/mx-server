package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import logger.LogLevel;
import logger.Logger;

/**
 * Handles all DB interaction. Security is minimal - use only for testing purposes.
 * @author jay
 *
 */
public class DBHandler {
	
	private static final String DB_DRIVER = "jdbc:postgresql://";
	private static final String DB_URL = "localhost";
	private static final String DB_NAME = "mxdb";
	private static final String USERNAME = "mxmini";
	private static final String PASSWORD = "mxmini";
	private static final String ROOTUSER = "";
	private static final String ROOTPASS = "";
	
	private Logger logger;
	
	public DBHandler(Logger logger){
		this.logger = logger;
		if(logger==null) this.logger = new Logger();
	}
	
	/**
	 * Create the DB.
	 */
	public void createDB(){
		Connection c = null;
		Statement s = null;
		try{
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + "?user=" + ROOTUSER + 
					"&password=" + ROOTPASS);
			s = c.createStatement();
			s.executeUpdate("CREATE DATABASE " + DB_NAME);
		}
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING, 
					"createDB: SQL exception: " + sqle.toString());
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
		if(c != null) try{
			c.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
	}
	
	/**
	 * Delete the DB
	 * @param suppressErrors Whether to suppress errors.
	 */
	public void deleteDB(boolean suppressErrors){
		Connection c = null;
		Statement s = null;
		try{
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + "?user=" + ROOTUSER + 
							"&password=" + ROOTPASS);
			s = c.createStatement();
			s.executeUpdate("DROP DATABASE " + DB_NAME);
		}
		catch(SQLException e){
			if(!suppressErrors)
				logger.log(LogLevel.WARNING,
						"deleteDB: SQL exception: " + e.toString());
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
		if(c != null) try{
			c.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
	}
	
	/**
	 * Delete the DB, without suppressing warnings.
	 */
	public void deleteDB(){
		deleteDB(false);
	}
	
	/**
	 * Recreate DB through its deletion and recreation.
	 */
	public void hardDBReset(){
		deleteDB();
		createDB();
		createTables();
	}
	
	/**
	 * Recreate DB through table deletion and recreation.
	 */
	public void softDBReset(){
		dropTables();
		createTables();
	}
	
	/**
	 * Create DB tables.
	 */
	public void createTables(){
		try{
			/* Customize for your DB... */
			Connection c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			Statement stmt = c.createStatement();
		    
			String makeSeq = "CREATE SEQUENCE rtg_id_seq";
			
			stmt.executeUpdate(makeSeq);
			
		    String makeTable = "CREATE TABLE rtg (" +
		    				  "id integer NOT NULL DEFAULT " +
		    				  		"nextval((\'\"rtg_id_seq\"\'::text) " +
		    				  		"::regclass), " +
		    				  "timestamp_server timestamp without time zone, " +
		    				  "box_id character varying(15), " +
		    				  "digital_in integer, " +
		    				  "digital_out smallint, " +
		    				  "main_voltage real, " +
		    				  "liion_voltage real, " +
		    				  "temp_1 real, " +
		    				  "temp_2 real, " +
		    				  "analog_1 real, " +
		    				  "analog_2 real, " +
		    				  "gps_lat double precision, " +
		    				  "gps_long double precision, " +
		    				  "gps_speed real, " +
		    				  "gps_dir real, " +
		    				  "gps_fix smallint, " +
		    				  "gps_sat_info smallint, " +
		    				  "timestamp_sample timestamp " +
		    				  		"without time zone, " +
		    				  "timestamp_last_valid timestamp " +
		    				  		"without time zone, " +
		    				  "text text, " +
		    				  "fms_status smallint, " +
		    				  "obd_status smallint, " +
		    				  "total_fuel real, " +
		    				  "total_distance real, " +
		    				  "CONSTRAINT id_rtg_prim_key " +
		    				  		"PRIMARY KEY (id )) " +
		    				  "WITHOUT OIDS;";
		    				  
		    stmt.executeUpdate(makeTable);
		}
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING, 
					"createTables: SQL exception: " + sqle.toString());
		}
	}
	
	/**
	 * Clear DB.
	 */
	public void dropTables(){
		/* Customize for your DB... */
		dropTable("rtg");
		dropSequence("rtg_id_seq");
	}
	
	/**
	 * Drops a single table.
	 * @param tableName Name of table to be dropped.
	 */
	public void dropTable(String tableName){
		Connection c = null;
		Statement s = null;
		try{
			/* Customize for your DB... */
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			s = c.createStatement();
		    
		    String sql = "DROP TABLE " + tableName;
		    s.executeUpdate(sql);
		}
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING,
					"dropTable: SQL exception: " + sqle.toString());
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
	}
	
	/**
	 * Drops a single sequence.
	 * @param seqName Name of sequence to be dropped.
	 */
	public void dropSequence(String seqName){
		Connection c = null;
		Statement s = null;
		try{
			/* Customize for your DB... */
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			s = c.createStatement();
		    
		    String sql = "DROP SEQUENCE " + seqName;
		    s.executeUpdate(sql);
		}
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING,
					"dropSequence: SQL exception: " + sqle.toString());
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
	}
	
	/**
	 * Clears all DB entries.
	 */
	public void clearDB(){
		Connection c = null;
		Statement s = null;
		try{
			/* Customize for your DB... */
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			s = c.createStatement();
		    
			String sql = "DELETE FROM rtg";
			s.executeUpdate(sql);
			
			sql = "DELETE from units";
			s.executeUpdate(sql);
			
			sql = "DELETE from sqllist";
			s.executeUpdate(sql);
		}
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING,
					"clearDB: SQL exception: " + sqle.toString());
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
		if(s != null) try{
			s.close();
		}
		catch(SQLException sqle){
			// Swallow...
		}
	}
	
	/**
	 * Test function.
	 */
	public void printDB(){
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		try{
			/* Customize for your DB... */
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			s = c.createStatement();
			rs = s.executeQuery( "SELECT * FROM rtg") ;
			while(rs.next()){
				System.out.println("*** DB CONTENT: " + rs.getString("box_id"));
			}
		}
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING,
					"printDB: SQL exception: " + sqle.toString());
		}
		finally{
			if(rs != null) try{
				rs.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
			if(s != null) try{
				s.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
			if(c != null) try{
				c.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
		}
	}
	
	/**
	 * Test function.
	 * @param data Data to insert in db.
	 */
	public void insertTestData(String data){
		Connection c = null;
		PreparedStatement ps = null;
		try{
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			String prepSQL = "INSERT INTO rtg (box_id) " +
					"VALUES (?)";
			ps = c.prepareStatement(prepSQL);
			ps.setString(1, data);
			ps.executeUpdate();
		}	
		catch(SQLException sqle){
			logger.log(LogLevel.WARNING,
					"insertTestData: SQL exception: " + sqle.toString());
		}
		finally{
			if(ps != null) try{
				ps.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
			if(c != null) try{
				c.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
		}
	}
	
	public static synchronized void insertGpsData(String box_id, float latitude, float longitude,
			float speed, float direction, short fix, short sat, 
			Timestamp timestampSample){
		Connection c = null;
		PreparedStatement ps = null;
		try{
			c = DriverManager.getConnection
					(DB_DRIVER + DB_URL + "/" + DB_NAME, USERNAME, PASSWORD); 
			String prepSQL = "INSERT INTO rtg (box_id, " +
					"gps_lat, gps_long, gps_speed, " +
					"gps_dir, gps_fix, gps_sat_info, " +
					"timestamp_sample) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			ps = c.prepareStatement(prepSQL);
			ps.setString(1, box_id);
			ps.setFloat(2,  latitude);
			ps.setFloat(3, longitude);
			ps.setFloat(4, speed);
			ps.setFloat(5, direction);
			ps.setShort(6, fix);
			ps.setShort(7, sat);
			ps.setTimestamp(8, timestampSample);
			ps.executeUpdate();
		}	
		catch(SQLException sqle){
			System.err.println("insertGpsData: SQL exception: " + sqle.toString());
		}
		finally{
			if(ps != null) try{
				ps.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
			if(c != null) try{
				c.close();
			}
			catch(SQLException sqle){
				// Swallow...
			}
		}
	}
}
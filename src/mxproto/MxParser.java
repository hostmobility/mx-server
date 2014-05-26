package mxproto;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.ByteUtils;
import core.ClientSocket;
import database.DBHandler;

/**
 * Used to handle various types of incoming MX commands.
 * @author jay
 *
 */
public class MxParser {
	public static void handleGpsData(ClientSocket cs, byte[] data, int arrayOffset){
		//data[0 + arrayOffset]; // Timestamp
		//data[1 + arrayOffset]; // Timestamp
		//data[2 + arrayOffset]; // Timestamp
		//data[3 + arrayOffset]; // Timestamp
		//logInfo("Timestamp[0] => " + ByteUtils.make8(data[0 + arrayOffset]));
		//logInfo("Timestamp[1] => " + ByteUtils.make8(data[1 + arrayOffset]));
		//logInfo("Timestamp[2] => " + ByteUtils.make8(data[2 + arrayOffset]));
		//logInfo("Timestamp[3] => " + ByteUtils.make8(data[3 + arrayOffset]));
		//logInfo("TimestampInt => " + ByteUtils.make32(data[0 + arrayOffset], data[1 + arrayOffset], 
		//	data[2 + arrayOffset], data[3 + arrayOffset]));
		int timestampInt = ByteUtils.make32(data[0 + arrayOffset], data[1 + arrayOffset], data[2 + arrayOffset], data[3 + arrayOffset]);
    	
    	int second = timestampInt & 0x3f; /* 6 bits */
    	timestampInt = timestampInt >> 6;
    	
    	int minute = timestampInt & 0x3f; /* 6 bits */
    	timestampInt = timestampInt >> 6;
    	
    	int hour = timestampInt & 0x1f; /* 5 bits */
    	timestampInt = timestampInt >> 5;
    	
    	int day = timestampInt & 0x1f;  /* 5 bits */
    	timestampInt = timestampInt >> 5;
    	
    	int month = timestampInt & 0x0f; /* 4 bits */
    	timestampInt = timestampInt >> 4;
    	
    	int year = timestampInt & 0x1f; /* 5 bits */
    	timestampInt = timestampInt >> 5;

    	//int valid = timestampInt & 0x01; /* 1 bit - unused? */ 
    	
    	String ddmmyy;

    	if(day < 10) {
    		ddmmyy = "0" + day;
    	} else {
    		ddmmyy = "" + day;
    	}

    	if(month < 10) {
    		ddmmyy += "0" + month;
    	} else {
    		ddmmyy += month;
    	}

    	if(year < 10) {
    		ddmmyy += "0" + year;
    	} else {
    		ddmmyy += year;
    	}

    	String hhmmss;

    	if(hour < 10) {
    		hhmmss = "0" + hour;
    	} else {
    		hhmmss = "" + hour;
    	}

    	if(minute < 10) {
    		hhmmss += "0" + minute;
    	} else {
    		hhmmss += minute;
    	}

    	if(second < 10) {
    		hhmmss += "0" + second;
    	} else {
    		hhmmss+= second;
    	}
    	
    	DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
    	Date date = null;
    	try{
    		date = dateFormat.parse(ddmmyy + hhmmss);
    	}
    	catch(ParseException pe){
    		// Swallow
    	}
    	
    	Timestamp timestampSample;
    	if(date!=null)
    		timestampSample = new Timestamp(date.getTime());
    	else
    		timestampSample = new Timestamp(0);
    	
		//data[4 + arrayOffset]; // Timestamp fraction
		//data[5 + arrayOffset]; // Timestamp fraction
		//data[6 + arrayOffset]; // Timestamp last valid
		//data[7 + arrayOffset]; // Timestamp last valid
		//data[8 + arrayOffset]; // Timestamp last valid
		//data[9 + arrayOffset]; // Timestamp last valid
		//float lastValidTimestamp = Float.intBitsToFloat(
		//		ByteUtils.make32(data[6 + arrayOffset], data[7 + arrayOffset], 
    	//	data[8 + arrayOffset], data[9 + arrayOffset]));
		//data[10 + arrayOffset]; // Fix valid
		short fix = (short) ByteUtils.make8(data[10 + arrayOffset]);
		//data[11 + arrayOffset]; // Latitude
		//data[12 + arrayOffset]; // Latitude
		//data[13 + arrayOffset]; // Latitude
		//data[14 + arrayOffset]; // Latitude
		//logInfo("Lat[0] => " + ByteUtils.make8(data[11 + arrayOffset]));
		//logInfo("Lat[1] => " + ByteUtils.make8(data[12 + arrayOffset]));
		//logInfo("Lat[2] => " + ByteUtils.make8(data[13 + arrayOffset]));
		//logInfo("Lat[3] => " + ByteUtils.make8(data[14 + arrayOffset]));
		//logInfo("LatInt => " + ByteUtils.make32(data[11 + arrayOffset], data[12 + arrayOffset], 
		//	data[13 + arrayOffset], data[14 + arrayOffset]));
		int latInt = ByteUtils.make32(data[11 + arrayOffset], data[12 + arrayOffset], 
				data[13 + arrayOffset], data[14 + arrayOffset]);
		DecimalFormat dec6 = new DecimalFormat("##.######");
		dec6.setRoundingMode(RoundingMode.HALF_UP);
		float latitude = (float) latInt / 1000000.0f;
		latitude = Float.parseFloat(dec6.format(latitude));
		//data[15 + arrayOffset]; // Longitude
		//data[16 + arrayOffset]; // Longitude
		//data[17 + arrayOffset]; // Longitude
		//data[18 + arrayOffset]; // Longitude
		//logInfo("Long[0] => " + ByteUtils.make8(data[15 + arrayOffset]));
		//logInfo("Long[1] => " + ByteUtils.make8(data[16 + arrayOffset]));
		//logInfo("Long[2] => " + ByteUtils.make8(data[17 + arrayOffset]));
		//logInfo("Long[3] => " + ByteUtils.make8(data[18 + arrayOffset]));
		//logInfo("LongInt => " + ByteUtils.make32(data[15 + arrayOffset], data[16 + arrayOffset], 
		//	data[17 + arrayOffset], data[18 + arrayOffset]));
		int longInt = ByteUtils.make32(data[15 + arrayOffset], data[16 + arrayOffset], 
				data[17 + arrayOffset], data[18 + arrayOffset]);
		float longitude = (float) longInt / 1000000.0f;
		longitude = Float.parseFloat(dec6.format(longitude));
		//data[19 + arrayOffset]; // Velocity
		//data[20 + arrayOffset]; // Velocity
		float velocity = (float) ByteUtils.make16(data[19 + arrayOffset], data[20 + arrayOffset]);
		//data[21 + arrayOffset]; // Direction
		//data[22 + arrayOffset]; // Direction
		float direction = (float) ByteUtils.make16(data[21 + arrayOffset], data[22 + arrayOffset]);
		//data[23 + arrayOffset]; // Satellites used
		short satellites = (short) ByteUtils.make8(data[23 + arrayOffset]);
		//data[24 + arrayOffset]; // Pdop
		//data[25 + arrayOffset]; // Hdop
		//data[26 + arrayOffset]; // Vdop
		//data[27 + arrayOffset]; // Altitude
		//data[28 + arrayOffset]; // Altitude
		//data[29 + arrayOffset]; // Altitude
		//data[30 + arrayOffset]; // Altitude
		//data[31 + arrayOffset]; // Ids in fix
		//data[32 + arrayOffset]; // Ids in fix
		//data[33 + arrayOffset]; // Ids in fix
		//data[34 + arrayOffset]; // Ids in fix
		
		cs.logInfo(String.format("Box Id: %s\n"
				+ "Latitude: %f\n"
				+ "Longitude: %f\n"
				+ "Velocity: %f\n"
				+ "Direction: %f\n"
				+ "Fix: %d\n"
				+ "SatInfo: %d", 
				"Test Box", latitude, longitude, velocity, 
				direction, fix, satellites));

		DBHandler.insertGpsData("Test Box", 
				latitude, longitude, velocity, 
				direction, fix, satellites, timestampSample);
		
		cs.logInfo("GPS data saved.");
	}
}

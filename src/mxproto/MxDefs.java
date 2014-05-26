package mxproto;

public abstract class MxDefs {
	
	/* Sizedefs */
	public final static int HEADER_SIZE = 6;
	public final static int PACKET_MIN_SIZE = HEADER_SIZE;
	public final static int PACKET_MAX_SIZE = 128;
	
	/* Packet layout */
	public final static int LOM_HIGH_OFFSET = 0; // Unused at the moment (just zero).
	public final static int LOM_LOW_OFFSET = 1;
	public final static int FLAGS_SEQ_OFFSET = 2;
	public final static int SRC_OFFSET = 3;
	public final static int DST_OFFSET = 4;
	public final static int CRC_OFFSET = 5;
	public final static int CMD_OFFSET = 6;
	public final static int DATA_OFFSET = CMD_OFFSET + 1; // + 1 from CMD_ID
	
	/* Status */
	public final static int STATUS_OK               	= 0x01;
	public final static int STATUS_DATA_NOT_READY     	= 0x02;
	public final static int STATUS_CMD_NOT_SUPPORTED  	= 0x03;
	public final static int STATUS_DATA_NOT_SUPPORTED	= 0x04;
	public final static int STATUS_SENSOR_FAULT       	= 0x05;
	public final static int STATUS_OUT_OF_RANGE       	= 0x06;
	
	/* Error types */
	public final static int ERROR_CHECKSUM           	= 0x01;
	public final static int ERROR_LENGTH              	= 0x02;
	
	/* Functions */
	public final static int FUNC_NONE           		= 0x00;
	public final static int FUNC_APPLICATION    		= 0x01;
	public final static int FUNC_PIC            		= 0x02;
	public final static int FUNC_CAN            		= 0x03;
	public final static int FUNC_GPIO           		= 0x04;
	public final static int FUNC_ANALOG         		= 0x05;
	public final static int FUNC_GPS            		= 0x06;
	public final static int FUNC_MEMORY         		= 0x07;
	public final static int FUNC_TILT           		= 0x08;
	public final static int FUNC_AT						= 0x09;
	
	/* Commands */
	public final static int CMD_READ                	= 0x01;
	public final static int CMD_WRITE               	= 0x02;
	public final static int CMD_SUBSCRIBE           	= 0x03;
	public final static int CMD_UNSUBSCRIBE         	= 0x04;
	public final static int CMD_FUNCTION            	= 0x05;

	public final static int CMD_EVENT               	= 0x10;
	public final static int CMD_ACK						= 0x11;
	
	public final static int CMD_READ_REPLY          	= (0x80 | CMD_READ);
	public final static int CMD_WRITE_REPLY         	= (0x80 | CMD_WRITE);
	public final static int CMD_SUBSCRIBE_REPLY    		= (0x80 | CMD_SUBSCRIBE);
	public final static int CMD_UNSUBSCRIBE_REPLY   	= (0x80 | CMD_UNSUBSCRIBE);
	public final static int CMD_FUNCTION_REPLY      	= (0x80 | CMD_FUNCTION);

	public final static int CMD_ERROR            		= 0xF0;
	
	/* ***************************** TYPES **************************** */
	
	/* PIC */
	public final static int PIC_SW_VERSION          	= 0x0001;
	public final static int PIC_RESTART_CAUSE       	= 0x0002;
	public final static int PIC_MODEM_RESTART_CAUSE 	= 0x0003;
	public final static int PIC_IRQ_CAUSE           	= 0x0004;
	public final static int PIC_CHARGE_ENABLED      	= 0x0005;
	public final static int PIC_CHARGE_STATE        	= 0x0006;
	public final static int PIC_BL_VERSION          	= 0x0007;
	public final static int PIC_DEVICE_ID           	= 0x0008;

	public final static int PIC_SERIAL_NUMBER       	= 0x000A;

	public final static int PIC_TEMP_DIGIN          	= 0x0010;
	public final static int PIC_WATCHDOG_CONFIGURE  	= 0x0011;
	public final static int PIC_LED1_FUNCTION       	= 0x0012;
	public final static int PIC_LED2_FUNCTION       	= 0x0013;
	public final static int PIC_LED3_FUNCTION       	= 0x0014;
	public final static int PIC_GPS_UPDATE_RATE     	= 0x0015;

	public final static int LED_PIC_POWER_FUNCTION       = 0x00;
	public final static int LED_GSM_SYNC_FUNCTION        = 0x01;
	public final static int LED_GPS_STATUS_FUNCTION      = 0x02;
	public final static int LED_CAN_STATUS_FUNCTION      = 0x03;
	public final static int LED_JAVA_CONTROLLED_FUNCTION = 0x20;

	/* #define PIC_PERIODIC_WAKEUP     0x0020 */
	public final static int PIC_TEMP1_THRESHOLD        	= 0x0021;
	public final static int PIC_TEMP2_THRESHOLD        	= 0x0022;
	public final static int PIC_INPUT_V_THRESHOLD      	= 0x0023;
	public final static int PIC_BATTERY_V_THRESHOLD    	= 0x0024;
	/* #define PIC_POSITION_THRESHOLD     0x0025 */
	/* #define PIC_VELOCITY_THRESHOLD     0x0026 */
	public final static int PIC_LI_ION_V_THRESHOLD     	= 0x0027;
	public final static int PIC_INPUT_V_LOSS           	= 0x0028;
	public final static int PIC_SLEEP_TIME_MINUTES     	= 0x002A;

	public final static int PIC_IRQ_SOURCES            	= 0x0030;
	public final static int PIC_SLEEP                  	= 0x0031;
	public final static int PIC_RESET_WATCHDOG         	= 0x0032;
	public final static int PIC_BATT_OK_STATUS         	= 0x0033;
	public final static int PIC_MEASURE_BATTERY        	= 0x0034;
	/* #define PIC_SLEEP_REFRESH_TIME     0x0035 */
	/* #define PIC_SLEEP_REFRESH_RUN_TIME 0x0036 */

	public final static int PIC_START_UPGRADE       	= 0x0040;
	public final static int PIC_DATA_UPGRADE        	= 0x0041;
	public final static int PIC_FINISH_UPGRADE      	= 0x0042;
	public final static int PIC_ABORT_UPGRADE       	= 0x0043;

	public final static int PIC_UPGRADE_PING        	= 0x0045;

	public final static int PIC_GPS_STATISTICS      	= 0x0050;
	public final static int PIC_MODEM_STATISTICS    	= 0x0051;
	public final static int PIC_CAN_STATISTICS      	= 0x0052;

	public final static int PIC_RESET               	= 0x0080;

	public final static int PIC_MOTION_DETECT_ACTIVE_HYSTERESIS    	= 0x0090; /* 1 = 100msec */
	public final static int PIC_MOTION_DETECT_INACTIVE_HYSTERESIS  	= 0x0091; /* 1 = 100msec */
	public final static int PIC_TILT_XYZ_MAXIMUM_PUSH_RATE         	= 0x0092; /* 1 = 10ms */
	public final static int PIC_TILT_MOTION_THRESHOLD              	= 0x0093;
	public final static int PIC_TILT_MOTION_MAX_SINGLE_THRESHOLD  	= 0x0094;
	public final static int PIC_TILT_MOTION_DETECT_THRESHOLD      	= 0x0095;

	public final static int PIC_CAN_MODE              	= 0x0100;
	public final static int CAN_CONFIG_MODE_PGN         = 0x0001;
	public final static int CAN_CONFIG_MODE_RAW         = 0x0002;

	/* GPIO */
	public final static int GPIO_DIG_IN_ALL         	= 0x0001;
	public final static int GPIO_DIG_IN_1           	= 0x0002;
	public final static int GPIO_DIG_IN_2           	= 0x0003;
	public final static int GPIO_DIG_IN_3           	= 0x0004;
	public final static int GPIO_DIG_IN_4           	= 0x0005;
	public final static int GPIO_ACC                	= 0x0006;
	public final static int GPIO_DIG_OUT_1          	= 0x0011;
	public final static int GPIO_DIG_OUT_2          	= 0x0012;
	public final static int GPIO_BUZZER             	= 0x0013;
	public final static int GPIO_LED1               	= 0x0014;
	public final static int GPIO_LED2               	= 0x0015;
	public final static int GPIO_LED3               	= 0x0016;
	public final static int GPIO_F2M                	= 0x0017;
	public final static int GPIO_PULSES_1           	= 0x0020;
	public final static int GPIO_PULSES_2           	= 0x0021;
	public final static int GPIO_INTERRUPT_PIC      	= 0x00F0;
	public final static int GPIO_INTERRUPT_MODEM    	= 0x00F1;

	/* GPS */
	public final static int GPS_POWERED             	= 0x0001;
	public final static int GPS_DATA                	= 0x0002;
	public final static int GPS_INFORMATION         	= 0x0003;
	public final static int GPS_NMEA                	= 0x0004;
	public final static int GPS_ANTENNA_SELECTION   	= 0x0005;

	/* ANALOG */
	public final static int ANALOG_INPUT_VOLTAGE            = 0x0001;
	public final static int ANALOG_BATTERY_VOLTAGE          = 0x0002;
	public final static int ANALOG_CHARGING_BATTERY_VOLTAGE = 0x0003;
	public final static int ANALOG_LI_ION_VOLTAGE           = 0x0004; /* MX-3 WP/A only */
	public final static int ANALOG_4_20_1_CURRENT           = 0x0005; /* MX-3 WP/A only */
	public final static int ANALOG_4_20_2_CURRENT           = 0x0006; /* MX-3 WP/A only */
	public final static int ANALOG_TEMP1                    = 0x0010;
	public final static int ANALOG_TEMP2                    = 0x0011;

	/* ACC */
	public final static int TILT_CURRENT_VALUES     	= 0x0001;
	public final static int TILT_SENSITIVITY_LEVEL  	= 0x0002;
	public final static int TILT_MOTION_STATE       	= 0x0003;
	public final static int TILT_PULSE_DETECTION    	= 0x0004;

	/* MEMORY */
	public final static int SD_STATUS                	= 0x0001;
	public final static int SD_OPEN_FILE             	= 0x0002;
	public final static int SD_CLOSE_FILE            	= 0x0003;
	public final static int SD_READ_FILE             	= 0x0004;
	public final static int SD_WRITE_FILE            	= 0x0005;
	public final static int SD_SEEK_FILE             	= 0x0006;
	public final static int SD_ERASE_FILE            	= 0x0007;
	public final static int SD_STATUS_SHORT          	= 0x0008;
	public final static int SD_STAT_FILE             	= 0x0009;
	public final static int SD_UNLINK_FILE           	= 0x000a;
	public final static int SD_UNMOUNT_DRIVE         	= 0x000b;
	public final static int SD_MOUNT_DRIVE           	= 0x000c;
	public final static int SD_MKDIR                 	= 0x000d;
	public final static int SD_RENAME_FILE           	= 0x000e;
	public final static int SD_POINTER_POS           	= 0x000f;
	public final static int SD_TEST_EOF              	= 0x0010;

	/* CAN */
	public final static int CAN_SETUP              		= 0x0001;
	public final static int CAN_STATUS             		= 0x0002;
	public final static int CAN_REGISTER_PGN       		= 0x0010;
	public final static int CAN_UNREGISTER_PGN     		= 0x0011;
	public final static int CAN_REGISTERED_PGNS    		= 0x0012;
	public final static int CAN_UNREGISTER_ALL_PGN 		= 0x0013;
	public final static int CAN_REGISTER_RAW       		= 0x0014;
	public final static int CAN_UNREGISTER_RAW     		= 0x0015;
	public final static int CAN_REGISTERED_RAWS    		= 0x0016;
	public final static int CAN_UNREGISTER_ALL_RAW 		= 0x0017;
	public final static int CAN_TRANSMIT_PGN       		= 0x0020;
	public final static int CAN_TRANSMIT_RAW       		= 0x0021;
}

package me.MnC.MnC_SERVER_MOD;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public final class Config
{
	static final Logger _log = Logger.getLogger("Minecraft");

	// database config
	public static String DATABASE_DRIVER;
	
	public static String DATABASE_URL;
	
	public static String DATABASE_LOGIN;
	
	public static String DATABASE_PASSWORD;
	
	// other configuration files
	public static String SHOP_CONFIG;
	
	
	//chat configuration
	public static long CHAT_ANNOUNCEMENTS_DELAY;
	public static String CHAT_ANNOUNCEMENTS_FILE;
	public static String CHAT_MOTD_FILE;
	
	//feedback file
	public static String FEEDBACK_FILE;
	
	//homes file
	public static String HOMES_FILE;
	
	//estates config
	public static int ESTATE_MAX_SIZE;
	public static int ESTATE_MAX_SIDE_SIZE;
	
	//basic world config
	public static String BW_BAN_REGIONS_CONFIG_FILE;
	public static String BW_BAN_REGIONS_DEVIATIONS_FILE;
	
	//optimization - mob limiter
	public static int MOB_LIMITER_RADIUS;
	public static int MOB_LIMITER_LIMIT;
	
	// Jail Manager
	public static int JAIL_SPAWN_X;
	public static int JAIL_SPAWN_Y;
	public static int JAIL_SPAWN_Z;
	public static int JAIL_BORDER_LEFT;
	public static int JAIL_BORDER_RIGHT;
	public static int JAIL_BORDER_TOP;
	public static int JAIL_BORDER_BOTTOM;
	
 	public static void load(String config_path)
	{
		_log.info("Loading config.");
		try{
			Properties config = new Properties();
			InputStream is = new FileInputStream(config_path);
			config.load(is);
			is.close();
			
			//database config
			DATABASE_DRIVER				= config.getProperty("DatabaseDriver", "com.mysql.jdbc.Driver");
			DATABASE_URL				= config.getProperty("DatabaseURL", "jdbc:mysql://localhost/mineandcraft");
			DATABASE_LOGIN				= config.getProperty("DatabaseLogin", "root");
			DATABASE_PASSWORD			= config.getProperty("DatabasePassword", "");

			//other config files
			SHOP_CONFIG					= config.getProperty("ShopConfigPath");

			//chat config
			CHAT_ANNOUNCEMENTS_DELAY 	= Long.parseLong(config.getProperty("ChatAnnouncementsDelay"));
			CHAT_ANNOUNCEMENTS_FILE		= config.getProperty("ChatAnnouncementsFile");
			CHAT_MOTD_FILE				= config.getProperty("ChatMotdFile");
			
			//feedback file
			FEEDBACK_FILE = config.getProperty("FeedbackFile","plugins/MineAndCraft_plugin/FeedbackFile.dat");
			
			//homes
			HOMES_FILE = config.getProperty("HomesFile","plugins/MineAndCraft_plugin/Homes.dat");
			
			//estates config
			try{
				ESTATE_MAX_SIZE = Integer.parseInt(config.getProperty("EstateMaxSize","2000"));
			}catch(Exception e){}
			try{
				ESTATE_MAX_SIDE_SIZE = Integer.parseInt(config.getProperty("EstateMaxSideSize","60"));
			}catch(Exception e){}
			
			//basic world config
			BW_BAN_REGIONS_CONFIG_FILE = config.getProperty("BasicWorldBanRegionsConfigFile", "plugins/MineAndCraft_plugin/BWBanRegions.dat");
			BW_BAN_REGIONS_DEVIATIONS_FILE = config.getProperty("BasicWorldBanRegionsDeviationsFile", "plugins/MineAndCraft_plugin/BWBanDeviations.dat");
			
			//optimization - mob limiter
			try{
				MOB_LIMITER_RADIUS = Integer.parseInt(config.getProperty("MobRemoverRadius","30"));
				MOB_LIMITER_LIMIT = Integer.parseInt(config.getProperty("MobRemoverLimit","100"));
			}catch(Exception e){}
			
			// Jail Manager
			JAIL_SPAWN_X = Integer.parseInt(config.getProperty("JailSpawnX","0"));
			JAIL_SPAWN_Y = Integer.parseInt(config.getProperty("JailSpawnY","62"));
			JAIL_SPAWN_Z = Integer.parseInt(config.getProperty("JailSpawnZ","0"));
			JAIL_BORDER_LEFT = Integer.parseInt(config.getProperty("JailBorderLeft","-25"));
			JAIL_BORDER_RIGHT = Integer.parseInt(config.getProperty("JailBorderRight","25"));
			JAIL_BORDER_BOTTOM = Integer.parseInt(config.getProperty("JailBorderBottom","-25"));
			JAIL_BORDER_TOP = Integer.parseInt(config.getProperty("JailBorderTop","25"));
		}
		catch(Exception e)
		{
			_log.severe("Failed to load config file '" + config_path + "':" + e.getMessage());
			return;
		}
		_log.info("Config loaded.");		
	}
	
	private Config(){}	//no instances
}

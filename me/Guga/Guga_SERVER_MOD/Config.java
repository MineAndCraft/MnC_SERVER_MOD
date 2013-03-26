package me.Guga.Guga_SERVER_MOD;

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
	
	
	public void load(String config_path)
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

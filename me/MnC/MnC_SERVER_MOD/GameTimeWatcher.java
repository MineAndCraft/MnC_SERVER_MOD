package me.MnC.MnC_SERVER_MOD;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GameTimeWatcher
{
	private class WatcherThread extends Thread
	{
		public void run()
		{
			GameTimeWatcher.broadcastTime();
			long current = System.currentTimeMillis();
			try{
				Thread.sleep(1800000 - (current % 1800000)); 
			}catch(Exception e){}
			while(true)
			{
				GameTimeWatcher.broadcastTime();
				try{
					Thread.sleep(1800000); //1000 * 60 * 30 - 30 minutes in milliseconds 
				}catch(Exception e){}
			}
		}
	}

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",Locale.ENGLISH);
	
	private static GameTimeWatcher _instance = null;
	
	private final WatcherThread _watcher;
	
	private GameTimeWatcher()
	{
		_instance = this;
		_watcher = new WatcherThread();
	}
	
	/**
	 * Create the instance and start the watcher thread
	 */
	public static void start()
	{
		if(_instance == null)
		{
			_instance = new GameTimeWatcher();
		}
		_instance._watcher.start();
	}
	
	/**
	 * Stop the watcher thread and destroy the instance
	 */
	public static void stop()
	{
		if(_instance == null)
			return;
		_instance._watcher.interrupt();
		_instance = null;
	}
	
	/**
	 * Broadcasts current time
	 */
	public static void broadcastTime()
	{
		Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE+"[TIME] "+dateFormat.format(new Date()));
	}
}

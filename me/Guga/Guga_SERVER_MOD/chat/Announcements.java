package me.Guga.Guga_SERVER_MOD.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import me.Guga.Guga_SERVER_MOD.Config;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.ChatColor;

public class Announcements
{
	private final ArrayList<String> messages = new ArrayList<String>();
	
	final Guga_SERVER_MOD plugin = Guga_SERVER_MOD.getInstance();
	
	private int taskID = 0;
	
	private int position = 0;
	
	public Announcements()
	{
		loadAnnouncements();
		if(messages.size() == 0)
		{
			plugin.log.info("[Chat] Announcements file is empty, no annoncing will take place.");
			return;
		}
		start();
	}
	
	public void start()
	{
		long ticks = Config.CHAT_ANNOUNCEMENTS_DELAY/50l; // one tick is 1/20 s => 50 ms is one tick
		taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run()
			{
				if (messages.size() > 0)
				{
					if (!(position < messages.size()))
					{
						position = 0;
					}
					ChatHandler.broadcast(ChatColor.LIGHT_PURPLE + messages.get(position));
					position++;
				}
			}
		}, ticks, ticks);
	}
	
	public void stop()
	{
		if(taskID!=0)
			plugin.getServer().getScheduler().cancelTask(taskID);
	}
	
	public void loadAnnouncements()
	{
		plugin.log.info("[Chat] Loading Announcements.");
		messages.clear();
		File announcementsFile = new File(Config.CHAT_ANNOUNCEMENTS_FILE);
		try {
			if(!announcementsFile.exists())
			{
				announcementsFile.createNewFile();
				return;
			}
			
			FileInputStream in = new FileInputStream(announcementsFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				messages.add(line);
			}
			reader.close();
			in.close();
		} catch (Exception e) {
			plugin.log.warning("[Chat] Failed to load Announcements.");
		}
		plugin.log.info("[Chat] Announcements loaded.");
	}
}

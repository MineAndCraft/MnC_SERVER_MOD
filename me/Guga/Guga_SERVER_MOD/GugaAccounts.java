package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Statement;

public class GugaAccounts 
{
	GugaAccounts (Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	
	public boolean LoginUser(Player p,String password)
	{
		boolean logged = false;
		try
		{
			Statement stat = plugin.dbConfig.getConection().createStatement();
			ResultSet result = stat.executeQuery("SELECT count(*),id FROM `"+ plugin.dbConfig.getName()+"`.`mnc_users` WHERE username_clean='"+p.getName().toLowerCase()+"' AND password='"+Util.sha1(password)+"' LIMIT 1;");
			result.next();
			int count = result.getInt(1);
			int id = result.getInt(2);
			stat.close();
			if(count == 1)
			{
				logged = true;
				stat = plugin.dbConfig.getConection().createStatement();
				stat.executeQuery("UPDATE `mnc_users` SET `lastlogin` = NOW() WHERE `id`='"+String.valueOf(id)+"';");
				stat.close();
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		if(logged)
		{
			loggedUsers.add(p.getName());
			return true;
		}
		return false;
	}

	public boolean UserIsLogged(Player p)
	{
		 return loggedUsers.contains(p.getName());
	}
	
	public boolean UserIsLogged(String playerName)
	{
		return loggedUsers.contains(playerName);
	}
	
	public boolean UserIsRegistered(Player p)
	{
		try
		{
		    Statement stat = plugin.dbConfig.getConection().createStatement();
		    ResultSet result = stat.executeQuery("SELECT count(*) FROM `"+plugin.dbConfig.getName()+"`.`mnc_users` WHERE username_clean='"+p.getName().toLowerCase()+"' LIMIT 1;");
		    result.next();
		    int count = result.getInt(1);
		    stat.close();
		    if(count == 1)
		    	return true;
		    else
		    	return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public void StartTpTask()
	{
		if(running)
			return;

		taskId = plugin.scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable(){
		Iterator<String> it;
		String playerName;
			public void run()
			{
				if(tpTask.isEmpty())
					StopTpTask();
				
				it = tpTask.iterator();
				while(it.hasNext())
				{
					playerName = it.next();
					Player p = plugin.getServer().getPlayer(playerName);
					if(p==null)
					{
						tpTask.remove(playerName);
						return;
					}
					if(UserIsLogged(playerName))
					{
						tpTask.remove(playerName);
						return;
					}
					
					p.teleport(playerStart.get(playerName));
					if (UserIsRegistered(p))
					{
						p.sendMessage("Nejste prihlasen! Pro prihlaseni napiste "+ChatColor.YELLOW+" /login VaseHeslo"+ChatColor.WHITE+"!");
						if(playerKickTime.get(playerName) < System.currentTimeMillis())
						{
							p.kickPlayer("Nestihl(a) jste se prihlasit do 30 sekund!");
						}
					}
					else
					{
						p.sendMessage(ChatColor.YELLOW + "Registrujte se na webu - odkaz: " + ChatColor.RED + "http://mineandcraft.cz/navod-na-pripojeni/");
					}
				}
			}
		}, 100, 100);
		running = true;
	}
	
	private int taskId;
	private boolean running = false;
	
	public void StopTpTask()
	{
		if(!running)
			return;
		plugin.scheduler.cancelTask(taskId);
		running = false;
	}
	
	public void SetStartLocation(Player p,Location newLoc)
	{
		playerStart.put(p.getName(), newLoc);
	}
	
	// *********************************START LOCATION*********************************
	public HashMap<String, Location> playerStart = new HashMap<String, Location>(); // <playerName, location>
	public HashMap<String, Long> playerKickTime = new HashMap<String, Long>();
	public ArrayList<String> tpTask = new ArrayList<String>();  // <pName>

	
	
	// *********************************PLAYERS ONLINE*********************************
	public  ArrayList<String> loggedUsers = new ArrayList<String>();
	
	public static Guga_SERVER_MOD plugin;
}
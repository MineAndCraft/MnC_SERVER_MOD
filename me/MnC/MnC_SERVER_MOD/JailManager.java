package me.MnC.MnC_SERVER_MOD;
/*
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

/*
public class JailManager
{
	private static final Logger _log = Logger.getLogger(JailManager.class.getName());
	
	private World _world;
	
	private Map<String,Long> _jailExpirations = new HashMap<String,Long>();
	
	private String _worldName;
	
	public JailManager(String jailWorldName)
	{
		_worldName = jailWorldName;
	}
	
	public void load()
	{
		_world = Bukkit.getServer().getWorld(_worldName);
		if(_world == null)
		{
			//TODO make this use skyblock environment by default
			_world = Bukkit.getServer().createWorld(WorldCreator.name(_worldName).environment(Environment.NORMAL));
		}
		_world.setSpawnLocation(Config.JAIL_SPAWN_X, Config.JAIL_SPAWN_Y, Config.JAIL_SPAWN_Z);
		_log.info("JailManager sucessfully loaded.");
	}
	
	public void withdraw(Player player)
	{
		if(_jailExpirations.remove(player.getName().toLowerCase()) != null)
		{
			_log.info("[Jail] Player "+player.getName()+" has left jail.");
			player.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
		}
	}
	
	public void put(OfflinePlayer player,Double time)
	{
		// Admin can't be jailed
		if(GameMasterHandler.IsAdmin(player.getName()))
		{
			return;
		}
		
		_log.info("[Jail] Player "+player.getName()+" was jailed for "+time+" hours.");
		long expiration = System.currentTimeMillis()+ (long)(time*3600000);
		_jailExpirations.put(player.getName().toLowerCase(), expiration);
		if(player.isOnline())
		{
			Player p = (Player)player; 
			p.teleport(_world.getSpawnLocation());
			p.sendMessage(ChatColor.YELLOW+"You have been jailed until "+new Date(expiration)+".\n Get more info with /jailinfo");		}
	}
	
	public boolean isJailed(Player player)
	{
		return getJailExpiration(player) > 0;
	}
	
	public long getJailExpiration(Player player)
	{
		Long expiration = _jailExpirations.get(player.getName().toLowerCase());
		if(expiration == null)
			return 0;
		else if(expiration > System.currentTimeMillis())
		{
			return expiration;
		}
		else
		{
			withdraw(player);
			return 0;
		}
	}
	
	public boolean isLocationInJail(Location location)
	{
		return location.getWorld().getName().equals(_world.getName()) && Config.JAIL_BORDER_LEFT <= location.getBlockX() && location.getBlockX() <= Config.JAIL_BORDER_RIGHT && Config.JAIL_BORDER_BOTTOM <= location.getBlockZ() && location.getBlockZ() <= Config.JAIL_BORDER_TOP;
	}
	
	public Location getSpawn()
	{
		return _world.getSpawnLocation();
	}

	public void CommandJailinfo(Player p)
	{
		long expiration = getJailExpiration(p);
		if(expiration == 0)
			p.sendMessage("You are not jailed....");
		else
		{
			p.sendMessage("Your jail ends "+new Date(expiration).toString());
		}
	}

	public void listJailedPlayerTo(Player sender)
	{
		sender.sendMessage("These players are currently jailed:");
		for(Map.Entry<String, Long> e : _jailExpirations.entrySet())
		{
			sender.sendMessage("  '"+e.getKey()+"' - "+new Date(e.getValue()));
		}
		
		
	}
}
*/
package me.MnC.MnC_SERVER_MOD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import me.MnC.MnC_SERVER_MOD.Handlers.ServerRegionHandler;
import me.MnC.MnC_SERVER_MOD.basicworld.RandomSpawnsHandler;


public abstract class AutoSaver 
{
	public static void SetPlugin(MnC_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	
	public static void StartSaver()
	{
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run()
			{
				//plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[Autosaver] Saving game data.");
				plugin.log.info("[Autosaver] Saving game data.");
				SaveWorldStructures();
			}
		}, 18000, 18000);
	}
	
	public static void SaveWorldStructures()
	{
		for(World world : plugin.getServer().getWorlds())
		{
			world.save();
			plugin.log.info("Saving world " + world.getName() + ".");
		}
		Bukkit.savePlayers();
		plugin.userManager.save();
	}
	
	public static void SaveAll()
	{
		Bukkit.savePlayers();
		plugin.userManager.save();
		ServerRegionHandler.SaveRegions();
		RandomSpawnsHandler.SaveSpawns();
		plugin.arena.SavePvpStats();
		plugin.arena.SaveArenas();
	}
	
	private static MnC_SERVER_MOD plugin;
}

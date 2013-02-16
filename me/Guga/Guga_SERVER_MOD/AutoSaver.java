package me.Guga.Guga_SERVER_MOD;

import org.bukkit.ChatColor;
import org.bukkit.World;

import me.Guga.Guga_SERVER_MOD.Handlers.GugaRegionHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.SpawnsHandler;


public abstract class AutoSaver 
{
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	
	public static void StartSaver()
	{
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run()
			{
				SaveWorldStructures();
				plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[Server] Autosave: Ukladam mapy pro Vase bezpeci...");
				plugin.log.info("[AutoSaver] Saving worlds...");
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
		plugin.getServer().savePlayers();
	}
	public static void SaveAll()
	{
		plugin.userManager.save();
		GugaAnnouncement.SaveAnnouncements();
		GugaRegionHandler.SaveRegions();
		//GugaAuctionHandler.SaveAuctions();
		//GugaAuctionHandler.SavePayments();
		SpawnsHandler.SaveSpawns();
		plugin.arena.SavePvpStats();
		plugin.arena.SaveArenas();
	}
	private static Guga_SERVER_MOD plugin;
}

package me.Guga.Guga_SERVER_MOD;

import me.Guga.Guga_SERVER_MOD.Handlers.ChatHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.HomesHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.SpawnsHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BasicWorld 
{
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public static boolean IsBasicWorld(Location loc)
	{
		if (loc.getWorld().getName().matches("world_basic"))
			return true;
		return false;
	}
	public static void BasicWorldJoin(Player p)
	{
		p.teleport(plugin.getServer().getWorld("world_basic").getSpawnLocation());
	}
	public static void BasicWorldLeave(Player p)
	{
		p.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
	}
	public static void BasicWorldEnter(Player p)
	{
		p.teleport(plugin.getServer().getWorld("world_basic").getSpawnLocation());
		HomesHandler.addHome(p, plugin.getServer().getWorld("world_basic").getSpawnLocation());
		p.sendMessage(ChatColor.RED + "*************************************");
		p.sendMessage(ChatColor.DARK_AQUA + "Nyni se nachazite ve svete pro novacky, " +
				"kde musite dosahnout " + ChatColor.DARK_RED + "LEVELU 10" + ChatColor.DARK_AQUA +" (level zjistite prikazem /rpg status). " + ChatColor.DARK_RED + 
				"LEVELY" + ChatColor.DARK_AQUA +" ziskavate kopanim. Pote se muzete teleportovat do " + ChatColor.DARK_RED + "PROFESIONALNIHO SVETA.");
		
		p.sendMessage(ChatColor.YELLOW + "Vice na o nasem profesionalnim svete na: " + ChatColor.DARK_BLUE + ">>> www.mineandcraft.cz/navod-na-pripojeni <<<");
		p.sendMessage(ChatColor.RED + "*************************************");
	}
	public static void BasicWorldLeaveToWorld(Player p)
	{
		Location loc = SpawnsHandler.getRandomSpawn();
		ChatHandler.InitializeDisplayName(p);
		HomesHandler.addHome(p, loc);
		p.teleport(loc);
	}
	public static void setSpawn(Location l)
	{
		plugin.getServer().getWorld("world_basic").setSpawnLocation((int)l.getX(), (int)l.getY(), (int)l.getZ());
	}
	private static Guga_SERVER_MOD plugin;
}

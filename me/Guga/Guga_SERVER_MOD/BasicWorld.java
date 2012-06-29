package me.Guga.Guga_SERVER_MOD;

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
		p.setBedSpawnLocation(plugin.getServer().getWorld("world_basic").getSpawnLocation());
		p.sendMessage(ChatColor.RED + "Tento svet bude vasim sidlem dokud nedosahnete levelu 15!");
		p.sendMessage(ChatColor.RED + "Levely ziskate kopanim!");
		p.sendMessage(ChatColor.YELLOW + "Vice na o nasem profesionalnim svete na: mineandcraft.cz");
	}
	public static void BasicWorldLeaveToWorld(Player p)
	{
		p.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
		p.setBedSpawnLocation(plugin.getServer().getWorld("world").getSpawnLocation());
	}
	public static void setSpawn(Location l)
	{
		plugin.getServer().getWorld("world_basic").setSpawnLocation((int)l.getX(), (int)l.getY(), (int)l.getZ());
	}
	private static Guga_SERVER_MOD plugin;
}

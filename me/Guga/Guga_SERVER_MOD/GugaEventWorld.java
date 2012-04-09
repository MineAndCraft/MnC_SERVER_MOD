package me.Guga.Guga_SERVER_MOD;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.Guga.Guga_SERVER_MOD.InventoryBackup;;

public class GugaEventWorld 
{
	GugaEventWorld(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public boolean IsEventWorld(Location loc)
	{
		if (loc.getWorld().getName().matches("world_event"))
			return true;
		return false;
	}
	public void PlayerJoin(Location loc, Player p)
	{
		if (!IsEventWorld(p.getLocation()))
		{
			baseLocation.put(p.getName(), p.getLocation());
			InventoryBackup.InventoryClearWrapped(p);
		}
		p.teleport(plugin.getServer().getWorld("world_event").getSpawnLocation());
	}
	public void PlayerLeave(Player p)
	{
		if (!IsEventWorld(p.getLocation()))
		{
			p.sendMessage("Tento prikaz jde pouzit pouze v EventWorldu!");
			return;
		}
		if (baseLocation.get(p.getName()) != null)
		{
			p.teleport(baseLocation.get(p.getName()));
			baseLocation.remove(p.getName());
		}
		else
		{
			p.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
		}
		InventoryBackup.InventoryReturnWrapped(p, true);
	}
	public void togglePvP(Player p)
	{
		if(plugin.getServer().getWorld("world_event").getPVP())
		{
			plugin.getServer().getWorld("world_event").setPVP(false);
			p.sendMessage("PvP for arena world is disable");
		}
		else
		{
			plugin.getServer().getWorld("world_event").setPVP(true);
			p.sendMessage("PvP for arena world is enable");
		}
	}
	public void toggleMobs(Player p)
	{
		if(plugin.getServer().getWorld("world_event").getAllowAnimals())
		{
			plugin.getServer().getWorld("world_event").setSpawnFlags(false, false);
			p.sendMessage("MobSpawns for arena world is disable");
		}
		else
		{
			plugin.getServer().getWorld("world_event").setSpawnFlags(true, true);
			p.sendMessage("MobSpawns for arena world is enable");
		}
	}
	public void toggleRegion(Player p)
	{
		if(region==false)
		{
			region=true;
			p.sendMessage("Region for arena is on");	
		}
		else
		{
			region=false;
			p.sendMessage("Region for arena is off");	
		}
	}
	public static boolean regionStatus()
	{
		if(region==true)
		{
			return true;
		}
		else
			return false;
	}
	private Guga_SERVER_MOD plugin;
	private static boolean region=true;
	private HashMap<String,Location> baseLocation = new HashMap<String,Location>();
}

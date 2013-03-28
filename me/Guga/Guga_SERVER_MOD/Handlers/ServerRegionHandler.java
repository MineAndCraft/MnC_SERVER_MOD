package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.ServerRegion;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.entity.Player;

public abstract class ServerRegionHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		ServerRegionHandler.plugin = plugin;
	}
	public static void AddRegion(String name, String world, String[] owners, int x1, int x2, int z1, int z2)
	{
		ServerRegionHandler.regions.add(new ServerRegion(name, world ,owners, x1, x2, z1, z2));
		ServerRegionHandler.SaveRegions();
	}
	public static boolean CanInteract(Player p, int x, int z)
	{
		ServerRegion region = ServerRegionHandler.GetRegionByCoords(x, z, p.getWorld().getName());
		if (region != null)
		{
			String[] owners = region.GetOwners();
			int i = 0;
			while (i < owners.length)
			{
				if (owners[i].equalsIgnoreCase(p.getName()))
					return true;
				else if (owners[i].equalsIgnoreCase("all"))
					return true;
				i++;
			}
			return false;
		}
		return true;
	}
	public static void RemoveRegion(ServerRegion region)
	{
		ServerRegionHandler.regions.remove(region);
	}
	public static ArrayList<ServerRegion> GetAllRegions()
	{
		return ServerRegionHandler.regions;
	}
	public static boolean SetRegionOwners(String name, String[] owners)
	{
		ServerRegion region = ServerRegionHandler.GetRegionByName(name);
		if (region == null)
			return false;
		region.SetOwners(owners);
		return true;
	}
	public static ServerRegion GetRegionByName(String name)
	{
		Iterator<ServerRegion> i = ServerRegionHandler.regions.iterator();
		while (i.hasNext())
		{
			ServerRegion region = i.next();
			if (region.GetName().equalsIgnoreCase(name))
			{
				return region;
			}
		}
		return null;
	}
	public static ServerRegion GetRegionByCoords(int x, int z, String world)
	{
		Iterator<ServerRegion> i = ServerRegionHandler.regions.iterator();
		while (i.hasNext())
		{
			ServerRegion region = i.next();
			if (region.IsInRegion(x, z, world))
			{
				return region;
			}
		}
		return null;
	}
	public static String OwnersToLine(String[] owners)
	{
		int i = 0;
		String ownersString = "";
		while (i < owners.length)
		{
			if (i == owners.length - 1)
				ownersString += owners[i];
			else
				ownersString += owners[i] + ",";
			
			i++;
		}
		return ownersString;
	}
	public static void SaveRegions()
	{
		plugin.log.info("Saving Regions file...");
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/Regions.dat", GugaFile.WRITE_MODE);
		file.Open();
		ServerRegion region;
		String name;
		String world;
		String[] owners;
		int[] coords;
		String line;
		Iterator<ServerRegion> i = ServerRegionHandler.regions.iterator();
		while (i.hasNext())
		{
			region = i.next();
			name = region.GetName();
			world = region.GetWorld();
			owners = region.GetOwners();
			coords = region.GetCoords();
			line = name + ";" + world + ";" + ServerRegionHandler.OwnersToLine(owners) + ";" + coords[ServerRegion.X1] + ";" + coords[ServerRegion.X2] + ";" + coords[ServerRegion.Z1] + ";" + coords[ServerRegion.Z2];
			file.WriteLine(line);
		}
		file.Close();
	}
	public static void LoadRegions()
	{
		plugin.log.info("Loading Regions file...");
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/Regions.dat", GugaFile.READ_MODE);
		file.Open();
		String line;
		String name;
		String world;
		String[] owners;
		int x1;
		int x2;
		int z1;
		int z2;
		String[] splittedString;
		while ((line = file.ReadLine()) != null)
		{
			splittedString = line.split(";");
			name = splittedString[0];
			world = splittedString[1];
			owners = splittedString[2].split(",");
			x1 = Integer.parseInt(splittedString[3]);
			x2 = Integer.parseInt(splittedString[4]);
			z1 = Integer.parseInt(splittedString[5]);
			z2 = Integer.parseInt(splittedString[6]);
			ServerRegionHandler.regions.add(new ServerRegion(name, world, owners, x1, x2, z1, z2));
		}
		file.Close();
	}
	private static Guga_SERVER_MOD plugin;
	private static ArrayList<ServerRegion> regions = new ArrayList<ServerRegion>();
}

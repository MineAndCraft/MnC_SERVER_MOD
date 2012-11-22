package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaRegion;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.entity.Player;

public abstract class GugaRegionHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		GugaRegionHandler.plugin = plugin;
	}
	public static void AddRegion(String name, String world, String[] owners, int x1, int x2, int z1, int z2)
	{
		GugaRegionHandler.regions.add(new GugaRegion(name, world ,owners, x1, x2, z1, z2));
		GugaRegionHandler.SaveRegions();
	}
	public static boolean CanInteract(Player p, int x, int z)
	{
		GugaRegion region = GugaRegionHandler.GetRegionByCoords(x, z, p.getWorld().getName());
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
	public static void RemoveRegion(GugaRegion region)
	{
		GugaRegionHandler.regions.remove(region);
	}
	public static ArrayList<GugaRegion> GetAllRegions()
	{
		return GugaRegionHandler.regions;
	}
	public static boolean SetRegionOwners(String name, String[] owners)
	{
		GugaRegion region = GugaRegionHandler.GetRegionByName(name);
		if (region == null)
			return false;
		region.SetOwners(owners);
		return true;
	}
	public static GugaRegion GetRegionByName(String name)
	{
		Iterator<GugaRegion> i = GugaRegionHandler.regions.iterator();
		while (i.hasNext())
		{
			GugaRegion region = i.next();
			if (region.GetName().equalsIgnoreCase(name))
			{
				return region;
			}
		}
		return null;
	}
	public static GugaRegion GetRegionByCoords(int x, int z, String world)
	{
		Iterator<GugaRegion> i = GugaRegionHandler.regions.iterator();
		while (i.hasNext())
		{
			GugaRegion region = i.next();
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
		GugaRegion region;
		String name;
		String world;
		String[] owners;
		int[] coords;
		String line;
		Iterator<GugaRegion> i = GugaRegionHandler.regions.iterator();
		while (i.hasNext())
		{
			region = i.next();
			name = region.GetName();
			world = region.GetWorld();
			owners = region.GetOwners();
			coords = region.GetCoords();
			line = name + ";" + world + ";" + GugaRegionHandler.OwnersToLine(owners) + ";" + coords[GugaRegion.X1] + ";" + coords[GugaRegion.X2] + ";" + coords[GugaRegion.Z1] + ";" + coords[GugaRegion.Z2];
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
			GugaRegionHandler.regions.add(new GugaRegion(name, world, owners, x1, x2, z1, z2));
		}
		file.Close();
	}
	private static Guga_SERVER_MOD plugin;
	private static ArrayList<GugaRegion> regions = new ArrayList<GugaRegion>();
}

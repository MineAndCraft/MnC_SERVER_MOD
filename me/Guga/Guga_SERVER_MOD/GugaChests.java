package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class GugaChests 
{
	GugaChests(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		LoadChests();
	}
	public void LockBlock(Block chest,String chestOwner)
	{
		chestList.add(new Chest(chestOwner, chest.getLocation()));
		SaveChests();
	}
	public void UnlockBlock(Block chest,String chestOwner)
	{
		Iterator<Chest> i = chestList.iterator();
		int index = 0;
		while (i.hasNext())
		{
			Chest c = i.next();
			if (LocationEquals(c.location, chest.getLocation()))
				break;

			index++;
		}
		chestList.remove(index);
		SaveChests();
	}
	public String GetBlockOwner(Block chest)
	{
		Iterator<Chest> i = chestList.iterator();
		while (i.hasNext())
		{
			Chest c = i.next();
			if (LocationEquals(c.location, chest.getLocation()))
				return c.owner;
		}
		return "notFound";
	}
	
	public void LoadChests()
	{
		plugin.log.info("Loading Chests Data...");
		GugaFile file = new GugaFile(LockerFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		double locX;
		double locY;
		double locZ;
		String world;
		while ((line = file.ReadLine()) != null)
		{
			locX = Double.parseDouble(line.split(";")[0]);
			locY = Double.parseDouble(line.split(";")[1]);
			locZ = Double.parseDouble(line.split(";")[2]);
			world = line.split(";")[3];
			chestList.add(new Chest(line.split(";")[4], new Location(plugin.getServer().getWorld(world),locX, locY, locZ)));
		}
		file.Close();
	}
	public boolean LocationEquals(Location loc1, Location loc2)
	{
		if (loc1.getBlockX() == loc2.getBlockX())
		{
			if (loc1.getBlockY() == loc2.getBlockY())
			{
				if (loc1.getBlockZ() == loc2.getBlockZ())
				{
					if(loc1.getWorld() == loc2.getWorld())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	public void SaveChests()
	{
		plugin.log.info("Saving Chests Data...");
		GugaFile file = new GugaFile(LockerFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<Chest> i = chestList.iterator();
		while (i.hasNext())
		{
			Chest chest = i.next();
			String x = Integer.toString(chest.location.getBlockX());
			String y = Integer.toString(chest.location.getBlockY());
			String z = Integer.toString(chest.location.getBlockZ());
			String world = chest.location.getWorld().getName();
			
			String line;
			line = x+";"+y+";"+z+";"+world+";"+chest.owner;
			file.WriteLine(line);
		}
		
		file.Close();
	}
	public static class Chest
	{
		public Chest(String owner, Location location)
		{
			this.owner = owner;
			this.location = location;
		}
		public String GetOwner() { return owner; }
		public Location GetLocation() { return location; }
		private String owner;
		private Location location;
	}
	public ArrayList<Chest> chestList = new ArrayList<Chest>();
	private String LockerFile = "plugins/Chests.dat";
	public static Guga_SERVER_MOD plugin;
}

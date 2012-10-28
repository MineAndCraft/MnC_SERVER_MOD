package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.GugaChests.Chest;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class GugaFurnances 
{
	GugaFurnances(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		LoadFurnances();
	}
	public void LockBlock(Block furnance,String chestOwner)
	{
		chestList.add(new Chest(chestOwner, furnance.getLocation()));
		SaveFurnances();
	}
	public void UnlockBlock(Block furnance,String chestOwner)
	{
		Iterator<Chest> i = chestList.iterator();
		int index = 0;
		while (i.hasNext())
		{
			Chest c = i.next();
			if (LocationEquals(c.GetLocation(), furnance.getLocation()))
				break;

			index++;
		}
		chestList.remove(index);
		SaveFurnances();
	}
	public String GetBlockOwner(Block chest)
	{
		Iterator<Chest> i = chestList.iterator();
		while (i.hasNext())
		{
			Chest c = i.next();
			if (LocationEquals(c.GetLocation(), chest.getLocation()))
				return c.GetOwner();
		}
		return "notFound";
	}
	public void LoadFurnances()
	{
		plugin.log.info("Loading Furnace Data...");
		GugaFile file = new GugaFile(LockerFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		//int i = 0;
		double locX;
		double locY;
		double locZ;
		while ((line = file.ReadLine()) != null)
		{
			locX = Double.parseDouble(line.split(";")[0]);
			locY = Double.parseDouble(line.split(";")[1]);
			locZ = Double.parseDouble(line.split(";")[2]);
			chestList.add(new Chest(line.split(";")[3], new Location(plugin.getServer().getWorld("world"),locX, locY, locZ)));
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
					return true;
				}
			}
		}
		return false;
	}
	public void SaveFurnances()
	{
		plugin.log.info("Saving Furnace Data...");
		GugaFile file = new GugaFile(LockerFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<Chest> i = chestList.iterator();
		while (i.hasNext())
		{
			Chest chest = i.next();
			String x = Integer.toString(chest.GetLocation().getBlockX());
			String y = Integer.toString(chest.GetLocation().getBlockY());
			String z = Integer.toString(chest.GetLocation().getBlockZ());
			
			String line;
			line = x+";"+y+";"+z+";"+chest.GetOwner();
			file.WriteLine(line);
		}
		
		file.Close();
	}
	public ArrayList<Chest> chestList = new ArrayList<Chest>();
	private String LockerFile = "plugins/Furnances.dat";
	public static Guga_SERVER_MOD plugin;
}

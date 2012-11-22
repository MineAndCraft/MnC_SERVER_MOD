package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.GugaChests.Chest;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class GugaDispensers 
{
	GugaDispensers(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		LoadDispensers();
	}
	public void LockBlock(Block furnance,String chestOwner)
	{
		chestList.add(new Chest(chestOwner, furnance.getLocation()));
		SaveDispensers();
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
		SaveDispensers();
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
	public void LoadDispensers()
	{
		plugin.log.info("Loading Dispensers Data...");
		GugaFile file = new GugaFile(LockerFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		//int i = 0;
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
	public void SaveDispensers()
	{
		plugin.log.info("Saving Dispensers Data...");
		GugaFile file = new GugaFile(LockerFile, GugaFile.WRITE_MODE);
		file.Open();

		Iterator<Chest> i = chestList.iterator();
		while (i.hasNext())
		{
			Chest chest = i.next();
			String x = Integer.toString(chest.GetLocation().getBlockX());
			String y = Integer.toString(chest.GetLocation().getBlockY());
			String z = Integer.toString(chest.GetLocation().getBlockZ());
			String world = chest.GetLocation().getWorld().getName();
			String line;
			line = x+";"+y+";"+z+";"+world+";"+chest.GetOwner();
			file.WriteLine(line);
		}
		
		file.Close();
	}
	public ArrayList<Chest> chestList = new ArrayList<Chest>();
	private String LockerFile = "plugins/MineAndCraft_plugin/Dispensers.dat";
	public static Guga_SERVER_MOD plugin;
}

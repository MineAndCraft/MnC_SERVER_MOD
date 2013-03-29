package me.Guga.Guga_SERVER_MOD.basicworld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Location;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.util.GugaFile;

public class RandomSpawnsHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD GugaSM)
	{
		plugin = GugaSM;
	}
	public static void SaveSpawns()
	{
		plugin.log.info("Saving Spawns file...");
		GugaFile file = new GugaFile(RandomSpawnsHandler.spawnsFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<RandomSpawn> i = RandomSpawnsHandler.spawns.iterator();
		while (i.hasNext())
		{
			RandomSpawn spawn = i.next();
			Location l = spawn.getLocation();
			String line = spawn.getName() + ";" + l.getX() + ";" + l.getY() + ";" + l.getZ();
			file.WriteLine(line);
		}
		file.Close();
	}
	public static void LoadSpawns()
	{
		plugin.log.info("Loading Spawns file...");
		GugaFile file = new GugaFile(RandomSpawnsHandler.spawnsFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		while ((line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			Location loc = new Location(plugin.getServer().getWorld("world"), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
			RandomSpawnsHandler.AddSpawn(split[0], loc);
		}
		file.Close();
		
	}
	public static void AddSpawn(String sName, Location loc)
	{
		spawns.add(new RandomSpawn(sName, loc));
	}
	public static void RemoveSpawn(String sName)
	{
		if(RandomSpawnsHandler.GetSpawnByName(sName) != null)
		{
			RandomSpawn spawn = RandomSpawnsHandler.GetSpawnByName(sName);
			spawns.remove(spawn);
		}
	}
	public static RandomSpawn GetSpawnByName(String sName)
	{
		Iterator<RandomSpawn> i = spawns.iterator();
		while(i.hasNext())
		{
			RandomSpawn spawn = i.next();
			if(spawn.getName().equalsIgnoreCase(sName))
				return spawn;
		}
		return null;
	}
	public static Location getRandomSpawn()
	{
		Random r = new Random();
		int index = r.nextInt(spawns.size());
		RandomSpawn spawn = spawns.get(index);
		return spawn.getLocation();
	}
	public static ArrayList<RandomSpawn> spawns = new ArrayList<RandomSpawn>();
	private static String spawnsFile = "plugins/MineAndCraft_plugin/spawnsFile.dat";
	private static Guga_SERVER_MOD plugin;
}

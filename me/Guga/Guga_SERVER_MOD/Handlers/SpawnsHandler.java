package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Location;

import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaSpawns;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

public class SpawnsHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD GugaSM)
	{
		plugin = GugaSM;
	}
	public static void SaveSpawns()
	{
		plugin.log.info("Saving Spawns file...");
		GugaFile file = new GugaFile(SpawnsHandler.spawnsFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<GugaSpawns> i = SpawnsHandler.spawns.iterator();
		while (i.hasNext())
		{
			GugaSpawns spawn = i.next();
			Location l = spawn.getLocation();
			String line = spawn.getName() + ";" + l.getX() + ";" + l.getY() + ";" + l.getZ();
			file.WriteLine(line);
		}
		file.Close();
	}
	public static void LoadSpawns()
	{
		plugin.log.info("Loading Spawns file...");
		GugaFile file = new GugaFile(SpawnsHandler.spawnsFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		while ((line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			Location loc = new Location(plugin.getServer().getWorld("world"), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
			SpawnsHandler.AddSpawn(split[0], loc);
		}
		file.Close();
		
	}
	public static void AddSpawn(String sName, Location loc)
	{
		spawns.add(new GugaSpawns(sName, loc));
	}
	public static void RemoveSpawn(String sName)
	{
		if(SpawnsHandler.GetSpawnByName(sName) != null)
		{
			GugaSpawns spawn = SpawnsHandler.GetSpawnByName(sName);
			spawns.remove(spawn);
		}
	}
	public static GugaSpawns GetSpawnByName(String sName)
	{
		Iterator<GugaSpawns> i = spawns.iterator();
		while(i.hasNext())
		{
			GugaSpawns spawn = i.next();
			if(spawn.getName().equalsIgnoreCase(sName))
				return spawn;
		}
		return null;
	}
	public static Location getRandomSpawn()
	{
		Random r = new Random();
		int index = r.nextInt(spawns.size());
		GugaSpawns spawn = spawns.get(index);
		return spawn.getLocation();
	}
	public static ArrayList<GugaSpawns> spawns = new ArrayList<GugaSpawns>();
	private static String spawnsFile = "plugins/spawnsFile.dat";
	private static Guga_SERVER_MOD plugin;
}

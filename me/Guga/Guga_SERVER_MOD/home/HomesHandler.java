package me.Guga.Guga_SERVER_MOD.home;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.util.GugaFile;

public class HomesHandler 
{
	public static void setPlugin(Guga_SERVER_MOD SM)
	{
		plugin = SM;
	}
	
	public static void loadHomes()
	{
		plugin.log.info("Loading Homes file...");
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/Homes.dat", GugaFile.READ_MODE);
		file.Open();
		String line;
		String playerName;
		int x;
		int y;
		int z;
		String world;
		String[] splittedLine;
		while ((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			playerName = splittedLine[0];
			x = Integer.parseInt(splittedLine[1]);
			y = Integer.parseInt(splittedLine[2]);
			z = Integer.parseInt(splittedLine[3]);
			world = splittedLine[4];
			homes.add(new Home(playerName, x, y, z, world));
		}
		file.Close();
	}
	
	public static void saveHomes()
	{
		plugin.log.info("Saving Homes file...");
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/Homes.dat", GugaFile.WRITE_MODE);
		file.Open();
		String line;
		String playerName;
		int x;
		int y;
		int z;
		String world;
		Iterator<Home> i = HomesHandler.homes.iterator();
		Home home;
		while(i.hasNext())
		{
			home = i.next();
			playerName = home.getPlayerName();
			x = home.getX();
			y = home.getY();
			z = home.getZ();
			world = home.getWorld();
			line = playerName + ";" + x + ";" + y + ";" + z + ";" + world;
			file.WriteLine(line);
		}
		file.Close();
	}
	
	public static void teleport(Player p)
	{
		Location loc = getLocation(getHomeByPlayer(p.getName()));
		p.teleport(loc);
	}
	
	public static Home getHomeByPlayer(String playerName)
	{
		Iterator<Home> i = HomesHandler.homes.iterator();
		Home home;
		while(i.hasNext())
		{
			home = i.next();
			if(home.getPlayerName().equalsIgnoreCase(playerName))
				return home;
		}
		return null;
	}
	
	public static Location getLocation(Home home)
	{
		return new Location(plugin.getServer().getWorld(home.getWorld()), home.getX(), home.getY(), home.getZ());
	}
	
	public static void addHome(Player p)
	{
		Home actual = getHomeByPlayer(p.getName());
		if(actual == null)
		{
			homes.add(new Home (p.getName(), (int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ(), p.getLocation().getWorld().getName()));
		}
		else
		{
			homes.remove(actual);
			homes.add(new Home (p.getName(), (int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ(), p.getLocation().getWorld().getName()));
		}
		saveHomes();
	}
	
	public static void addHome(Player p, Location loc)
	{
		Home actual = getHomeByPlayer(p.getName());
		if(actual == null)
		{
			homes.add(new Home (p.getName(), (int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), loc.getWorld().getName()));
		}
		else
		{
			homes.remove(actual);
			homes.add(new Home (p.getName(), (int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), loc.getWorld().getName()));
		}
		saveHomes();
	}
	public static void addHome(Home home)
	{
		Home actual = getHomeByPlayer(home.getPlayerName());
		if(actual == null)
		{
			homes.add(home);
		}
		else
		{
			homes.remove(actual);
			homes.add(home);
		}
	}
	public static boolean isWorldAllowedToSetHomeIn(String world)
	{
		return world.matches("world") || world.matches("world_basic");
	}
	
	private static ArrayList<Home> homes = new ArrayList<Home>();
	static Guga_SERVER_MOD plugin;
}

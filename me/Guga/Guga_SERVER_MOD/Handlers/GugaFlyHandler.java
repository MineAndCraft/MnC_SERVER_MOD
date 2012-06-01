package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;


import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaFly;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

public class GugaFlyHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public static void SaveFly()
	{
		plugin.log.info("Saving fly file...");
		GugaFile file = new GugaFile(GugaFlyHandler.flyFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<GugaFly> i = GugaFlyHandler.flyingPlayers.iterator();
		while (i.hasNext())
		{
			GugaFly fly = i.next();
			String line = fly.getName() + ";" + fly.getExpiration();
			file.WriteLine(line);
		}
		file.Close();
	}
	
	public static void LoadFly()
	{
		plugin.log.info("Loading fly file...");
		GugaFile file = new GugaFile(flyFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		while ((line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			GugaFlyHandler.AddFlyingPlayer(split[0], Long.parseLong(split[1]));
		}
		file.Close();
		
	}
	
	public static void AddFlyingPlayer(String name, Long exp)
	{
		GugaFlyHandler.flyingPlayers.add(new GugaFly(name, exp));
	}
	public static boolean isFlying(String playerName)
	{
		GugaFly fly;
		if((fly = GugaFlyHandler.GetGugaFlyByName(playerName)) != null)
		{
			long exp = fly.getExpiration();
			if(GugaFlyHandler.flyingPlayers.contains(fly) && (exp > System.currentTimeMillis()))
			{
				return true;
			}
			else
				GugaFlyHandler.flyingPlayers.remove(fly);
				return false;
		}
		else
			return false;
	}
	
	public static boolean offFlying(String playerName)
	{
		GugaFly fly;
		if((fly = GugaFlyHandler.GetGugaFlyByName(playerName)) != null)
		{
			long exp = fly.getExpiration();
			if(GugaFlyHandler.flyingPlayers.contains(fly) && (exp < System.currentTimeMillis()))
			{
				GugaFlyHandler.flyingPlayers.remove(fly);
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	public static GugaFly GetGugaFlyByName(String name)
	{
		Iterator<GugaFly> i = GugaFlyHandler.flyingPlayers.iterator();
		while(i.hasNext())
		{
			GugaFly fly = i.next();
			if(fly.getName().matches(name))
				return fly;
		}
		return null;
	}
	public static ArrayList<GugaFly> flyingPlayers = new ArrayList<GugaFly>();
	public static String flyFile = "plugins/Fly.dat";
	public static Guga_SERVER_MOD plugin;
}

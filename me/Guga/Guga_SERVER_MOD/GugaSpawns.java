package me.Guga.Guga_SERVER_MOD;

import org.bukkit.Location;

public class GugaSpawns 
{
	public GugaSpawns(String spawnName, Location loc)
	{
		this.spawnName = spawnName;
		this.loc = loc;
	}
	
	public String getName()
	{
		return this.spawnName;
	}
	public Location getLocation()
	{
		return this.loc;
	}
	String spawnName;
	Location loc;
}

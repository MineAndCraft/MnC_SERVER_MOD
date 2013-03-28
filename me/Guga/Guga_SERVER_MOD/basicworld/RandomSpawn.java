package me.Guga.Guga_SERVER_MOD.basicworld;

import org.bukkit.Location;

public class RandomSpawn 
{
	public RandomSpawn(String spawnName, Location loc)
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

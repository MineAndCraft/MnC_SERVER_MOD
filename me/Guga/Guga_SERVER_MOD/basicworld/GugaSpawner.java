package me.Guga.Guga_SERVER_MOD.basicworld;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class GugaSpawner 
{
	public GugaSpawner(Guga_SERVER_MOD plugin, Location loc, int interval)
	{
		this.isRunning = true;
		this.canSpawn = false;
		this.plugin = plugin;
		this.location = loc;
		this.interval = interval;
		this.cap = 10;
		this.spawnType = null;
		this.StartThread();
	}
	public GugaSpawner(Guga_SERVER_MOD plugin, Location loc, int interval, int cap, EntityType spawnType)
	{
		this.isRunning = true;
		this.canSpawn = false;
		this.plugin = plugin;
		this.location = loc;
		this.interval = interval;
		this.cap = cap;
		this.spawnedCount = 0;
		this.spawnType = spawnType;
		this.StartThread();
	}
	public void ToggleSpawnState()
	{
		this.canSpawn = !this.canSpawn;
	}
	public void SetSpawnState(boolean canSpawn)
	{
		this.canSpawn = canSpawn;
	}
	public boolean GetSpawnState()
	{
		return this.canSpawn;
	}
	public Location GetLocation()
	{
		return this.location;
	}
	public void StartThread()
	{
		this.spawnThread = new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() 
			{
				while (isRunning)
				{
					try 
					{
						if (canSpawn && (spawnedCount < cap))
						{
							plugin.getServer().getWorld("world").spawnCreature(location, spawnType);
							spawnedCount++;
						}
						Thread.sleep(interval * 1000);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		this.spawnThread.start();
	}
	public void TerminateThread()
	{
		this.isRunning = false;
	}
	private Guga_SERVER_MOD plugin;
	private Location location;
	private EntityType spawnType;
	private int interval;
	private int cap;
	private int spawnedCount;
	private Thread spawnThread;
	private boolean isRunning;
	private boolean canSpawn;
}

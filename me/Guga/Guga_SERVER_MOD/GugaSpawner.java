package me.Guga.Guga_SERVER_MOD;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;

public class GugaSpawner 
{
	public GugaSpawner(Guga_SERVER_MOD plugin, Location loc, int interval)
	{
		this.isRunning = true;
		this.plugin = plugin;
		this.location = loc;
		this.interval = interval;
		this.cap = 10;
		this.spawnType = null;
		this.StartSpawning();
	}
	public GugaSpawner(Guga_SERVER_MOD plugin, Location loc, int interval, int cap, CreatureType spawnType)
	{
		this.isRunning = true;
		this.plugin = plugin;
		this.location = loc;
		this.interval = interval;
		this.cap = cap;
		this.spawnedCount = 0;
		this.spawnType = spawnType;
		this.StartSpawning();
	}
	public void StartSpawning()
	{
		this.spawnThread = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while (isRunning)
				{
					plugin.log.info("RUNNING");
					try 
					{
						if (GugaEvent.canSpawn && (spawnedCount < cap))
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
	private CreatureType spawnType;
	private int interval;
	private int cap;
	private int spawnedCount;
	private Thread spawnThread;
	private boolean isRunning = true;
}

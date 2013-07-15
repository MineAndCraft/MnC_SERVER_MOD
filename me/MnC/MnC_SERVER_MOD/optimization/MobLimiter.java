package me.MnC.MnC_SERVER_MOD.optimization;

import java.util.logging.Logger;

import me.MnC.MnC_SERVER_MOD.Config;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobLimiter implements Listener
{
	public final Logger log = Logger.getLogger(MobLimiter.class.toString());
	
	public MobLimiter()
	{
		//do nothing
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntitySpawn(CreatureSpawnEvent event)
	{
		Entity entity = event.getEntity();
		Location location = event.getLocation();
		
		if(event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.EGG)
			return;
		
		if(!location.getWorld().getName().equalsIgnoreCase("world"))
			return;
		
		if(Config.MOB_LIMITER_LIMIT == 0)
			return;
		
		int count=0;		
		for(Entity e : entity.getNearbyEntities(Config.MOB_LIMITER_LIMIT, Config.MOB_LIMITER_LIMIT, Config.MOB_LIMITER_LIMIT))
		{
			if(e.getType() == entity.getType() && (e instanceof Monster || e instanceof Animals || e instanceof Villager))
			{
				count++;
			}			
		}
		
		if(count >= Config.MOB_LIMITER_LIMIT)
		{
			event.setCancelled(true);
			log.info("Denied to spawn '"+entity.getType()+"' near "+location.getWorld().getName()+"["+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+"]");
		}	
	}
}

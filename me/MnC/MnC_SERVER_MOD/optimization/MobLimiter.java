package me.MnC.MnC_SERVER_MOD.optimization;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import me.MnC.MnC_SERVER_MOD.Config;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;

public class MobLimiter implements Listener
{
	public final Logger log = Logger.getLogger(MobLimiter.class.toString());
	
	public MobLimiter()
	{
		//do nothing
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntitySpawn(CreatureSpawnEvent event)
	{
		if(event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.EGG)
			return;
		
		fixTooManyMobs(event.getEntity());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event)
	{
		if(event.isNewChunk())
			return;
		
		for(Entity e : event.getChunk().getEntities())
		{
			fixTooManyMobs(e);
		}
		
	}

	/**
	 * Kills concentrated mobs over limit
	 * @param mob Entity around which to search
	 */
	private void fixTooManyMobs(Entity mob)
	{
		if(mob == null)
			return;
		
		if(mob.getType() == EntityType.PLAYER)
			return;
		
		if(!(mob instanceof Monster || mob instanceof Animals || mob instanceof Villager))
			return;
		
		if(Config.MOB_LIMITER_LIMIT == 0)
			return;
		
		//check for too many entities
		List<Entity> entities = new LinkedList<Entity>();
		for(Entity e : mob.getWorld().getEntities())
		{
			if(e.getType() == EntityType.PLAYER)
				continue;
			if(e.getType() != mob.getType())
				continue;
			if(e.getLocation().distance(mob.getLocation()) < Config.MOB_LIMITER_RADIUS)
				entities.add(e);
		}
		
		if(entities.size() > Config.MOB_LIMITER_LIMIT)
		{
			int toKill = entities.size()-Config.MOB_LIMITER_LIMIT;
			log.info("Found "+entities.size()+" entities '"+mob.getType()+"' near '"+mob.getLocation().getWorld().getName()+"'["+mob.getLocation().getBlockX()+", "+mob.getLocation().getBlockY()+", "+mob.getLocation().getBlockZ()+"]. Triming by "+toKill+".");
			for(int i=0;i < toKill;i++)
			{
				Entity e = entities.get(i);				
				if(e instanceof LivingEntity)
					((LivingEntity)e).damage(((LivingEntity) e).getHealth()+1);
				e.remove();
			}
			log.info("Killed "+toKill+" entities '"+mob.getType()+"'.");
		}
	}
}

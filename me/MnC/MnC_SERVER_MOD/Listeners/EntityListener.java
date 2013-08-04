package me.MnC.MnC_SERVER_MOD.Listeners;

import java.util.Iterator;
import java.util.List;

import me.MnC.MnC_SERVER_MOD.GugaEvent;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.Estates.EstateHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.CommandsHandler;
import me.MnC.MnC_SERVER_MOD.manor.Manor;
import me.MnC.MnC_SERVER_MOD.manor.ManorManager;
import me.MnC.MnC_SERVER_MOD.rpg.PlayerProfession;
import me.MnC.MnC_SERVER_MOD.basicworld.BasicWorld;

import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityListener implements Listener
{
	public EntityListener(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event)
	{
		if((event.getEntity() instanceof Wither))
		{
			if(!(event.getEntity().getWorld().getName().matches("world_nether")))
			{
				event.setCancelled(true);
			}
		}
		
		if(event.getSpawnReason() == SpawnReason.NATURAL && event.getEntity() instanceof Monster)
		{
			Manor manor = ManorManager.getInstance().getManorByLocation(event.getEntity().getLocation());
			if(manor != null && !manor.getFlag("monsters"))
			{
				event.setCancelled(true);
			}
		}
		
		if(event.getLocation().getWorld().getName().equals("world_build") && event.getSpawnReason() == SpawnReason.NATURAL)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			if (event.getRegainReason() == RegainReason.REGEN)
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			if(((Player)e.getEntity()).getWorld().getName().matches("arena"))
			{
				if(e instanceof EntityDamageByEntityEvent)
				{
					if(plugin.arena.IsImortal(((Player)e.getEntity()).getName()))
						e.setCancelled(true);
				}
				else
					e.setCancelled(true);
			}
			if (CommandsHandler.godMode.contains(((Player)e.getEntity()).getName().toLowerCase()))
			{
				e.setCancelled(true);
				return;
			}
			if (GugaEvent.godMode)
			{
				if (GugaEvent.players.contains(((Player)e.getEntity()).getName().toLowerCase()))
				{
					e.setCancelled(true);
					return;
				}
			}
		}
		if (e instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)e;
			if (event.getDamager() instanceof Player)
			{
				Player damager = (Player)event.getDamager();
				MinecraftPlayer player = plugin.userManager.getUser(damager.getName());
				if (!player.isAuthenticated())
				{
					e.setCancelled(true);
					return;
				}
				if(player.getProfession() != null && player.getProfession().getLevel() < 10 && !BasicWorld.IsBasicWorld(damager.getLocation()))
				{
					e.setCancelled(true);
					return;
				}
			}
		}

		if (e.getEntity() instanceof Player)
		{
			Player player = (Player)e.getEntity();
			if(!plugin.userManager.userIsLogged(player.getName()))
				e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e)
	{
		//TODO that thing about skill points was not that weird
		if (plugin.arena.IsArena(e.getEntity().getLocation()))
		{
			if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)e.getEntity().getLastDamageCause();
				if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player))
				{
					Player damager = (Player)event.getDamager();
					e.getDrops().clear();
					Player target = (Player)event.getEntity();
					plugin.arena.ArenaKill(damager, target);
					((PlayerDeathEvent)e).setDeathMessage(null);
				}
			}
			return;
		}
		if (e.getEntity().getLastDamageCause() != null)
		{
			if (!(e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			{
				return;
			}
			Entity target = e.getEntity();
			Entity ent = ((EntityDamageByEntityEvent)target.getLastDamageCause()).getDamager();
			if (ent instanceof Player)
			{
				Player damager = (Player)ent;
				PlayerProfession prof = plugin.userManager.getUser(damager.getName()).getProfession();
				if (target instanceof Creeper)
				{
					prof.gainExperience(75);
				}
				else if(target instanceof Spider)
				{
					prof.gainExperience(8);
				}
				else if(target instanceof Skeleton)
				{
					prof.gainExperience(10);
				}
				else if(target instanceof Zombie)
				{
					prof.gainExperience(8);
				}
				else if (target instanceof Enderman)
				{
					prof.gainExperience(12);
				}
				else
				{
					prof.gainExperience(5);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if(event.getEntity() instanceof Creeper)
		{
			event.setCancelled(true);
			return;
		}
		if (plugin.arena.IsArena(event.getLocation()))
		{
			event.setCancelled(true);
			return;
		}
		
		Manor manor = ManorManager.getInstance().getManorByLocation(event.getEntity().getLocation());
		if(manor != null && !manor.getFlag("tnt"))
		{
			event.setCancelled(true);
		}
		
		List<Block> blockList = event.blockList();

		Iterator<Block> iter = event.blockList().iterator();
		boolean foundChest = false;
		while (iter.hasNext())
		{
			Block block = iter.next();
			if (block.getTypeId() == 54)
			{
				foundChest = true;
				break;
			}

		}
		if (foundChest)
		{
			event.setCancelled(true);
			iter = blockList.iterator();
			while(iter.hasNext())
			{
				Block block = iter.next();
				if (block.getTypeId() != 54)
				{
					block.setTypeId(0);
				}
			}
		}
				
		for(Block block : event.blockList())
		{
			if(EstateHandler.getResidenceId(block) != 0)
			{
				event.setCancelled(true);
				break;
			}
		}
	}
	
	public MnC_SERVER_MOD plugin;
}
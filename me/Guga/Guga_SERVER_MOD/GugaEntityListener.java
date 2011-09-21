package me.Guga.Guga_SERVER_MOD;

import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;

public class GugaEntityListener extends EntityListener
{
	GugaEntityListener(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public void onEntityRegainHealth(EntityRegainHealthEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("ENTITY_REGAIN_EVENT: entity=" + e.getEntity().toString());
		}
		if (e.getEntity() instanceof Player)
		{
			if (e.getRegainReason() == RegainReason.REGEN)
			{
				e.setCancelled(true);
			}
		}
	}
	public void onEntityDamage(EntityDamageEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("ENTITY_DAMAGE_EVENT: entity=" + e.getEntity().toString() + ",dmg=" + e.getDamage());
		}
		if (e.getEntity() instanceof Player)
		{
			if (GugaCommands.godMode.contains(((Player)e.getEntity()).getName().toLowerCase()))
			{
				e.setCancelled(true);
				return;
			}
		}
		if (plugin.arena.IsArena(e.getEntity().getLocation()))
		{
			if (e instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)e;
				if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player))
				{
					Player damager = (Player)event.getDamager();
					Player target = (Player)event.getEntity();
					if (event.getDamage() >= target.getHealth())
					{
						plugin.arena.ArenaKill(damager, target);
					}
				}
			}
			return;
		}
		if (e instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)e;
			if (event.getDamager() instanceof Player)
			{
				Player damager = (Player)event.getDamager();
				if (!plugin.acc.UserIsLogged(damager))
				{
					e.setCancelled(true);
					return;
				}
				GugaProfession prof = plugin.professions.get(damager.getName());
				LivingEntity target = (LivingEntity)event.getEntity();
				if (xpCache == target)
				{
					return;
				}
				if (prof instanceof GugaHunter)
				{
					if (!(target instanceof Player))
					{
						prof.GainExperience(3);
						event.setDamage(((GugaHunter) prof).IncreaseDamage(event.getDamage()));
					}
				}
				else if(prof instanceof GugaMiner)
				{
					if (!(target instanceof Player))
					{
						prof.GainExperience(1);
					}
				}
				if (event.getEntity() instanceof LivingEntity)
				{
					// *********************** KILL **************************
					if (event.getDamage() >= target.getHealth())
					{
						if (prof instanceof GugaMiner)
						{
							if (target instanceof Creeper)
							{
								prof.GainExperience(20);
							}
							else if(target instanceof Spider)
							{
								prof.GainExperience(15);
							}
							else if(target instanceof Skeleton)
							{
								prof.GainExperience(15);
							}
							else if(target instanceof Zombie)
							{
								prof.GainExperience(10);
							}
							else if (target instanceof Player)
							{
								prof.GainExperience(200);
								damager.getWorld().dropItem(e.getEntity().getLocation(), new ItemStack(262,20));
							}
							else
							{
								prof.GainExperience(5);
							}
						}
						else if (prof instanceof GugaHunter)
						{
							if (target instanceof Creeper)
							{
								prof.GainExperience(75);
							}
							else if(target instanceof Spider)
							{
								prof.GainExperience(50);
							}
							else if(target instanceof Skeleton)
							{
								prof.GainExperience(50);
							}
							else if(target instanceof Zombie)
							{
								prof.GainExperience(25);
							}
							else if (target instanceof Player)
							{
								prof.GainExperience(200);
								damager.getWorld().dropItem(e.getEntity().getLocation(), new ItemStack(262,20));
							}
							else
							{
								prof.GainExperience(15);
							}
						}
						xpCache = target;
					}
				}
			}
		}
		if (plugin.config.accountsModule)
		{
			Player p[] = e.getEntity().getServer().getOnlinePlayers();
			int i=0;
			while (i < p.length)
			{
				if (e.getEntity().getEntityId() == p[i].getEntityId())
				{	
					if (!plugin.acc.UserIsLogged(p[i]))
					{
						e.setCancelled(true);
					}
					break;
				}
				i++;
			}
		}
	}
	public void onEntityDeath(EntityDeathEvent e)
	{
		if (e.getDroppedExp() > 0)
		{
			e.setDroppedExp(0);
		}
	}
	public void onEntityExplode(EntityExplodeEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("ENTITY_EXPLODE_EVENT: entity=" + e.getEntity().toString());
		}
		if (plugin.arena.IsArena(e.getLocation()))
		{
			e.setCancelled(true);
			return;
		}
		List<Block> blockList = e.blockList();
		
		Iterator<Block> iter = e.blockList().iterator();
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
			e.setCancelled(true);
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
	}
	private LivingEntity xpCache;
	public static Guga_SERVER_MOD plugin;
}
package me.Guga.Guga_SERVER_MOD.Listeners;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.BasicWorld;
import me.Guga.Guga_SERVER_MOD.GugaBonusDrop;
import me.Guga.Guga_SERVER_MOD.GugaEventWorld;
import me.Guga.Guga_SERVER_MOD.GugaProfession2;
import me.Guga.Guga_SERVER_MOD.GugaRegion;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Locker;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;
import me.Guga.Guga_SERVER_MOD.MinecraftPlayer;
import me.Guga.Guga_SERVER_MOD.Handlers.GameMasterHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaRegionHandler;
import org.bukkit.Material;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;

public class GugaBlockListener implements Listener
{
	public GugaBlockListener(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e)
	{
		MinecraftPlayer player = plugin.userManager.getUser(e.getPlayer().getName());
		if (!player.isAuthenticated())
		{
			e.setCancelled(true);
			return;
		}
		
		if(player.getProfession() != null && player.getProfession().GetLevel() < 10 && !BasicWorld.IsBasicWorld(e.getBlock().getLocation()))
		{
			e.getPlayer().sendMessage("Jste novacek. Novacci smi kopat jenom ve svete pro novacky.  Dostanete se tam /pp bw");
			e.setCancelled(true);
			return;
		}
		
		if (plugin.debug == true)
		{
			plugin.log.info("BLOCK_BREAK_EVENT: playerName="+e.getPlayer().getName()+",typeID="+e.getBlock().getTypeId());
		}
		
		//plugin.logger.LogBlockBreak(e, e.getBlock().getTypeId());
		long timeStart = System.nanoTime();
		Player p = e.getPlayer();
		if (plugin.arena.IsArena(p.getLocation()))
		{
			if (GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
			{
				return;
			}
			p.sendMessage(ChatColor.BLUE+"[ARENA]: "+ChatColor.WHITE+"V arene nemuzes kopat!");
			e.setCancelled(true);
			return;
		}
		if(plugin.EventWorld.IsEventWorld(p.getLocation()) && GugaEventWorld.regionStatus())
		{
			if (GameMasterHandler.IsAtleastRank(p.getName(), Rank.EVENTER))
			{
				return;
			}
			p.sendMessage(ChatColor.BLUE+"[EVENTWORLD]: "+ChatColor.WHITE+"V EventWorldu nemuzes kopat!");
			e.setCancelled(true);
			return;
		}
		
		if (!GugaRegionHandler.CanInteract(p, e.getBlock().getX(), e.getBlock().getZ()))
		{
			if (!GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
			{
				e.setCancelled(true);
				GugaRegion region = GugaRegionHandler.GetRegionByCoords(e.getBlock().getX(), e.getBlock().getZ(), p.getWorld().getName());
		        p.sendMessage(ChatColor.BLUE+"[REGIONS]: "+ ChatColor.WHITE + "Tady nemuzes kopat! Nazev pozemku: " + region.GetName());
		        if(region.GetWorld().equals("world_basic"))
		        {
		        	if(player.getProfession()!=null && player.getProfession().GetLevel() < 10)
		        	{
		        		BasicWorld.basicWorldRegionBlockBreak(region,e.getPlayer(),e.getBlock());
		        	}
		        }
				return;
			}
		}
		
		GugaProfession2 prof = player.getProfession();
		int level = prof.GetLevel();
		//*************************GRIEFING PROTECTION*************************
		Block targetBlock;
		targetBlock = e.getBlock();
		if(level >= 10 && BasicWorld.IsBasicWorld(e.getPlayer().getLocation()))
		{
			if(!GameMasterHandler.IsAtleastGM(e.getPlayer().getName()))
			{
				e.getPlayer().sendMessage("Pro opusteni zakladniho sveta napiste "+ ChatColor.YELLOW + "/pp spawn");
			}
		}
		//*************************************************************************
		int blockType = targetBlock.getTypeId();
		if (Locker.LockableBlocks.isLockableBlock(blockType))
		{
			if(plugin.blockLocker.IsLocked(targetBlock))
			{
				e.setCancelled(true);
				String owner = plugin.blockLocker.GetBlockOwner(targetBlock);
				if (owner.equalsIgnoreCase(p.getName()))
				{
					p.sendMessage(ChatColor.BLUE+"[LOCKER]:"+ChatColor.WHITE+" Nemuzete rozbit zamcenou truhlu! Nejdrive ji odemknete.");
				}
				else
				{
					p.sendMessage(ChatColor.BLUE+"[LOCKER]:"+ChatColor.WHITE+" Nemuzete rozbit zamcenou truhlu! " + owner + " je vlastnikem teto truhly.");
				}
				return;
			}
		}
		
		int typeId = targetBlock.getTypeId();
		prof.onBlockBreak(targetBlock);
		if (typeId == 1)
		{
			GugaBonusDrop bonus = prof.CobbleStoneDrop();
			if (bonus == GugaBonusDrop.DIAMOND)
			{
				p.getWorld().dropItem(targetBlock.getLocation(), new ItemStack(264,1));
				prof.GainExperience(50);
				p.sendMessage(ChatColor.BLUE+"[RPG]: "+ChatColor.WHITE+"Nasel jste diamant!");
			}
			else if (bonus == GugaBonusDrop.GOLD)
			{
				p.getWorld().dropItem(targetBlock.getLocation(), new ItemStack(14,1));
				prof.GainExperience(40);
				p.sendMessage(ChatColor.BLUE+"[RPG]: "+ChatColor.WHITE+"Nasel jste zlato!");
			}
			else if (bonus == GugaBonusDrop.IRON)
			{
				p.getWorld().dropItem(targetBlock.getLocation(), new ItemStack(15,1));
				prof.GainExperience(30);
				p.sendMessage(ChatColor.BLUE+"[RPG]: "+ChatColor.WHITE+"Nasel jste zelezo!");
			}
			else if (bonus == GugaBonusDrop.EMERALD)
			{
				p.getWorld().dropItem(targetBlock.getLocation(), new ItemStack(388,1));
				prof.GainExperience(60);
				p.sendMessage(ChatColor.BLUE+"[RPG]: "+ChatColor.WHITE+"Nasel jste emerald!");
			}
		}
		
		if (plugin.debug == true)
		{
			plugin.log.info("BLOCK_BREAK_EVENT: Time=" + ((System.nanoTime() - timeStart)/1000));
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e)
	{
		MinecraftPlayer player = plugin.userManager.getUser(e.getPlayer().getName());
		if (!player.isAuthenticated())
		{
			e.setCancelled(true);
			return;
		}
		
		if(player.getProfession() != null && player.getProfession().GetLevel() < 10 && !BasicWorld.IsBasicWorld(e.getBlock().getLocation()))
		{
			e.getPlayer().sendMessage("Jste novacek. Novacci smi stavet jenom ve svete pro novacky. Dostanete se tam /pp bw");
			e.setCancelled(true);
			return;
		}
		
		if (plugin.debug)
		{
			plugin.log.info("BLOCK_PLACE_EVENT: typeID="+e.getBlock().getTypeId()+",PlayerName="+e.getPlayer().getName());
		}
		//plugin.logger.LogBlockPlace(e);
		if (plugin.arena.IsArena(e.getBlock().getLocation()))
		{
			if (!GameMasterHandler.IsAtleastRank(e.getPlayer().getName(), Rank.BUILDER))
			{
				e.setCancelled(true);
				return;
			}
		}
		Player p = e.getPlayer(); 
		if(plugin.EventWorld.IsEventWorld(p.getLocation()) && GugaEventWorld.regionStatus())
		{
			if (GameMasterHandler.IsAtleastRank(p.getName(), Rank.EVENTER))
			{
				return;
			}
			p.sendMessage(ChatColor.BLUE+"[EVENTWORLD]: "+ChatColor.WHITE+"V EventWorldu nemuzes pokladat blocky!");
			e.setCancelled(true);
			return;
		}
		
		if (!GugaRegionHandler.CanInteract(e.getPlayer(), e.getBlock().getX(), e.getBlock().getZ()))
		{
			if (!GameMasterHandler.IsAtleastRank(e.getPlayer().getName(), Rank.BUILDER))
			{
				e.setCancelled(true);
				GugaRegion region = GugaRegionHandler.GetRegionByCoords(e.getBlock().getX(), e.getBlock().getZ(), p.getWorld().getName());
				e.getPlayer().sendMessage(ChatColor.BLUE+"[REGIONS]: "+ChatColor.WHITE+"Tady nemuzete stavet! Nazev pozemku: " + region.GetName());
			}
		}
		//54
		Block block = e.getBlockPlaced();
		if(Locker.LockableBlocks.isLockableBlock(block.getTypeId()))
		{
			if(block.getTypeId() == Locker.LockableBlocks.CHEST.getID() && !plugin.blockLocker.IsLocked(block))
			{
				Block W=block.getRelative(BlockFace.WEST);
				Block E=block.getRelative(BlockFace.EAST);
				Block N=block.getRelative(BlockFace.NORTH);
				Block S=block.getRelative(BlockFace.SOUTH);
				
				if(W.getTypeId() == 54 || E.getTypeId() == 54 || N.getTypeId() == 54 || S.getTypeId() == 54)
				{
					String username = e.getPlayer().getName();
					if((plugin.blockLocker.IsLocked(W) && plugin.blockLocker.IsOwner(W,username)) ||
						(plugin.blockLocker.IsLocked(W) && plugin.blockLocker.IsOwner(E,username)) ||
						(plugin.blockLocker.IsLocked(W) && plugin.blockLocker.IsOwner(N,username)) ||
						(plugin.blockLocker.IsLocked(W) && plugin.blockLocker.IsOwner(S,username)))
					{
						p.sendMessage(ChatColor.BLUE+"[LOCKER]:"+ChatColor.WHITE+" Nemuzete postavit truhlu vedle zamcene truhly!");
						e.setCancelled(true);
						return;
					}
					else
					{
						plugin.blockLocker.LockBlock(block,e.getPlayer().getName());
						p.sendMessage(ChatColor.BLUE + "[AUTOLOCKER]: " + ChatColor.WHITE+"Vase dvojtruhla byla zamcena.");
						return;
					}
				}
				else
				{
					plugin.blockLocker.LockBlock(block, e.getPlayer().getName());
					p.sendMessage(ChatColor.BLUE + "[AUTOLOCKER]: " + ChatColor.WHITE+"Vase truhla byla zamcena.");
					return;
				}			
			}
			else if(block.getTypeId() == Locker.LockableBlocks.FURNANCE.getID() && !plugin.blockLocker.IsLocked(block))
			{
				plugin.blockLocker.LockBlock(block,e.getPlayer().getName());
				p.sendMessage(ChatColor.BLUE + "[AUTOLOCKER]: " + ChatColor.WHITE+"Vase pec byla zamcena.");
			}
			else if(block.getTypeId() == Locker.LockableBlocks.BURNING_FURNANCE.getID() && !plugin.blockLocker.IsLocked(block))
			{
				plugin.blockLocker.LockBlock(block,e.getPlayer().getName());
				p.sendMessage(ChatColor.BLUE + "[AUTOLOCKER]: " + ChatColor.WHITE+"Vase pec byla zamcena.");
			}
			else if(block.getTypeId() == Locker.LockableBlocks.DISPENSER.getID() && !plugin.blockLocker.IsLocked(block))
			{
				plugin.blockLocker.LockBlock(block,e.getPlayer().getName());
				p.sendMessage(ChatColor.BLUE + "[AUTOLOCKER]: " + ChatColor.WHITE+"Vas davkovac byl zamcen.");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent e)
	{
		//prevents fire from spreading
		if(e.getCause() == IgniteCause.SPREAD)
		{
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockSpread(BlockSpreadEvent e)
	{
		if(e.getBlock().getType() == Material.FIRE)
		{
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
 	public void onBlockRedstoneChange(BlockRedstoneEvent e)
	{
		Block block = e.getBlock();
		int blockID = block.getTypeId();
		if (plugin.debug)
		{
			plugin.log.info("REDSTONE_CHANGE_EVENT: typeID=" + blockID + ",blockData=" + block.getData() + ",x=" + block.getX() + ",y=" + block.getY() + ",z=" + block.getZ());
		}
		if (!redStoneDebug.isEmpty())
		{
			Iterator<Player> it = redStoneDebug.iterator();
			while(it.hasNext())
			{
				it.next().sendMessage("RS_EVENT: ID=" + blockID + ",x=" + block.getX() + ",y=" + block.getY() + ",z=" + block.getZ());
			}
		}
	}

	public ArrayList<Player> redStoneDebug = new ArrayList<Player>();
	public static Guga_SERVER_MOD plugin;
}

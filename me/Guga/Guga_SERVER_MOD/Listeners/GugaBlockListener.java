package me.Guga.Guga_SERVER_MOD.Listeners;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.BasicWorld;
import me.Guga.Guga_SERVER_MOD.GugaBonusDrop;
import me.Guga.Guga_SERVER_MOD.GugaEventWorld;
import me.Guga.Guga_SERVER_MOD.GugaProfession;
import me.Guga.Guga_SERVER_MOD.GugaRegion;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Locker;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;
import me.Guga.Guga_SERVER_MOD.Handlers.GameMasterHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaRegionHandler;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
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
		if (plugin.debug == true)
		{
			plugin.log.info("BLOCK_BREAK_EVENT: playerName="+e.getPlayer().getName()+",typeID="+e.getBlock().getTypeId());
		}
		if(e.getPlayer().getItemInHand().getTypeId() == ID_SELECT_ITEM && GameMasterHandler.IsAdmin(e.getPlayer().getName()))
		{
			e.setCancelled(true);
			return;
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
				return;
			}
		}
		
		if (!plugin.acc.UserIsLogged(p) && plugin.config.accountsModule)
		{
			e.setCancelled(true);
			p.sendMessage("******************************");
			if (plugin.acc.UserIsRegistered(p))
			{
				p.sendMessage("NEJSTE PRIHLASEN! Prihlaste se pomoci /login heslo");
			}
			else
			{
				p.sendMessage("NEJSTE ZAREGISTROVAN! Prosim zaregistrujte se /register heslo");
			}
			p.sendMessage("******************************");
			return;
		}
		GugaProfession prof = plugin.professions.get(p.getName());
		int level = prof.GetLevel();
		//boolean canBreak = false;
		//*************************GRIEFING PROTECTION*************************
		Block targetBlock;
		targetBlock = e.getBlock();
		/*if (prof == null)
		{
			int i = 0;
			while (i < allowedBlocksTier1.length)
			{
				if (e.getBlock().getTypeId() == allowedBlocksTier1[i])
				{
					canBreak = true;
					break;
				}
				i++;
			}
			if (canBreak)
			{
				Block blockAbove = targetBlock.getRelative(BlockFace.UP);
				if (blockAbove.getTypeId() == 55)
				{
					canBreak = false;
				}
			}
			if (GameMasterHandler.IsAtleastGM(p.getName()))
			{
				if(plugin.acc.UserIsLogged(p))
				{
					canBreak = true;
				}
			}
			if (!canBreak)
			{
				p.sendMessage(ChatColor.BLUE+"[RPG]: "+ChatColor.WHITE+"Musite byt alespon level 5, aby jste mohl kopat tento druh bloku!");
				e.setCancelled(true);
				return;
			}
		}
		else
		{
			level = prof.GetLevel();
			if (level < 5)
			{
				int i = 0;
				while (i < allowedBlocksTier1.length)
				{
					if (e.getBlock().getTypeId() == allowedBlocksTier1[i])
					{
						canBreak = true;
						break;
					}
					i++;
				}
				if (canBreak)
				{
					Block blockAbove = targetBlock.getRelative(BlockFace.UP);
					if (blockAbove.getTypeId() == 55)
					{
						canBreak = false;
					}
				}
				if (GameMasterHandler.IsAtleastGM(p.getName()))
				{
					if(plugin.acc.UserIsLogged(p))
					{
						canBreak = true;
					}
				}
				if (!canBreak)
				{
					p.sendMessage(ChatColor.BLUE+"[RPG]: "+ChatColor.WHITE+"Musite byt alespon level 5, aby jste mohl kopat tento druh bloku!");
					e.setCancelled(true);
					return;
				}
			}*/
		if(level >= 10 && BasicWorld.IsBasicWorld(e.getPlayer().getLocation()))
		{
			if(!GameMasterHandler.IsAtleastGM(e.getPlayer().getName()))
			{
				e.getPlayer().sendMessage("Pro opusteni zakladniho sveta napiste "+ ChatColor.YELLOW + "/world");
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
				if (owner == p.getName())
				{
					p.sendMessage(ChatColor.BLUE+"[LOCKER]:"+ChatColor.WHITE+" Nemuzete rozbit zamcenou truhlu! Nejdrive ji odemknete.");
				}
				else
				{
					p.sendMessage(ChatColor.BLUE+"[LOCKER]:"+ChatColor.WHITE+" Nemuzete rozbit zamcenou truhlu! " + owner + " je vlastnikem teto truhly.");
				}
			}
		}
		
		int typeId = targetBlock.getTypeId();
		prof.GainExperience(3);
		//TODO: balance experience
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
		
		/*else if ((typeId == 15) || (typeId == 14) || (typeId == 56))
			{
				int drops;
				if (dropsCache.contains(targetBlock))
				{
					dropsCache.remove(dropsCache.indexOf(targetBlock));
					return;
				}
				boolean bonusExp = false;
				drops = ((GugaMiner)prof).BonusDrop(targetBlock);
				int i = 0;
				while (i< drops)
				{
					if (typeId ==56)
					{
						p.getWorld().dropItem(targetBlock.getLocation(), new ItemStack(264,1));
					}
					else
					{
						p.getWorld().dropItem(targetBlock.getLocation(), new ItemStack(targetBlock.getTypeId(),1));
					}
					bonusExp = true;
					if (i == 1)
					{
						i++;
					}//**************ON BLOCK PLACE**************
					i++;
				}
				if (bonusExp)
				{
					prof.GainExperience(25);
				}
			}
		}
		else if (prof instanceof GugaHunter)
		{
			int typeId = targetBlock.getTypeId();
			if (!IsInstaBreakBlock(typeId))
			{*/
				prof.GainExperience(1);
			//}
		//}
		if (plugin.debug == true)
		{
			plugin.log.info("BLOCK_BREAK_EVENT: Time=" + ((System.nanoTime() - timeStart)/1000));
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e)
	{
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
					String wo = plugin.blockLocker.GetBlockOwner(W);
					String eo = plugin.blockLocker.GetBlockOwner(E);
					String no = plugin.blockLocker.GetBlockOwner(N);
					String so = plugin.blockLocker.GetBlockOwner(S);
					if( (wo!=null && !username.matches(wo) && plugin.blockLocker.IsLocked(W)) ||
						(eo!=null && !username.matches(eo) && plugin.blockLocker.IsLocked(E)) ||
						(no!=null && !username.matches(no) && plugin.blockLocker.IsLocked(N)) ||
						(so!=null && !username.matches(so) && plugin.blockLocker.IsLocked(S)))
					{
						p.sendMessage(username+"><"+plugin.blockLocker.GetBlockOwner(S)+".."+plugin.blockLocker.IsLocked(S));
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
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBurn(BlockBurnEvent e)
	{
		Block block = e.getBlock();
		if (block.getTypeId() == 5)
		{
			Block relBlock;
			BlockFace faceVals[] = BlockFace.values();
			int i = 0;
			while (i < faceVals.length)
			{
				relBlock = block.getRelative(faceVals[i]);
				if (relBlock.getTypeId() == 51)
				{
					relBlock.setTypeId(0);
				}
				i++;
			}
			
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
	private int ID_SELECT_ITEM = 269;
	public int[] allowedBlocksTier1 = {1, 2, 3, 12, 13, 14, 15, 16, 17, 18, 24, 31, 32, 37, 38, 39, 40, 56, 78, 79, 81, 82};
	public int[] instaBreakBlocks = {6, 18, 30, 31, 32, 37, 38, 39, 40, 50, 55, 59, 75, 76, 83};
	public ArrayList<Block> dropsCache = new ArrayList<Block>();
	public ArrayList<Player> redStoneDebug = new ArrayList<Player>();
	public static Guga_SERVER_MOD plugin;
}

package me.MnC.MnC_SERVER_MOD.Listeners;

import java.util.ArrayList;
import java.util.Iterator;

import me.MnC.MnC_SERVER_MOD.GugaEventWorld;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.ServerRegion;
import me.MnC.MnC_SERVER_MOD.Estates.EstateHandler;
import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.ServerRegionHandler;
import me.MnC.MnC_SERVER_MOD.RPG.BonusDrop;
import me.MnC.MnC_SERVER_MOD.RPG.PlayerProfession;
import me.MnC.MnC_SERVER_MOD.basicworld.BasicWorld;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

public class BlockListener implements Listener
{
	public BlockListener(MnC_SERVER_MOD gugaSM)
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
			ChatHandler.FailMsg(player.getPlayerInstance(), "Jste novacek. Novacci smi stavet jenom ve svete pro novacky. Dostanete se tam /pp bw.");
			e.setCancelled(true);
			return;
		}
		
		
		if (plugin.debug == true)
		{
			plugin.log.info("BLOCK_BREAK_EVENT: playerName="+e.getPlayer().getName()+",typeID="+e.getBlock().getTypeId());
		}
		Player p = e.getPlayer();
		long timeStart = System.nanoTime();
		if (plugin.arena.IsArena(p.getLocation()))
		{
			if (GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
			{
				return;
			}
			ChatHandler.FailMsg(p,"V arene nemuzete kopat.");
			e.setCancelled(true);
			return;
		}
		if(plugin.EventWorld.IsEventWorld(p.getLocation()) && GugaEventWorld.regionStatus())
		{
			if (GameMasterHandler.IsAtleastRank(p.getName(), Rank.EVENTER))
			{
				return;
			}
			ChatHandler.FailMsg(p, "V EventWorldu nemuzete kopat!");
			e.setCancelled(true);
			return;
		}
		
		if (!ServerRegionHandler.CanInteract(p, e.getBlock().getX(), e.getBlock().getZ()))
		{
			if (!GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
			{
				e.setCancelled(true);
				ServerRegion region = ServerRegionHandler.GetRegionByCoords(e.getBlock().getX(), e.getBlock().getZ(), p.getWorld().getName());
				ChatHandler.FailMsg(p, "Tady nemuzete kopat! Nazev pozemku: " + ChatColor.YELLOW + region.GetName());
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
		
		//UserRegions
		if(!EstateHandler.canInteract(player.getName(), e.getBlock()) && !GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
		{
			e.setCancelled(true);
			ChatHandler.FailMsg(p, "Nemuzete kopat blocky na pozemku jineho hrace.");
		}
		
		PlayerProfession prof = player.getProfession();
		int level = prof.GetLevel();
		//*************************GRIEFING PROTECTION*************************
		Block block;
		block = e.getBlock();
		if(level >= 10 && BasicWorld.IsBasicWorld(e.getPlayer().getLocation()))
		{
			if(!GameMasterHandler.IsAtleastGM(e.getPlayer().getName()) && !(level > 20))
			{
				ChatHandler.InfoMsg(p, "Pro opusteni zakladniho sveta napiste /pp spawn");
			}
		}
		
		if(!(block.getTypeId() == 50 || block.getTypeId() == 78 ))
		{
			prof.GainExperience(4);
		}
		
		if (plugin.debug == true)
		{
			plugin.log.info("BLOCK_BREAK_EVENT: Time=" + ((System.nanoTime() - timeStart)/1000));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreakMonitor(BlockBreakEvent event)
	{
		MinecraftPlayer player = plugin.userManager.getUser(event.getPlayer().getName());
		BonusDrop.dropBonusDrops(player.getProfession(), event.getBlock());
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
		
		if(player.getProfession() == null || player.getProfession().GetLevel() < 10 && !BasicWorld.IsBasicWorld(e.getBlock().getLocation()))
		{
			ChatHandler.FailMsg(player.getPlayerInstance(), "Jste novacek. Novacci smi stavet jenom ve svete pro novacky. Dostanete se tam /pp bw.");
			e.setCancelled(true);
			return;
		}
		
	
		if(e.getBlock().getType() == Material.TNT && (player.getProfession() == null || player.getProfession().GetLevel() < 50))
		{
			ChatHandler.FailMsg(player.getPlayerInstance(), "Nemate lvl 50, nemuzete pouzit TNT.");
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
			ChatHandler.FailMsg(p, "V EventWorldu nemuzete pokladat blocky!");
			e.setCancelled(true);
			return;
		}
		
		if (!ServerRegionHandler.CanInteract(e.getPlayer(), e.getBlock().getX(), e.getBlock().getZ()))
		{
			if (!GameMasterHandler.IsAtleastRank(e.getPlayer().getName(), Rank.BUILDER))
			{
				e.setCancelled(true);
				ServerRegion region = ServerRegionHandler.GetRegionByCoords(e.getBlock().getX(), e.getBlock().getZ(), p.getWorld().getName());
				ChatHandler.FailMsg(p, "Tady nemuzete stavet! Nazev pozemku: " + ChatColor.YELLOW  + region.GetName());
				return;
			}
		}
		
		//UserRegions
		if(!EstateHandler.canInteract(player.getName(), e.getBlock()) && !GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
		{
			e.setCancelled(true);
			ChatHandler.FailMsg(p, "Nemuzete pokladat blocky na pozemku jineho hrace.");
		}
		
		Block block = e.getBlockPlaced();
		
		if(block.getTypeId() == 19)
		{
			World world = block.getWorld();
			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			this.clearWaterSponge(world, x, y, z, 4);
			block.setTypeId(12);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent e)
	{
		BlockIgniteEvent.IgniteCause cause = e.getCause();

		if (cause == BlockIgniteEvent.IgniteCause.LIGHTNING)
		{
			e.setCancelled(true);
			return;
		}

		if (cause == BlockIgniteEvent.IgniteCause.LAVA)
		{
			e.setCancelled(true);
			return;
		}
		
		if (cause == BlockIgniteEvent.IgniteCause.SPREAD)
		{
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent e)
	{
		e.setCancelled(true);
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
	private void clearWaterSponge(World world, int x, int y, int z, int radius)
	{
		for (int cx = -radius; cx <= radius; cx++) 
		{
			for (int cy = -radius; cy <=radius; cy++) 
			{
				for (int cz = -radius; cz <= radius; cz++) 
				{
					if (world.getBlockTypeIdAt(x + cx, y + cy, z + cz) == 8 || world.getBlockTypeIdAt(x + cx, y + cy, z + cz) == 9) //isWater?
					{
						world.getBlockAt(x + cx, y + cy, z + cz).setTypeId(0);
					}
				}
			}
		}
	}

	public ArrayList<Player> redStoneDebug = new ArrayList<Player>();
	public static MnC_SERVER_MOD plugin;
}

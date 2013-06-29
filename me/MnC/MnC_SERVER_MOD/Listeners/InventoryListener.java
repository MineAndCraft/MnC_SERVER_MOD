package me.MnC.MnC_SERVER_MOD.Listeners;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener
{
	public InventoryListener(){}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event)
	{
		InventoryHolder source = null;
		
		try{
			source = event.getSource().getHolder();
		}catch(Exception ex){}
		
		if(source ==  null)
			return;
		
		Block block = null;
		
		if(source instanceof DoubleChest)
		{
			Chest chest = ((Chest)((DoubleChest) source).getRightSide());
			block = chest.getBlock();
		}
		else if(source instanceof Chest)
		{
			Chest chest = (Chest)source;
			block = chest.getBlock();
		}
		else if(source instanceof Furnace)
		{
			Furnace furnace = (Furnace)source;
			block = furnace.getBlock();
		}
		
		if(block != null)
		{
			if(MnC_SERVER_MOD.getInstance().blockLocker.isLocked(block))
			{
				event.setCancelled(true);
				// Is it a hopper? Break it.
				try{
					if(event.getInitiator().getHolder() instanceof Hopper)
					{
						Location loc = ((Hopper)event.getInitiator().getHolder()).getLocation();
						final int x = loc.getBlockX();
						final int y = loc.getBlockY();
						final int z = loc.getBlockZ();
						final String w = loc.getWorld().getName();
						MnC_SERVER_MOD.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MnC_SERVER_MOD.getInstance(),new Runnable(){
							public void run(){
								try{
									MnC_SERVER_MOD.getInstance().getServer().getWorld(w).getBlockAt(x, y, z).breakNaturally();
								}catch(Exception e){}
							}
						});
					}
				}catch(Exception ex){}
			}
		}
	}
}

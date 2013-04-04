package me.MnC.MnC_SERVER_MOD.Listeners;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener
{
	public InventoryListener()
	{}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent e)
	{
		InventoryHolder source = e.getSource().getHolder();
		InventoryHolder dest = e.getDestination().getHolder();
		InventoryType sourceType = source.getInventory().getType();
		InventoryType destType = dest.getInventory().getType();
		//if chest is destination
		if(destType == InventoryType.CHEST)
		{
			Chest chest = null;
			try{
				Chest ch = (Chest)dest;
				chest = ch;
			}catch(ClassCastException ex)
			{
				try{
					DoubleChest ch = (DoubleChest)dest;
					chest = (Chest)ch.getLeftSide();
				}catch(Exception ex2){ return;}
			}
			
			if(MnC_SERVER_MOD.getInstance().blockLocker.isLocked(chest.getBlock()))
			{
				e.setCancelled(true);
			}
		}
		else if(destType == InventoryType.FURNACE)
		{
			Furnace furnace = (Furnace)dest;
			if(MnC_SERVER_MOD.getInstance().blockLocker.isLocked(furnace.getBlock()))
			{
				e.setCancelled(true);
			}
		}

		//if chest is source
		else if(sourceType == InventoryType.CHEST)
		{
			Chest chest = null;
			try{
				Chest ch = (Chest)dest;
				chest = ch;
			}catch(ClassCastException ex)
			{
				try{
					DoubleChest ch = (DoubleChest)dest;
					chest = (Chest)ch.getLeftSide();
				}catch(Exception ex2){ return;}
			}
			if(MnC_SERVER_MOD.getInstance().blockLocker.isLocked(chest.getBlock()))
			{
				e.setCancelled(true);
			}
		}
		else if(sourceType == InventoryType.FURNACE)
		{
			Furnace furnace = (Furnace)source;
			if(MnC_SERVER_MOD.getInstance().blockLocker.isLocked(furnace.getBlock()))
			{
				e.setCancelled(true);
			}
		}
	}
}

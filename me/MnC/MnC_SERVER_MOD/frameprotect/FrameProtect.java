package me.MnC.MnC_SERVER_MOD.frameprotect;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.UserManager;
import me.MnC.MnC_SERVER_MOD.Estates.EstateHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class FrameProtect implements Listener
{
	public FrameProtect()
	{
		MnC_SERVER_MOD.getInstance().getServer().getPluginManager().registerEvents(this, MnC_SERVER_MOD.getInstance());
	}	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDestroyByEntity(HangingBreakByEntityEvent event)
	{
		Entity entity = event.getEntity();
		if(!entity.getWorld().getName().equalsIgnoreCase("world"))
			return;
		
		int estateId = EstateHandler.getResidenceId(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ());
		if(estateId > 0)
		{
			if(event.getRemover() instanceof Player)
			{
				Player player = (Player)event.getRemover();
				if(!GameMasterHandler.IsAtleastRank(player.getName(), Rank.BUILDER) && !EstateHandler.hasUserResidenceAccess(estateId, UserManager.getInstance().getUser(player.getName()).getId()))
				{
					player.sendMessage("You cannot break this frame. This estate is not yours.");
					event.setCancelled(true);
				}
			}
			else // disable breaking by non-players
			{
				event.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onClick(PlayerInteractEntityEvent event)
	{
		Entity entity = event.getRightClicked();
		
		if(!(entity instanceof ItemFrame || entity instanceof Painting))
			return;
		
		if(!entity.getWorld().getName().equalsIgnoreCase("world"))
			return;
		
		int estateId = EstateHandler.getResidenceId(entity.getLocation().getBlockX(), entity.getLocation().getBlockZ());
		if(estateId > 0)
		{
			Player player = event.getPlayer();
			if(!GameMasterHandler.IsAtleastRank(player.getName(), Rank.BUILDER) && !EstateHandler.hasUserResidenceAccess(estateId, UserManager.getInstance().getUser(player.getName()).getId()))
			{
				player.sendMessage("You cannot break this frame. This estate is not yours.");
				event.setCancelled(true);
			}
		}
	}	
}

package me.MnC.MnC_SERVER_MOD.Listeners;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class CustomListener implements Listener
{
	private MnC_SERVER_MOD plugin;
	
	public CustomListener(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onVotifierEvent(VotifierEvent event) 
	{
		Vote vote = event.getVote();
		String player = vote.getUsername();
		Player p;
		if(plugin.userManager.userIsRegistered(player))
		{
			plugin.currencyManager.addCredits(player, 5);
			plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "Hrac " + ChatColor.LIGHT_PURPLE + player + ChatColor.DARK_GREEN +" ziskava 4 kredity, 3 diamanty a 1 emerald za hlasovani. " + ChatColor.AQUA + "Hlasuj take.");
			if((p = plugin.getServer().getPlayer(player)) != null)
			{
				give(p);
			}
			
		}
	}
	
	public void give(Player p)
	{
		p.getInventory().addItem(new ItemStack(264, 3));
		p.getInventory().addItem(new ItemStack(388, 1));
	}

	public final ItemStack gift_DIAMOND = new ItemStack(264, 1);
	public final ItemStack gift_EMERALD = new ItemStack(388, 1);
}

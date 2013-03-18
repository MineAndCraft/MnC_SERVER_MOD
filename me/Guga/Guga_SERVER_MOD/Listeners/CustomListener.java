package me.Guga.Guga_SERVER_MOD.Listeners;

import java.util.ArrayList;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import com.vexsoftware.votifier.model.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CustomListener implements Listener
{
	public CustomListener(Guga_SERVER_MOD plugin)
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
			plugin.currencyManager.addCredits(player, 1);
			plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "Hrac " + ChatColor.LIGHT_PURPLE + player + ChatColor.DARK_GREEN +" ziskava po 1 kreditu, 1 diamantu a 1 emeraldu za hlasovani. Hlasuj take.");
			if((p = plugin.getServer().getPlayer(player)) != null)
			{
				give(p);
			}
			
		}
	}
	public void give(Player p)
	{
		p.getInventory().addItem(new ItemStack(264, 1));
		p.getInventory().addItem(new ItemStack(388, 1));
	}
	public ArrayList<String> notGifted= new ArrayList<String>();
	public ItemStack gift_DIAMOND = new ItemStack(264, 1);
	public ItemStack gift_EMERALD = new ItemStack(388, 1);
	private Guga_SERVER_MOD plugin;
}

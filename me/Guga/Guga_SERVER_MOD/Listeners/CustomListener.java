package me.Guga.Guga_SERVER_MOD.Listeners;

import java.util.ArrayList;

import me.Guga.Guga_SERVER_MOD.GugaVirtualCurrency;
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
		GugaVirtualCurrency curr;
		Player p;
		if((curr = plugin.FindPlayerCurrency(player)) != null)
		{
			curr.AddCurrency(5);
			plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "Hrac " + player + " ziskava 5 kreditu, 3 diamanty a 1 emerald za hlasovani. Hlasuj take.");
			if((p = plugin.getServer().getPlayer(player)) != null)
			{
				give(p);
			}
			
		}
	}
	public void give(Player p)
	{
		p.getInventory().addItem(gift_DIAMOND);
		p.getInventory().addItem(gift_EMERALD);
	}
	public ArrayList<String> notGifted= new ArrayList<String>();
	public ItemStack gift_DIAMOND = new ItemStack(264, 3);
	public ItemStack gift_EMERALD = new ItemStack(388, 1);
	private Guga_SERVER_MOD plugin;
}

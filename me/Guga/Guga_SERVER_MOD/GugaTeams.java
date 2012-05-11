package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GugaTeams 
{
	public static void addToTeam(String[]names, String team)
	{
		int i=0;
		if(team.equalsIgnoreCase("blue"))
		{
			while(i<names.length)
			{
				if(!blueTeamPlayers.contains(names[i]))
				{
					blueTeamPlayers.add(names[i]);
				}
				i++;
			}
			return;
		}
		else if(team.equalsIgnoreCase("red"))
		{
			while(i<names.length)
			{
				if(!redTeamPlayers.contains(names[i]))
				{
					redTeamPlayers.add(names[i]);
				}
				i++;
			}
		}
	}
	public void deleteTeams()
	{
		blueTeamPlayers=null;
		redTeamPlayers=null;
	}
	public void removePlayer(String name)
	{
		if(blueTeamPlayers.contains(name))
		{
			blueTeamPlayers.remove(name);
		}
		else if(redTeamPlayers.contains(name))
		{
			redTeamPlayers.remove(name);
		}
	}
	public void deleteInventories()
	{
		int i=0;
		while(i<blueTeamPlayers.size())
		{
			Player p = plugin.getServer().getPlayer(blueTeamPlayers.iterator().next());
			InventoryBackup.CreateBackup(p.getName(), p.getInventory().getArmorContents(), p.getInventory().getContents());
			InventoryBackup.InventoryClearWrapped(p);
			i++;
		}
		while(i<redTeamPlayers.size())
		{
			Player p = plugin.getServer().getPlayer(redTeamPlayers.iterator().next());
			InventoryBackup.CreateBackup(p.getName(), p.getInventory().getArmorContents(), p.getInventory().getContents());
			InventoryBackup.InventoryClearWrapped(p);
			i++;
		}
	}
	public void backInventories()
	{
		int i=0;
		while(i<blueTeamPlayers.size())
		{
			Player p = plugin.getServer().getPlayer(blueTeamPlayers.iterator().next());
			InventoryBackup.InventoryReturnWrapped(p, true);
			i++;
		}
		while(i<redTeamPlayers.size())
		{
			Player p = plugin.getServer().getPlayer(redTeamPlayers.iterator().next());
			InventoryBackup.InventoryReturnWrapped(p, true);
			i++;
		}
	}
	public void addItem(String team,int itemID,int ammount)
	{
		int i = 0;
		if(team.equalsIgnoreCase("blue"))
		{
			while(i<blueTeamPlayers.size())
			{
				Player p = plugin.getServer().getPlayer(blueTeamPlayers.iterator().next());
				p.getInventory().addItem(new ItemStack(itemID, ammount));
			}
		}
		else if(team.equalsIgnoreCase("red"))
		{
			while(i<redTeamPlayers.size())
			{
				Player p = plugin.getServer().getPlayer(redTeamPlayers.iterator().next());
				p.getInventory().addItem(new ItemStack(itemID, ammount));
			}
		}
	}
	public void disable()
	{
		backInventories();
		deleteTeams();
	}
	private static ArrayList<String> blueTeamPlayers = new ArrayList<String>();
	private static ArrayList<String> redTeamPlayers = new ArrayList<String>();
	private static Guga_SERVER_MOD plugin;
}

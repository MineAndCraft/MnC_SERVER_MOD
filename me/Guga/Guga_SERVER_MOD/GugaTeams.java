package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GugaTeams 
{
	public static void SetPlugin(Guga_SERVER_MOD GugaSM)
	{
		plugin = GugaSM;
	}
	public static void addToTeam(String[]names, String team)
	{
		int i=0;
		if(team.equalsIgnoreCase("blue"))
		{
			while(i<names.length)
			{
				if(!redTeamPlayers.contains(names[i]) && (!blueTeamPlayers.contains(names[i])))
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
				if(!redTeamPlayers.contains(names[i]) && (!blueTeamPlayers.contains(names[i])))
				{
					redTeamPlayers.add(names[i]);
				}
				i++;
			}
		}
	}
	
	public static void removePlayer(String name)
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
	
	public static void AddItemToPlayers(int itemID, int amount, String team)
	{
		if(team.equalsIgnoreCase("blue"))
		{
			Iterator<String> i = blueTeamPlayers.iterator();
			while (i.hasNext())
			{
				Player p = plugin.getServer().getPlayer(i.next());
				if (p != null)
					p.getInventory().addItem(new ItemStack(itemID, amount));
			}
		}
		else if(team.equalsIgnoreCase("red"))
		{
			Iterator<String> i = redTeamPlayers.iterator();
			while (i.hasNext())
			{
				Player p = plugin.getServer().getPlayer(i.next());
				if (p != null)
					p.getInventory().addItem(new ItemStack(itemID, amount));
			}
		}
	}
	
	public static void deleteTeams()
	{
		blueTeamPlayers.clear();
		redTeamPlayers.clear();
	}
	private static ArrayList<String> blueTeamPlayers = new ArrayList<String>();
	private static ArrayList<String> redTeamPlayers = new ArrayList<String>();
	private static Guga_SERVER_MOD plugin;
}

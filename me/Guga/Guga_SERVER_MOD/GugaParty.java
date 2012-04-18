package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GugaParty 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		GugaParty.plugin = plugin;
	}
	public static boolean isInParty(String playerName)
	{
		if(players.containsKey(playerName))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static void addParty(Player p,String partyName)
	{
		if(!(partyExists(partyName)))
		{
			if(!(isInParty(p.getName())))
			{
				players.put(p.getName(), partyName);
				parties.add(partyName);
				p.sendMessage("Party byla vytvoøena");
			}
			else
			{
				p.sendMessage("V party jiz jste");
			}
		}
		else
		{
			p.sendMessage("Party s timto jmenem uz existuje");
		}
	}
	public static boolean partyExists(String partyName)
	{
		if(parties.contains(partyName))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static void invitePlayer(Player p,String playerName)
	{
		if(plugin.getServer().getPlayer(playerName)!=null)
		{
			Player target=plugin.getServer().getPlayer(playerName);
			String partyName=players.get(p.getName());
			invites.put(target.getName(),partyName);
			target.sendMessage(ChatColor.DARK_PURPLE+"Byl jste pozván do party chatu hracem " + p.getName()+". Napiste /invite pro prijmuti!");
		}
		else
		{
			p.sendMessage("Hrac s timto nickem neni on-line");
		}
	}
	public static void inviteAccepted(Player p)
	{
		if(invites.containsKey(p.getName()))
		{
			String party=invites.get(p.getName());
			invites.remove(p.getName());
			if(!(players.containsKey(p.getName())))
			{
				players.put(p.getName(),party);
				p.sendMessage("Jste v party chatu!");
			}
			else
			{
				p.sendMessage("V party jiz jste");
			}
		}
		else
		{
			p.sendMessage("Nebyl jste pozvan do party");
		}
	}
	public static void removePlayer(Player p)
	{
		if(players.containsKey(p.getName()))
		{
			players.remove(p.getName());
			p.sendMessage("Opustil jste party");
		}
		else
		{
			p.sendMessage("Nejste v zadne party");
		}
	}
	public static void sendMessage(Player p, String message)
	{
		if(players.containsKey(p.getName()))
		{
			String playersParty=players.get(p.getName());
			Iterator<Entry<String, String>> it = players.entrySet().iterator();
			while(it.hasNext())
			{
				Entry <String, String>e = it.next();
				if(e.getValue().matches(playersParty))
				{
					if(plugin.getServer().getPlayer(e.getKey())!=null)
					{
						Player target=plugin.getServer().getPlayer(e.getKey());
						target.sendMessage(ChatColor.DARK_GREEN+"[PARTY] "+ChatColor.WHITE+"<"+p.getName()+"> "+message);
					}
				}
			}
		}
		else
		{
			p.sendMessage("Nejste v party");
		}
	}
	
	private static ArrayList<String> parties = new ArrayList<String>();
	public static HashMap<String,String> players = new HashMap<String,String>(); //player>party
	private static Guga_SERVER_MOD plugin;
	public static HashMap<String,String> invites = new HashMap<String,String>(); //player>party
}

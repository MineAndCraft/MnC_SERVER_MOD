package me.Guga.Guga_SERVER_MOD;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public class GugaMute 
{
	public static boolean toggleChatMute()
	{
		if(muteForAll==false)
		{
			muteForAll=true;
			return muteForAll;
		}
		else
		{
			muteForAll=false;
			return muteForAll;
		}
	}
	public static boolean statusChatMute()
	{
		return muteForAll;
	}
	public static void addPlayer(String name,int time)
	{
		long x=System.currentTimeMillis()+(time * 60000); //60s*1000mls xD
		if(mutes.containsKey(name))
		{
			mutes.remove(name);
			mutes.put(name, x);
		}
		else
		{
			mutes.put(name, x);
		}
	}
	public static void removePlayer(String name)
	{
		mutes.remove(name);
	}
	public static boolean getPlayerStatus(String name)
	{
		Long timeOfEnd=mutes.get(name);
		if(!(mutes.isEmpty()))
		{
			if(timeOfEnd==null)
			{
				return false;
			}
			else
			{
				if(System.currentTimeMillis()>mutes.get(name))
				{
					removePlayer(name);
					return false;
				}
				else
					return true;
			}
		}
		else
		{
			return false;
		}
	}
	public static void printPlayers(Player p)
	{
		p.sendMessage("LIST OF ACCTUALLY MUTED PLAYERS");
		Iterator<Entry<String, Long>> it = mutes.entrySet().iterator();
		while(it.hasNext())
		{
			Entry <String, Long>e =  it.next(); 
			p.sendMessage(e.getKey());
		}
	}
	static boolean muteForAll=false;
	public static HashMap<String,Long> mutes = new HashMap<String,Long>();
}

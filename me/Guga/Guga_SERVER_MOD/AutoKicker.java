package me.Guga.Guga_SERVER_MOD;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class AutoKicker 
{
	public AutoKicker(Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}
	
	public void startThread()
	{
		this.plugin.scheduler.scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			public void run()
			{
				plugin.log.info("Kick");
				kickUnactivePlayers();
			}
		}, 300, 300);
	}
	
	public void addPlayer(String player, long kickTime)
	{
		if(this.players.containsKey(player))
		{
			this.players.remove(player);
			this.players.put(player, kickTime);
		}
		else
			this.players.put(player, kickTime);
	}
	
	private void kickUnactivePlayers()
	{
		Iterator<Entry<String, Long>> it = this.players.entrySet().iterator();
		while(it.hasNext())
		{
			Entry <String, Long> e =  it.next(); 
			if(e.getValue() < System.currentTimeMillis())
			{
				GugaVirtualCurrency curr = plugin.FindPlayerCurrency(e.getKey());
				if(plugin.getServer().getPlayer(e.getKey()) != null)
				{
					if(!curr.IsVip())
						plugin.getServer().getPlayer(e.getKey()).kickPlayer("Byl jste prilis dlouhou dobu neaktivni!");
				}
			}
		}
	}
	private HashMap<String, Long> players = new HashMap<String, Long>();
	private Guga_SERVER_MOD	plugin;
}

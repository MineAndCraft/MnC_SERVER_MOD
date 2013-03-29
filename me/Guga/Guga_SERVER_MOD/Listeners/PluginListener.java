package me.Guga.Guga_SERVER_MOD.Listeners;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Estates.EstatesDynMapHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginListener implements Listener
{
	final Guga_SERVER_MOD plugin;
	
	public PluginListener(Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginEnable(PluginEnableEvent event)
	{
		if(event.getPlugin().getName().equals("dynmap"))
		{
			EstatesDynMapHandler.setup();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginDisable(PluginDisableEvent event)
	{
		if(event.getPlugin().getName().equals("dynmap"))
		{
			EstatesDynMapHandler.disable();
		}
	}
}

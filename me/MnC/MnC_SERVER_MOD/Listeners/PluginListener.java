package me.MnC.MnC_SERVER_MOD.Listeners;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.Estates.EstatesDynMapHandler;
import me.MnC.MnC_SERVER_MOD.manor.ManorDynMapHandler;
import me.MnC.MnC_SERVER_MOD.tagger.Tagger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginListener implements Listener
{
	final MnC_SERVER_MOD plugin;
	
	public PluginListener(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginEnable(PluginEnableEvent event)
	{
		if(event.getPlugin().getName().equals("dynmap"))
		{
			EstatesDynMapHandler.setup();
			
			ManorDynMapHandler.setup();
		}
		else if(event.getPlugin().getName().equals("ProtocolLib"))
		{
			Tagger.start();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginDisable(PluginDisableEvent event)
	{
		if(event.getPlugin().getName().equals("dynmap"))
		{
			EstatesDynMapHandler.disable();
			
			ManorDynMapHandler.disable();
		}
		else if(event.getPlugin().getName().equals("ProtocolLib"))
		{
			Tagger.stop();
		}
	}
}

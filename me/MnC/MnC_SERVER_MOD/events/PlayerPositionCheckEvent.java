package me.MnC.MnC_SERVER_MOD.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;

public class PlayerPositionCheckEvent extends Event
{
	private MinecraftPlayer _player;
	
	public PlayerPositionCheckEvent(MinecraftPlayer player)
	{
		_player = player;
	}
	
	public MinecraftPlayer getPlayer()
	{
		return _player;
	}
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
	    return handlers;
	}
	

}

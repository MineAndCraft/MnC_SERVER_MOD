package me.MnC.MnC_SERVER_MOD.rpg;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event to be called by PlayerProfession when a player levels up
 *
 */
public class PlayerProfessionLevelUpEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
	    return handlers;
	}
	
	private PlayerProfession _profession;
	
	PlayerProfessionLevelUpEvent(PlayerProfession profession)
	{
		_profession = profession;
	}
	
	public PlayerProfession getProfession()
	{
		return _profession;
	}
}

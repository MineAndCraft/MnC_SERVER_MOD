package me.MnC.MnC_SERVER_MOD.RPG;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GugaProfessionPlayerLevelUpEvent extends Event
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
	
	private GugaProfession2 _profession;
	
	GugaProfessionPlayerLevelUpEvent(GugaProfession2 profession)
	{
		_profession = profession;
	}
	
	public GugaProfession2 getProfession()
	{
		return _profession;
	}
}

package me.Guga.Guga_SERVER_MOD;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GugaSpectator 
{
	GugaSpectator(Guga_SERVER_MOD gugaSM, Player tarPlayer, Player specPlayer)
	{
		plugin = gugaSM;
		target = tarPlayer;
		spectator = specPlayer;
		SpectateStart();
	}
	public void SpectateStart()
	{
		spectatorBaseLocation = spectator.getLocation();
		spectatorInventory = spectator.getInventory().getContents();
		Teleport();
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				Invis();
			}
		}, 5);
	}
	public void SpectateStop()
	{
		if (spectatorInventory != null)
		{
			spectator.getInventory().setContents(spectatorInventory);
		}
		spectator.teleport(spectatorBaseLocation);
		UnInvis();
	}
	public Player GetSpectator()
	{
		return spectator;
	}
	public Player GetTarget()
	{
		return target;
	}
	public void Teleport()
	{
		if (spectator.isOnline() && target.isOnline())
		{
			spectator.teleport(target.getLocation());
		}
	}
	public void CloneInventory()
	{
		if (spectator.isOnline() && target.isOnline())
		{
			spectator.getInventory().setContents(target.getInventory().getContents());
		}
	}
	public void Invis()
	{
		UnInvis();
		GugaCommands.InvisPlayerForAll(spectator);
		GugaCommands.InvisPlayerTo(target, spectator);
	}
	public void UnInvis()
	{
		GugaCommands.UnInvisPlayerForAll(spectator);
		GugaCommands.UnInvisPlayerTo(target, spectator);
	}
	private Player spectator;
	private Player target;
	private Location spectatorBaseLocation;
	private ItemStack[] spectatorInventory;
	private Guga_SERVER_MOD plugin;
}

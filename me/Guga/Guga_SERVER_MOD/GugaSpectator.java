package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GugaSpectator 
{
	GugaSpectator(Guga_SERVER_MOD gugaSM, Player tarPlayer, Player specPlayer)
	{
		plugin = gugaSM;
		target = tarPlayer;
		spectator = specPlayer;
		invis = new GugaInvisibility(spectator, 50, plugin);
		SpectateStart();
	}
	public void SpectateStart()
	{
		spectatorList.add(spectator);
		spectatorBaseLocation = spectator.getLocation();
		//spectatorInventory = spectator.getInventory().getContents();
		Teleport();
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				invis.Start();
				InvisTarget();
			}
		}, 10);
	}
	public void SpectateStop()
	{
		/*if (spectatorInventory != null)
		{
			spectator.getInventory().setContents(spectatorInventory);
		}*/
		spectatorList.remove(spectator);
		spectator.teleport(spectatorBaseLocation);
		invis.Stop();
		UnInvisTarget();
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
	public void InvisTarget()
	{
		UnInvisTarget();
		((CraftPlayer)spectator).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(target.getEntityId()));
	}
	public void UnInvisTarget()
	{
		((CraftPlayer)spectator).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(((CraftPlayer)target).getHandle()));
	}
	private Player spectator;
	private Player target;
	private Location spectatorBaseLocation;
	//private ItemStack[] spectatorInventory;
	private GugaInvisibility invis;
	
	public static ArrayList<Player> spectatorList = new ArrayList<Player>();
	private Guga_SERVER_MOD plugin;
}

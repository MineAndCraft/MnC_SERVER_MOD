package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GugaInvisibility 
{
	GugaInvisibility(Player p, int range, Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		this.range = range;
		player = p;
	}
	public void Start()
	{
		plugin.scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable() {
			public void run()
			{
				CheckPlayers();
				HidePlayers();
			}
		}, 20, 20);
	}
	public void Stop()
	{
		plugin.scheduler.cancelTask(taskID);
	}
	private void CheckPlayers()
	{
		Player[] pList = plugin.getServer().getOnlinePlayers();
		Location pLoc = player.getLocation();
		for (Player p : pList)
		{
			if (pLoc.distance(p.getLocation()) < range)
			{
				if ( (!hidden.contains(p)) && (!needHide.contains(p)) )
				{
					needHide.add(p);
				}
			}
			else 
			{
				if (hidden.contains(p))
				{
					hidden.remove(p);
					SendShowPacket(p);
				}
				else if (needHide.contains(p))
				{
					needHide.remove(p);
				}
			}
		}
	}

	private void HidePlayers()
	{
		ArrayList <Player> needHideTemp = new ArrayList<Player>();
		needHideTemp = needHide;
		Iterator<Player> i = needHideTemp.iterator();
		while (i.hasNext())
		{
			Player p = i.next();
			SendShowPacket(p);
			SendHidePacket(p);
			needHide.remove(p);
			hidden.add(p);
		}
	}
	private void SendHidePacket(Player p)
	{
		((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(player.getEntityId()));
	}
	private void SendShowPacket(Player p)
	{
		((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(((CraftPlayer)player).getHandle()));
	}
	private ArrayList <Player> needHide = new ArrayList<Player>();
	private ArrayList <Player> hidden = new ArrayList<Player>();
	private Player player;
	private int range;
	private int taskID;
	private Guga_SERVER_MOD plugin;
}

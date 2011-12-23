package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class GugaEvent 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		GugaEvent.plugin = plugin;
	}
	public static void TeleportPlayersTo(String pName)
	{
		Player teleporter = GugaEvent.plugin.getServer().getPlayer(pName);
		if (teleporter == null)
			return;
		Iterator<String> i = GugaEvent.players.iterator();
		while (i.hasNext())
		{
			String name = i.next();
			Player p = GugaEvent.plugin.getServer().getPlayer(name);
			if (p != null)
			{
				GugaEvent.CreateTPBackup(name, p.getLocation());
				p.teleport(teleporter);
			}
		}
	}
	public static void TeleportPlayersBack()
	{
		Iterator<Entry<String, Location>> i = GugaEvent.teleportCache.entrySet().iterator();
		Set<Entry<String, Location>> set = new HashSet<Entry<String, Location>>();
		while (i.hasNext())
		{
			set.add(i.next());
		}
		i = set.iterator();
		while (i.hasNext())
		{
			Entry<String, Location> entry = i.next();
			Player p = GugaEvent.plugin.getServer().getPlayer(entry.getKey());
			if (p != null)
			{
				p.teleport(entry.getValue());
				GugaEvent.teleportCache.remove(entry.getKey());
			}
		}
	}
	public static void ClearInventories()
	{
		Iterator<String> i = GugaEvent.players.iterator();
		while (i.hasNext())
		{
			String pName = i.next();
			Player p = GugaEvent.plugin.getServer().getPlayer(pName);
			if (p != null)
			{
				GugaEvent.CreateInvBackup(pName, p.getInventory());
				p.getInventory().clear();
			}
		}
	}
	public static ArrayList<String> GetPlayers()
	{
		return GugaEvent.players;
	}
	public static void ReturnInventories(boolean clear)
	{
		Iterator<String> i = GugaEvent.inventoryCache.keySet().iterator();
		ArrayList<String> tempPlayers = new ArrayList<String>();
		while (i.hasNext())
		{
			String name = i.next();
			tempPlayers.add(name);
		}
		i = tempPlayers.iterator();
		while(i.hasNext())
		{
			String pName = i.next();
			if (GugaEvent.plugin.getServer().getPlayer(pName) != null)
			{
				GugaEvent.ReturnBackup(pName, clear);
			}
		}
	}
	public static void AddPlayer(String pName)
	{
		if (!GugaEvent.ContainsPlayer(pName))
			GugaEvent.players.add(pName);
	}
	public static void ClearPlayers()
	{
		GugaEvent.players.clear();
	}
	public static boolean ContainsPlayer(String pName)
	{
		Iterator<String> i = GugaEvent.players.iterator();
		while (i.hasNext())
		{
			String name = i.next();
			if (name.equalsIgnoreCase(pName))
				return true;
		}
		return false;
	}
	private static void CreateTPBackup(String pName, Location loc)
	{
		GugaEvent.teleportCache.put(pName, loc);
	}
	private static void CreateInvBackup(String pName, Inventory inv)
	{
		ItemStack[] items = inv.getContents();
		int i = 0;
		ArrayList<ItemStack> itemsList = new ArrayList<ItemStack>();
		while (i < items.length)
		{
			if (items[i] != null)
				itemsList.add(items[i]);
			i++;
		}
		GugaEvent.inventoryCache.put(pName, itemsList);
	}
	private static void ReturnBackup(String pName, boolean clear)
	{
		Player p = GugaEvent.plugin.getServer().getPlayer(pName);
		if (p == null)
			return;
		ArrayList<ItemStack> items = GugaEvent.inventoryCache.get(pName);
		if (items == null)
			return;
		Iterator<ItemStack> i = items.iterator();
		if (clear)
			p.getInventory().clear();
		while (i.hasNext())
		{
			ItemStack item = i.next();
			p.getInventory().addItem(item);
		}
	}
	private static HashMap<String, Location> teleportCache = new HashMap<String, Location>();
	private static HashMap<String, ArrayList<ItemStack>> inventoryCache = new HashMap<String, ArrayList<ItemStack>>();
	private static ArrayList<String> players = new ArrayList<String>();
	private static Guga_SERVER_MOD plugin;
}

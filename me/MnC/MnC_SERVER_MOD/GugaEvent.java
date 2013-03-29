package me.MnC.MnC_SERVER_MOD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import me.MnC.MnC_SERVER_MOD.util.InventoryBackup;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public abstract class GugaEvent 
{
	public static void SetPlugin(MnC_SERVER_MOD plugin)
	{
		GugaEvent.plugin = plugin;
	}
	public static void ToggleGroupSpawning(String groupName)
	{
		Iterator<GugaSpawner> i = GugaEvent.spawners.get(groupName).iterator();
		if (!i.hasNext())
			return;
		GugaSpawner spawner = i.next();
		spawner.ToggleSpawnState();
		boolean state = spawner.GetSpawnState();
		while (i.hasNext())
		{
			spawner = i.next();
			spawner.SetSpawnState(state);
		}
	}
	public static void AddSpawnerToGroup(String groupName, Location loc, int interval, int typeID)
	{
		EntityType[] vals = EntityType.values();
		ArrayList<GugaSpawner> list = GugaEvent.spawners.get(groupName);
		if (list == null)
		{
			list = new ArrayList<GugaSpawner>();
			list.add(new GugaSpawner(GugaEvent.plugin, loc, interval, 1000, vals[typeID]));
			GugaEvent.spawners.put(groupName, list);
		}
		else
		{
			GugaSpawner spawner = new GugaSpawner(GugaEvent.plugin, loc, interval, 1000, vals[typeID]);
			spawner.SetSpawnState(GugaEvent.GetGroupState(groupName));
			list.add(spawner);
		}
	}
	public static void ClearSpawnersFromGroup(String groupName)
	{
		ArrayList<GugaSpawner> list = GugaEvent.spawners.get(groupName);
		if (list == null)
			return;
		Iterator<GugaSpawner> i = list.iterator();
		while (i.hasNext())
		{
			GugaSpawner spawner = i.next();
			spawner.TerminateThread();
		}
		list.clear();
		GugaEvent.spawners.remove(groupName);
	}
	public static boolean GetGroupState(String groupName)
	{
		Iterator<GugaSpawner> i = GugaEvent.spawners.get(groupName).iterator();
		if (i == null || !i.hasNext())
			return false;
		return i.next().GetSpawnState();
	}
	public static void ClearAllGroups()
	{
		Iterator<String> i = GugaEvent.GetGroupNames().iterator();
		while (i.hasNext())
		{
			GugaEvent.ClearSpawnersFromGroup(i.next());
		}
	}
	public static ArrayList<GugaSpawner> GetSpawnersOfGroup(String groupName)
	{
		return GugaEvent.spawners.get(groupName);
	}
	public static Set<String> GetGroupNames()
	{
		return GugaEvent.spawners.keySet();
	}
	public static void RemoveSpawnerFromGroup(String groupName, int index)
	{
		ArrayList<GugaSpawner> list = GugaEvent.GetSpawnersOfGroup(groupName);
		if (index < list.size())
			list.remove(index);
	}
	public static void AddItemToPlayers(int itemID, int amount)
	{
		Iterator<String> i = GugaEvent.players.iterator();
		while (i.hasNext())
		{
			Player p = GugaEvent.plugin.getServer().getPlayer(i.next());
			if (p != null)
				p.getInventory().addItem(new ItemStack(itemID, amount));
		}
	}
	public static void ToggleAcceptInvites()
	{
		GugaEvent.acceptInv = !GugaEvent.acceptInv;
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
			if (p != null )
			{
				if (InventoryBackup.CreateBackup(pName, p.getInventory().getArmorContents(), p.getInventory().getContents(), p.getActivePotionEffects()))
				{
					p.getInventory().clear();
					p.getInventory().setArmorContents(null);
					Iterator<PotionEffect> it = p.getActivePotionEffects().iterator();
					while(i.hasNext())
					{
						p.removePotionEffect(it.next().getType());
					}
				}
			}
		}
	}
	public static ArrayList<String> GetPlayers()
	{
		return GugaEvent.players;
	}
	public static void ReturnInventories(boolean clear)
	{
		Iterator<InventoryBackup> i = InventoryBackup.GetBackups().iterator();
		ArrayList<InventoryBackup> temp = new ArrayList<InventoryBackup>();
		while(i.hasNext())
		{
			InventoryBackup backUp = i.next();
			Player p;
			if ((p = GugaEvent.plugin.getServer().getPlayer(backUp.GetOwner())) != null)
			{
				temp.add(backUp);
				if (clear)
					p.getInventory().clear();
				ItemStack[] items = backUp.GetInventory();
				int i2 = 0;
				while (i2 < items.length)
				{
					if (items[i2] != null)
						p.getInventory().addItem(items[i2]);
					i2++;
				}
				p.getInventory().setArmorContents(backUp.GetArmor());
				p.addPotionEffects(backUp.GetPotions());
			}
		}
		InventoryBackup.RemoveBackups(temp);
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
	public static void RemovePlayer(String pName)
	{
		GugaEvent.players.remove(pName);
	}
	public static ArrayList<String> GetItemCountStats(int id)
	{
		int[] values = new int[GugaEvent.players.size()];
		String[] array = new String[GugaEvent.players.size()];
		Iterator<String> iter = GugaEvent.players.iterator();
		int i = 0;
		while (iter.hasNext())
		{
			String pName = iter.next();
			int count = GugaEvent.GetItemCountById(pName, id);
			array[i] = pName + ";" + count;
			values[i] = count;
			i++;
		}
		Arrays.sort(values);
		i = (values.length - 1);
		ArrayList<String> returnArray = new ArrayList<String>();
		while (i >= 0)
		{
			int i2 = 0;
			while (i2 < values.length)
			{
				int val = Integer.parseInt(array[i2].split(";")[1]);
				if (val == values[i])
					returnArray.add(array[i2]);
				i2++;
			}
			i--;
		}
		return returnArray;
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
	private static int GetItemCountById(String pName, int id)
	{
		Player p = GugaEvent.plugin.getServer().getPlayer(pName);
		if (p == null)
			return 0;
		
		ItemStack[] items = p.getInventory().getContents();
		int count = 0;
		int i = 0;
		while (i < items.length)
		{
			if (items[i] != null)
			{
				if (items[i].getTypeId() == id)
					count += items[i].getAmount();
			}
			i++;
		}
		return count;
	}
	public static ArrayList<String> players = new ArrayList<String>();
	public static boolean godMode = false;
	public static boolean acceptInv = false;
	public static int playersCap = 20;
	private static HashMap<String, Location> teleportCache = new HashMap<String, Location>();
	//private static HashMap<String, ArrayList<ItemStack>> inventoryCache = new HashMap<String, ArrayList<ItemStack>>();
	//private static HashMap<String, ItemStack[]> armorCache = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ArrayList<GugaSpawner>> spawners = new HashMap<String, ArrayList<GugaSpawner>>();
	private static MnC_SERVER_MOD plugin;
}

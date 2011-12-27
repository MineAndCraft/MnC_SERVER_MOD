package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryBackup 
{
	public InventoryBackup(String owner, ItemStack[] armor, ItemStack[] inventory)
	{
		this.owner = owner;
		this.armor = armor;
		this.inventory = inventory;
	}
	public String GetOwner()
	{
		return this.owner;
	}
	public ItemStack[] GetArmor()
	{
		return this.armor;
	}
	public ItemStack[] GetInventory()
	{
		return this.inventory;
	}
	public static ArrayList<InventoryBackup> GetBackups()
	{
		return list;
	}
	public static InventoryBackup GetInventoryBackup(String pName)
	{
		Iterator<InventoryBackup> i = list.iterator();
		while (i.hasNext())
		{
			InventoryBackup inv = i.next();
			if (inv.GetOwner().equalsIgnoreCase(pName))
				return inv;
		}
		return null;
	}
	public static boolean CreateBackup(String pName, ItemStack[] armor, ItemStack[] inventory)
	{
		if (GetInventoryBackup(pName) != null)
			return false;
		list.add(new InventoryBackup(pName,armor, inventory));
		return true;
	}
	public static void RemoveBackup(String pName)
	{
		InventoryBackup inv = GetInventoryBackup(pName);
		if (inv != null)
			list.remove(inv);	
	}
	public static void RemoveBackup(InventoryBackup backUp)
	{
		list.remove(backUp);
	}
	public static void RemoveBackups(ArrayList<InventoryBackup> backUps)
	{
		Iterator<InventoryBackup> i = backUps.iterator();
		while (i.hasNext())
		{
			list.remove(i.next());
		}
	}
	public static void InventoryClearWrapped(Player p)
	{
		PlayerInventory inv = p.getInventory();
		CreateBackup(p.getName(), inv.getArmorContents(), inv.getContents());
		inv.clear();
		inv.setArmorContents(null);
	}
	public static void InventoryReturnWrapped(Player p, boolean clear)
	{
		InventoryBackup backup;
		if ( (backup = GetInventoryBackup(p.getName())) == null)
			return;
		PlayerInventory inv = p.getInventory();
		
		inv.setArmorContents(backup.GetArmor());
		ItemStack[] items = backup.GetInventory();
		if (clear)
			inv.clear();
		int i = 0;
		while (i < items.length)
		{
			if (items[i] != null)
				inv.addItem(items[i]);
			i++;
		}
		RemoveBackup(backup);
	}
	private String owner;
	private ItemStack[] armor;
	private ItemStack[] inventory;
	private static ArrayList<InventoryBackup> list = new ArrayList<InventoryBackup>();
}

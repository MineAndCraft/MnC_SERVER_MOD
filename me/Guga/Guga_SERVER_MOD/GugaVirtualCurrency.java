package me.Guga.Guga_SERVER_MOD;


import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GugaVirtualCurrency
{
	GugaVirtualCurrency(Guga_SERVER_MOD gugaSM, String pName)
	{
		playerName = pName;
		plugin = gugaSM;
	}
	GugaVirtualCurrency(Guga_SERVER_MOD gugaSM, String pName, int curr, Date exprDate)
	{
		playerName = pName;
		currency = curr;
		plugin = gugaSM;
		vipExpiration = exprDate;
		UpdateVipStatus();
	}
	public int GetCurrency()
	{
		return currency;
	}
	public boolean IsVip()
	{
		return vipActive;
	}
	public long GetExpirationDate()
	{
		return vipExpiration.getTime();
	}
	public void SetExpirationDate(Date date)
	{
		vipExpiration = date;
		UpdateVipStatus();
		UpdateDisplayName();
	}
	public void AddCurrency(int curr)
	{
		currency +=curr;
	}
	public void RemoveCurrency(int curr)
	{
		currency -= curr;
	}
	public void SetCurrency(int curr)
	{
		currency = curr;
	}
	public Location GetLastTeleportLoc()
	{
		return lastTeleportLoc;
	}
	public void SetLastTeleportLoc(Location loc)
	{
		lastTeleportLoc = loc;
	}
	public String GetPlayerName()
	{
		return playerName;
	}
	public void UpdateDisplayName()
	{
		Player p = plugin.getServer().getPlayer(playerName);
		if (p==null)
			return;
		
		if (IsVip())
		{
			p.setDisplayName(ChatColor.AQUA + "VIP'" + ChatColor.WHITE + p.getName());
		}
		else
		{
			p.setDisplayName(p.getName());
		}
	}
	public void BuyItem(int itemID, int amount)
	{
		Player p = plugin.getServer().getPlayer(playerName);
		if (amount < 0)
		{
			p.sendMessage("Amount has to be > 0!");
			return;
		}
		int totalPrice = GetTotalPrice(itemID, amount);
		if (totalPrice == -1)
		{
			p.sendMessage("Item Not Found");
			return;
		}
		else if (totalPrice > 0)
		{
			if (CanBuyItem(totalPrice))
			{
				Purchase(itemID, totalPrice, amount);
				p.sendMessage("Buying " + amount + "x " + GetItem(itemID).toString() + " for " + totalPrice);
				p.sendMessage("Your credits left: " + currency);
			}
			else
			{
				p.sendMessage("You dont have enough money to buy this!");
			}
		}
	}
	private void UpdateVipStatus()
	{
		if (vipExpiration.after(new Date()))
			vipActive = true;
		else
			vipActive = false;
	}
	private Prices GetItem(int itemID)
	{
		for (Prices i : Prices.values())
		{
			if (i.GetItemID() == itemID)
			{
				return i;
			}
		}
		return null;
	}
	private boolean CanBuyItem(int price)
	{
		if (currency >= price)
		{
			return true;
		}
		return false;
	}
	private int GetTotalPrice(int itemID, int amount)
	{
		Prices item;
		if ((item = GetItem(itemID)) != null)
		{
			return item.GetItemPrice() * amount;
		}
		return -1;
	}
	private void Purchase(int itemID, int price, int amount)
	{
		Player p = plugin.getServer().getPlayer(playerName);
		ItemStack order = new ItemStack(itemID, amount);
		PlayerInventory pInventory = p.getInventory();
		pInventory.addItem(order);
		currency -= price;
		plugin.SaveCurrency();
	}
	public Guga_SERVER_MOD GetPlugin()
	{
		return plugin;
	}
	private String playerName;
	private int currency;
	private Location lastTeleportLoc;
	private boolean vipActive;
	private Date vipExpiration;
	private Guga_SERVER_MOD plugin;
}

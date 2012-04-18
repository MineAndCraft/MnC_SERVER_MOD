package me.Guga.Guga_SERVER_MOD;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
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
	public GugaVirtualCurrency(Guga_SERVER_MOD gugaSM, String pName, int curr, Date exprDate)
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
	public enum VipItems
	{
		SAND(12), COBBLESTONE(4), WOODEN_PLANKS(5), STONE(1), DIRT(3), SANDSTONE(24);
		private VipItems(int id)
		{
			this.id = id;
		}
		public int GetID()
		{
			return this.id;
		}
		public static boolean IsVipItem(int itemID)
		{
			for (VipItems i : VipItems.values())
			{
				if (i.GetID() == itemID)
					return true;
			}
			return false;
		}
		private int id;
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
			p.setDisplayName(ChatColor.RED + "VIP'" + ChatColor.WHITE + p.getName());
			p.setPlayerListName(ChatColor.GOLD+p.getName());
		}
		else
		{
			p.setDisplayName(p.getName());
		}
	}
	public void BuyItem(String itemName, int amount)
	{
		Player p = plugin.getServer().getPlayer(playerName);
		if (amount < 0)
		{
			p.sendMessage("Pocet musi byt vyssi nez 0!");
			return;
		}
		Prices item = null;
		for (Prices i : Prices.values())
		{
			if (i.toString().equalsIgnoreCase(itemName))
			{
				item = i;
				break;
			}
		}
		if (item == null)
		{
			p.sendMessage("Item nenalezen");
			return;
		}
		int totalPrice = GetTotalPrice(item, amount);
		if (totalPrice > 0)
		{
			if (CanBuyItem(totalPrice))
			{
				Purchase(item, totalPrice, amount);
				p.sendMessage("Koupil jste " + amount + "x " + item.toString() + " za " + totalPrice + " kreditu.");
				p.sendMessage("Zbyva kreditu: " + currency);
			}
			else
			{
				p.sendMessage("Nemate dostatecne mnozstvi kreditu!");
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
	private boolean CanBuyItem(int price)
	{
		if (currency >= price)
		{
			return true;
		}
		return false;
	}
	private int GetTotalPrice(Prices item, int amount)
	{
		return item.GetItemPrice() * amount;
	}
	private void Purchase(Prices item, int price, int amount)
	{
		Player p = plugin.getServer().getPlayer(playerName);
		if (p == null)
			return;
		ItemStack order = null;
		if (item.toString().contains("EGG_"))
		{
			if (item == Prices.EGG_CAVE_SPIDER)
				order = new ItemStack(item.GetItemID(), amount, (short) 59);
			else if (item == Prices.EGG_CHICKEN)
				order = new ItemStack(item.GetItemID(), amount, (short) 93);
			else if (item == Prices.EGG_COW)
				order = new ItemStack(item.GetItemID(), amount, (short) 92);
			else if (item == Prices.EGG_ENDERMAN)
				order = new ItemStack(item.GetItemID(), amount, (short) 58);
			else if (item == Prices.EGG_MAGMA_SLIME)
				order = new ItemStack(item.GetItemID(), amount, (short) 62);
			else if (item == Prices.EGG_MOOSHROOM)
				order = new ItemStack(item.GetItemID(), amount, (short) 96);
			else if (item == Prices.EGG_PIG)
				order = new ItemStack(item.GetItemID(), amount, (short) 90);
			else if (item == Prices.EGG_PIGMAN)
				order = new ItemStack(item.GetItemID(), amount, (short) 57);
			else if (item == Prices.EGG_SHEEP)
				order = new ItemStack(item.GetItemID(), amount, (short) 91);
			/*else if (item == Prices.EGG_SILVERFISH)
				order = new ItemStack(item.GetItemID(), amount, (short) 60);*/
			else if (item == Prices.EGG_SKELETON)
				order = new ItemStack(item.GetItemID(), amount, (short) 51);
			else if (item == Prices.EGG_SLIME)
				order = new ItemStack(item.GetItemID(), amount, (short) 55);
			else if (item == Prices.EGG_SPIDER)
				order = new ItemStack(item.GetItemID(), amount, (short) 52);
			else if (item == Prices.EGG_VILLAGER)
				order = new ItemStack(item.GetItemID(), amount, (short) 120);
			else if (item == Prices.EGG_WOLF)
				order = new ItemStack(item.GetItemID(), amount, (short) 95);
			else if (item == Prices.EGG_ZOMBIE)
				order = new ItemStack(item.GetItemID(), amount, (short) 54);
			else if (item==Prices.EGG_OCELOT)
				order = new ItemStack(item.GetItemID(), amount, (short) 98);		
		}
		else if (item == Prices.KRUMPAC_EFFICIENCY_V)
		{
			order = new ItemStack(item.GetItemID(), amount);
			order.addEnchantment(Enchantment.DIG_SPEED, 5);
			order.addEnchantment(Enchantment.DURABILITY, 3);
		}
		else
			order = new ItemStack(item.GetItemID(), amount);
		if (item != null)
			plugin.logger.LogShopTransaction(item, amount, this.playerName);
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

package me.MnC.MnC_SERVER_MOD.Currency;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import me.MnC.MnC_SERVER_MOD.Config;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopManager
{
	//requires resource 'Shop.cfg'
	
	private FileConfiguration itemConfig;
	private MnC_SERVER_MOD plugin;
	
	public ShopManager(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
		File configFile = new File(Config.SHOP_CONFIG);
		this.itemConfig = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public void buyItem(String playerName,String itemName,int amount)
	{
		Player player = plugin.getServer().getPlayerExact(playerName);
		if (!(amount > 0))
		{
			ChatHandler.FailMsg(player,"Pocet musi byt vyssi nez 0!");
			return;
		}
		if(!itemName.matches("^[a-zA-Z0-9_]+$"))
		{
			ChatHandler.FailMsg(player,"Nazev itemu neni platny.");
			return;
		}
		
		ConfigurationSection item = null;
		try
		{
			item = this.itemConfig.getConfigurationSection("Items."+itemName);
		}catch(Exception x){}
		
		if (item == null)
		{
			ChatHandler.FailMsg(player,"Item nenalezen");
			return;
		}
		
		handlePurchase(player,playerName,item,amount);
	}
	
	private synchronized boolean handlePurchase(Player player, String playerName, ConfigurationSection item, int amount)
	{
		int itemId = item.getInt("ID",0);
		float price = (float)item.getDouble("Price",0);
		int type = item.getInt("Type", 0);
		
		if(itemId == 0|| amount==0)
			return false;
		
		float balance = plugin.currencyManager.getBalance(playerName);
		float totalPrice = (price*amount);
		if(balance < totalPrice)
		{
			ChatHandler.FailMsg(player,"Nemate dostatecne mnozstvi kreditu!");
			return false;
		}
		
		if(amount > 64)
		{
			ChatHandler.FailMsg(player, "Muzete koupit maximalne 64 kusu naraz.");
			return false;
		}
		
		if(player.getInventory().firstEmpty() == -1)
		{
			ChatHandler.FailMsg(player, "Musite mit v inventari alespon jeden volny slot.");
			return false;
		}
		
		ItemStack purchase = new ItemStack(itemId, amount, (short)type);
		player.getInventory().addItem(purchase);
		plugin.logger.LogShopTransaction(this.getItemNameByItem(item), amount, playerName);
		plugin.currencyManager.addCredits(playerName,-totalPrice);
		
		ChatHandler.SuccessMsg(player, "Koupil jste " + ChatColor.YELLOW + amount + "x " + item.getName() + 
				ChatColor.GREEN +" za " + ChatColor.YELLOW +totalPrice + ChatColor.GREEN + " kreditu.");
		ChatHandler.InfoMsg(player, "Zbyva kreditu: " + ChatColor.GOLD + plugin.currencyManager.getBalance(playerName));
		return true;
	}

	private String getItemNameByItem(ConfigurationSection item)
	{
		if(item!=null)
			return item.getName();
		return "UNKNOWN";
	}
	
	public ArrayList<ShopItem> getShopItemList()
	{
		ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		ConfigurationSection s = this.itemConfig.getConfigurationSection("Items");
		if(s==null)
			return items;
		
		Set<String> itemNames = s.getKeys(false);
		for(String name : itemNames)
		{
			ConfigurationSection itemData = this.itemConfig.getConfigurationSection("Items."+name);
			if(itemData==null)
				continue;
			items.add(new ShopItem(name,
					(float)itemData.getDouble("Price",0),
					itemData.getInt("ID",0),
					itemData.getInt("Type",0)));
		}
		
		return items;
	}
	
	public class ShopItem
	{
		private String name;
		private float price;
		private int id;
		private int type;

		ShopItem(String name,float price,int id,int type)
		{
			this.name = name;
			this.price = price;
			this.id = id;
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public int getId() {
			return id;
		}

		public float getPrice() {
			return price;
		}

		public String getName() {
			return name;
		}

		public String getIdString() {
			return (this.type==0)? String.valueOf(this.id) : String.format("%d:%d", this.id,this.type);
		}
	}
}

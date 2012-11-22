package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.GugaAuction;
import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GugaAuctionHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		GugaAuctionHandler.plugin = plugin;
	}
	public static boolean CreateAuction(int itemID, int amount, int price, Player owner)
	{
		if (!owner.getInventory().contains(itemID, amount))
			return false;
		GugaAuctionHandler.RemoveItem(itemID, amount, owner);
		GugaAuctionHandler.auctions.add(new GugaAuction(itemID, amount, price, owner.getName()));
		GugaAuctionHandler.SaveAuctions();
		return true;
	}
	public static boolean CancelAuction(int index, Player player)
	{
		ArrayList<GugaAuction> list = GugaAuctionHandler.GetPlayerAuctions(player);
		if (list.size() <= index)
			return false;
		GugaAuction auction = list.get(index);
		GugaAuctionHandler.auctions.remove(auction);
		player.getInventory().addItem(new ItemStack(auction.GetItemID(), auction.GetAmount()));
		GugaAuctionHandler.SaveAuctions();
		return true;
	}
	public static boolean BuyAuction(Player buyer, int index)
	{
		GugaAuction auction = GugaAuctionHandler.auctions.get(index);
		int price = auction.GetPrice();
		if (!buyer.getInventory().contains(GugaAuctionHandler.CURRENCY, price))
			return false;
		GugaAuctionHandler.auctions.remove(auction);
		GugaAuctionHandler.RemoveItem(GugaAuctionHandler.CURRENCY, price, buyer);
		buyer.getInventory().addItem(new ItemStack(auction.GetItemID(), auction.GetAmount()));
		Player owner = GugaAuctionHandler.plugin.getServer().getPlayer(auction.GetOwner());
		if (owner == null)
		{
			String line = auction.GetOwner() + ";" + auction.GetPrice();
			GugaAuctionHandler.payBuffer.add(line);
		}
		else
		{
			owner.getInventory().addItem(new ItemStack(GugaAuctionHandler.CURRENCY, auction.GetPrice()));
			owner.sendMessage("Vase aukce se prodala! Ziskal jste " + price + " Gold ingotu.");
		}
		GugaAuctionHandler.SaveAuctions();
		GugaAuctionHandler.SavePayments();
		return true;
	}
	public static void RemoveItem(int itemID, int amount, Player player)
	{
		ItemStack[] stack = player.getInventory().getContents();
		int i = 0;
		while (i < stack.length)
		{
			if (stack[i] != null)
				{
				if (stack[i].getTypeId() == itemID)
				{
					if (stack[i].getAmount() >= amount)
					{
						if (stack[i].getAmount() == amount)
						{
							player.getInventory().clear(i);
						}
						else
							player.getInventory().setItem(i, new ItemStack(itemID, stack[i].getAmount() - amount));
						return;
					}
					else
					{
						player.getInventory().clear(i);
						amount -= stack[i].getAmount();
					}
				}
			}
			i++;
		}
	}
	public static ArrayList<GugaAuction> GetAllAuctions()
	{
		return GugaAuctionHandler.auctions;
	}
	public static ArrayList<GugaAuction> GetAuctionPage(int page)
	{
		ArrayList<GugaAuction> list = new ArrayList<GugaAuction>();
		int firstItem = GugaAuctionHandler.ITEMS_PER_PAGE * (page-1);
		int lastItem = GugaAuctionHandler.ITEMS_PER_PAGE * page;
		int i = firstItem;
		if (GugaAuctionHandler.auctions.size() < firstItem)
		{
			return null;
		}
		while (i <= lastItem)
		{
			if (GugaAuctionHandler.auctions.size() <= i)
				break;
			GugaAuction auction = GugaAuctionHandler.auctions.get(i);
			list.add(auction);
			i++;
		}
		return list;
	}
	public static ArrayList<GugaAuction> GetPlayerAuctions(Player player)
	{
		Iterator<GugaAuction> i = GugaAuctionHandler.auctions.iterator();
		ArrayList<GugaAuction> list = new ArrayList<GugaAuction>();
		while (i.hasNext())
		{
			GugaAuction auction = i.next();
			if (auction.GetOwner().matches(player.getName()))
			{
				list.add(auction);
			}
		}
		return list;
	}
	public static GugaAuction GetPlayerAuction(int index, Player player)
	{
		return GugaAuctionHandler.GetPlayerAuctions(player).get(index);
	}
	public static int GetPagesCount()
	{
		double pages = (double)GugaAuctionHandler.auctions.size() / (double)GugaAuctionHandler.ITEMS_PER_PAGE;
		int num;
		if ( (pages - (int)pages) > 0)
			num = (int)pages + 1;
		else
			num = (int)pages;
		return num;
	}
	public static void CheckPayments(Player player)
	{
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> i = GugaAuctionHandler.payBuffer.iterator();
		while (i.hasNext())
		{
			String line = i.next();
			list.add(line);
		}
		i = list.iterator();
		while (i.hasNext())
		{
			String line = i.next();
			if (line.contains(player.getName()))
			{
				int money = Integer.parseInt(line.split(";")[1]);
				player.getInventory().addItem(new ItemStack(GugaAuctionHandler.CURRENCY,money));
				GugaAuctionHandler.payBuffer.remove(line);
				player.sendMessage("Vase aukce se prodala! Ziskal jste " + money + " Gold ingotu.");
			}
		}
		GugaAuctionHandler.SavePayments();
	}
	public static void SaveAuctions()
	{
		plugin.log.info("Saving Auctions file...");
		GugaFile file = new GugaFile(GugaAuctionHandler.auctionFile, GugaFile.WRITE_MODE);
		Iterator<GugaAuction> i = GugaAuctionHandler.auctions.iterator();
		file.Open();
		while (i.hasNext())
		{
			GugaAuction auction = i.next();
			String line = auction.GetItemID() + ";" + auction.GetAmount() + ";" + auction.GetPrice() + ";" + auction.GetOwner();
			file.WriteLine(line);
		}
		file.Close();
	}
	public static void SavePayments()
	{
		plugin.log.info("Saving Payments file...");
		GugaFile file = new GugaFile(GugaAuctionHandler.paymentFile,GugaFile.WRITE_MODE);
		Iterator<String> i = GugaAuctionHandler.payBuffer.iterator();
		file.Open();
		while (i.hasNext())
		{
			String line = i.next();
			file.WriteLine(line);
		}
		file.Close();
	}
	public static void LoadAuctions()
	{
		plugin.log.info("Loading Auctions file...");
		GugaFile file = new GugaFile(GugaAuctionHandler.auctionFile, GugaFile.READ_MODE);
		String line;
		file.Open();
		while ( (line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			int itemID = Integer.parseInt(split[0]);
			int amount = Integer.parseInt(split[1]);
			int price = Integer.parseInt(split[2]);
			String owner = split[3];
			GugaAuctionHandler.auctions.add(new GugaAuction(itemID, amount, price, owner));
		}
		file.Close();
	}
	public static void LoadPayments()
	{
		plugin.log.info("Loading Payments file...");
		GugaFile file = new GugaFile(GugaAuctionHandler.paymentFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		while ( (line = file.ReadLine() ) != null)
		{
			GugaAuctionHandler.payBuffer.add(line);
		}
		file.Close();
	}
	private static ArrayList<GugaAuction> auctions = new ArrayList<GugaAuction>();
	private static ArrayList<String> payBuffer = new ArrayList<String>();
	public static int CURRENCY = 266;
	public static int ITEMS_PER_PAGE = 20;
	private static Guga_SERVER_MOD plugin;
	private static String auctionFile = "plugins/MineAndCraft_plugin/Auctions.dat";
	private static String paymentFile = "plugins/MineAndCraft_plugin/Payments.dat";
}

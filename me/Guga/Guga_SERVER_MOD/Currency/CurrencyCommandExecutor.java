package me.Guga.Guga_SERVER_MOD.Currency;

import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Currency.ShopManager.ShopItem;
import me.Guga.Guga_SERVER_MOD.chat.ChatHandler;
import me.Guga.Guga_SERVER_MOD.util.DataPager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CurrencyCommandExecutor implements CommandExecutor
{
	public CurrencyCommandExecutor()
	{
	}
	
	Guga_SERVER_MOD plugin = Guga_SERVER_MOD.getInstance();
	
	@Override
	public boolean onCommand(CommandSender csender, Command command, String commandLabel, String[] args) 
	{
		if(!(csender instanceof Player))
			return false;
		Player sender = (Player)csender;
		if(command.getName().equalsIgnoreCase("credits"))
		{
			if (args.length == 0)
			{
				sender.sendMessage("/credits send <hrac> <pocet> - Posle zadany pocet kreditu danemu hraci.");
				sender.sendMessage("/credits balance - Zjisti stav Vasich kreditu.");
				return true;
			}
			else if (args.length == 1)
			{
				String subCommand = args[0];
				if(subCommand.matches("balance"))
				{
					sender.sendMessage("Vas ucet:");
					sender.sendMessage("Kredity: " + ChatColor.GOLD + plugin.currencyManager.getBalance(sender.getName()));
					return true;
				}
			}
			else if(args.length == 3)
			{
				String subCommand = args[0];
				String player = args[1];
				int amount = Integer.parseInt(args[2]);
				if(subCommand.matches("send"))
				{
					if(plugin.currencyManager.getBalance(sender.getName()) >= amount)
					{
						if(plugin.currencyManager.addCredits(player, amount))
						{
							plugin.currencyManager.addCredits(sender.getName(), -amount);
							ChatHandler.SuccessMsg(sender, "Kredity byly uspesne odeslany");
							Player reciever;
							if((reciever = plugin.getServer().getPlayer(player)) != null)
							{
								ChatHandler.InfoMsg(reciever, "Na ucet Vam prisly kredity od hrace " + ChatColor.GRAY + 
										sender.getName() + ChatColor.YELLOW + " o hodnote " + ChatColor.GRAY + amount);
							}
							return true;
						}
						else
							ChatHandler.FailMsg(sender, "Kredity se nepodarilo poslat. Zkontrolujte prosim spravnost zadaneho nicku.");
					}
					else
						ChatHandler.FailMsg(sender, "Na tuto akci nemate dostatek kreditu");
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("shop"))
		{
			if (plugin.arena.IsArena(sender.getLocation()))
			{
				ChatHandler.FailMsg(sender, "V arene nemuzete pouzit prikaz /shop!");
				return false;
			}
			
			if (args.length == 0)
			{
				sender.sendMessage("Shop Menu:");
				sender.sendMessage("/shop buy <nazev>  -  Koupi dany item (1).");
				sender.sendMessage("/shop balance  -  Zobrazi vase kredity.");
				sender.sendMessage("/shop items <strana>  -  Seznam itemu, ktere se daji koupit.");
				return true;
			}
			else if (args.length == 1)
			{
				String subCommand = args[0];
				if (subCommand.matches("balance"))
				{
					sender.sendMessage("Vas ucet:");
					sender.sendMessage("Kredity: " + plugin.currencyManager.getBalance(sender.getName()));
					return true;
				}
			}
			else if(args.length >= 1 && args[0].equals("items"))
			{
				int page = 1;
				if(args.length >= 2)
					page = Integer.parseInt(args[1]);
				DataPager<ShopItem> pager = new DataPager<ShopItem>(plugin.shopManager.getShopItemList(), 15);
				Iterator<ShopItem> i = pager.getPage(page).iterator();

				sender.sendMessage("SEZNAM ITEMU:");
				sender.sendMessage("STRANA " + page + "/" + pager.getPageCount());
				while (i.hasNext())
				{
					ShopItem item = i.next();
					sender.sendMessage(String.format("%s - %s - cena za kus: %d ", item.getName(),item.getIdString(),item.getPrice()));
				}
				return true;
			}
			else if (args[0].equals("buy") && (args.length == 3 || args.length == 2))
			{
				String arg1 = args[1];
				int arg2 = 1;
				if(args.length == 3)
					arg2 = Integer.parseInt(args[2]);
				plugin.shopManager.buyItem(sender.getName(),arg1, arg2);
				return true;
			}
		}
		return false;
	}
}

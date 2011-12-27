package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import me.Guga.Guga_SERVER_MOD.GugaArena.ArenaTier;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class GugaCommands 
{
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public static void CommandWho(Player sender)
	{
		Player[] p = plugin.getServer().getOnlinePlayers();
		String pName;
		double playerX;
		double playerZ;
		double senderX = sender.getLocation().getX();
		double senderZ = sender.getLocation().getZ();
		
		double distX;
		double distZ;
		double distance;
		sender.sendMessage("******************************");
		sender.sendMessage("HRACI ONLINE:");
		int i = 0;
		while (i < p.length)
		{
			pName = p[i].getName();
			if (p[i].getName()!= sender.getName())
			{
				playerX = p[i].getLocation().getX();
				playerZ = p[i].getLocation().getZ();
				
				distX = Math.abs(playerX-senderX);
				distZ = Math.abs(playerZ-senderZ);
				distance = Math.sqrt((distX*distX)+(distZ*distZ));
				distance = Math.round(distance*10);
				distance = distance/10;
				String msg;
				if (plugin.professions.get(p[i].getName()) != null)
				{
					msg = "- " + pName;
					msg += /*"  Prof: " +plugin.professions.get(p[i].getName()).GetProfession() +*/ "   Level " + plugin.professions.get(p[i].getName()).GetLevel()+ "  " + distance + " bloku daleko";
					if (plugin.FindPlayerCurrency(pName).IsVip())
					{
						sender.sendMessage(ChatColor.GOLD + msg);
					}
					else
					{
						sender.sendMessage(msg);
					}
				}
				else
				{
					msg = "- " + pName + "  " + distance + " bloku daleko";
					if (plugin.FindPlayerCurrency(pName).IsVip())
					{
						sender.sendMessage(ChatColor.GOLD + msg);
					}
					else
					{
						sender.sendMessage(msg);
					}
				}
			}
			else
			{
				String msg;
				if (plugin.professions.get(p[i].getName()) != null)
				{
					msg = "- " + pName;
					msg += /*"  Prof: " +plugin.professions.get(p[i].getName()).GetProfession() +*/ "   Level " + plugin.professions.get(p[i].getName()).GetLevel();
					if (plugin.FindPlayerCurrency(pName).IsVip())
					{
						sender.sendMessage(ChatColor.GOLD + msg);
					}
					else
					{
						sender.sendMessage(msg);
					}
				}
				else
				{
					msg = "- " + pName;

					if (plugin.FindPlayerCurrency(pName).IsVip())
					{
						sender.sendMessage(ChatColor.GOLD + msg);
					}
					else
					{
						sender.sendMessage(msg);
					}
				}
			}
			
			i++;
		}
		sender.sendMessage("******************************");
		
	}	
	public static void CommandHelp(Player sender)
	{
		//log.info("<"+sender.getName() + " has used /help Command>");
		sender.sendMessage("******************************");
		sender.sendMessage("GUGA MINECRAFT SERVER MOD "+Guga_SERVER_MOD.version);
		sender.sendMessage("******************************");
		sender.sendMessage("Seznam prikazu:");
		sender.sendMessage(" /lock  -  Zamkne truhlu.");
		sender.sendMessage(" /unlock  -  Odemkne truhlu.");
		sender.sendMessage(" /who  -  Seznam online hracu.");
		sender.sendMessage(" /login <heslo>  -  Prihlasi zaregistrovaneho hrace.");
		sender.sendMessage(" /register <pass>  -  Zaregistruje noveho hrace.");
		sender.sendMessage(" /password <stare_heslo> <nove_heslo>  -  Zmeni heslo.");
		sender.sendMessage(" /rpg  -  Menu Profesi.");
		sender.sendMessage(" /arena  -  Menu areny.");
		sender.sendMessage(" /shop  -  Menu Obchodu.");
		sender.sendMessage(" /vip  -  VIP menu.");
		sender.sendMessage(" /places - Menu mist, kam se da teleportovat.");
		sender.sendMessage(" /ah - Menu Aukce.");
		sender.sendMessage(" /r <message>  -  Odpoved na whisper.");
		if (GameMasterHandler.IsAdmin(sender.getName()))
		{
			sender.sendMessage(" /gm  -  GameMaster's menu.");
			sender.sendMessage("/event  -  Event menu.");
		}
		sender.sendMessage("******************************");
		sender.sendMessage("Created by Guga 2011");
		sender.sendMessage("******************************");
	}
	public static void CommandLock(Player sender)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54
		if (blockType == 54)
		{
			if (plugin.chests.GetChestOwner(chest).matches("notFound"))
			{
				plugin.chests.LockChest(chest,sender.getName());
				sender.sendMessage("Vase truhla byla zamcena.");
			}
			else
			{
				sender.sendMessage("Truhlu jiz nekdo zamkl!");
			}
		}	
		else
		{
			sender.sendMessage("Tento blok neni truhla!");
		}
		
	}
	public static void CommandStatus(Player sender, String args[])
	{
		if (args.length >= 1)
		{
			plugin.acc.SetStatus(sender,args);
		}
		else
		{
			sender.sendMessage("Please enter your new status");
		}
	}
	public static void CommandUnlock(Player sender)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54

		if (blockType == 54)
		{
			if ( (plugin.chests.GetChestOwner(chest).matches(sender.getName())) || (GameMasterHandler.IsAtleastGM(sender.getName())) )
			{
				plugin.chests.UnlockChest(chest,sender.getName());
				sender.sendMessage("Vase truhla byla odemcena.");
			}
			else
			{
				sender.sendMessage("Tuto truhlu nemuzete odemknout!");
			}
		}	
		else
		{
			sender.sendMessage("Tento blok neni truhla!");
		}
		
	}
	public static void CommandShop(Player sender, String args[])
	{
		if (!plugin.acc.UserIsLogged(sender))
		{
			sender.sendMessage("You need to login first!");
			return;
		}
		GugaVirtualCurrency p = plugin.FindPlayerCurrency(sender.getName());
		if (args.length == 0)
		{
			sender.sendMessage("Shop Menu:");
			sender.sendMessage("/shop info  -  Info o Obchodu.");
			sender.sendMessage("/shop buy <itemID> <pocet>  -  Koupi dany pocet itemu.");
			sender.sendMessage("/shop buy <itemID>  -  Koupi dany item (1).");
			sender.sendMessage("/shop balance  -  Zobrazi vase kredity.");
			sender.sendMessage("/shop items  -  Seznam itemu, ktere se daji koupit.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("info"))
			{
				sender.sendMessage("not yet");
			}
			else if (subCommand.matches("balance"))
			{
				sender.sendMessage("Vas ucet:");
				sender.sendMessage("Kredity: " + p.GetCurrency());
			}
			else if(subCommand.matches("items"))
			{
				for (Prices i : Prices.values())
				{
				 sender.sendMessage(i.toString() +"-    id: " + i.GetItemID()+ "   cena: "+ i.GetItemPrice());
				}
			}
		}
		else if (args.length == 2)
		{
			String subCommand = args[0];
			String arg1 = args[1];
			
			if (subCommand.matches("buy"))
			{
				p.BuyItem(Integer.parseInt(arg1), 1);
			}
		}
		else if (args.length == 3)
		{
			String subCommand = args[0];
			String arg1 = args[1]; // item
			String arg2 = args[2]; // amount
			if (subCommand.matches("buy"))
			{
				p.BuyItem(Integer.parseInt(arg1), Integer.parseInt(arg2));
			}
			
		}
	}
	public static void CommandVIP(Player sender, String args[])
	{
		GugaVirtualCurrency vip = plugin.FindPlayerCurrency(sender.getName());
		if (!plugin.acc.UserIsLogged(sender))
		{
			sender.sendMessage("Nejprve se musite prihlasit!");
			return;
		}
		if (!vip.IsVip())
		{
			sender.sendMessage("Pouze VIP mohou pouzivat tento prikaz!");
			return;
		}
		if (GugaEvent.ContainsPlayer(sender.getName()))
		{
			sender.sendMessage("Nemuzete pouzivat VIP prikazy v prubehu eventu!");
			return;
		}
		if (plugin.arena.IsArena(sender.getLocation()))
		{
			sender.sendMessage("VIP Prikazy nemuzete pouzivat v arene!");
			return;
		}
		if (args.length == 0)
		{
			sender.sendMessage("VIP MENU:");
			sender.sendMessage("/vip expiration  -  Zobrazi, kdy vyprsi vas VIP status.");
			sender.sendMessage("/vip tp  -  Teleport podprikaz.");
			sender.sendMessage("/vip time  -  Podprikaz zmeny casu.");
			sender.sendMessage("/vip item  -  Podprikaz itemu.");
			
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("expiration"))
			{
				sender.sendMessage("Vase VIP vyprsi: " + new Date(vip.GetExpirationDate()));
			}
			else if (subCommand.matches("tp"))
			{
				sender.sendMessage("Teleport Menu:");
				sender.sendMessage("/vip tp player <jmeno>  -  Teleport k danemu hraci.");
				sender.sendMessage("/vip tp spawn  -  Teleport na spawn.");
				sender.sendMessage("/vip tp back  -  Teleport zpet na predchozi pozici.");
				sender.sendMessage("/vip tp bed  -  Teleport k posteli.");
			}
			else if (subCommand.matches("time"))
			{
				sender.sendMessage("Time Menu:");
				sender.sendMessage("/vip time set <hodnota>  -  Nastavi cas na 0-24000");
				sender.sendMessage("/vip time reset  -  Zmeni cas zpet na serverovy cas.");
			}
			else if (subCommand.matches("item"))
			{
				sender.sendMessage("Item Menu:");
				sender.sendMessage("/vip item add <itemID>  -  Prida stack daneho itemu");
			}
		}
		else if (args.length == 2)
		{
			String subCommand = args[0];
			String arg1 = args[1];
			if (subCommand.matches("tp"))
			{
				if (arg1.matches("back"))
				{
					Location locCache = sender.getLocation();
					Location tpLoc = vip.GetLastTeleportLoc();
					if (tpLoc == null)
					{
						sender.sendMessage("Nejdrive se musite nekam teleportovat!");
						return;
					}
					sender.teleport(tpLoc);
					vip.SetLastTeleportLoc(locCache);
				}
				else if (arg1.matches("spawn"))
				{
					vip.SetLastTeleportLoc(sender.getLocation());
					sender.teleport(sender.getWorld().getSpawnLocation());
				}
				else if (arg1.matches("bed"))
				{
					vip.SetLastTeleportLoc(sender.getLocation());
					Location loc = sender.getBedSpawnLocation();
					Location tpLoc = loc;
					boolean canTeleport = false;
					int i = loc.getBlockY();
					while (!canTeleport)
					{
						loc = tpLoc;
						loc.add(0, 1, 0);
						if (loc.getBlock().getTypeId() == 0)
						{
							if (loc.getBlock().getRelative(BlockFace.UP).getTypeId() == 0)
							{
								tpLoc = loc;
								break;
							}
						}
						if (i >= 127)
						{
							break;
						}
						i++;
					}
					sender.teleport(tpLoc);
				}
			}
			else if (subCommand.matches("time"))
			{
				if (arg1.matches("reset"))
				{
					if (!sender.isPlayerTimeRelative())
					{
						sender.resetPlayerTime();
						sender.sendMessage("Cas byl restartovan");
					}
					else
						sender.sendMessage("Vas cas nepotrebuje restartovat!");
				}
			}
		}
		else if (args.length == 3)
		{
			String subCommand = args[0];
			String arg1 = args[1];
			String arg2 = args[2];
			if (subCommand.matches("tp"))
			{
				if (arg1.matches("player"))
				{
					vip.SetLastTeleportLoc(sender.getLocation());
					Player p = plugin.getServer().getPlayer(arg2);
					sender.teleport(p);
				}
			}
			else if (subCommand.matches("item"))
			{
				if (arg1.matches("add"))
				{
					int itemID = Integer.parseInt(arg2);
					if (itemID == 4 || itemID == 12 || itemID == 1 || itemID == 3 || itemID == 24)
					{
						ItemStack item = new ItemStack(itemID, 64);
						PlayerInventory pInventory = sender.getInventory();
						pInventory.addItem(item);
						sender.sendMessage("Item pridan!");
					}
					else
						sender.sendMessage("Tento item nejde pridat!");
				}
			}
			else if (subCommand.matches("time"))
			{
				if (arg1.matches("set"))
				{
					int time = Integer.parseInt(arg2);
					if ( (time >= 0) && (time <= 24000) )
					{
						sender.setPlayerTime(time, false);
						sender.sendMessage("Cas byl uspesne zmenen");
					}
					else 
						sender.sendMessage("Tato hodnota nelze nastavit!");
				}
			}
		}
	}
	public static void CommandRpg(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("RPG MENU:");
			sender.sendMessage("/rpg status  -  Zobrazi vas status.");
			sender.sendMessage("/rpg skills  -  Zobrazi vase bonusy.");
		//	sender.sendMessage("/rpg select <miner/hunter>  -  Vybere profesi.");
			sender.sendMessage("/rpg info  -  Info o profesi.");
		//	sender.sendMessage("/rpg info <miner/hunter>  -  Info o dane profesi.");*/
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("status"))
			{
				GugaProfession prof;
				if ((prof = plugin.professions.get(sender.getName())) != null)
				{
					int lvl = prof.GetLevel();
					int xp = prof.GetXp();
					int xpNeeded = prof.GetXpNeeded();
					sender.sendMessage("********************");
					sender.sendMessage("**Level:" + lvl);
					sender.sendMessage("**XP:" + xp + "/" + xpNeeded);
					sender.sendMessage("********************");
					sender.sendMessage("********************");
				}
				else 
				{
					sender.sendMessage("Nejdrive si musite zvolit profesi!");
				}
			}
			else if (subCommand.matches("skills"))
			{
				GugaProfession prof;
				if ((prof = plugin.professions.get(sender.getName())) == null)
				{
					sender.sendMessage("Nejdrive si musite zvolit profesi!");
				}
					int chance[] = prof.GetChances();
					//int bonus[] = miner.GetBonusDrops();
					int iron = chance[plugin.IRON];
					int gold = chance[plugin.GOLD];
					int diamond = chance[plugin.DIAMOND];
					double chanceIron = ( (double)iron / (double)1000 )  * (double)100;
					double chanceGold = ( (double)gold / (double)1000 )  * (double)100;
					double chanceDiamond = ( (double)diamond/ (double)1000 )  * (double)100;
					sender.sendMessage("********************");
					sender.sendMessage("**Sance na nalezeni ve stonu:");
					sender.sendMessage("**Iron: " + chanceIron + "%");
					sender.sendMessage("**Gold: " + chanceGold + "%");
					sender.sendMessage("**Diamond: " + chanceDiamond + "%");
					sender.sendMessage("********************");
					
					/*iron = bonus[plugin.IRON];
					gold = bonus[plugin.GOLD];
					diamond = bonus[plugin.DIAMOND];
					
					sender.sendMessage("**Bonusove dropy z:");
					sender.sendMessage("**Iron: +" + iron);
					sender.sendMessage("**Gold: +" + gold);
					sender.sendMessage("**Diamond: +" + diamond);
					
					sender.sendMessage("********************");
					sender.sendMessage("********************");*/
					
				//}
				/*else if (prof instanceof GugaHunter)
				{
					GugaHunter hunter = (GugaHunter)prof;
					double regen = ((double)hunter.GetHpRegen())/2;
					int dmg = hunter.GetDamageIncrease();
					sender.sendMessage("********************");
					sender.sendMessage("**HP Regen: " + regen + "hp za minutu");
					sender.sendMessage("**Zvyseny damage: " + dmg);
					sender.sendMessage("********************");
					sender.sendMessage("********************");
				}*/
			}
			else if (subCommand.matches("select"))
			{
				sender.sendMessage("Prosim uvedte profesi kterou chcete! Miner nebo Hunter");
			}
			else if (subCommand.matches("info"))
			{
				/*sender.sendMessage("********************");
				sender.sendMessage("**Profesi si muzete vybrat tak, ze napisete");
				sender.sendMessage("** /rpg select <vase_profese>");
				sender.sendMessage("**Mate na vyber ze dvou profesi:");
				sender.sendMessage("**      -Hunter a Miner");
				sender.sendMessage("**Kazda profese ma jine bonusy");*/
				sender.sendMessage("**XP ziskavate za zabijeni monster a kopani");
			//	sender.sendMessage("**Kazda profese dostava rozdilny pocet XP.");
				sender.sendMessage("**Maximalni level: " + new GugaProfession().GetLvlCap());
			}
		}
		else if (args.length == 2)
		{
			String subCommand = args[0];
			String arg1 = args[1];
			
			if (subCommand.matches("select"))
			{
				if (arg1.matches("hunter"))
				{
					if (plugin.professions.get(sender.getName()) == null)
					{
						GugaHunter prof = new GugaHunter(sender.getName(),0,plugin);
						plugin.professions.put(sender.getName(), prof);
						prof.StartRegenHp();
						sender.sendMessage("Stal jste se Hunterem!");
					}
					else 
					{
						sender.sendMessage("Nemuzete si znovu zvolit profesi!");
					}
				}
				else if (arg1.matches("miner"))
				{
					if (plugin.professions.get(sender.getName()) == null)
					{
						GugaMiner prof = new GugaMiner(sender.getName(),0,plugin);
						plugin.professions.put(sender.getName(), prof);
						sender.sendMessage("Stal jste se Minerem!");
					}
					else 
					{
						sender.sendMessage("Nemuzete si znovu zvolit profesi!");
					}
				}
				else 
				{
					sender.sendMessage("Toto neni profese!");
				}
			}
			else if (subCommand.matches("info"))
			{
				/*if (arg1.matches("hunter"))
				{
					sender.sendMessage("********************");
					sender.sendMessage("**Hunterovo Bonusy:");
					sender.sendMessage("** - Hp Regen (+0,5 kazde 2 levely)");
					sender.sendMessage("** - Bonus Damage (+1 kazde 4 levely)");
					sender.sendMessage("********************");
					sender.sendMessage("********************");
					
				}*/
			//	else if (arg1.matches("miner"))
			//	{
					/*sender.sendMessage("********************");
					sender.sendMessage("**Minerovo Bonusy:");
					sender.sendMessage("** - Zvysene dropy z:");
					sender.sendMessage("**      -Iron (+1 every 6 levels)");
					sender.sendMessage("**      -Gold (+1 every 8 levels)");
					sender.sendMessage("**      -Diamond (+1 every 10 levels)");*/
					sender.sendMessage("********************");
					sender.sendMessage("** - Sance vzacneho dropu ze Stone:");
					sender.sendMessage("**      -Iron (+0.1% kazdych 10 levelu)");
					sender.sendMessage("**      -Gold (+0.1% kazdych 20 levelu)");
					sender.sendMessage("**      -Diamond (+0.1% kazdych 50 levelu)");
					sender.sendMessage("********************");
			//	}
			}
		}
		else if (args.length == 3)
		{
			String subCommand = args[0];
			String player = args[1];
			String xp = args[2];
			if (subCommand.matches("xp") && sender instanceof ConsoleCommandSender)
			{
				plugin.professions.get(player).GainExperience(Integer.parseInt(xp));
			}
		}
	}
	public static void CommandPlaces(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("PLACES MENU:");
			sender.sendMessage("/places list  -  Seznam vsech moznych mist.");
			sender.sendMessage("/places port <jmeno>  -  Teleportuje hrace na dane misto.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("list"))
			{
				sender.sendMessage("SEZNAM MIST:");
				Iterator<GugaPlace> i;
				if (GameMasterHandler.IsAtleastGM(sender.getName()))
				{
					i = GugaPort.GetAllPlaces().iterator();

				}
				else
				{
					i = GugaPort.GetPlacesForPlayer(sender.getName()).iterator();
				}
				while (i.hasNext())
				{
					GugaPlace e = i.next();
					if (e.GetOwner().equalsIgnoreCase("all"))
					{
						sender.sendMessage(ChatColor.BLUE + " - " + e.GetName());
					}
					else
					{
						sender.sendMessage(ChatColor.YELLOW + " - " + e.GetName());
					}
				}
			}
		}
		else if (args.length == 2)
		{
			String subCommand = args[0];
			String name = args[1];
			if (subCommand.matches("port"))
			{
				if (plugin.arena.IsArena(sender.getLocation()))
				{
					sender.sendMessage("V arene nemuzete pouzit prikaz /places!");
					return;
				}
				GugaPlace place;
				if ( (place = GugaPort.GetPlaceByName(name)) != null)
				{
					if (GugaPort.GetPlacesForPlayer(sender.getName()).contains(place) || GameMasterHandler.IsAtleastGM(sender.getName()))
					{
						place.Teleport(sender);
						return;
					}
				}
				sender.sendMessage("Toto misto neexistuje!");
			}
		}
	}
	public static void CommandAH(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("AUCTION HOUSE MENU:");
			sender.sendMessage("Jako Platidlo slouzi Gold Ingoty!");
			sender.sendMessage("Prikazy:");
			sender.sendMessage("/ah show - Zobrazi pocet stran.");
			sender.sendMessage("/ah show <strana> - Zobrazi danou stranku nabidky aukce.");
			sender.sendMessage("/ah buy <id> - Koupi aukci podle id z /ah show.");
			sender.sendMessage("/ah create <itemID> <pocet> <cena> - Vytvori novou aukci.");
			sender.sendMessage("/ah cancel <id> - Stornuje aukci podle ID z /ah my.");
			sender.sendMessage("/ah my - Zobrazi vase aukce.");
			return;
		}
		if (args.length >= 1)
		{
			String subCmd = args[0];
			if (subCmd.equalsIgnoreCase("show"))
			{
				if (args.length == 1)
				{
					sender.sendMessage("Aktualni pocet stran: " + GugaAuctionHandler.GetPagesCount());
				}
				else if (args.length == 2)
				{
					
					int page = Integer.parseInt(args[1]);
					ArrayList<GugaAuction> list = GugaAuctionHandler.GetAuctionPage(page);
					if (list.size() == 0)
					{
						sender.sendMessage("Tato strana neexistuje!");
						return;
					}
					sender.sendMessage("ID ; itemID ; pocet ; cena ; vlastnik");
					Iterator<GugaAuction> i = list.iterator();
					int i2 = GugaAuctionHandler.ITEMS_PER_PAGE * (page - 1);
					while (i.hasNext())
					{
						GugaAuction auction = i.next();
						int itemID = auction.GetItemID();
						int amount = auction.GetAmount();
						int price = auction.GetPrice();
						String owner = auction.GetOwner();
						sender.sendMessage(i2 + " ; " + itemID + " ; " + amount + " ; " + price + " ; " + owner);
						i2++;
					}
				}
			}
			else if (subCmd.equalsIgnoreCase("create"))
			{
				if (args.length == 4)
				{
					int itemID = Integer.parseInt(args[1]);
					int amount = Integer.parseInt(args[2]);
					int price = Integer.parseInt(args[3]);
					if (GugaAuctionHandler.CreateAuction(itemID, amount, price, sender))
						sender.sendMessage("Aukce uspesne vytvorena!");
					else
					{
						sender.sendMessage("Nemate dostatek itemu v inventari!");
					}
					
				}
			}
			else if (subCmd.equalsIgnoreCase("buy"))
			{
				if (args.length == 2)
				{
					int index = Integer.parseInt(args[1]);
					if (GugaAuctionHandler.GetAllAuctions().get(index).GetOwner().matches(sender.getName()))
					{
						sender.sendMessage("Nemuzete koupit vlastni aukci!");
						return;
					}
					if (GugaAuctionHandler.BuyAuction(sender, index))
						sender.sendMessage("Aukce uspesne koupena!");
					else
						sender.sendMessage("Nemate na zaplaceni!");
				}
			}
			else if (subCmd.equalsIgnoreCase("cancel"))
			{
				if (args.length == 2)
				{
					int index = Integer.parseInt(args[1]);
					if (GugaAuctionHandler.CancelAuction(index, sender))
						sender.sendMessage("Aukce uspesne zrusena.");
					else
						sender.sendMessage("Aukce s timto ID neexistuje!");
				}
			}
			else if (subCmd.equalsIgnoreCase("my"))
			{
				if (args.length == 1)
				{
					ArrayList<GugaAuction> list = GugaAuctionHandler.GetPlayerAuctions(sender);
					if (list.size() == 0)
					{
						sender.sendMessage("Nemate zadnou aukci!");
						return;
					}
					Iterator<GugaAuction> i = list.iterator();
					int i2 = 0;
					sender.sendMessage("ID ; itemID ; amount ; price");
					while (i.hasNext())
					{
						GugaAuction auction = i.next();
						int itemID = auction.GetItemID();
						int amount = auction.GetAmount();
						int price = auction.GetPrice();
						sender.sendMessage(i2 + " ; " + itemID + " ; " + amount + " ; " + price);
						i2++;
					}
				}
			}
		}
	}
	public static void CommandModule(String args[])
	{
		if (args.length >= 1)	
		 {
			if (args[0].equalsIgnoreCase("ChestsModule"))
			{
				plugin.config.chestsModule = !plugin.config.chestsModule;
				plugin.config.SetConfiguration();
				plugin.log.info("chestModule = "+plugin.config.chestsModule);
			}
			else if (args[0].equalsIgnoreCase("AccountsModule"))
			{
				plugin.config.accountsModule = !plugin.config.accountsModule;
				plugin.config.SetConfiguration();
				plugin.log.info("accountsModule = "+plugin.config.accountsModule);
			}
		 }
		 else
		 {
			 plugin.log.info("Modules:");
			 plugin.log.info("	AccountsModule	= "+plugin.config.accountsModule);
			 plugin.log.info("	ChestsModule	= "+plugin.config.chestsModule);
		 }
	}
	public static void CommandRegister(Player sender, String args[])
	{
		if(plugin.acc.UserIsRegistered(sender))
		{
			sender.sendMessage("Tento ucet je jiz zaregistrovan!");
		}
		else
		{
			if (args.length > 0)
			{
				String pass = args[0];
				plugin.acc.RegisterUser(sender, pass);
			}
			else
			{
				sender.sendMessage("Prosim zadejte vase heslo!");
			}
		}
	}
	public static void CommandArena(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("ARENA MENU:");
			sender.sendMessage("Commands:");
			sender.sendMessage("/arena join - Teleportuje hrace do areny");
			sender.sendMessage("/arena leave - Vrati hrace do normalniho sveta");
			sender.sendMessage("/arena stats - Zobrazi zebricek nejlepsich hracu");
			sender.sendMessage("/arena info - Info about arena and ranks");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("join"))
			{
				if (!plugin.arena.IsArena(sender.getLocation()))
				{
					plugin.arena.PlayerJoin(sender);
				}
				else
				{
					sender.sendMessage("V arene jiz jste!");
				}
			}
			else if (subCommand.matches("leave"))
			{
				plugin.arena.PlayerLeave(sender);
			}
			else if (subCommand.matches("setspawn") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				plugin.arena.SetSpawn(sender);
			}
			else if (subCommand.matches("removespawn") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				plugin.arena.RemoveSpawn(sender);
			}
			else if (subCommand.matches("stats"))
			{
				plugin.arena.ShowPvpStats(sender);
			}
			else if (subCommand.matches("info"))
			{
				Integer kills = plugin.arena.GetPlayerStats(sender);
				sender.sendMessage("Vas rank: " +ArenaTier.GetTier(kills).toString());
				sender.sendMessage("Pocet killu: " +kills);
			}
		}
	}
	public static void CommandReply(Player sender, String args[])
	{
		if (args.length > 0)
		{
			Player p;
			if ( (p = reply.get(sender)) != null)
			{
				int i = 0;
				String msg = "";
				while (i < args.length)
				{
					msg += args[i] + " ";
					i++;
				}
				String cmd = "/tell " + p.getName() + " " + msg;
				sender.chat(cmd);
				//sender.sendMessage(ChatColor.GRAY + "To " + p.getName() + ": " + msg);
				reply.put(p, sender);
				return;
			}
			sender.sendMessage("Nemate komu odpovedet!");
		}
	}
	public static void CommandEvent(Player sender, String args[])
	{
		if (!GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("join"))
				{
					if (GugaEvent.acceptInv)
					{
						GugaEvent.AddPlayer(sender.getName().toLowerCase());
						sender.sendMessage("Byl jste uspesne prihlasen k eventu");
					}
					else
						sender.sendMessage("Nyni se nemuzete prihlasit k zadnemu eventu!");
				}
			}
			return;
		}
		if (args.length == 0)
		{
			String stateGodMode;
			if (GugaEvent.godMode)
				stateGodMode = "[ON]";
			else
				stateGodMode = "[OFF]";
			String stateInv;
			if (GugaEvent.acceptInv)
				stateInv = "[ON]";
			else
				stateInv = "[OFF]";
			sender.sendMessage("EVENT MENU:");
			sender.sendMessage("Commands:");
			sender.sendMessage("/event players - Shows players submenu.");
			sender.sendMessage("/event inventory - Shows inventory submenu.");
			sender.sendMessage("/event spawners - Shows spawners submenu.");
			sender.sendMessage("/event teleport - Teleports all tagged players to your location.");
			sender.sendMessage("/event tpback - Teleports all players back to their original locations.");
			sender.sendMessage("/event give <itemID> <amount> - Adds specified item to tagged players.");
			sender.sendMessage("/event godmode " + stateGodMode + " - Toggles immortality for tagged players.");
			sender.sendMessage("/event stats <itemID> - Prints stats of all tagged players.");
			sender.sendMessage("/event allowinv " + stateInv + " - Allow players to join your event.");
			return;
		}
		String arg1 = args[0];
		if (arg1.equalsIgnoreCase("teleport"))
		{
			GugaEvent.TeleportPlayersTo(sender.getName());
			sender.sendMessage("Players teleported.");
			return;
		}
		else if (arg1.equalsIgnoreCase("allowinv"))
		{
			GugaEvent.ToggleAcceptInvites();
			String stateInv;
			if (GugaEvent.acceptInv)
				stateInv = "[ON]";
			else
				stateInv = "[OFF]";
			sender.sendMessage("Accept Invites " + stateInv);
		}
		else if (arg1.equalsIgnoreCase("godmode"))
		{
			GugaEvent.godMode = !GugaEvent.godMode;
			String state;
			if (GugaEvent.godMode)
				state = "[ON]";
			else
				state = "[OFF]";
			sender.sendMessage("GodMode " + state);
			return;
		}
		else if (arg1.equalsIgnoreCase("tpback"))
		{
			GugaEvent.TeleportPlayersBack();
			sender.sendMessage("Players teleported back.");
			return;
		}
		else if (arg1.equalsIgnoreCase("stats"))
		{
			sender.sendMessage("PLAYER STATS FOR ID " + args[1] + ":");
			if (args.length == 2)
			{
				Iterator<String> i = GugaEvent.GetItemCountStats(Integer.parseInt(args[1])).iterator();
				while (i.hasNext())
				{
					String[] split = i.next().split(";");
					sender.sendMessage(split[0] + " - " + split[1]);
				}
			}
			return;
		}
		else if (arg1.equalsIgnoreCase("give"))
		{
			if (args.length == 3)
			{
				GugaEvent.AddItemToPlayers(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				sender.sendMessage("Items added.");
				return;
			}
		}
		else if (arg1.equalsIgnoreCase("spawners"))
		{
			if (args.length == 1)
			{
				sender.sendMessage("/event spawners add <group> <typeID> <interval> - Creates a spawner.");
				sender.sendMessage("/event spawners remove <group> <index> - Removes spawner from a group.");
				sender.sendMessage("/event spawners clear <group> - Removes all spawners of a specified group.");
				sender.sendMessage("/event spawners toggle <group> - Turns spawning ON or OFF.");
				sender.sendMessage("/event spawners list - Prints all spawner groups.");
				sender.sendMessage("/event spawners list <group> - Prints all spawners of specified group.");
				sender.sendMessage("/event spawners ids - Prints list of all mob ids.");
			}
			else if (args.length == 2)
			{
				if (args[1].equalsIgnoreCase("list"))
				{
					Iterator<String> i = GugaEvent.GetGroupNames().iterator();
					while (i.hasNext())
					{
						String group = i.next();
						String state;
						if (GugaEvent.GetGroupState(group))
							state = "[ON]";
						else
							state = "[OFF]";
						sender.sendMessage(group + " " + state);
					}
				}
			}
			else if (args.length == 3)
			{
				if (args[1].equalsIgnoreCase("list"))
				{
					Iterator<GugaSpawner> i = GugaEvent.GetSpawnersOfGroup(args[2]).iterator();
					int index = 0;
					sender.sendMessage(args[2] + " SPAWNERS:");
					while (i.hasNext())
					{
						GugaSpawner spawner = i.next();
						Location loc = spawner.GetLocation();
						String state;
						if (spawner.GetSpawnState())
							state = "[ON]";
						else
							state = "[OFF]";
						sender.sendMessage(index + ": " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + state);
						index++;
					}
				}
				if (args[1].equalsIgnoreCase("clear"))
				{
					GugaEvent.ClearSpawnersFromGroup(args[2]);
					sender.sendMessage("All spawners removed.");
				}
				else if (args[1].equalsIgnoreCase("toggle"))
				{
					String state;
					GugaEvent.ToggleGroupSpawning(args[2]);
					if (GugaEvent.GetGroupState(args[2]))
						state = "[ON]";
					else
						state = "[OFF]";
					sender.sendMessage(args[2] + " " + state);
				}
				else if (args[1].equalsIgnoreCase("ids"))
				{
					int i = 0;
					for (CreatureType type : CreatureType.values())
					{
						sender.sendMessage(i + type.getName());
						i++;
					}
				}
				return;
			}
			else if (args.length == 4)
			{
				if (args[1].equalsIgnoreCase("remove"))
				{
					GugaEvent.RemoveSpawnerFromGroup(args[2], Integer.parseInt(args[3]));
					sender.sendMessage("Spawner removed.");
				}
				return;
			}
			else if (args.length == 5)
			{
				if (args[1].equalsIgnoreCase("add"))
				{
					GugaEvent.AddSpawnerToGroup(args[2], sender.getLocation(), Integer.parseInt(args[4]), Integer.parseInt(args[3]));
					sender.sendMessage("Spawner has been added.");
				}
				return;
			}
		}
		else if (arg1.equalsIgnoreCase("inventory"))
		{
			if (args.length == 1)
			{
				sender.sendMessage("/event inventory clear - Clears inventories of tagged players.");
				sender.sendMessage("/event inventory return <0 / 1> - Returns old items back to players. 0 or 1 determines, whether items gained in event gonna be deleted. 0 - not deleted, 1 - deleted.");
				return;
			}
			else if (args.length == 2)
			{
				if (args[1].equalsIgnoreCase("clear"))
				{
					GugaEvent.ClearInventories();
					sender.sendMessage("Inventories cleared.");
				}
				return;
			}
			else if (args.length == 3)
			{
				if (args[1].equalsIgnoreCase("return"))
				{
					GugaEvent.ReturnInventories(args[2].equals("1"));
					sender.sendMessage("Inventories returned.");
				}
				return;
			}
		}
		else if (arg1.equalsIgnoreCase("players"))
		{
			if (args.length == 1)
			{
				sender.sendMessage("/event players add <name1,name2,name3> - Tags specified players for event.");
				sender.sendMessage("/event players remove <name> - Removes specified player from the list.");
				sender.sendMessage("/event players clear - Removes all tags.");
				sender.sendMessage("/event players list - List of tagged players.");
				return;
			}
			else if (args.length == 2) 
			{
				if (args[1].equalsIgnoreCase("clear"))
				{
					GugaEvent.ClearPlayers();
					sender.sendMessage("Player list cleared.");
				}
				else if (args[1].equalsIgnoreCase("list"))
				{
					Iterator<String> i = GugaEvent.GetPlayers().iterator();
					sender.sendMessage("PLAYER LIST:");
					while (i.hasNext())
					{
						sender.sendMessage(i.next());
					}
				}
				return;
			}
			else if (args.length == 3)
			{
				if (args[1].equalsIgnoreCase("add"))
				{
					String[] names = args[2].split(",");
					int i = 0;
					while (i < names.length)
					{
						GugaEvent.AddPlayer(names[i]);
						i++;
					}
					sender.sendMessage("Player(s) added.");
				}
				else if (args[1].equalsIgnoreCase("remove"))
				{
					if (GugaEvent.ContainsPlayer(args[2]))
					{
						GugaEvent.RemovePlayer(args[2]);
						sender.sendMessage("Player removed from the list.");
					}
					else
						sender.sendMessage("Player not found.");
				}
				return;
			}
		}
	}
	public static void CommandGM(Player sender, String args[])
	{
		if (!GameMasterHandler.IsAtleastGM(sender.getName()))
			return;
		if (!plugin.acc.UserIsLogged(sender))
		{
			sender.sendMessage("Musite byt prihlaseny, aby jste mohl pouzit tento prikaz!");
			return;
		}
		if (args.length == 0)
		{
			sender.sendMessage("GM MENU:");
			sender.sendMessage("Commands:");
			if (GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("/gm ip <name> - Shows an IP of a player");
				sender.sendMessage("/gm setspawn - Sets a world spawn to GM's position");
				sender.sendMessage("/gm credits - Credits sub-menu.");
				sender.sendMessage("/gm setvip <name> <months>  -  Set VIP to certain player for (now + months)");
				sender.sendMessage("/gm getvip <name>  -  Gets VIP expiration date");
				sender.sendMessage("/gm announce  - Announcements sub-menu.");
				sender.sendMessage("/gm genblock <typeID> <reltiveX> <relativeY> <relativeZ>  -  Spawns a blocks from block you point at.");
				sender.sendMessage("/gm godmode <name>  -  Toggles immortality for a certain player.");
				sender.sendMessage("/gm invis <name>  -  Toggles invisibility for a certain player.");
				sender.sendMessage("/gm spectate  -  Spectation sub-menu.");
				sender.sendMessage("/gm places - Places sub-menu.");
				sender.sendMessage("/gm regions - Regions sub-menu.");
			}
			sender.sendMessage("/gm ban - Bans sub-menu.");
			sender.sendMessage("/gm log - Shows a log records for target block.");
			sender.sendMessage("/gm tp <x> <y> <z>  -  Teleports gm to specified coords.");
			sender.sendMessage("/gm gmmode <name> -  Toggles gm mode for a certain player.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("log"))
			{
				plugin.logger.PrintBlockData(sender, sender.getTargetBlock(null, 20));
			}
			else if (subCommand.matches("setspawn") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				Location pLoc = sender.getLocation();
				sender.getWorld().setSpawnLocation((int)pLoc.getX(), (int)pLoc.getY(), (int)pLoc.getZ());
				sender.sendMessage("New World Spawn has been set!");
			}
			else if (subCommand.matches("announce") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("/gm announce print - Prints messages and indexes.");
				sender.sendMessage("/gm announce remove <index> - Removes a message from the list.");
				sender.sendMessage("/gm announce add <message> - Adds new message to the list.");
			}
			else if (subCommand.matches("ban"))
			{
				sender.sendMessage("/gm ban add <player> <hours> - Bans a player for number of hours.");
				sender.sendMessage("/gm ban remove <player> - Removes a ban.");
				sender.sendMessage("/gm ban list  -  Shows all banned players.");
			}
			else if (subCommand.matches("credits") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("/gm credits add <player> <amount>  -  Add credits to a player.");
				sender.sendMessage("/gm credits remove <player> <amount>  -  Remove credits to a player.");
				sender.sendMessage("/gm credits balance <player>  -  Shows credits of a player.");
			}
			
			else if (subCommand.matches("spectate") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("/gm spectate player <name> - Start spectating certain player.");
				sender.sendMessage("/gm spectate stop - Stop spectating.");
			}
			else if(subCommand.matches("places") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("/gm places list  - Show list of all places.");	
				sender.sendMessage("/gm places add <name> <owner> - Adds actual position to places (owner all = public).");	
				sender.sendMessage("/gm places remove <name> - Removes a certain place from the list.");	
			}
			else if (subCommand.matches("regions") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("/gm regions list  - Show list of all places.");	
				sender.sendMessage("/gm regions add <name> <owner1,owner2> <x1> <x2> <z1> <z2> - Adds Region");	
				sender.sendMessage("/gm regions owners <name> <owners> - Changes owners of certain region.");	
				sender.sendMessage("/gm regions remove <name> - Removes a certain region from the list.");	
			}
		}
		else if (args.length == 2)
		{
			String subCommand = args[0];
			String arg1 = args[1];
			Player p;
			if (subCommand.matches("ip") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if ((p = plugin.getServer().getPlayer(arg1)) != null)
				{
					sender.sendMessage("Players IP:" + p.getAddress());
				}
				else
				{
					sender.sendMessage("This player is not online!");
				}
			}
			else if (subCommand.matches("regions") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				sender.sendMessage("LIST OF REGIONS:");
				Iterator<GugaRegion> i = GugaRegionHandler.GetAllRegions().iterator();
				while (i.hasNext())
				{
					GugaRegion region = i.next();
					String[] owners = region.GetOwners();
					int[] coords = region.GetCoords();
					sender.sendMessage(" - " + region.GetName() + " [" + GugaRegionHandler.OwnersToLine(owners) + "]   <" + coords[GugaRegion.X1] + "," + coords[GugaRegion.X2] + "," + coords[GugaRegion.Z1] + "," + coords[GugaRegion.Z2] + ">");
				}
			}
			else if (subCommand.matches("places") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (arg1.matches("list"))
				{
					sender.sendMessage("LIST OF PLACES:");
					Iterator<GugaPlace> i = GugaPort.GetAllPlaces().iterator();
					while (i.hasNext())
					{
						GugaPlace e = i.next();
						sender.sendMessage(" - " + e.GetName() + "(" + e.GetOwner() + ")");
					}
				}
			}
			else if(subCommand.matches("announce") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (arg1.matches("print"))
				{
					int i = 0;
					String msg;
					while ( (msg = GugaAnnouncement.GetAnnouncement(i)) != null)
					{
						sender.sendMessage("[" + i + "]  -  " + msg);
						i++;
					}
				}
			}
			else if (subCommand.matches("getvip") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				GugaVirtualCurrency vip = plugin.FindPlayerCurrency(arg1);
				if (vip.IsVip())
				{
					sender.sendMessage("VIP Status for " + arg1 + " expires " + new Date(vip.GetExpirationDate()));
				}
				else
				{
					sender.sendMessage("This player is not a VIP");
				}
			}
			else if (subCommand.matches("gmmode"))
			{
				if (!GameMasterHandler.IsAdmin(sender.getName()))
					if (!arg1.equalsIgnoreCase(sender.getName()))
					{
						sender.sendMessage("You can only set gmmode to yourself!");
						return;
					}
				if ((p = plugin.getServer().getPlayer(arg1)) != null)
				{
					GameMode mode = p.getGameMode();
					if (mode == GameMode.CREATIVE)
					{
						p.setGameMode(GameMode.SURVIVAL);
						sender.sendMessage("GM Mode for " + arg1 + " has been turned off");
					}
					else
					{
						p.setGameMode(GameMode.CREATIVE);
						sender.sendMessage("GM Mode for " + arg1 + " has been turned on");
					}
				}
			}
			else if (subCommand.matches("godmode"))
			{
				if (godMode.contains(arg1.toLowerCase()))
				{
					godMode.remove(arg1);
					sender.sendMessage("Immortality for " + arg1 + " has been turned off");
				}
				else
				{
					godMode.add(arg1.toLowerCase());
					sender.sendMessage("Immortality for " + arg1 + " has been turned on");
				}
			}
			else if (subCommand.matches("invis") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				ToggleInvisibility(sender, arg1);
			}
			else if (subCommand.matches("ban"))
			{
				if (arg1.matches("list"))
				{
					Iterator<GugaBan> i = GugaBanHandler.GetBanList().iterator();
					sender.sendMessage("List of bans: ");
					while (i.hasNext())
					{
						GugaBan ban = i.next();
						if (new Date(ban.GetExpiration()).after(new Date()))
							plugin.log.info(ban.GetPlayerName() + "  -  " + new Date(ban.GetExpiration()).toString());
					}
				}
			}
			else if (subCommand.matches("spectate") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (arg1.matches("stop"))
				{
					if (RemoveSpectation(sender))
					{
						sender.sendMessage("Spectation stopped");
					}
					else
					{
						sender.sendMessage("You are not spectating anyone!");
					}
				}
			}
		}
		else if (args.length > 2)
		{
			String subCommand = args[0];
			if (subCommand.matches("announce") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				String arg1 = args[1];
				if (arg1.matches("remove"))
				{
					String arg2 = args[2];
					if (arg2 != null)
					{
						int num = Integer.parseInt(arg2);
						if (GugaAnnouncement.RemoveAnnouncement(num))
						{
							sender.sendMessage("Announcement has been succesfuly removed.");
						}
						else
						{
							sender.sendMessage("This announcement doesnt exist!");
						}
					}
				}
				else if (arg1.matches("add"))
				{
					String msg = "";
					int i = 2;
					while (i < args.length)
					{
						msg += args[i];
						msg += " ";
						i++;
					}
					GugaAnnouncement.AddAnnouncement(msg);
					sender.sendMessage("Announcement succesfuly added! <" + msg + ">");
				}
			}
			else if (subCommand.matches("ban"))
			{
				if (args.length == 3)
				{
					String arg1 = args[1];
					String arg2 = args[2];
					if (arg1.matches("remove"))
					{
						GugaBanHandler.RemoveBan(arg2);
						sender.sendMessage("Ban succefuly removed!");
					}
				}
				else if (args.length == 4)
				{
					String arg1 = args[1];
					String arg2 = args[2];
					String arg3 = args[3];
					if (arg1.matches("add"))
					{
						Calendar c = Calendar.getInstance();
						c.setTime(new Date());
						c.add(Calendar.HOUR, Integer.parseInt(arg3));
						GugaBanHandler.AddBan(arg2, c.getTimeInMillis());
						Player p = plugin.getServer().getPlayer(arg2);
						if (p != null)
							p.kickPlayer("Vas ucet byl zabanovan na " + arg3 + " hodiny.");
						sender.sendMessage("Ban succesfuly added!");
					}
				}
			}
			else if (subCommand.matches("places") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (args.length == 3)
				{
					String arg1 = args[1];
					String arg2 = args[2];
					if (arg1.matches("remove"))
					{
						GugaPlace place;
						if ( (place = GugaPort.GetPlaceByName(arg2)) != null)
						{
							GugaPort.RemovePlace(place);
							sender.sendMessage("Place successfully removed");
						}
						else
						{
							sender.sendMessage("This place doesnt exist!");
						}
					}
				}
				else if (args.length == 4)
				{
					String arg1 = args[1];
					String arg2 = args[2];
					String arg3 = args[3];
					if (arg1.matches("add"))
					{
						if (GugaPort.GetPlaceByName(arg2) != null)
						{
							sender.sendMessage("This place already exists!");
							return;
						}
						GugaPort.AddPlace(arg2, arg3, sender.getLocation());
						sender.sendMessage("Place successfully added");
					}
				}
			}
			else if (subCommand.matches("regions") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (args.length == 3)
				{
					String subCmd = args[1];
					if (subCmd.matches("remove"))
					{
						String name = args[2];
						GugaRegion region = GugaRegionHandler.GetRegionByName(name);
						if (region == null)
						{
							sender.sendMessage("Region not found!");
							return;
						}
						GugaRegionHandler.RemoveRegion(region);
						sender.sendMessage("Region successfully removed!");
					}
				}
				else if (args.length == 4)
				{
					String subCmd = args[1];
					if (subCmd.matches("owners"))
					{
						String name = args[2];
						String[] owners = args[3].split(",");
						if (GugaRegionHandler.SetRegionOwners(name, owners))
							sender.sendMessage("Owners successfuly set!");
						else
							sender.sendMessage("Region not found!");
					}
				}
				else if (args.length == 8)
				{
					String subCmd = args[1];
					if (subCmd.matches("add"))
					{
						String name = args[2];
						if (GugaRegionHandler.GetRegionByName(name) != null)
						{
							sender.sendMessage("Region with this name already exists!");
							return;
						}
						String[] owners = args[3].split(",");
						int x1 = Integer.parseInt(args[4]);
						int x2 = Integer.parseInt(args[5]);
						int z1 = Integer.parseInt(args[6]);
						int z2 = Integer.parseInt(args[7]);
						GugaRegionHandler.AddRegion(name, owners, x1, x2, z1, z2);
						sender.sendMessage("Region successfully added");
					}
				}
			}
			else if (subCommand.matches("credits") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (args.length == 3)
				{
					String arg1 = args[1];
					String name = args[2];
					if (arg1.matches("balance"))
					{
						GugaVirtualCurrency p = plugin.FindPlayerCurrency(name);
						if (p == null)
						{
							sender.sendMessage("This account doesnt have any credits.");
							return;
						}
						sender.sendMessage("This account has " + p.GetCurrency() + " credits.");
					}
				}
				else if (args.length == 4)
				{
					String arg1 = args[1];
					String name = args[2];
					int amount = Integer.parseInt(args[3]);
					if (arg1.matches("add"))
					{
						if (amount > 1000)
						{
							sender.sendMessage("You cannot add that much!");
							return;
						}
						if (amount <= 0)
						{
							sender.sendMessage("Amount has to be > 0!");
							return;
						}
						GugaVirtualCurrency p = plugin.FindPlayerCurrency(name);
						if (p == null)
						{
							sender.sendMessage("Couldnt find player with this name");
							return;
						}
						p.AddCurrency(amount);
						Player dest = plugin.getServer().getPlayer(name);
						if (dest != null)
							dest.sendMessage("You received +" + amount + " credits!");
						sender.sendMessage("You added +" + amount + " credits to " + name);
					}
					else if (arg1.matches("remove"))
					{
						if (amount > 1000)
						{
							sender.sendMessage("You cannot remove that much!");
							return;
						}
						if (amount <= 0)
						{
							sender.sendMessage("Amount has to be > 0!");
							return;
						}
						GugaVirtualCurrency p = plugin.FindPlayerCurrency(name);
						if (p == null)
						{
							sender.sendMessage("Couldnt find player with this name");
							return;
						}
						p.RemoveCurrency(amount);
						plugin.getServer().getPlayer(name).sendMessage("You lost +" + amount + " credits!");
						sender.sendMessage("You removed +" + amount + " credits from " + name);
					}
				}
			}
			else if (subCommand.matches("genblock") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (args.length == 5)
				{
					int typeID = Integer.parseInt(args[1]);
					int x = Integer.parseInt(args[2]);
					int y = Integer.parseInt(args[3]);
					int z = Integer.parseInt(args[4]);
					plugin.GenerateBlockType(sender, typeID, x, y, z);
				}
			}
			else if (subCommand.matches("setvip") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (args.length == 3)
				{
					String pName = args[1];
					int months = Integer.parseInt(args[2]);
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					//int i = 0;
					//while (i < months)
					//{
						//c.roll(Calendar.MONTH, true);
					c.add(Calendar.MONTH, months);
						//i++;
					//}
					GugaVirtualCurrency p = plugin.FindPlayerCurrency(pName);
					p.SetExpirationDate(c.getTime());
					sender.sendMessage("Vip Status for " + pName + " is active till " + c.getTime());
				}
			}
			else if (subCommand.matches("tp"))
			{
				if (args.length == 4)
				{
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);
					int z = Integer.parseInt(args[3]);
					Location loc = new Location(sender.getWorld(), x, y, z);
					sender.teleport(loc);
				}
			}
			else if (subCommand.matches("spectate") && GameMasterHandler.IsAdmin(sender.getName()))
			{
				if (args.length == 3)
				{
					String arg1 = args[1];
					if (arg1.matches("player"))
					{
						String tarName = args[2];
						Player tarPlayer = plugin.getServer().getPlayer(tarName);
						spectation.put(tarPlayer.getName(), new GugaSpectator(plugin,tarPlayer,sender));
						sender.sendMessage("Starting spectation.");
					}
				}
			}
		}
	}
	public static void CommandLogin(Player sender, String args[])
	{
		 String pass = args[0];
		 if (plugin.acc.UserIsRegistered(sender))
		 {
			 if (!plugin.acc.UserIsLogged(sender))
			 {
				 if (!plugin.acc.LoginUser(sender, pass))
					 return;
				 sender.teleport(plugin.acc.playerStart.get(sender.getName()));
				/* GugaProfession prof;
					if ((prof = plugin.professions.get(sender.getName())) != null)
					{
						if (prof instanceof GugaHunter)
						{
							((GugaHunter)prof).StartRegenHp();
						}
					}*/
			 }
			 else
			 {
				 sender.sendMessage("Jste jiz prihlaseny!");
			 }
		 }
		 else
		 {
			 sender.sendMessage("Nejdrive se musite zaregistrovat!");
		 }
	}
	public static void CommandDebug()
	{
		plugin.debug = !plugin.debug;
		plugin.log.info("DEBUG="+plugin.debug);
	}
	public static void CommandPassword(Player sender, String args[])
	{
		if (plugin.acc.UserIsRegistered(sender))
		{
			if (plugin.acc.UserIsLogged(sender))
			{
				if (args.length == 2)
				{
					plugin.acc.ChangePassword(sender, args[0], args[1]);
				}
				else
				{
					sender.sendMessage("Prosim vlozte vase stare a nove heslo");
				}
			}
			else
			{
				sender.sendMessage("Nejdrive se musite prihlasit!");
			}
		}
		else
		{
			sender.sendMessage("Nejdrive se musite zaregistrovat!");
		}
	}
	private static void ToggleInvisibility(Player sender, String pName)
	{
		Player p = plugin.getServer().getPlayer(pName);
		GugaInvisibility inv;
		if (!p.isOnline())
		{
			return;
		}
		if ( (inv = invis.get(p)) != null)
		{
			inv.Stop();
			invis.remove(p);
			sender.sendMessage("Invisibility for " + pName + " has been turned off");
		}
		else
		{
			inv = new GugaInvisibility(p, 50, plugin);
			inv.Start();
			invis.put(p, inv);
			sender.sendMessage("Invisibility for " + pName + " has been turned on");
		}
	}
	private static boolean RemoveSpectation(Player spectator)
	{
		Iterator<Entry<String, GugaSpectator>> i;
		i = spectation.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, GugaSpectator> element = i.next();
			GugaSpectator spec = element.getValue();
			if (spec.GetSpectator().getName().matches(spectator.getName()))
			{
				spec.SpectateStop();
				i.remove();
				return true;
			}
		}
		return false;
	}
	public static HashMap<Player, Player> reply = new HashMap<Player, Player>();
	public static ArrayList<String> godMode = new ArrayList<String>();
	public static HashMap<Player, GugaInvisibility> invis = new HashMap<Player, GugaInvisibility>();
	public static HashMap<String, GugaSpectator> spectation = new HashMap<String, GugaSpectator>(); // <target, GugaSpectator>
	private static Guga_SERVER_MOD plugin;
}
package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.AutoSaver;
import me.Guga.Guga_SERVER_MOD.BasicWorld;
import me.Guga.Guga_SERVER_MOD.DataPager;
import me.Guga.Guga_SERVER_MOD.Enchantments;
import me.Guga.Guga_SERVER_MOD.Enchantments.EnchantmentResult;
import me.Guga.Guga_SERVER_MOD.GameMaster;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;
import me.Guga.Guga_SERVER_MOD.GugaAnnouncement;
import me.Guga.Guga_SERVER_MOD.GugaArena.ArenaSpawn;
import me.Guga.Guga_SERVER_MOD.GugaArena.ArenaTier;
import me.Guga.Guga_SERVER_MOD.GugaEvent;
import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaMute;
import me.Guga.Guga_SERVER_MOD.GugaParty;
import me.Guga.Guga_SERVER_MOD.GugaProfession2;
import me.Guga.Guga_SERVER_MOD.GugaRegion;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Homes;
import me.Guga.Guga_SERVER_MOD.Locker;
import me.Guga.Guga_SERVER_MOD.MinecraftPlayer;
import me.Guga.Guga_SERVER_MOD.MinecraftPlayer.ConnectionState;
import me.Guga.Guga_SERVER_MOD.PlacesManager.Place;
import me.Guga.Guga_SERVER_MOD.Extensions.Residences.ResidenceHandler;
import me.Guga.Guga_SERVER_MOD.ShopManager.ShopItem;
import me.Guga.Guga_SERVER_MOD.VipManager.VipItems;
import me.Guga.Guga_SERVER_MOD.VipManager.VipUser;
import me.Guga.Guga_SERVER_MOD.Listeners.GugaEntityListener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public abstract class GugaCommands 
{
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public static void TestCommand(String[] args)
	{
		Player p = plugin.getServer().getPlayer(args[0]);
		if (p == null)
			return;
		String str = "HELLO NIGGER";
		p.sendPluginMessage(plugin, "Guga", str.getBytes());
		
	}
	public static void CommandWho(Player sender)
	{
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
		String list = "";
		int playerCap = plugin.getServer().getMaxPlayers();
		int i = 0;
		sender.sendMessage(ChatColor.BLUE + "****************ON-LINE HRACI(" + onlinePlayers.length + "/" + playerCap +")****************");
		while(i<onlinePlayers.length)
		{
			Player p = onlinePlayers[i];
			GugaProfession2 prof = plugin.userManager.getUser(p.getName()).getProfession();
			int level = (prof==null)? 0 : prof.GetLevel();
			if(GameMasterHandler.IsRank(p.getName(), Rank.ADMIN))
			{
				list += ChatColor.AQUA + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else if(GameMasterHandler.IsRank(p.getName(), Rank.GAMEMASTER))
			{
				list += ChatColor.GREEN + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else if(GameMasterHandler.IsRank(p.getName(), Rank.BUILDER))
			{
				list += ChatColor.GOLD + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else if(plugin.vipManager.isVip((p.getName())))
			{
				list += ChatColor.GOLD + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else
			{
				list += p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			if(i == (onlinePlayers.length-1))
				list += ".";
			else
				list += ", ";
			i++;
		}
		sender.sendMessage(list);
		sender.sendMessage("****************************************************");
	}	
	public static void CommandHelp(Player sender)
	{
		//log.info("<"+sender.getName() + " has used /help Command>");
		sender.sendMessage("******************************");
		sender.sendMessage("GUGA MINECRAFT SERVER MOD "+Guga_SERVER_MOD.version);
		sender.sendMessage("******************************");
		sender.sendMessage("Seznam prikazu:");
		sender.sendMessage(ChatColor.AQUA + " /login " + ChatColor.GRAY + "<heslo>  " + ChatColor.WHITE + "-  Prihlasi zaregistrovaneho hrace.");
		sender.sendMessage(ChatColor.AQUA + " /lock " + ChatColor.WHITE + "- Zamkne block.");
		sender.sendMessage(ChatColor.AQUA + " /unlock  " + ChatColor.WHITE + "- Odemkne block.");
		sender.sendMessage(ChatColor.AQUA + " /who  " + ChatColor.WHITE + "-  Seznam online hracu.");
		sender.sendMessage(ChatColor.AQUA + " /password " + ChatColor.GRAY + "<stare_heslo> <nove_heslo>  " + ChatColor.WHITE + "-  Zmeni heslo.");
		sender.sendMessage(ChatColor.AQUA + " /rpg  " + ChatColor.WHITE + "-  Informace o Vasem RPG.");
		sender.sendMessage(ChatColor.AQUA + " /credits " + ChatColor.WHITE + "- Menu ekonomiky.");
		sender.sendMessage(ChatColor.AQUA + " /arena  " + ChatColor.WHITE + "-  Menu areny.");
		sender.sendMessage(ChatColor.AQUA + " /eventworld  " + ChatColor.WHITE + "-  Menu EventWorldu.");
		sender.sendMessage(ChatColor.AQUA + " /shop  " + ChatColor.WHITE + "-  Menu Obchodu.");
		sender.sendMessage(ChatColor.AQUA + " /vip  " + ChatColor.WHITE + "-  VIP menu.");
		sender.sendMessage(ChatColor.AQUA + " /places " + ChatColor.WHITE + "- Menu mist, kam se da teleportovat.");
		sender.sendMessage(ChatColor.AQUA + " /r " + ChatColor.GRAY + "<message> " + ChatColor.WHITE + "-  Odpoved na whisper.");
		if (GameMasterHandler.IsAdmin(sender.getName()))
		{
			sender.sendMessage(ChatColor.AQUA + " /gm " + ChatColor.WHITE + "- GameMaster's menu.");
			sender.sendMessage(ChatColor.AQUA + "/event " + ChatColor.WHITE + "- Event menu.");
		}
		sender.sendMessage("******************************");
		sender.sendMessage("Created by Guga 2011");
		sender.sendMessage("******************************");
	}
	public static void CommandHome(Player p, String args[])
	{
		if(!(p.getWorld().getName().matches("world") || p.getWorld().getName().matches("world_basic")))
		{
			ChatHandler.FailMsg(p, "Tento prikaz zde nelze pouzit!");
			return;
		}
		if(args.length == 0)
		{
			Homes home;
			if((home = HomesHandler.getHomeByPlayer(p.getName())) != null)
			{
				p.teleport(HomesHandler.getLocation(home));
				ChatHandler.SuccessMsg(p, "Byl jste teleportovan na home!");
			}
			else
			{
				ChatHandler.FailMsg(p, "Vas home jeste nebyl nastaven!");
			}
		}
		else if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("set"))
			{
				if(HomesHandler.isWorldAllowedToSetHomeIn(p.getWorld().getName()))
				{
					HomesHandler.addHome(p);
					ChatHandler.SuccessMsg(p, "Vas home byl nastaven!");
				}
				else
				{
					ChatHandler.FailMsg(p, "V tomto svete si nemuzete nastavit home!");
				}
			}
			else
			{
				ChatHandler.FailMsg(p, "Prikaz home neprebira tento argument!");
			}
		}
		else
		{
			ChatHandler.FailMsg(p, "Prikaz home neprebira dalsi argumenty!");
		}
	}
	public static void CommandLocker(Player sender)
	{
		sender.sendMessage(ChatColor.BLUE+"***********");
		sender.sendMessage(ChatColor.BLUE+"LOCKER");
		sender.sendMessage(ChatColor.BLUE+"***********");
		sender.sendMessage("Zamcit muzete bednu, davkovac a pec!");
		sender.sendMessage("Blocky se zamykaji automaticky pri polozeni!");
		sender.sendMessage("PRIKAZY:");
		sender.sendMessage(ChatColor.AQUA + "/lock " + ChatColor.WHITE + "- zamce block");
		sender.sendMessage(ChatColor.AQUA + "/unlock " + ChatColor.WHITE + "- odemce block");
	}
	public static void CommandLock(Player sender)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54
		if (Locker.LockableBlocks.isLockableBlock(blockType))
		{
			if (!plugin.blockLocker.IsLocked(chest))
			{
				plugin.blockLocker.LockBlock(chest,sender.getName());
				ChatHandler.SuccessMsg(sender, "Vas block byl zamcen.");
			}
			else
			{
				ChatHandler.FailMsg(sender, "Tento block jiz nekdo zamknul");
			}
		}	
		else
		{
			ChatHandler.FailMsg(sender, "Tento block nelze zamcit!");
		}
	}
	public static void CommandConfirm(Player sender, String args[])
	{
		Player p = vipTeleports.get(sender);
		VipUser vip = plugin.vipManager.getVip(p.getName());
		if (p != null && vip != null)
		{
			if (GugaEvent.ContainsPlayer(p.getName()))
			{
				ChatHandler.FailMsg(sender, "Hrac se nemuze teleportovat v prubehu Eventu!");
				ChatHandler.FailMsg(p, "Nemuzete se teleportovat v prubehu Eventu!");
				return;
			}
			if (plugin.arena.IsArena(p.getLocation()))
			{
				ChatHandler.FailMsg(sender, "Hrac nemuze pouzit teleport v Arene!");
				ChatHandler.FailMsg(p, "Nemuzete pouzit teleport v Arene!");
				return;
			}
			plugin.vipManager.SetLastTeleportLoc(p.getName(),p.getLocation());
			p.teleport(sender);
			vipTeleports.remove(sender);
			
			ChatHandler.InfoMsg(sender, "Teleport prijat!");
		}
		else
			ChatHandler.FailMsg(sender, "Nemate zadny pozadavek na teleport!");
	}
	public static void CommandUnlock(Player sender)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54

		if (Locker.LockableBlocks.isLockableBlock(blockType) && plugin.blockLocker.IsLocked(chest))
		{ 
			if (plugin.blockLocker.IsOwner(chest, sender.getName()) || GameMasterHandler.IsAtleastGM(sender.getName()))
			{
				plugin.blockLocker.UnlockBlock(chest);
				ChatHandler.SuccessMsg(sender, "Vas blok byl odemcen.");
			}
			else
			{
				ChatHandler.FailMsg(sender, "Tento blok nemuzete odemknout!");
			}
		}
		else
		{
			ChatHandler.FailMsg(sender, "Tento block nelze odemknout!");
		}
		
	}
	
	public static void CommandCredits(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("/credits send <hrac> <pocet> - Posle zadany pocet kreditu danemu hraci.");
			sender.sendMessage("/credits balance - Zjisti stav Vasich kreditu.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if(subCommand.matches("balance"))
			{
				sender.sendMessage("Vas ucet:");
				sender.sendMessage("Kredity: " + ChatColor.GOLD + plugin.currencyManager.getBalance(sender.getName()));
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
					}
					else
						ChatHandler.FailMsg(sender, "Kredity se nepodarilo poslat. Zkontrolujte prosim spravnost zadaneho nicku.");
				}
				else
					ChatHandler.FailMsg(sender, "Na tuto akci nemate dostatek kreditu");
			}
		}
	}
	public static void CommandShop(Player sender, String args[])
	{
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Nejdrive se prihlaste!");
			return;
		}
		if (plugin.arena.IsArena(sender.getLocation()))
		{
			ChatHandler.FailMsg(sender, "V arene nemuzete pouzit prikaz /shop!");
			return;
		}
		if (plugin.EventWorld.IsEventWorld(sender.getLocation()))
		{
			ChatHandler.FailMsg(sender, "V EventWorldu nemuzete pouzit prikaz /shop!");
			return;
		}
		
		if (args.length == 0)
		{
			sender.sendMessage("Shop Menu:");
			sender.sendMessage("/shop buy <nazev>  -  Koupi dany item (1).");
			sender.sendMessage("/shop balance  -  Zobrazi vase kredity.");
			sender.sendMessage("/shop items <strana>  -  Seznam itemu, ktere se daji koupit.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("balance"))
			{
				sender.sendMessage("Vas ucet:");
				sender.sendMessage("Kredity: " + plugin.currencyManager.getBalance(sender.getName()));
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
		}
		else if (args[0].equals("buy") && (args.length == 3 || args.length == 2))
		{
			String arg1 = args[1];
			int arg2 = 1;
			if(args.length == 3)
				arg2 = Integer.parseInt(args[2]);
			plugin.shopManager.buyItem(sender.getName(),arg1, arg2);
		}
	}
	
	public static void CommandVIP(Player sender, String args[])
	{
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Nejprve se musite prihlasit!");
			return;
		}
		VipUser vip = plugin.vipManager.getVip(sender.getName());
		if (vip == null)
		{
			ChatHandler.FailMsg(sender, "Pouze VIP mohou pouzivat tento prikaz!");
			return;
		}
		if (GugaEvent.ContainsPlayer(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Nemuzete pouzivat VIP prikazy v prubehu eventu!");
			return;
		}
		if (plugin.arena.IsArena(sender.getLocation()))
		{
			ChatHandler.FailMsg(sender, "Prikazy pro VIP zde nelze pouzit.");
			return;
		}
		if (plugin.EventWorld.IsEventWorld(sender.getLocation()))
		{
			ChatHandler.FailMsg(sender, "Prikazy pro VIP zde nelze pouzit.");
			return;
		}
		if (BasicWorld.IsBasicWorld(sender.getLocation()))
		{
			ChatHandler.FailMsg(sender, "Prikazy pro VIP zde nelze pouzit.");
			return;
		}
		if (sender.getWorld().getName().startsWith("survival_"))
		{
			ChatHandler.FailMsg(sender, "Prikazy pro VIP zde nelze pouzit.");
			return;
		}
		if (args.length == 0)
		{
			sender.sendMessage("VIP MENU:");
			sender.sendMessage("/vip expiration  -  Zobrazi, kdy vyprsi vas VIP status.");
			sender.sendMessage("/vip tp  -  Teleport podprikaz.");
			sender.sendMessage("/vip time  -  Podprikaz zmeny casu.");
			sender.sendMessage("/vip item  -  Podprikaz itemu.");
			sender.sendMessage("/vip nohunger - Utisi Vas hlad.");
			sender.sendMessage("/vip fly - Podprikaz letani.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("expiration"))
			{
				//TODO: use better time handler
				sender.sendMessage("Vase VIP vyprsi: " + new Date(vip.getExpiration()*1000));
			}
			else if (subCommand.matches("tp"))
			{
				sender.sendMessage("Teleport Menu:");
				sender.sendMessage("/vip tp player <jmeno>  -  Teleport k danemu hraci.");
				sender.sendMessage("/vip tp spawn  -  Teleport na spawn.");
				sender.sendMessage("/vip tp back  -  Teleport zpet na predchozi pozici.");
				sender.sendMessage("/vip tp bed  -  Teleport k posteli.");
				sender.sendMessage("/vip tp death  -  Teleportuje vas na posledni misto smrti.");
			}
			else if (subCommand.matches("time"))
			{
				sender.sendMessage("Time Menu:");
				sender.sendMessage("/vip time set <hodnota>  -  Nastavi cas na 0-24000.");
				sender.sendMessage("/vip time reset  -  Zmeni cas zpet na serverovy cas.");
			}
			else if (subCommand.matches("item"))
			{
				sender.sendMessage("Item Menu:");
				sender.sendMessage("/vip item add <itemID> <pocetStacku> -  Prida dany pocet stacku daneho itemu.");
				sender.sendMessage("/vip item list - Vypise vsechny dostupne itemy a jejich ID.");
			}
			else if(subCommand.matches("nohunger"))
			{
				sender.setFoodLevel(20);
				sender.setSaturation(20);
				sender.sendMessage("Uspesne jste se najedli");
			}
			else if(subCommand.matches("fly"))
			{
				sender.sendMessage("VIP FLY MENU:");
				sender.sendMessage("/vip fly on - Zapne letani.");
				sender.sendMessage("/vip fly off - Vypne letani.");
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
					Location tpLoc = plugin.vipManager.GetLastTeleportLoc(sender.getName());
					if (tpLoc == null)
					{
						ChatHandler.FailMsg(sender, "Nejdrive se musite nekam teleportovat!");
						return;
					}
					sender.teleport(tpLoc);
					plugin.vipManager.SetLastTeleportLoc(sender.getName(),locCache);
				}
				else if (arg1.matches("spawn"))
				{
					plugin.vipManager.SetLastTeleportLoc(sender.getName(),sender.getLocation());
					sender.teleport(sender.getWorld().getSpawnLocation());
				}
				else if (arg1.matches("bed"))
				{
					plugin.vipManager.SetLastTeleportLoc(sender.getName(),sender.getLocation());
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
				else if (arg1.matches("death"))
				{
					if(GugaEntityListener.playersDeaths.containsKey(sender.getName()))
					{
						sender.teleport(GugaEntityListener.playersDeaths.get(sender.getName()));
						ChatHandler.SuccessMsg(sender, "Byl jsi uspesne teleportovan na misto posledni smrti!");
					}
					else
					{
						ChatHandler.FailMsg(sender, "Misto smrti zatim neexistuje!");
					}
				}
				else if (arg1.matches("endtrapka"))
				{
					sender.chat("/pp endtrapka");
				}
			}
			else if (subCommand.matches("item"))
			{
				if (args[1].equalsIgnoreCase("list"))
				{
					sender.sendMessage("SEZNAM ITEMU: (Nazev - ID)");
					for (VipItems i : VipItems.values())
					{
						sender.sendMessage(i.toString() + " - " + i.GetID());
					}
				}
			}
			else if (subCommand.matches("time"))
			{
				if (arg1.matches("reset"))
				{
					if (!sender.isPlayerTimeRelative())
					{
						sender.resetPlayerTime();
						ChatHandler.SuccessMsg(sender, "Cas byl restartovan");
					}
					else
						ChatHandler.FailMsg(sender, "Vas cas nepotrebuje restartovat!");
				}
			}
			else if (subCommand.matches("fly"))
			{
				if(args[1].matches("on"))
				{
					if(sender.getWorld().getName().matches("world"))
					{
						plugin.vipManager.setFly(sender.getName(),true);
						ChatHandler.SuccessMsg(sender, "Letani zapnuto!");
					}
					else
						ChatHandler.FailMsg(sender, "Letani je povoleno pouze v hlavnim svete");
				}
				else if(args[1].matches("off"))
				{
					plugin.vipManager.setFly(sender.getName(),false);
					ChatHandler.SuccessMsg(sender, "Letani vypnuto!");
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
					Player p = plugin.getServer().getPlayer(arg2);
					if (p == null)
					{
						ChatHandler.FailMsg(sender,"Tento hrac neni online!");
						return;
					}
					if (p.getLocation().getWorld().getName().matches("world_basic"))
					{
						ChatHandler.FailMsg(sender, "Tento hrac je ve svete pro novacky!");
						return;
					}
					if (p.getLocation().getWorld().getName().matches("arena"))
					{
						ChatHandler.FailMsg(sender, "Tento hrac je v arene!");
						return;
					}
					if (p.getLocation().getWorld().getName().matches("world_event"))
					{
						ChatHandler.FailMsg(sender, "Tento hrac je v EventWorldu!");
						return;
					}
					ChatHandler.InfoMsg(plugin.getServer().getPlayer(arg2), "Hrac " + sender.getName() + " se na vas chce teleportovat, pro prijeti napiste prikaz /y");
					vipTeleports.put(p, sender);
					ChatHandler.SuccessMsg(sender, "Pozadavek odeslan");
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
						ChatHandler.SuccessMsg(sender,"Cas byl uspesne zmenen");
					}
					else 
						ChatHandler.FailMsg(sender,"Tato hodnota nelze nastavit!");
				}
			}
		}
		else if (args.length == 4)
		{
			String subCommand = args[0];
			if(subCommand.matches("item"))
			{
				int itemID = Integer.parseInt(args[2]);
				if(VipItems.IsVipItem(itemID))
				{
					if(args[1].matches("add"))
					{
						int numberOfStacks = Integer.parseInt(args[3]);
						int i = 0;
						while(i<numberOfStacks)
						{
							sender.getInventory().addItem(new ItemStack(itemID, 64));
							i++;
						}
						ChatHandler.SuccessMsg(sender, "Itemy byly pridany!");
					}
				}
				else
					ChatHandler.FailMsg(sender, "Tento item nelze pridat!");
			}
		}
	}
	public static void CommandRpg(Player sender, String args[])
	{
		GugaProfession2 prof;
		if ((prof = plugin.userManager.getUser(sender.getName()).getProfession()) != null)
		{
			int lvl = prof.GetLevel();
			int xp = prof.GetXp();
			int xpNeeded = prof.GetXpNeeded();
			sender.sendMessage(ChatColor.YELLOW + "**************************");
			sender.sendMessage("*Guga RPG version 1 stats*");
			sender.sendMessage("**Level:" + lvl);
			sender.sendMessage("**XP:" + xp + "/" + xpNeeded);
			sender.sendMessage(ChatColor.YELLOW + "**************************");
			int chance[] = prof.GetChances();
			int iron = chance[plugin.IRON];
			int gold = chance[plugin.GOLD];
			int diamond = chance[plugin.DIAMOND];
			int emerald = chance[plugin.EMERALD];
			double chanceIron = ( (double)iron / (double)1000 )  * (double)100;
			double chanceGold = ( (double)gold / (double)1000 )  * (double)100;
			double chanceDiamond = ( (double)diamond/ (double)1000 )  * (double)100;
			double chanceEmerald = ( (double)emerald/ (double)1000 )  * (double)100;
			sender.sendMessage(ChatColor.GOLD + "***Sance na nalezeni ve stonu***");
			sender.sendMessage(ChatColor.GRAY + "ZELEZO: " + ChatColor.WHITE + chanceIron + "% (+0.1% kazdych 10 levelu)");
			sender.sendMessage(ChatColor.GOLD + "ZLATO: " + ChatColor.WHITE  + chanceGold + "% (+0.1% kazdych 20 levelu)");
			sender.sendMessage(ChatColor.AQUA + "DIAMANT: " + ChatColor.WHITE + chanceDiamond + "% (+0.1% kazdych 50 levelu)");
			sender.sendMessage(ChatColor.GREEN + "EMERALD: " + ChatColor.WHITE + chanceEmerald + "% (+0.1% kazdych 100 levelu)");
			sender.sendMessage(ChatColor.YELLOW + "**************************");
		}
		else 
		{
			plugin.log.warning("Cannot get profession for player "+sender.getName()+".");
		}
	}
	
	public static void CommandPlaces(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "**********PLACES menu**********");
			sender.sendMessage(ChatColor.AQUA + "/places create " + ChatColor.GRAY + "<jmeno> " + ChatColor.WHITE + "- Vytvori Vam novy place na miste, kde stojite.");
			sender.sendMessage(ChatColor.AQUA + "/places list " + ChatColor.GRAY + "<strana> " + ChatColor.WHITE + "-  Seznam vsech dostupnych mist.");
			sender.sendMessage(ChatColor.AQUA + "/pp " + ChatColor.GRAY + "<jmeno> " + ChatColor.WHITE + "- Teleportuje Vas na dany place.");
			sender.sendMessage(ChatColor.AQUA + "/places me " + ChatColor.WHITE + "- Zobrazi vase places.");
			sender.sendMessage(ChatColor.AQUA + "/places set " + ChatColor.WHITE + "- Zobrazi dostupna nastaveni.");
			sender.sendMessage(ChatColor.YELLOW + "*******************************");
		}
		else
		{
			String subCommand = args[0];
			String args1 = (args.length>=2)? args[1] : "";
			String args2 = (args.length>=3)? args[2] : "";
			String args3 = (args.length>=4)? args[3] : "";
			String args4 = (args.length>=5)? args[4] : "";
			if(subCommand.matches("me"))
			{
				if (BasicWorld.isNew(sender))
				{
					ChatHandler.FailMsg(sender, "Teleportacni prikazy nemuzete pouzivat ve svete pro novacky!");
					return;
				}
				ArrayList<Place> places = plugin.placesManager.getPlacesByOwner(sender.getName());
				if(places.isEmpty())
				{
					ChatHandler.FailMsg(sender, "Bohuzel nemate zadna mista");
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + "**********Vase PLACES:*********");
					Iterator<Place> it = places.iterator();
					while (it.hasNext())
					{
						Place current = it.next();
						sender.sendMessage(ChatColor.GOLD + "* " + current.getName());
					}
					sender.sendMessage(ChatColor.YELLOW + "*******************************");
				}
			}
			else if (subCommand.matches("set"))
			{
				if (BasicWorld.isNew(sender))
				{
					ChatHandler.FailMsg(sender, "Teleportacni prikazy nemuzete pouzivat ve svete pro novacky!");
					return;
				}
				if(args1.matches("players"))
				{
					if(args2.matches("add"))
					{
						if (plugin.placesManager.isOwner(args3, sender.getName()))
						{
							if(plugin.placesManager.isTeleportPrivate(args3))
							{
								if(plugin.placesManager.addTeleportAccess(args3, args4))
								{
									ChatHandler.SuccessMsg(sender,"Uzivatel '"+args4+"' ma nyni pristup k portu '"+args[3]+"'.");
								}
								else
								{
									ChatHandler.FailMsg(sender,"Pristup se nepodarilo pridat.");
								}
							}
							else
								ChatHandler.FailMsg(sender,"Tento teleport neni privatni");
						}
						else
						{
							ChatHandler.FailMsg(sender, "Nejste majitelem mista " + args3 + "!");
						}
					}
					else if(args[2].matches("remove"))
					{
						if (plugin.placesManager.isOwner(args3, sender.getName()))
						{
							if(plugin.placesManager.isTeleportPrivate(args3))
							{
								if(plugin.placesManager.removeTeleportAccess(args3, args4))
								{
									ChatHandler.SuccessMsg(sender,"Uzivatel '"+args4+"' uz nema pristup k portu '"+args3+"'.");
								}
								else
								{
									ChatHandler.FailMsg(sender,"Pristup se nepodarilo odebrat.");
								}
							}
							else
								ChatHandler.FailMsg(sender,"Tento teleport neni privatni");	
						}
						else
						{
							ChatHandler.FailMsg(sender, "Nejste majitelem mista " + args3 + "!");
						}
					}
					else if(args2.matches("list"))
					{
						if (plugin.placesManager.isOwner(args3, sender.getName()))
						{
							ArrayList<String> users = plugin.placesManager.listTeleportAccess(args[3]);
							sender.sendMessage("K portu '"+args3+"' maji pristup (celkem "+String.valueOf(users.size())+"):");
							sender.sendMessage(users.toString());
						}
						else
						{
							ChatHandler.FailMsg(sender, "Nejste majitelem mista " + args[3] + "!");
						}
					}
				
				}
				else if(args1.matches("welcome"))
				{
					if (plugin.placesManager.isOwner(args[2], sender.getName()))
					{
						StringBuilder msg = new StringBuilder();
						for(int i=3;i<args.length;)
						{
							msg.append(args[i++]);
							if(i < args.length)
								msg.append(" ");
						}
						if(plugin.placesManager.setWelcomeMessage(args[2],msg.toString()))
						{
							ChatHandler.SuccessMsg(sender, "Nastaveni bylo uspesne!");
						}
						else
						{
							ChatHandler.FailMsg(sender, "Nepodarilo se nastavit uvitaci zpravu.");
						}
					}
					else
					{
						ChatHandler.FailMsg(sender, "Nejste majitelem mista " + args[2] + "!");
					}
				}
				else if(args1.equalsIgnoreCase("type"))
				{
					String type;
					if(args3.equalsIgnoreCase("public"))
						type = "public";
					else if(args3.equalsIgnoreCase("private"))
						type = "private";
					else
					{
						ChatHandler.FailMsg(sender, "Invalid place type");
						return;
					}
					if(plugin.placesManager.modifyTeleportType(args2, type))
					{
						for(String u : plugin.placesManager.listTeleportAccess(args2))
						{
							plugin.placesManager.removeTeleportAccess(args2, u);
						}
						ChatHandler.SuccessMsg(sender, "Place was sucessfully modified");
					}
					else
						ChatHandler.FailMsg(sender, "Place modification failed");
				}	
				else
				{
					sender.sendMessage(ChatColor.YELLOW + "*******Nastaveni PLACES:*******");
					sender.sendMessage(ChatColor.AQUA + "/places set players add " + ChatColor.GRAY + "<jmenoPortu> <player> " + ChatColor.WHITE + "- Nastavi uzivatele, ktery muze pouzivat port.");
					sender.sendMessage(ChatColor.AQUA + "/places set players remove " + ChatColor.GRAY + " <jmenoPortu> <player> " + ChatColor.WHITE + " - Odebere uzivatele, ktery muze pouzivat port.");
					sender.sendMessage(ChatColor.AQUA + "/places set players list " + ChatColor.GRAY + " <jmenoPortu> <player> " + ChatColor.WHITE + " - Vypise uzivatele, kteri mohou pouzivat port.");
					sender.sendMessage(ChatColor.AQUA + "/places set welcome " + ChatColor.GRAY + " <jmenoPortu> <zprava> " + ChatColor.WHITE + " - Nastavi zpravu pro navstevniky Vaseho portu.");
					sender.sendMessage(ChatColor.AQUA + "/places set type " + ChatColor.GRAY + " <jmenoPortu> public|private " + ChatColor.WHITE + " - Nastavi typ portu public nebo private. (verejny/soukromy)");
					sender.sendMessage(ChatColor.YELLOW + "*******************************");
				}
			}
			else if(subCommand.matches("list"))
			{
				ArrayList<Place> places = plugin.placesManager.getAccessiblePlaces(sender.getName());
				DataPager<Place> pager = new DataPager<Place>(places, 15);
				int pageNum;
				try{
					pageNum = Integer.parseInt(args[1]);
				}
				catch(Exception e){
					pageNum =1;
				}
				Iterator <Place> i = pager.getPage(pageNum).iterator();
				sender.sendMessage(ChatColor.YELLOW + "***Seznam dostupnych PLACES:***");
				sender.sendMessage(String.format("STRANA %d/%d",pageNum,pager.getPageCount()));
				Place placeData=null;
				while (i.hasNext())
				{
					placeData = i.next();
					sender.sendMessage(String.format("* %s - x:%d, z: %d",placeData.getName(),placeData.getX(),placeData.getZ()));
				}
				sender.sendMessage(ChatColor.YELLOW + "*******************************");
			}
			else if(subCommand.equalsIgnoreCase("create"))
			{
				long balance = plugin.currencyManager.getBalance(sender.getName());
				if(balance < 550)
				{
					ChatHandler.FailMsg(sender, "Nemate dost kreditu. Place stoji 550.");
					return;
				}
				if(plugin.placesManager.addTeleport(args1.trim(), sender.getName(), sender.getLocation().getBlockX(), sender.getLocation().getBlockY(), sender.getLocation().getBlockZ(), sender.getLocation().getWorld().getName(), "private"))
				{
					plugin.currencyManager.addCredits(sender.getName(), -550);
					ChatHandler.SuccessMsg(sender, "Place byl vytvoren");
				}
				else
				{
					ChatHandler.FailMsg(sender, "Place se nepodarilo vytvorit.");
				}
			}
		}
	}
	public static void CommandPP(Player sender, String args[])
	{
		if(args.length==1)
		{
			Teleport(sender,args[0]);
		}
	}

	public static void CommandEventWorld(Player sender, String args[])
	{
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			sender.sendMessage("K pouziti tohoto prikazu je treba se prihlasit!");
			return;
		}
		if (args.length==0)
		{
			sender.sendMessage("EventWorld MENU:");
			sender.sendMessage("Commands:");
			sender.sendMessage("/ew join - Teleportuje hrace do eventWorldu");
			sender.sendMessage("/ew leave - Vrati hrace do normalniho sveta");
			GameMaster gm = GameMasterHandler.GetGMByName(sender.getName());
			if((gm = GameMasterHandler.GetGMByName(sender.getName())) == null)
			{
				return;
			}
			if(GameMasterHandler.IsAtleastGM(sender.getName()) || gm.GetRank() == Rank.EVENTER )
			{
				sender.sendMessage("/ew togglemobs - Toggle mobs on/off");
				sender.sendMessage("/ew togglepvp - Toggle PvP on/off");
				sender.sendMessage("/ew toggleregion - Toggle region on/off");
				if(GameMasterHandler.IsAdmin(sender.getName()))
				{
					sender.sendMessage("/eventworld setspawn - Sets a EventWorld spawn to GM's position");
				}
			}
			if(gm.GetRank() == Rank.EVENTER)
			{
				sender.sendMessage("/ew mode");
			}
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (plugin.arena.IsArena(sender.getLocation()))
			{
				sender.sendMessage("Z areny se nedostanete do EventWordu!");
				return;
			}
			else
			{
				if (subCommand.matches("join"))
				{
					if (!plugin.EventWorld.IsEventWorld(sender.getLocation()))
					{
						plugin.EventWorld.PlayerJoin(sender.getLocation(),sender);
					}
					else
					{
						sender.sendMessage("V EventWorldu jiz jste!");
					}
				}
				else if(subCommand.matches("leave"))
				{
					if (plugin.EventWorld.IsEventWorld(sender.getLocation()))
					{
						plugin.EventWorld.PlayerLeave(sender);
					}
					else
					{
						sender.sendMessage("Nejste v EW!");
					}
				}
				else if(subCommand.matches("mode") && GameMasterHandler.IsAtleastRank(sender.getName(), Rank.EVENTER))
				{
					if(plugin.EventWorld.IsEventWorld(sender.getLocation()))
					{
						if(sender.getGameMode() == GameMode.SURVIVAL)
						{
							sender.setGameMode(GameMode.CREATIVE);
							ChatHandler.SuccessMsg(sender, "Creative mod nastaven");
						}
						else
						{
							sender.setGameMode(GameMode.SURVIVAL);
							ChatHandler.SuccessMsg(sender, "Survival mod nastaven");
						}
					}
					else
					{
						ChatHandler.FailMsg(sender,"Tento prikaz funguje jen v EW");
					}
				}
				else if(subCommand.matches("togglepvp") && GameMasterHandler.IsAtleastRank(sender.getName(), Rank.EVENTER))
				{
					plugin.EventWorld.togglePvP(sender);
				}
				else if(subCommand.matches("togglemobs") && GameMasterHandler.IsAtleastRank(sender.getName(), Rank.EVENTER))
				{
					plugin.EventWorld.toggleMobs(sender);
				}
				else if(subCommand.matches("toggleregion") && GameMasterHandler.IsAtleastRank(sender.getName(), Rank.EVENTER))
				{
					plugin.EventWorld.toggleRegion(sender);
				}
				else if(subCommand.matches("setspawn") && GameMasterHandler.IsAdmin(sender.getName()))
				{
					Location l = sender.getLocation();
					sender.getWorld().setSpawnLocation((int)l.getX(), (int)l.getY(), (int)l.getZ());
					sender.sendMessage("New spawn for EventWorld has been set!");
				}
			}
		}
	}
	
	public static void CommandArena(Player sender, String args[])
	{
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			sender.sendMessage("K pouziti tohoto prikazu je treba se prihlasit!");
			return;
		}
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
				if (plugin.EventWorld.IsEventWorld(sender.getLocation()))
				{
					sender.sendMessage("Z EventWorldu se nedostanete do areny!");
					return;
				}
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
	public static void CommandParty(Player sender, String args[])
	{
		if(args.length==0)
		{
			sender.sendMessage(" /party <jmeno> - vytvori a prihlasi Vas do party");
			sender.sendMessage(" /party q - opustite party");
			sender.sendMessage(" /invite <jmeno hrace> - pozve hrace do Vasi party");
			sender.sendMessage(" /invite - prijme pozvani do party");
			sender.sendMessage(" /p - posle zpravu do party chatu");
		}
		else if(args.length==1)
		{	
			if(args[0].matches("q"))
			{
				GugaParty.removePlayer(sender);
			}
			else
			{
				GugaParty.addParty(sender, args[0]);
			}
		}
	}
	public static void CommandInvite(Player sender, String args[])
	{
		if(args.length==0)
		{
			GugaParty.inviteAccepted(sender);
		}
		else if(args.length==1)
		{
			GugaParty.invitePlayer(sender, args[0]);
		}
	}
	public static void CommandSendPartyMsg(Player sender, String args[])
	{
		if(args.length!=0)
		{
			if(args.length!=0)
			{
				String text="";
				for(int i=0;i<args.length;i++)
				{
					if(i==0)
						text=text+args[i];
					else
						text=text+" "+args[i];
				}
				GugaParty.sendMessage(sender, text);
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
				reply.put(p, sender);
				return;
			}
			sender.sendMessage("Nemate komu odpovedet!");
		}
	}

	public static void CommandFeedback(Player sender, String args[])
	{
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			sender.sendMessage("Nejprve se musite prihlasit!");
			return;
		}
		if(args.length == 0)
			return;
		int i = 0;
		String feed = "";
		while (i < args.length)
		{
			feed += args[i];
			i++;
		}
		GugaFile file = new GugaFile(FeedbackFile, GugaFile.APPEND_MODE);
		String line = "Feedback (" + sender.getName() + ") " + feed;
		file.Open();
		file.WriteLine(line);
		file.Close();
		sender.sendMessage("Zpetna vazba byla odeslana. Dekujeme za Vasi podporu!");
	}
	public static void CommandEvent(Player sender, String args[])
	{
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			sender.sendMessage("Nejprve se musite prihlasit!");
			return;
		}
		if ((!GameMasterHandler.IsAtleastGM(sender.getName())) && (!GameMasterHandler.IsRank(sender.getName(), Rank.EVENTER)))
		{
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("join"))
				{
					if (GugaEvent.acceptInv)
					{
						if (GugaEvent.players.size() < GugaEvent.playersCap)
						{
							GugaEvent.AddPlayer(sender.getName().toLowerCase());
							sender.sendMessage("Byl jste uspesne prihlasen k eventu");
						}
						else
							sender.sendMessage("Neni mozne se pripojit - Event je plny!");
								
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
			//sender.sendMessage("/event spawners - Shows spawners submenu.");
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
		else if(arg1.equalsIgnoreCase("msg"))
		{
			int i = 1;
			String msg = "";
			while(i<(args.length))
			{
				msg += " " + args[i];
				i++;
			}
			plugin.getServer().broadcastMessage(ChatColor.AQUA + "[EVENT]" + ChatColor.RED + msg);
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
		/*else if (arg1.equalsIgnoreCase("spawners"))
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
					for (EntityType type : EntityType.values())
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
		}*/
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
				sender.sendMessage("/event players list <page> - List of tagged players.");
				sender.sendMessage("/event players cap <value> [" + GugaEvent.playersCap + "] - Sets a new cap.");
				return;
			}
			else if (args.length == 2) 
			{
				if (args[1].equalsIgnoreCase("clear"))
				{
					GugaEvent.ClearPlayers();
					sender.sendMessage("Player list cleared.");
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
						if (GugaEvent.players.size() < GugaEvent.playersCap)
							GugaEvent.AddPlayer(names[i]);
						else
							break;
						i++;
					}
					sender.sendMessage(i + " Player(s) added.");
				}
				else if (args[1].equalsIgnoreCase("cap"))
				{
					GugaEvent.playersCap = Integer.parseInt(args[2]);
					sender.sendMessage("New cap has been set.");
				}
				else if (args[1].equalsIgnoreCase("list"))
				{
					DataPager<String> pager = new DataPager<String>(GugaEvent.GetPlayers(), 15);
					Iterator<String> i = pager.getPage(Integer.parseInt(args[2])).iterator();
					sender.sendMessage("PLAYER LIST:");
					sender.sendMessage("PAGE " + args[2] + "/" + pager.getPageCount());
					while (i.hasNext())
					{
						sender.sendMessage(i.next());
					}
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
	public static void CommandHelper(Player sender, String args[])
	{
		GameMaster gm;
	    if ((gm = GameMasterHandler.GetGMByName(sender.getName())) != null)
	    {
	      if (gm.GetRank() == GameMaster.Rank.HELPER)
	      {
	        Player[] players = plugin.getServer().getOnlinePlayers();
	        String command = "/gm ";
	        int r = 0;
	        while (r < args.length)
	        {
	          command = command + args[r] + " ";
	          r++;
	        }
	        r = 0;
	        while (r < players.length)
	        {
	          if (GameMasterHandler.IsAtleastGM(players[r].getName()))
	          {
	            players[r].sendMessage(ChatColor.GRAY + sender.getName() + " used command: " + command);
	          }
	          r++;
	        }
	        if (args.length == 0)
	        {
	          sender.sendMessage("**************MENU HELPERU**************");
	          sender.sendMessage("/helper mute - Zobrazi podmenu.");
	          sender.sendMessage("/helper kick <hrac> - Vykopne hrace ze serveru kvuli zadanemu duvodu.");
	          sender.sendMessage("/helper tp - Zobrazi podmenu.");
	        }
	        else if (args[0].toLowerCase().matches("tp"))
	        {
	          if (args.length == 1)
	          {
	            sender.sendMessage("/helper tp <hrac> - teleportuje Vas k zadanemu hraci.");
	            sender.sendMessage("/helper tp back - teteportuje Vas zpet.");
	          }
	          else if (args.length == 2)
	          {
	            if (args[1].toLowerCase().matches("back"))
	            {
	              if (backTeleport.containsKey(sender.getName()))
	              {
	                sender.teleport((Location)backTeleport.get(sender.getName()));
	                ChatHandler.SuccessMsg(sender, "Byl jste teleportovan.");
	              }
	              else {
	                ChatHandler.FailMsg(sender, "Nikam jste se jeste neteleportoval.");
	              }
	            }
	            else {
	              Player p = plugin.getServer().getPlayer(args[1]);
	              if (p != null)
	              {
	                String name = sender.getName();
	                if (backTeleport.containsKey(name))
	                {
	                  backTeleport.remove(name);
	                  backTeleport.put(name, sender.getLocation());
	                }
	                else {
	                  backTeleport.put(name, sender.getLocation());
	                }sender.teleport(p);
	                ChatHandler.SuccessMsg(sender, "Byl jste teleportovan.");
	              }
	              else {
	                ChatHandler.FailMsg(sender, "Hrac je offline.");
	              }
	            }
	          }
	        } else if (args[0].toLowerCase().matches("mute"))
	        {
	          if (args.length == 1)
	          {
	            sender.sendMessage("/helper mute add <name> <time> - Ztlumi hrace na urcity cas v minutach.");
	            sender.sendMessage("/helper mute list - Zorazi seznam ztlumenych hracu.");
	          }
	          else if (args.length == 2)
	          {
	            if (args[1].toLowerCase().matches("list"))
	            {
	              GugaMute.printPlayers(sender);
	            }
	          }
	          else if (args.length == 4)
	          {
	            if (args[1].toLowerCase().matches("add"))
	            {
	              GugaMute.addPlayer(args[2], Integer.parseInt(args[3]));
	              ChatHandler.SuccessMsg(sender, "Hrac byl ztlumen.");
	            }
	          }
	        }
	        else if (args[0].toLowerCase().matches("kick"))
	        {
	          if (args.length == 2)
	          {
	            Player p;
	            if ((p = plugin.getServer().getPlayer(args[1])) != null)
	            {
	              p.kickPlayer("Byl jste vykopnut helperem.");
	              ChatHandler.SuccessMsg(sender, "Hrac byl vykopnut");
	            }
	            else {
	              ChatHandler.FailMsg(sender, "Tento hrac neni online");
	            }
	          }
	        }
	      }
	    }
	}		
					
	public static void CommandGM(Player sender, String args[])
	{
		if (!(GameMasterHandler.IsAtleastRank(sender.getName(), Rank.BUILDER)))
			return;
		Player []players = plugin.getServer().getOnlinePlayers();
		StringBuilder command = new StringBuilder("/gm ");
		int r=0;
		while(r < args.length)
		{
			command.append(args[r]);
			command.append(" ");
			r++;
		}
		if(GameMasterHandler.IsAdmin(sender.getName()))
		{
			int i = 0;
			while(i < players.length)
			{
				if(GameMasterHandler.IsAdmin(players[i].getName()) && (sender.getName() != players[i].getName()))
				{
					players[i].sendMessage(ChatColor.GRAY+sender.getName() + " used command: " + command);
				}
				i++;
			}
		}
		else if(GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			int i = 0;
			while(i < players.length)
			{
				if(GameMasterHandler.IsAtleastGM(players[i].getName()) && (sender.getName() != players[i].getName()))
				{
					players[i].sendMessage(ChatColor.GRAY+sender.getName() + " used command: " + command);
				}
				i++;
			}
		}
		else if(GameMasterHandler.IsAtleastRank(sender.getName(), Rank.BUILDER))
		{
			int i = 0;
			while(i < players.length)
			{
				if(GameMasterHandler.IsAtleastRank(players[i].getName(), Rank.BUILDER) && (sender.getName() != players[i].getName()))
				{
					players[i].sendMessage(ChatColor.GRAY+sender.getName() + " used command: " + command);
				}
				i++;
			}
		}
		if (!plugin.userManager.userIsLogged(sender.getName()))
		{
			sender.sendMessage("Musite byt prihlaseny, aby jste mohl pouzit tento prikaz!");
			return;
		}
		String subCommand = (args.length>0) ? args[0] : "";
		String arg1 = (args.length>=2) ? args[1] : "";
		String arg2 = (args.length>=3) ? args[2] : "";
		String arg3 = (args.length>=4) ? args[3] : "";
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
				sender.sendMessage("/gm replace <typeID> <typeID2> <reltiveX> <relativeY> <relativeZ> - Replaces a blocks from block you point at.");
				sender.sendMessage("/gm godmode <name>  -  Toggles immortality for a certain player.");
				sender.sendMessage("/gm spectate  -  Spectation sub-menu.");
				sender.sendMessage("/gm places - Places sub-menu.");
				sender.sendMessage("/gm regions - Regions sub-menu.");
				sender.sendMessage("/gm arena - Arenas sub-menu.");
				sender.sendMessage("/gm rank - Ranks sub-menu.");
				sender.sendMessage("/gm fly <name> - Toggles fly mode for certain player.");
				sender.sendMessage("/gm spawn - Spawns sub-menu.");
				sender.sendMessage("/gm save-all - Saves all files of plugin and worlds.");
				sender.sendMessage("/gm book - Books sub-menu.");
				sender.sendMessage("/gm enchant - Enchantments sub-menu.");
			}
			if(GameMasterHandler.IsAtleastGM(sender.getName()))
			{
				sender.sendMessage("/gm ban - Bans sub-menu.");
				//sender.sendMessage("/gm invis <name>  -  Toggles invisibility for a certain player.");
				sender.sendMessage("/gm mute - Mute sub-menu.");
				sender.sendMessage("/gm kill <player> - Kills target player.");
				sender.sendMessage("/gm on - Turn your GM status to on");
				sender.sendMessage("/gm off - Turn your GM status to off");
				sender.sendMessage("/gm bw - BasicWorld sub-menu");
				sender.sendMessage("/gm home- Homes sub-menu.");
				sender.sendMessage("/gm cmd <cmd> <arg1>... - Perform a bukkit command.");
				sender.sendMessage("/gm rsdebug - Toggles RedStone debug.");
				sender.sendMessage("/gm speed - Speed sub-menu");
				sender.sendMessage("/gm enderchest - Enderchest sub-menu.");
			}
			sender.sendMessage("/gm log - Shows a log records for target block.(+saveall - saves unsaved progress)");
			sender.sendMessage("/gm tp <x> <y> <z>  -  Teleports gm to specified coords.");
			sender.sendMessage("/gm gmmode <name> -  Toggles gm mode for a certain player.");
		}
		else if (subCommand.matches("log"))
		{
			if(args.length==1)
			{
				plugin.logger.PrintLogData(sender, sender.getTargetBlock(null, 20));
			}
			else if(args.length==2)
			{
				if(args[1].matches("saveall"))
				{
					sender.sendMessage("Saving breakLog data...");
					plugin.logger.SaveWrapperBreak();
					sender.sendMessage("Saving placeLog data...");
					plugin.logger.SaveWrapperPlace();
					sender.sendMessage("Save completed");
				}
				else if (args[1].equalsIgnoreCase("shop"))
					plugin.logger.PrintShopData(sender, Integer.parseInt(args[2]));
				else
				{
					ArrayList<String> data = plugin.logger.blockCache.get(sender);
					if (data.size() == 0)
					{
						sender.sendMessage("You have no data saved! Use /gm log first");
						return;
					}
					DataPager<String> pager = new DataPager<String>(data, 6);
					Iterator<String> i = pager.getPage(Integer.parseInt(args[1])).iterator();
					sender.sendMessage("LIST OF BLOCK DATA:");
					sender.sendMessage("PAGE " + Integer.parseInt(args[1]) + "/" + pager.getPageCount());
					while (i.hasNext())
						sender.sendMessage(i.next());
				}
			}
		}
		else if(subCommand.matches("rsdebug") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(plugin.bListener.redStoneDebug.contains(sender))
			{
				plugin.bListener.redStoneDebug.remove(sender);
				ChatHandler.SuccessMsg(sender, "RedStone debug successfully turned off!");
			}
			else
			{
				plugin.bListener.redStoneDebug.add(sender);
				ChatHandler.SuccessMsg(sender, "RedStone debug successfully turned on!");
			}
		}
		else if(subCommand.matches("home") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 2)
			{
				Homes home;
				if((home = HomesHandler.getHomeByPlayer(args[1])) != null)
				{
					sender.teleport(HomesHandler.getLocation(home));
					ChatHandler.SuccessMsg(sender, "You have been teleported to " + args[1] + " spawn!");
				}
			}
			else if(args.length == 3 && args[1].matches("set"))
			{
				Location loc = sender.getLocation();
				HomesHandler.addHome(new Homes(args[2], (int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), loc.getWorld().getName()));
				ChatHandler.SuccessMsg(sender, "Home has been uccessfully set");
			}
			else
			{
				sender.sendMessage("Usage:");
				sender.sendMessage("/gm home <player> - Teleports you to certain player's home");
				sender.sendMessage("/gm home set <player> - Sets home of certain player to your current position.");
			}
		}
		else if(subCommand.matches("rank") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 4)
			{
				if(arg1.matches("add"))
				{
					Player target = plugin.getServer().getPlayer(arg2);
					if(target != null)
					{
						if(arg3.equalsIgnoreCase("helper")||arg3.equalsIgnoreCase("eventer")||arg3.equalsIgnoreCase("builder")||arg3.equalsIgnoreCase("admin")||arg3.equalsIgnoreCase("gamemaster"))
						{
							if(!GameMasterHandler.gameMasters.contains(GameMasterHandler.GetGMByName(target.getName())))
							{
								GameMasterHandler.AddGMIng(target.getName(), arg3.toUpperCase());
								sender.sendMessage("User was succesfully added to GMs file!");
							}
							else
							{
								sender.sendMessage("User cannot be removed, because he already exists in GMs file. - Delete him to change rank.");
							}
						}
						else
						{
							sender.sendMessage("This rank can't be added.");
						}
					}
					else
					{
						sender.sendMessage("This player doesn't exist.");
					}
				}
			}
			else if(args.length == 3)
			{
				if(arg1.matches("remove"))
				{
					Player target = plugin.getServer().getPlayer(arg2);
					if(target != null)
					{
						GameMasterHandler.RemoveGMIng(arg2);
						sender.sendMessage("User was succesfully removed from GMs file!");
					}
					else
					{
						sender.sendMessage("This player doesn't exist.");
					}
				}
			}
			else
			{
				sender.sendMessage("/gm rank add <player> <rank> - Adds rank (EVENTER/BUILDER) for a certain player.");
				sender.sendMessage("/gm rank remove <player> - Removes rank for a certain player");
			}
		}
		else if (subCommand.matches("mute") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 2)
			{
				if(args[1].matches("list"))
				{
					GugaMute.printPlayers(((Player)sender));
				}
				if(args[1].matches("all"))
				{
					boolean status = GugaMute.toggleChatMute();
					if(status==true)
						sender.sendMessage("Mute for all players is on.");
					else
						sender.sendMessage("Mute for all players is off.");	
				}
			}
			else if(args.length==4)
			{
				if(args[1].matches("add"))
				{
					int i=0;
					boolean isOnline=false;
					Player []player=plugin.getServer().getOnlinePlayers();
					while(i<player.length)
					{
						if(player[i].getName().equalsIgnoreCase(args[2]))
						{
							GugaMute.addPlayer(args[2],Integer.parseInt(args[3]));
							player[i].sendMessage(ChatColor.RED+("Byl jste ztlumen na " + args[3]+" minut!"));
							sender.sendMessage("Player " + player[i].getName() + " was muted!");
							isOnline=true;
						}
						i++;
					}
					if(!(isOnline))
					{
						sender.sendMessage("This player is not online");
					}
				}
			}
			else
			{
				sender.sendMessage("/gm mute all - Toggle all chat messages on/off");
				sender.sendMessage("/gm mute add <name> <time> - Mute players chat messages for certain time");
				sender.sendMessage("/gm mute list - Shows list of muted players");
			}
		}
		else if (subCommand.matches("spawn") && GameMasterHandler.IsAdmin(sender.getName())) 
		{
			if(args.length == 3 && args[1].matches("add"))
			{
				SpawnsHandler.AddSpawn(args[2], sender.getLocation());
				SpawnsHandler.SaveSpawns();
				sender.sendMessage("Spawn was successfully added!");
			}
			else if(args.length == 3 && args[1].matches("remove"))
			{
				if(SpawnsHandler.GetSpawnByName(args[2]) != null)
				{
					SpawnsHandler.RemoveSpawn(args[2]);
					SpawnsHandler.SaveSpawns();
					sender.sendMessage("Spawn was successfully removed!");
				}
				else
				{
					sender.sendMessage("This spawn doesn't exist!");
				}
			}
			else
			{
				sender.sendMessage("/gm spawn add <spawnName> - Adds spawn to your position.");
				sender.sendMessage("/gm spawn remove <spawnName> - Removes certain spawn.");
			}
		}
		else if (subCommand.matches("bw") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 2)
			{
				if(arg1.matches("join"))
				{
					sender.teleport(BasicWorld.getSpawn());
				}
				else if(arg1.matches("leave"))					
				{
					sender.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
				}
			}
			else
			{
				sender.sendMessage("/gm bw join - Teleports you to BasicWorld.");
				sender.sendMessage("/gm bw leave - Teleports you Spawn of main world.");
			}
		}
		else if (subCommand.matches("time") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 3)
			{
				if(plugin.getServer().getWorld(args[1]) != null)
				{
					plugin.getServer().getWorld(args[1]).setTime(Integer.parseInt(args[2]));
					ChatHandler.SuccessMsg(sender, "Cas byl uspesne nastaven");
				}
			}
			else
				sender.sendMessage("Usage: /gm time <world> <value> - Sets time for certain world.");
		}
		else if (subCommand.matches("arena") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(args.length==2)
			{
				if (args[1].equalsIgnoreCase("list"))
				{
					Iterator<ArenaSpawn> i = plugin.arena.GetArenaList().iterator();
					sender.sendMessage("LIST OF ARENAS:");
					while (i.hasNext())
					{
						sender.sendMessage(i.next().GetName());
					}
				}
				else if (args[1].equalsIgnoreCase("next"))
				{
					plugin.arena.RotateArena();
					sender.sendMessage("Current Arena changed.");
				}
			}
			else if(args.length == 3)
			{
				if (args[1].equalsIgnoreCase("add"))
				{
					if (plugin.arena.IsArena(sender.getLocation()))
					{
						plugin.arena.AddArena(args[2], sender.getLocation());
						sender.sendMessage("Arena spawn succesfuly added.");
					}
					else
						sender.sendMessage("You must be in arena world!");
				}
				else if (args[1].equalsIgnoreCase("remove"))
				{
					if (plugin.arena.ContainsArena(args[2]))
					{
						plugin.arena.RemoveArena(args[2]);
						sender.sendMessage("Arena succesfuly removed.");
					}
					else
						sender.sendMessage("This arena doesnt exist!");
				}
			}
			else
			{
				sender.sendMessage("/gm arena add <name> - Adds new arena spawn at your location.");
				sender.sendMessage("/gm arena remove <name> - Removes specified arena.");
				sender.sendMessage("/gm arena list - List of all arenas.");
				sender.sendMessage("/gm arena next - Changes current arena to the next one.");
			}
		}
		else if (subCommand.matches("setspawn") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			Location pLoc = sender.getLocation();
			sender.getWorld().setSpawnLocation((int)pLoc.getX(), (int)pLoc.getY(), (int)pLoc.getZ());
			sender.sendMessage("New World Spawn has been set!");
		}
		else if (subCommand.matches("announce") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(args.length == 2 && args[1].matches("print"))
			{
				int i = 0;
				String msg;
				while ( (msg = GugaAnnouncement.GetAnnouncement(i)) != null)
				{
					sender.sendMessage("[" + i + "]  -  " + msg);
					i++;
				}
			}
			else if(args.length == 3 && args[1].matches("remove"))
			{
				int num = Integer.parseInt(args[2]);
				if (GugaAnnouncement.RemoveAnnouncement(num))
				{
					sender.sendMessage("Announcement has been succesfuly removed.");
				}
				else
				{
					sender.sendMessage("This announcement doesnt exist!");
				}
			}
			else if(args.length > 3 && arg1.matches("add"))
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
			else if(args.length==0)
			{
				sender.sendMessage("/gm announce print - Prints messages and indexes.");
				sender.sendMessage("/gm announce remove <index> - Removes a message from the list.");
				sender.sendMessage("/gm announce add <message> - Adds new message to the list.");
			}
		}
		else if (subCommand.matches("ban") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			switch(arg1)
			{
				case "add":
				{
					if(!(args.length >= 4))
					{
						sender.sendMessage("Usage: /gm ban add <player> <hours>[ <reason>]");
						break;
					}
					
					String playerNameToBan = arg2;
					long exp = Long.valueOf(arg3);
					if(exp == 0)
					{
						ChatHandler.FailMsg(sender, "Invalid ban duration");
						return;
					}
					long expiration = exp;
					if(!(exp == -1L))
					{
						expiration = Double.valueOf(arg3).longValue()*3600 + System.currentTimeMillis()/1000;
					}
					
					StringBuilder sb = new StringBuilder("");
					for(int i=4;i<args.length;i++)
					{
						sb.append(args[i]);
						sb.append(" ");
					}
					String reason = sb.toString();
					if(plugin.banHandler.banPlayer(playerNameToBan, expiration, reason,ChatHandler.getHonorableName(sender)))
					{
						sender.sendMessage("Player banned.");
						Player p = plugin.getServer().getPlayerExact(playerNameToBan);
						if (p != null)
							p.kickPlayer("Vas ucet byl zabanovan. Ban vyprsi "+ new Date(expiration).toString());
					}
					else
					{
						sender.sendMessage("Unable to ban player.");
					}
					if(plugin.banHandler.isIPWhitelisted(playerNameToBan))
					{
						if(plugin.banHandler.removeIPWhitelist(playerNameToBan))
						{
							sender.sendMessage("Player removed from whitelist");
						}
						else
						{
							sender.sendMessage("Unable to remove player from whitelist");
						}
					}
				}
					break;
				case "remove":
					if(!(args.length >=2))
					{
						sender.sendMessage("Usage: /gm ban remove <ban id>");
						break;
					}
					
					int banID = Integer.parseInt(args[2]);
					if(plugin.banHandler.unbanPlayer(banID))
					{
						sender.sendMessage("Ban #"+args[2]+" cancelled.");
					}
					else
					{
						sender.sendMessage("Could not cancel ban.");
					}
					break;
				case "mod":
				{
					if(!(args.length >= 4))
					{
						sender.sendMessage("Usage: /gm ban mod <ban id> <new duration>[ new description]");
						return;
					}
					int banId = Integer.parseInt(arg2);
					long exp = Long.valueOf(arg3);
					if(exp == 0)
					{
						ChatHandler.FailMsg(sender, "Invalid ban duration");
						return;
					}
					long expiration = exp;
					if(!(exp == -1L))
					{
						expiration = Double.valueOf(arg3).longValue()*3600 + System.currentTimeMillis()/1000;
					}
					
					StringBuilder sb = new StringBuilder("");
					for(int i=4;i<args.length;i++)
					{
						sb.append(args[i]);
						sb.append(" ");
					}
					String reason = sb.toString();
					if(plugin.banHandler.modifyBan(banId, expiration, reason))
					{
						sender.sendMessage("Ban modified.");
					}
					else
					{
						sender.sendMessage("Unable to modify ban.");
					}
				}
					break;
				case "whitelist":
					String args2 = (args.length>=3)? args[2] : "";
					switch(args2)
					{
						case "add":
							if(plugin.banHandler.addIPWhitelist(args[3]))
								sender.sendMessage("Player '"+args[3]+"' is now IPBan whitelisted.");
							else
								sender.sendMessage("Could not whitelist player");
							break;
						case "remove":
							if(plugin.banHandler.removeIPWhitelist(args[3]))
								sender.sendMessage("Player '"+args[3]+"' is no longer IPBan whitelisted.");
							else
								sender.sendMessage("Could not remove player from IPBan whitelist");
						case "list":
							ArrayList<String> whitelistedPlayers = plugin.banHandler.listIPWhitelisted();
							sender.sendMessage("List of IPBan whitelisted players:");
							if(whitelistedPlayers.isEmpty())
								sender.sendMessage("There are no whitelisted players yet.");
							else
								sender.sendMessage(" "+whitelistedPlayers.toString());
							break;
						default:
							sender.sendMessage("/gm ban whitelist add <playerName> - Adds player to whitelist.");
							sender.sendMessage("/gm ban whitelist remove <playerName> - Removes player from whitelist");
							sender.sendMessage("/gm ban whitelist list - Prints whitelisted players");
							break;
					}
					break;
				case "list":
					//GugaDataPager<GugaBan> pager = new GugaDataPager<GugaBan>(GugaBanHandler.GetBannedPlayers(), 15);
					//Iterator<GugaBan> i = pager.GetPage(Integer.parseInt(arg2)).iterator();
					//sender.sendMessage("LIST OF BANNED PLAYERS:");
					//sender.sendMessage("PAGE " + arg2 + "/" + pager.GetPagesCount());
					//while (i.hasNext())
					//{
					//	GugaBan ban = i.next();
					//	long hours = (ban.GetExpiration() - System.currentTimeMillis()) / (60 * 60 * 1000);
					//	sender.sendMessage(ban.GetPlayerName() + "  -  " + hours + " hours");
					//}
					sender.sendMessage("This does not work yet, use web banlist.");
					break;
				default:
					sender.sendMessage("/gm ban add <player> <hours> - Bans a player for number of hours.");
					sender.sendMessage("/gm ban remove <player> - Removes a ban.");
					sender.sendMessage("/gm ban whitelist - Whitelist sub-menu.");
					sender.sendMessage("/gm ban list <page>  -  Shows all banned players.");
					break;
			}
		}
		else if (subCommand.matches("speed") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if (args.length == 4)
			{
				Player target = plugin.getServer().getPlayer(args[2]);
				if (target == null)
				{
					sender.sendMessage("Player is not online");
					return;
				}
				if(!GameMasterHandler.IsAdmin(sender.getName()))
				{
					if(!args[2].equalsIgnoreCase(sender.getName()))
					{
						ChatHandler.FailMsg(sender, "You can set your speed only.");
						return;
					}
				}
				if(args[1].matches("fly"))
				{
					target.setFlySpeed(Float.parseFloat(args[3]));
					ChatHandler.SuccessMsg(sender, "Fly speed has been succesfuly set.");
				}
				else if(args[1].matches("walk"))
				{
					target.setWalkSpeed(Float.parseFloat(args[3]));
					ChatHandler.SuccessMsg(sender, "Walk speed has been succesfuly set.");
				}
			}
			else
			{
				sender.sendMessage("/gm speed fly <name> <speed> - Sets fly speed of a certain player.");
				sender.sendMessage("/gm speed walk <name> <speed> - Sets walk speed of a certain player.");
			}
		}
		else if (subCommand.matches("credits") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(args.length==1)
			{
				sender.sendMessage("/gm credits add <player> <amount>  -  Add credits to a player.");
				sender.sendMessage("/gm credits remove <player> <amount>  -  Remove credits to a player.");
				sender.sendMessage("/gm credits balance <player>  -  Shows credits of a player.");
			}
			else if (args.length == 3)
			{
				String name = arg2;
				if (arg1.matches("balance"))
				{
					sender.sendMessage("This account has " + plugin.currencyManager.getBalance(name) + " credits.");
				}
			}
			else if (args.length == 4)
			{
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
					plugin.currencyManager.addCredits(name, amount);
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
					if(plugin.currencyManager.addCredits(name, -amount))
					{
						sender.sendMessage("You removed +" + amount + " credits from " + name);
						Player p = null;
						if((p = plugin.getServer().getPlayerExact(name))!=null)
							p.sendMessage("You lost +" + amount + " credits!");
					}
					else
						sender.sendMessage("Failed to remove credits from player.");					
				}
			}
		}
		else if(subCommand.matches("places") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if (arg1.matches("remove"))
			{
				if (plugin.placesManager.portalExists(arg2))
				{
					if(!plugin.placesManager.removeTeleport(arg2))
					{
						sender.sendMessage("Cannot remove place.");
						return;
					}
					sender.sendMessage("Place successfully removed");
				}
				else
				{
					sender.sendMessage("This place doesnt exist!");
				}
			}
			else if (arg1.matches("list"))
			{
				ArrayList<Place> placesList = plugin.placesManager.listAllPlaces();
				DataPager<Place> pager = new DataPager<Place>(placesList,15);
				ArrayList<Place> page = pager.getPage(Integer.parseInt(args[2]));
				sender.sendMessage("List of places, page " + args[2] + " of " + pager.getPageCount());
				if(page == null)
				{
					sender.sendMessage("There are no places on this page.");
				}
				else
				{
					for(Place place : page)
					{
						sender.sendMessage(String.format("-%d %s=%s", place.getId(),place.getName(),place.getType()));
					}
				}
			}
			else if (arg1.matches("add") && args.length == 5)
			{
				if(plugin.placesManager.portalExists(arg2))
				{
					sender.sendMessage("This place already exists!");
					return;
				}
				if(!plugin.placesManager.addTeleport(arg2, arg3, sender.getLocation().getBlockX(), sender.getLocation().getBlockY(), sender.getLocation().getBlockZ(), sender.getLocation().getWorld().getName(), args[4]))
				{
					sender.sendMessage("Place not added.");
					return;
				}	
				sender.sendMessage("Place successfully added");
			}
			else if(arg1.matches("move"))
			{
				if(!plugin.placesManager.portalExists(arg2))
				{
					sender.sendMessage("This place already exists!");
					return;
				}
				if(plugin.placesManager.moveTeleport(arg2, sender.getLocation().getBlockX(), sender.getLocation().getBlockY(), sender.getLocation().getBlockZ(), sender.getLocation().getWorld().getName()))
					ChatHandler.SuccessMsg(sender, "Place moved to our location");
				else
					ChatHandler.FailMsg(sender, "Unable to move");
			}
			else if(arg1.matches("mod") && args.length==6)
			{
				if(!plugin.placesManager.portalExists(arg2))
				{
					sender.sendMessage("This place already exists!");
					return;
				}
				if(plugin.placesManager.modifyTeleport(arg2, arg3, args[4], args[5]))
					ChatHandler.SuccessMsg(sender, "Place modified");
				else
					ChatHandler.FailMsg(sender, "Unable to modify");
			}
			else
			{
				sender.sendMessage("Usage:");
				sender.sendMessage("/gm places list <page>  - Show list of all places.");	
				sender.sendMessage("/gm places add <name> <owner> <type> - Adds current position to places type is 'public', 'private', 'vip'.");
				sender.sendMessage("/gm places mod <name> <newname> <owner> <type>");
				sender.sendMessage("/gm places move <name>");
				sender.sendMessage("/gm places remove <name> - Removes a certain place from the list.");
			}
		}
		else if (subCommand.matches("regions") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(args.length==1)
			{
				sender.sendMessage("/gm regions list <page>  - Show list of all places.");	
				sender.sendMessage("/gm regions add <name> <world> <owner1,owner2> <x1> <x2> <z1> <z2> - Adds Region");	
				sender.sendMessage("/gm regions owners <name> <owners> - Changes owners of certain region.");	
				sender.sendMessage("/gm regions remove <name> - Removes a certain region from the list.");
			}
			else if (args.length == 3)
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
				else if (subCmd.equalsIgnoreCase("list"))
				{
					DataPager<GugaRegion> pager = new DataPager<GugaRegion>(GugaRegionHandler.GetAllRegions(), 15);
					sender.sendMessage("LIST OF REGIONS:");
					sender.sendMessage("PAGE " + args[2] + pager.getPageCount());
					Iterator<GugaRegion> i = pager.getPage(Integer.parseInt(args[2])).iterator();
					while (i.hasNext())
					{
						GugaRegion region = i.next();
						String[] owners = region.GetOwners();
						int[] coords = region.GetCoords();
						sender.sendMessage(" - " + region.GetName() + " [" + GugaRegionHandler.OwnersToLine(owners) + "]   <" + coords[GugaRegion.X1] + "," + coords[GugaRegion.X2] + "," + coords[GugaRegion.Z1] + "," + coords[GugaRegion.Z2] + ">");
					}
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
			else if (args.length == 9)
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
					String world = args[3];
					String[] owners = args[4].split(",");
					int x1 = Integer.parseInt(args[5]);
					int x2 = Integer.parseInt(args[6]);
					int z1 = Integer.parseInt(args[7]);
					int z2 = Integer.parseInt(args[8]);
					GugaRegionHandler.AddRegion(name, world, owners, x1, x2, z1, z2);
					sender.sendMessage("Region successfully added");
				}
			}
		}
		else if (subCommand.matches("save-all") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			AutoSaver.SaveWorldStructures();
			ChatHandler.SuccessMsg(sender, "Successfully saved!");
		}
		else if (subCommand.matches("book") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			sender.sendMessage("/gm book copy - Copies book in your hand.");
		}
		else if (subCommand.matches("on") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(disabledGMs.contains(sender.getName()))
			{
				disabledGMs.remove(sender.getName());
				ChatHandler.InitializeDisplayName(sender);
				sender.setGameMode(GameMode.CREATIVE);
				sender.sendMessage("GM state succesfully turned on!");
			}
		}
		else if (subCommand.matches("off") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(!disabledGMs.contains(sender.getName()))
			{
				disabledGMs.add(sender.getName());
				ChatHandler.InitializeDisplayName(sender);
				sender.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage("GM state succesfully turned off!");
			}
		}
		else if (subCommand.matches("enchant") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(args.length==2)
			{
				if(args[1].matches("all"))
					Enchantments.enchantAll(sender);
				else if(args[1].matches("rikubstyle"))
					Enchantments.enchantAllInRikubStyle(sender);
				ChatHandler.SuccessMsg(sender, "Enchantments has been added.");
			}
			else if(args.length==3)
			{
				EnchantmentResult result = Enchantments.enchantItem(sender, args[1], Integer.parseInt(args[2]));
				switch(result)
				{
					case ENCHANTED:
					{
						ChatHandler.SuccessMsg(sender, "Enchantment has been added.");
						break;
					}
					case INVALID_ID:
					{
						ChatHandler.FailMsg(sender, "Invalid name of enchantment.");
						break;
					}
					case CANNOT_ENCHANT:
					{
						ChatHandler.FailMsg(sender, "Cannot enchant this item.");
						break;
					}
					default:
					{
						ChatHandler.FailMsg(sender, "Unknown error.");
						break;
					}
				}
			}
			else
			{
				sender.sendMessage("/gm enchant <name> <level> - Enchants item in your hand to certain enchantment level.");
				sender.sendMessage("/gm enchant all - Enchants item in your to maximal natural level and all enchantments.");
				sender.sendMessage("/gm enchant rikubstyle - Enchants item in your hand to level 127 and all enchantments.");
			}
		}
		if (subCommand.matches("ip") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(args.length == 2)
			{
				Player p;
				if ((p = plugin.getServer().getPlayer(args[1])) != null)
				{
					sender.sendMessage("Players IP:" + p.getAddress());
				}
				else
				{
					sender.sendMessage("This player is not online!");
				}
			}
			else
			{
				sender.sendMessage("Usage: /gm ip <player>");
			}
		}
		else if (subCommand.matches("fly") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			Player target = plugin.getServer().getPlayer(args[1]);
			if (target == null)
			{
				sender.sendMessage("Hrac neni online!");
				return;
			}
			if (target.getAllowFlight())
			{
				target.setAllowFlight(false);
				target.setFlying(false);
				fly.remove(target.getName().toLowerCase());
				target.sendMessage("Fly mode byl vypnut!");
				sender.sendMessage("Fly mode succesfuly turned off.");
			}
			else
			{
				target.setAllowFlight(true);
				target.setFlying(true);
				fly.add(target.getName().toLowerCase());
				target.sendMessage("Fly mode byl zapnut!");
				sender.sendMessage("Fly mode succesfuly turned on.");
			}
		}
		else if (subCommand.matches("world") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 2)
			{
				if(plugin.getServer().getWorld(args[1]) != null)
				{
					sender.teleport(plugin.getServer().getWorld(args[1]).getSpawnLocation());
					ChatHandler.SuccessMsg(sender, "You have been teleported!");
				}
				else
					ChatHandler.FailMsg(sender, "This world doesn't exist!");
			}
			else
			{
				sender.sendMessage("Usage: /gm world <world_name>");
			}
		}
		else if(subCommand.matches("kill") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length==2)
			{
				Player target = plugin.getServer().getPlayer(args[1]);
				if(target.isOnline())
				{
					target.setHealth(0);
					target.sendMessage("Byl jste zabit adminem/GM!");
					sender.sendMessage("Hrac "+target.getName()+" byl zabit!");
				}
			}
			else
			{
				sender.sendMessage("Usage: /gm kill <player name>");
			}
		}
		else if (subCommand.matches("vip") && GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(arg1.equalsIgnoreCase("add") && args.length == 4)
			{
				String userName = arg2;
				double seconds = Double.valueOf(arg3)*2592000; //3600*24*30 - convert months to seconds
				if(plugin.vipManager.addVip(userName, Double.valueOf(seconds).longValue()))
					sender.sendMessage("Vip for user '"+userName+"' added.");
				else
					sender.sendMessage("Vip could not be added");
			}
			else if(arg1.equalsIgnoreCase("remove") && args.length==3)
			{
				if(plugin.vipManager.removeVip(arg2))
					sender.sendMessage("User '"+arg2+"' is no londer VIP.");
				else
					sender.sendMessage("Could not remove VIP.");
			}
			else if(arg1.equalsIgnoreCase("get") && args.length==3)
			{
				VipUser vip = plugin.vipManager.getVip(arg2);
				if(vip == null)
					sender.sendMessage("User '"+arg2+"' is not vip.");
				else if(vip.getExpiration() == -1)
					sender.sendMessage("User '"+arg2+"' is permanent vip.");
				else
					sender.sendMessage("Vip for '"+arg2+"' expires "+new Date(vip.getExpiration()).toString());
			}
			else if(arg1.equalsIgnoreCase("set") && args.length==4)
			{
				String userName = arg2;
				Double seconds = Double.parseDouble(arg3);
				if(seconds != -1) // don't make permanent VIP to seconds
					seconds = seconds*2592000; //3600*24*30 - convert months to seconds
				if(plugin.vipManager.setVip(userName, seconds.longValue()))
					sender.sendMessage("Vip for user '"+userName+"' updated.");
				else
					sender.sendMessage("Vip could not be updated");
			}
			else if(arg1.equalsIgnoreCase("list") && args.length==2)
			{
				sender.sendMessage("List of all VIP users:");
				sender.sendMessage(plugin.vipManager.listAllVips().toString());
			}
			else
			{
				sender.sendMessage("Usage:");
				sender.sendMessage("/gm vip add <user> <duration in months>");
				sender.sendMessage("/gm vip remove <user>");
				sender.sendMessage("/gm vip set <user <expiration in months from now>");
				sender.sendMessage("/gm vip list");
			}
		}
		else if (subCommand.matches("gmmode"))
		{
			if(args.length == 2)
			{
				if (!GameMasterHandler.IsAdmin(sender.getName()) && !args[1].equalsIgnoreCase(sender.getName()))
				{
					sender.sendMessage("You can only set gmmode to yourself!");
					return;
				}
				Player p;
				if ((p = plugin.getServer().getPlayer(args[1])) != null)
				{
					GameMode mode = p.getGameMode();
					if (mode == GameMode.CREATIVE)
					{
						p.setGameMode(GameMode.SURVIVAL);
						sender.sendMessage("GM Mode for " + args[1] + " has been turned off");
					}
					else
					{
						p.setGameMode(GameMode.CREATIVE);
						sender.sendMessage("GM Mode for " + args[1] + " has been turned on");
					}
				}
			}
			else
				sender.sendMessage("Usage: /gm gmmode <player name>");
		}
		else if (subCommand.matches("godmode")&&GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 2)
				if (godMode.contains(args[1].toLowerCase()))
				{
					godMode.remove(args[1]);
					sender.sendMessage("Immortality for " + args[1] + " has been turned off");
				}
				else
				{
					godMode.add(args[1].toLowerCase());
					sender.sendMessage("Immortality for " + args[1] + " has been turned on");
				}
			else
				sender.sendMessage("Usage: /gm godmode <player name>");
		}
		else if (subCommand.matches("cmd") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			String cmd = args[1];
			int i = 2;
			while (i < args.length)
			{
				cmd += " " + args[i++];
			}
			BukkitCommandParser.ParseCommand(sender, cmd);
		}
		else if (subCommand.matches("enderchest") && GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(args.length == 2)
			{
				Player target = plugin.getServer().getPlayer(args[1]);
				if(target != null)
				{
					sender.openInventory(target.getEnderChest());
				}
				else
					ChatHandler.FailMsg(sender, "Player is not offline.");
			}
			else
				sender.sendMessage("Usage: /gm enderchest <player>");
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
		else if (subCommand.matches("replace")&& GameMasterHandler.IsAdmin(sender.getName()))
		{
			if (args.length == 6)
			{
				int typeID1 = Integer.parseInt(args[1]);
				int typeID2 = Integer.parseInt(args[2]);
				int x = Integer.parseInt(args[3]);
				int y = Integer.parseInt(args[4]);
				int z = Integer.parseInt(args[5]);
				plugin.GenerateBlockType2(sender, typeID1, typeID2, x, y, z);
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
			
	}
	
	public static void CommandGMChat(CommandSender sender, String[] args)
	{
		if(!(GameMasterHandler.IsAtleastGM(sender.getName()) || sender instanceof ConsoleCommandSender && sender.isOp()))
		{
			return;
		}
		
		Player[] players = plugin.getServer().getOnlinePlayers();
		StringBuilder message = new StringBuilder();
		if(args.length > 0)
		{
			message.append(args[0]);
			for(int i = 1;i<args.length;i++)
			{
				message.append(" ");
				message.append(args[i]);
			}
		}
		int i = 0;
		while(i < players.length)
		{
			if(GameMasterHandler.IsAtleastGM(players[i].getName()))
			{
				players[i].sendMessage(String.format("%sGMC[%s]: %s%s",ChatColor.DARK_AQUA,sender.getName(),ChatColor.WHITE,message));
			}
			i++;
		}
	}
	
	public static void CommandWorld(Player sender)
	{
		if(plugin.userManager.getUser(sender.getName()).getProfession().GetLevel() >= 10)
		{
			if(BasicWorld.IsBasicWorld(sender.getLocation()))
			{
				BasicWorld.BasicWorldLeaveToWorld(sender);
			}
			else
			{
				ChatHandler.FailMsg(sender, "V tomto svete nelze prikaz pouzit!");
			}
		}
		else
		{
			ChatHandler.FailMsg(sender, "Jeste nemate GugaRPG level 10!");
		}
	}
	
	public static void CommandLogin(Player sender, String args[])
	{
		 String pass = (args.length>=1) ? args[0] : "";
		 if (plugin.userManager.userIsRegistered(sender.getName()))
		 {
			 MinecraftPlayer player = plugin.userManager.getUser(sender.getName());
			 if(player==null)
			 {
				 sender.sendMessage("Unable to login you.");
				 return;
			 }
			 if (!(player.getState() == ConnectionState.AUTHENTICATED))
			 {
				 player.login(pass);
				 if (player.getState() == ConnectionState.AUTHENTICATED)
				 {
					ChatHandler.SuccessMsg(sender, "Byl jste uspesne prihlasen!");
					GugaProfession2 prof = player.getProfession();
					if(prof!=null && prof.GetXp() == 0 && !BasicWorld.IsBasicWorld(sender.getLocation()))
					{
						BasicWorld.BasicWorldEnter(sender);
					}
				 }
				 else
				 {
					 ChatHandler.FailMsg(sender, "Prihlaseni se nezdarilo!");
					 return;
				 }
			 }
			 else
			 {
				 ChatHandler.FailMsg(sender, "Jiz jste prihlasen!");
			 }
		 }
		 else
		 {
			 ChatHandler.FailMsg(sender, "Nejdrive se zaregistrujte!");
		 }
	}
	
	public static void CommandRegister(Player sender, String[] args)
	{
		if(plugin.userManager.userIsRegistered(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Uz jste zaregistrovan!");
			return;
		}
		
		if(args.length == 3)
		{
			String pwd1 = args[0];
			String pwd2 = args[1];
			String email = args[2];
			if(!pwd1.equals(pwd2))
			{
				ChatHandler.FailMsg(sender, "Zadana hesla musi byt stejna.");
				return;
			}
			if(!email.matches("^([a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9_\\-\\.\\+]+\\.[a-zA-Z0-9_\\-\\.]+)$"))
			{
				ChatHandler.FailMsg(sender, "Vas email neni platny nebo neni podporovan");
				return;
			}
			if(plugin.userManager.register(sender,pwd1,email))
			{
				MinecraftPlayer player = plugin.userManager.getUser(sender.getName());
				ChatHandler.SuccessMsg(sender, "Byl jste uspesne zaregistrovan!");
				if(player.login(pwd1))
				{
					ChatHandler.SuccessMsg(sender, "Byl jste uspesne prihlasen!");
					ChatHandler.InitializeDisplayName(sender);
					GugaProfession2 prof = player.getProfession();
					if(prof!=null && prof.GetLevel() < 10 && !BasicWorld.IsBasicWorld(sender.getLocation()))
					{
						BasicWorld.BasicWorldEnter(sender);
					}
				}
				else
					ChatHandler.FailMsg(sender, "Nepodarilo se vas prihlasit, zkuste se odpojit a znovu pripojit.");
			}
			else
				ChatHandler.FailMsg(sender, "Registrace se nezdarila.");
		}
		else
		{
			sender.sendMessage("Pouziti: /register <heslo> <heslo znovu> <email>");
		}
	}

	public static void CommandDebug()
	{
		plugin.debug = !plugin.debug;
		plugin.log.info("DEBUG="+plugin.debug);
	}
	public static void CommandWhisper(CommandSender sender, String[]args)
	{
		if(args.length < 2)
		{
			sender.sendMessage("Usage: /tell <player> <message>");
			return;
		}
		
		Player p = plugin.getServer().getPlayer(args[0]);
		if(sender instanceof ConsoleCommandSender)
		{
			if(p == null)
			{
				plugin.log.info("This player is not online!");
				return;
			}

			StringBuilder msg = new StringBuilder();
			for(int i=1;i<args.length;i++)
			{
				msg.append(" ");
				msg.append(args[i]);
			}
			p.sendMessage(ChatColor.DARK_AQUA + "[" + "CONSOLE septa" + "]" + msg);
		}
		else
		{
			Player playerSender = (Player)sender;
			if(p == null)
			{
				ChatHandler.FailMsg(playerSender, "Tento hrac je offline!");
				return;
			}
			if(ChatHandler.isBlockedBy(playerSender.getName(),p.getName()))
			{
				sender.sendMessage("Tento hrac vas ma v blocklistu.");
				return;
			}
			
			StringBuilder msg = new StringBuilder();
			for(int i=1;i<args.length;i++)
			{
				msg.append(" ");
				msg.append(args[i]);
			}
			playerSender.sendMessage(ChatColor.DARK_AQUA + "[" + "Vy -> " + p.getName() + "]" + msg);
			p.sendMessage(ChatColor.DARK_AQUA + "[" + playerSender.getName() + " septa" + "]" + msg);
			GugaCommands.reply.put(p, playerSender);
		}
	}

	public static void CommandRegion(Player sender, String[] args)
	{
		if(!sender.isOp())
		{
			return;
		}
		
		if(args.length == 0)
		{
			sender.sendMessage("/region create <name>\n/region c1\n/region c2\n/region list\n/region access\n/region remove <name>");
		}
		else if(args[0].equalsIgnoreCase("create"))
		{
			if(!sender.getLocation().getWorld().getName().equalsIgnoreCase("world"))
			{
				ChatHandler.FailMsg(sender,"Regiony muzete vytvaret a upravovat jenom ve stavebnim svete.");
				return;
			}
			if(args.length >= 2)
				ResidenceHandler.createResidence(sender, args[1].trim());
			else
				sender.sendMessage("Usage: /region create <name>");
		}
		else if(args[0].equalsIgnoreCase("list"))
		{
			ArrayList<String> list = ResidenceHandler.getResidencesOf(sender.getName());
			sender.sendMessage("Your regions:\n "+list);
		}
		else if(args[0].equalsIgnoreCase("c1"))
		{
			if(!sender.getLocation().getWorld().getName().equalsIgnoreCase("world"))
			{
				ChatHandler.FailMsg(sender,"Regiony muzete vytvaret a upravovat jenom ve stavebnim svete.");
				return;
			}
			ResidenceHandler.pos1(sender.getName(), sender.getLocation().getBlockX(), sender.getLocation().getBlockZ());
		}
		else if(args[0].equalsIgnoreCase("c2"))
		{
			if(!sender.getLocation().getWorld().getName().equalsIgnoreCase("world"))
			{
				ChatHandler.FailMsg(sender,"Regiony muzete vytvaret a upravovat jenom ve stavebnim svete.");
				return;
			}
			ResidenceHandler.pos2(sender.getName(), sender.getLocation().getBlockX(), sender.getLocation().getBlockZ());
		}
		else if(args[0].equalsIgnoreCase("access"))
		{
			if(args.length < 3)
			{
				sender.sendMessage("/region access <region> list");
				sender.sendMessage("/region access <region> add <player>");
				sender.sendMessage("/region access <region> remove <player>");
			}
			else
			{
				if(!ResidenceHandler.getResidenceOwner(args[1]).equalsIgnoreCase(sender.getName()))
				{
					sender.sendMessage(String.format("Residence %s does not exist or is not yours",args[1]));
					return;
				}
				
				if(args[2].equalsIgnoreCase("list"))
				{
					sender.sendMessage(String.format("Folowing players can dig in %s residence:\n  %s",args[1],ResidenceHandler.getAllowedPlayers(args[1])));
				}
				else if(args[2].equalsIgnoreCase("add") && args.length==4)
				{
					if(ResidenceHandler.addResidenceAccess(args[1],args[3]))
					{
						ChatHandler.SuccessMsg(sender, "Access added");
					}
					else
					{
						ChatHandler.FailMsg(sender, "Failed to add access");
					}
				}
				else if(args[2].equalsIgnoreCase("remove"))
				{
					if(ResidenceHandler.removeResidenceAccess(args[1],args[3]))
					{
						ChatHandler.SuccessMsg(sender, "Access removed");
					}
					else
					{
						ChatHandler.FailMsg(sender, "Failed to remove access");
					}
				}
			}
		}
		else if(args[0].equalsIgnoreCase("remove"))
		{
			if(args.length >= 2)
				if(ResidenceHandler.removeResidence(args[1]))
					ChatHandler.SuccessMsg(sender, "Region removed.");
				else
					ChatHandler.FailMsg(sender, "Failed to remove region.");
			else
				sender.sendMessage("Usage: /region remove <name>");
		}
	}
	
	private static void Teleport(Player sender,String name)
	{
		if (plugin.arena.IsArena(sender.getLocation()))
		{
			sender.sendMessage("V arene nemuzete pouzit prikaz /places!");
			return;
		}
		if (plugin.EventWorld.IsEventWorld(sender.getLocation()))
		{
			sender.sendMessage("V EventWorldu nemuzete pouzit prikaz /places!");
			return;
		}
		plugin.placesManager.handlePlayerTeleport(sender,name);
	}

	public static HashMap<Player, Player> vipTeleports = new HashMap<Player, Player>();
	public static HashMap<Player, Player> reply = new HashMap<Player, Player>();
	public static ArrayList<String> godMode = new ArrayList<String>();
	public static ArrayList<String> fly = new ArrayList<String>();
	public static HashMap<String, Location> backTeleport = new HashMap<String,Location>();
	public static ArrayList<String> disabledGMs = new ArrayList<String>();
	public static String FeedbackFile = "plugins/MineAndCraft_plugin/FeedbackFile.dat";
	private static Guga_SERVER_MOD plugin;
	
	public static void CommandFriend(Player sender, String[] args)
	{
		sender.sendMessage("Friendlist does not work yet.");
	}
	
	public static void CommandBlock(Player sender, String[] args)
	{
		if(args.length == 1 && args[0].equalsIgnoreCase("list"))
		{
			sender.sendMessage("You have currently blocklisted these players:");
			sender.sendMessage(String.format("  %s",ChatHandler.listBlocklistedFor(sender).toString()));
		}
		else if(args.length == 1)
		{
			if(ChatHandler.addBlocklist(sender,args[0]))
				sender.sendMessage("User blocklisted");
			else
				sender.sendMessage("Cannot blocklist user.");
		}
		else if(args.length == 2 && args[0].equalsIgnoreCase("remove"))
		{
			if(ChatHandler.removeBlocklist(sender,args[1]))
				sender.sendMessage("User no longer blocklisted");
			else
				sender.sendMessage("Cannot remove user from blocklist.");
		}
		else
			sender.sendMessage("Usage:\n  /block <user>\n  /block list\n  /block remove <user>");
	}
	
	public static void CommandAnnounce(CommandSender sender, String[] args)
	{
		StringBuilder sb = new StringBuilder();
		if(args.length > 0)
		{
			sb.append(args[0]);
			for(int i = 1;i<args.length;i++)
			{
				sb.append(" ");
				sb.append(args[i]);
			}
		}
		
		ChatHandler.broadcast(String.format("%s[ANNOUNCEMENT] %s",ChatColor.DARK_AQUA, sb.toString()));
	}
}
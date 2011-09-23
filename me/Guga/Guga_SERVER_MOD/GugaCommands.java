package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
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
		sender.sendMessage("PLAYERS ONLINE:");
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
					msg += "  Prof: " +plugin.professions.get(p[i].getName()).GetProfession() + " lvl " + plugin.professions.get(p[i].getName()).GetLevel()+ "  " + distance + " blocks away";
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
					msg = "- " + pName + "  " + distance + " blocks away";
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
					msg += "  Prof: " +plugin.professions.get(p[i].getName()).GetProfession() + " lvl " + plugin.professions.get(p[i].getName()).GetLevel();
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
		sender.sendMessage("List of possible Commands:");
		sender.sendMessage(" /lock  -  Locks targeted chest.");
		sender.sendMessage(" /unlock  -  Unlocks targeted chest.");
		sender.sendMessage(" /who  -  List of all players online.");
		sender.sendMessage(" /login <pass>  -  Logs in a registered user.");
		sender.sendMessage(" /register <pass>  -  Registers a new user.");
		sender.sendMessage(" /password <old_pass> <new_pass>  -  Changes a password.");
		sender.sendMessage(" /status <status_msg>  -  Sets your status.");
		sender.sendMessage(" /rpg  -  Professions menu.");
		sender.sendMessage(" /arena  -  Arenas menu.");
		sender.sendMessage(" /shop  -  Shop menu.");
		sender.sendMessage(" /vip  -  VIP menu.");
		sender.sendMessage(" /places - Places menu.");
		sender.sendMessage("/r <message>  -  Replies to a whisper.");
		if (sender.isOp())
		{
			sender.sendMessage(" /gm  -  GameMaster's menu.");
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
				sender.sendMessage("Your chest has been locked.");
			}
			else
			{
				sender.sendMessage("Chest has an owner already!");
			}
		}	
		else
		{
			sender.sendMessage("This is not a chest!");
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
			if ( (plugin.chests.GetChestOwner(chest).matches(sender.getName())) || (sender.isOp()) )
			{
				plugin.chests.UnlockChest(chest,sender.getName());
				sender.sendMessage("Chest has been unlocked.");
			}
			else
			{
				sender.sendMessage("You cannot unlock this chest!");
			}
		}	
		else
		{
			sender.sendMessage("This is not a chest!");
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
			sender.sendMessage("/shop info  -  Info about Shopping system.");
			sender.sendMessage("/shop buy <itemID> <amount>  -  Buys specified amount of specified item.");
			sender.sendMessage("/shop buy <itemID>  -  Buys specified item (1).");
			sender.sendMessage("/shop balance  -  Shows your account balance.");
			sender.sendMessage("/shop items  -  Shows all items you can buy.");
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
				sender.sendMessage("Your account balance:");
				sender.sendMessage("Credits: " + p.GetCurrency());
			}
			else if(subCommand.matches("items"))
			{
				for (Prices i : Prices.values())
				{
				 sender.sendMessage(i.toString() +"-    id: " + i.GetItemID()+ "   price: "+ i.GetItemPrice());
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
			sender.sendMessage("You have to login first!");
			return;
		}
		if (!vip.IsVip())
		{
			sender.sendMessage("Only VIP can use this command!");
			return;
		}
		if (args.length == 0)
		{
			sender.sendMessage("VIP MENU:");
			sender.sendMessage("/vip expiration  -  Shows, when your VIP status expires.");
			sender.sendMessage("/vip tp  -  Teleport subcommand.");
			sender.sendMessage("/vip time  -  Time subcommand.");
			sender.sendMessage("/vip item  -  Items subcommand.");
			
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("expiration"))
			{
				sender.sendMessage("Your VIP expiration date is: " + new Date(vip.GetExpirationDate()));
			}
			else if (subCommand.matches("tp"))
			{
				sender.sendMessage("Teleport Menu:");
				sender.sendMessage("/vip tp player <name>  -  Teleports you to a certain player.");
				sender.sendMessage("/vip tp spawn  -  Teleports you to World spawn.");
				sender.sendMessage("/vip tp back  -  Teleports you to your last location you have teleported from.");
				sender.sendMessage("/vip tp bed  -  Teleports you to your bed.");
			}
			else if (subCommand.matches("time"))
			{
				sender.sendMessage("Time Menu:");
				sender.sendMessage("/vip time set <value>  -  Sets time to value between 0-24000");
				sender.sendMessage("/vip time reset  -  Resets time back to server time.");
			}
			else if (subCommand.matches("item"))
			{
				sender.sendMessage("Item Menu:");
				sender.sendMessage("/vip item add <itemID>  -  Add stack of item in your inventory");
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
						sender.sendMessage("You need to teleport somewhere first!");
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
					sender.teleport(sender.getBedSpawnLocation());
				}
			}
			else if (subCommand.matches("time"))
			{
				if (arg1.matches("reset"))
				{
					if (!sender.isPlayerTimeRelative())
					{
						sender.resetPlayerTime();
						sender.sendMessage("Time has been reset");
					}
					else
						sender.sendMessage("Your time is already equal to server time!");
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
						sender.sendMessage("Item added!");
					}
					else
						sender.sendMessage("You cannot add this item!");
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
						sender.sendMessage("Time has been successfully set");
					}
					else 
						sender.sendMessage("You cannot set this value!");
				}
			}
		}
	}
	public static void CommandRpg(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("RPG MENU:");
			sender.sendMessage("/rpg status  -  Shows your status.");
			sender.sendMessage("/rpg skills  -  Shows your skills and bonuses.");
			sender.sendMessage("/rpg select  -  Selects your professions.");
			sender.sendMessage("/rpg info  -  Info about professions.");
			sender.sendMessage("/rpg info <profession>  -  Info about certain profession.");
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
					String profName = prof.GetProfession();
					sender.sendMessage("********************");
					sender.sendMessage("**      " + profName);
					sender.sendMessage("**Level:" + lvl);
					sender.sendMessage("**XP:" + xp + "/" + xpNeeded);
					sender.sendMessage("********************");
					sender.sendMessage("********************");
				}
				else 
				{
					sender.sendMessage("You have to choose your profession first!");
				}
			}
			else if (subCommand.matches("skills"))
			{
				GugaProfession prof;
				if ((prof = plugin.professions.get(sender.getName())) == null)
				{
					sender.sendMessage("You have to choose your profession first!");
				}
				if (prof instanceof GugaMiner)
				{
					GugaMiner miner = (GugaMiner)prof;
					int chance[] = miner.GetChances();
					int bonus[] = miner.GetBonusDrops();
					int iron = chance[plugin.IRON];
					int gold = chance[plugin.GOLD];
					int diamond = chance[plugin.DIAMOND];
					sender.sendMessage("********************");
					sender.sendMessage("**Chance to find in cobblestone:");
					sender.sendMessage("**Iron: " + iron + "%");
					sender.sendMessage("**Gold: " + gold + "%");
					sender.sendMessage("**Diamond: " + diamond + "%");
					sender.sendMessage("********************");
					
					iron = bonus[plugin.IRON];
					gold = bonus[plugin.GOLD];
					diamond = bonus[plugin.DIAMOND];
					
					sender.sendMessage("**Bonus Drops from Ores:");
					sender.sendMessage("**Iron: +" + iron);
					sender.sendMessage("**Gold: +" + gold);
					sender.sendMessage("**Diamond: +" + diamond);
					
					sender.sendMessage("********************");
					sender.sendMessage("********************");
					
				}
				else if (prof instanceof GugaHunter)
				{
					GugaHunter hunter = (GugaHunter)prof;
					double regen = ((double)hunter.GetHpRegen())/2;
					int dmg = hunter.GetDamageIncrease();
					double speed = hunter.GetSpeedIncrease();
					sender.sendMessage("********************");
					sender.sendMessage("**HP Regen: " + regen + "hp per 60 seconds");
					sender.sendMessage("**Damage Increase: " + dmg);
					sender.sendMessage("**Movement Speed Increase: " + speed);
					sender.sendMessage("********************");
					sender.sendMessage("********************");
				}
			}
			else if (subCommand.matches("select"))
			{
				sender.sendMessage("Please type your desired profession name - hunter or miner");
			}
			else if (subCommand.matches("info"))
			{
				sender.sendMessage("********************");
				sender.sendMessage("**You can choose your profession");
				sender.sendMessage("**by typing /rpg select <profession>");
				sender.sendMessage("**There are 2 professions at the moment:");
				sender.sendMessage("**      -Hunter and Miner");
				sender.sendMessage("**Each profession has different abilities and skills");
				sender.sendMessage("**You can get experience for killing mobs and destroying blocks");
				sender.sendMessage("**Each profession gets different amount of experience for doing this.");
				sender.sendMessage("**Current max level is " + new GugaProfession().GetLvlCap());
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
						sender.sendMessage("You are now a Hunter!");
					}
					else 
					{
						sender.sendMessage("You already have a profession!");
					}
				}
				else if (arg1.matches("miner"))
				{
					if (plugin.professions.get(sender.getName()) == null)
					{
						GugaMiner prof = new GugaMiner(sender.getName(),0,plugin);
						plugin.professions.put(sender.getName(), prof);
						sender.sendMessage("You are now a Miner!");
					}
					else 
					{
						sender.sendMessage("You already have a profession!");
					}
				}
				else 
				{
					sender.sendMessage("This is not a profession!");
				}
			}
			else if (subCommand.matches("info"))
			{
				if (arg1.matches("hunter"))
				{
					sender.sendMessage("********************");
					sender.sendMessage("**Hunter's Bonuses:");
					sender.sendMessage("** - Hp Regen (+0,5 every 2 levels)");
					sender.sendMessage("** - Bonus Damage (+1 every 4 levels)");
					sender.sendMessage("** - Increased Movement Speed (+0.1 every level)");
					sender.sendMessage("********************");
					sender.sendMessage("********************");
					
				}
				else if (arg1.matches("miner"))
				{
					sender.sendMessage("********************");
					sender.sendMessage("**Miner's Bonuses:");
					sender.sendMessage("** - Increased Drops from:");
					sender.sendMessage("**      -Iron (+1 every 6 levels)");
					sender.sendMessage("**      -Gold (+1 every 8 levels)");
					sender.sendMessage("**      -Diamond (+1 every 10 levels)");
					sender.sendMessage("********************");
					sender.sendMessage("** - Chance on rare drop from Stone:");
					sender.sendMessage("**      -Iron (+1% every 5 levels)");
					sender.sendMessage("**      -Gold (+1% every 10 levels)");
					sender.sendMessage("**      -Diamond (+1% on level 15)");
					sender.sendMessage("********************");
				}
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
			sender.sendMessage("/places list  -  List of all possible places player can port to.");
			sender.sendMessage("/places port <name>  -  Teleports player to specified place.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("list"))
			{
				sender.sendMessage("LIST OF PLACES:");
				Iterator<GugaPlace> i;
				if (sender.isOp())
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
				GugaPlace place;
				if ( (place = GugaPort.GetPlaceByName(name)) != null)
				{
					if (GugaPort.GetPlacesForPlayer(sender.getName()).contains(place) || sender.isOp())
					{
						place.Teleport(sender);
						return;
					}
				}
				sender.sendMessage("This place doesnt exist!");
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
			sender.sendMessage("This name is already registered!");
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
				sender.sendMessage("Please enter your password!");
			}
		}
	}
	public static void CommandArena(Player sender, String args[])
	{
		if (args.length == 0)
		{
			sender.sendMessage("ARENA MENU:");
			sender.sendMessage("Commands:");
			sender.sendMessage("/arena join - Moves player to arena");
			sender.sendMessage("/arena leave - Removes a player from arena");
			sender.sendMessage("/arena stats - Shows a statistics of best players");
			//sender.sendMessage("/arena info - Info about arena system");
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
					sender.sendMessage("You are already in Arena!");
				}
			}
			else if (subCommand.matches("leave"))
			{
				plugin.arena.PlayerLeave(sender);
			}
			else if (subCommand.matches("setspawn"))
			{
				plugin.arena.SetSpawn(sender);
			}
			else if (subCommand.matches("removespawn"))
			{
				plugin.arena.RemoveSpawn(sender);
			}
			else if (subCommand.matches("stats"))
			{
				plugin.arena.ShowPvpStats(sender);
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
				sender.sendMessage(ChatColor.GRAY + "To " + p.getName() + ": " + msg);
				reply.put(p, sender);
				return;
			}
			sender.sendMessage("You have noone to reply to!");
		}
	}
	public static void CommandGM(Player sender, String args[])
	{
		if (!plugin.acc.UserIsLogged(sender))
		{
			sender.sendMessage("You have to be logged to use this command!");
			return;
		}
		if (args.length == 0)
		{
			sender.sendMessage("GM MENU:");
			sender.sendMessage("Commands:");
			sender.sendMessage("/gm ip <name> - Shows an IP of a player");
			sender.sendMessage("/gm setspawn - Sets a world spawn to GM's position");
			sender.sendMessage("/gm credits - Credits sub-menu.");
			sender.sendMessage("/gm setvip <name> <months>  -  Set VIP to certain player for (now + months)");
			sender.sendMessage("/gm getvip <name>  -  Gets VIP expiration date");
			sender.sendMessage("/gm announce  - Announcements sub-menu.");
			sender.sendMessage("/gm genblock <typeID> <reltiveX> <relativeY> <relativeZ>  -  Spawns a blocks from block you point at.");
			sender.sendMessage("/gm gmmode <name> -  Toggles gm mode for a certain player.");
			sender.sendMessage("/gm godmode <name>  -  Toggles immortality for a certain player.");
			sender.sendMessage("/gm tp <x> <y> <z>  -  Teleports gm to specified coords.");
			sender.sendMessage("/gm invis <name>  -  Toggles invisibility for a certain player.");
			sender.sendMessage("/gm spectate  -  Spectation sub-menu.");
			sender.sendMessage("/gm log - Shows a log records for target block.");
			sender.sendMessage("/gm places - Places sub-menu.");
		}
		else if (args.length == 1)
		{
			String subCommand = args[0];
			if (subCommand.matches("setspawn"))
			{
				Location pLoc = sender.getLocation();
				sender.getWorld().setSpawnLocation((int)pLoc.getX(), (int)pLoc.getY(), (int)pLoc.getZ());
				sender.sendMessage("New World Spawn has been set!");
			}
			else if (subCommand.matches("announce"))
			{
				sender.sendMessage("/gm announce print - Prints messages and indexes.");
				sender.sendMessage("/gm announce remove <index> - Removes a message from the list.");
				sender.sendMessage("/gm announce add <message> - Adds new message to the list.");
			}
			else if (subCommand.matches("credits"))
			{
				sender.sendMessage("/gm credits add <player> <amount>  -  add credits to a player.");
				sender.sendMessage("/gm credits remove <player> <amount>  -  remove credits to a player.");
			}
			
			else if (subCommand.matches("spectate"))
			{
				sender.sendMessage("/gm spectate player <name> - Start spectating certain player.");
				sender.sendMessage("/gm spectate stop - Stop spectating.");
			}
			else if (subCommand.matches("log"))
			{
				plugin.logger.PrintBlockData(sender, sender.getTargetBlock(null, 20));
			}
			else if(subCommand.matches("places"))
			{
				sender.sendMessage("/gm places list  - Show list of all places.");	
				sender.sendMessage("/gm places add <name> <owner> - Adds actual position to places (owner all = public).");	
				sender.sendMessage("/gm places remove <name> - Removes a certain place from the list.");	
			}
		}
		else if (args.length == 2)
		{
			String subCommand = args[0];
			String arg1 = args[1];
			Player p;
			if (subCommand.matches("ip"))
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
			else if (subCommand.matches("places"))
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
			else if(subCommand.matches("announce"))
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
			else if (subCommand.matches("getvip"))
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
			else if (subCommand.matches("invis"))
			{
				ToggleInvisibility(sender, arg1);
			}
			else if (subCommand.matches("spectate"))
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
			if (subCommand.matches("announce"))
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
			else if (subCommand.matches("places"))
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
							sender.sendMessage("Place sucesfully removed");
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
						sender.sendMessage("Place sucesfully added");
					}
				}
			}
			else if (subCommand.matches("credits"))
			{
				if (args.length == 4)
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
						plugin.getServer().getPlayer(name).sendMessage("You received +" + amount + " credits!");
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
			else if (subCommand.matches("genblock"))
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
			else if (subCommand.matches("setvip"))
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
			else if (subCommand.matches("spectate"))
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
				 plugin.acc.LoginUser(sender, pass);
				 sender.teleport(plugin.acc.playerStart.get(sender.getName()));
				 GugaProfession prof;
					if ((prof = plugin.professions.get(sender.getName())) != null)
					{
						if (prof instanceof GugaHunter)
						{
							((GugaHunter)prof).StartRegenHp();
						}
					}
			 }
			 else
			 {
				 sender.sendMessage("You are already logged!");
			 }
		 }
		 else
		 {
			 sender.sendMessage("You have to register first!");
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
					sender.sendMessage("Please enter your old and new password");
				}
			}
			else
			{
				sender.sendMessage("You have to login first!");
			}
		}
		else
		{
			sender.sendMessage("You have to register first!");
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
package me.Guga.Guga_SERVER_MOD.Listeners;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


import me.Guga.Guga_SERVER_MOD.GameMaster;
import me.Guga.Guga_SERVER_MOD.GugaBan;
import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaHunter;
import me.Guga.Guga_SERVER_MOD.GugaProfession;
import me.Guga.Guga_SERVER_MOD.GugaSpectator;
import me.Guga.Guga_SERVER_MOD.GugaVirtualCurrency;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.InventoryBackup;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;
import me.Guga.Guga_SERVER_MOD.Handlers.GameMasterHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaAuctionHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaBanHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaIPHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaCommands;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaMCClientHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaWorldSizeHandler;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class GugaPlayerListener implements Listener 
{
	public GugaPlayerListener(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		final Player p = e.getPlayer();
		Thread t = new Thread( new Runnable() {
			@Override
			public void run() 
			{
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!p.isOnline())
					return;
				if (!GugaMCClientHandler.HasClient(p))
				{
					p.kickPlayer("Stahnete si naseho klienta na www.mineandcraft.cz");
					return;
				}
				if (GugaBanHandler.IsWhiteListed(p))
					return;
				if (GugaBanHandler.GetGugaBan(p.getName()) == null)
					GugaBanHandler.AddBan(p.getName(), 0);
				
				if (GugaBanHandler.IsBanned(p.getName()))
				{
					GugaBan ban = GugaBanHandler.GetGugaBan(p.getName());
					long hours = (ban.GetExpiration() - System.currentTimeMillis()) / (60 * 60 * 1000);
					p.kickPlayer("Na nasem serveru jste zabanovan! Ban vyprsi za " + hours + " hodin(y)");
					return;
				}
				GugaIPHandler.UpdateBanAddr(p.getName());
				
				if (GugaIPHandler.IsWhiteListed(p))
					return;
				if (GugaIPHandler.GetGugaBan(p.getName()) == null)
					GugaIPHandler.AddBan(p.getName(), 0);
				GugaIPHandler.UpdateBanAddr(p.getName());
			}
		});
		t.start();
		if (p.getName().contains(" "))
		{
			p.kickPlayer("Prosim zvolte si jmeno bez mezery!");
			return;
		}
		if (p.getName().startsWith("ADMIN'") || p.getName().startsWith("GM'"))
		{
			if (!GameMasterHandler.GetNamesByRank(Rank.GAMEMASTER).contains(p.getName()))
			{
				p.kickPlayer("Na serveru neni zadny GM/ADMIN s timto jmenem!");
				return;
			}
		}
		if (!CanUseName(p.getName()))
		{
			p.kickPlayer("Prosim zvolte si jmeno slozene jen z povolenych znaku!   a-z A-Z 0-9 ' _ - .");
			return;
		}
		if (p.getName().matches(""))
		{
			p.kickPlayer("Prosim zvolte si jmeno!");
			return;
		}
		
		GugaAuctionHandler.CheckPayments(p);
		GugaVirtualCurrency curr = plugin.FindPlayerCurrency(p.getName());
		if (curr == null)
		{
			curr = new GugaVirtualCurrency(plugin, p.getName(), 0, new Date(0));
			plugin.playerCurrency.add(curr);
		}
		if (plugin.professions.get(p.getName()) == null)
			plugin.professions.put(p.getName(), new GugaProfession(p.getName(), 0, plugin));
		else if (curr.IsVip())
		{
			curr.UpdateDisplayName();
		}
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_JOIN_EVENT: playerName=" + e.getPlayer().getName());
		}
		long timeStart = System.nanoTime();
		p.sendMessage("******************************");
		p.sendMessage("Vitejte na serveru MineAndCraft!.");
		p.sendMessage("Pro zobrazeni prikazu napiste /help.");
		p.sendMessage("******************************");
		if(!(GameMasterHandler.IsAtleastGM(p.getName())))
		{
			if(GugaPlayerListener.IsCreativePlayer(p))
			{
				p.sendMessage("******************************");
				p.sendMessage("Jste creative user");
				p.sendMessage("******************************");
			}
			else
			{
				if (p.getGameMode().equals(GameMode.CREATIVE))
					p.setGameMode(GameMode.SURVIVAL);
			}
		}
		if (plugin.config.accountsModule)
		{
			if (plugin.acc.UserIsRegistered(p))
			{
				p.sendMessage("NEJSTE PRIHLASENY! Prosim prihlaste se pomoci /login heslo.");
				p.sendMessage("");
				p.sendMessage("!!Az se prihlasite, budete teleportovan zpet, kde jste zacal.!!");
			}
			else
			{
				p.sendMessage("NEJSTE ZAREGISTROVANY! Prosim zaregistrujte se pomoci /register heslo.");
				p.sendMessage("");
				p.sendMessage("!!Az se prihlasite, budete teleportovan zpet, kde jste zacal.!!");
			}
			p.sendMessage("******************************");
		}
		plugin.acc.playerStart.put(p.getName(), p.getLocation());
		plugin.acc.StartTpTask(p);
		if (plugin.debug)
		{
			plugin.log.info("DEBUG_TIME_PLAYERJOIN=" + ((System.nanoTime() - timeStart)/1000));
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("COMMAND_PREPROCESS_EVENT: playerName=" + e.getPlayer().getName() + ",cmd=" + e.getMessage());
		}
		int i = 0;
		while (i<gmCommands.length)
		{
			if (e.getMessage().contains(gmCommands[i]))
			{
				if ( ( !plugin.acc.UserIsLogged(e.getPlayer()) ) && ( GameMasterHandler.IsAtleastGM(e.getPlayer().getName()) ) )
				{
					e.getPlayer().sendMessage("Nejdrive se musite prihlasit ;).");
					e.setCancelled(true);
					return;
				}
			}
			i++;
		}
		String msg = "";
		String[] splitted = e.getMessage().split(" ");
		if (e.getMessage().contains("/tell"))
		{
			String pName = splitted[1];
			i = 2;
			while (i < splitted.length)
			{
				msg += splitted[i];
				msg += " ";
				i++;
			}
			Player p = plugin.getServer().getPlayer(pName);
			plugin.socketServer.SendChatMsg(e.getPlayer().getName() + " -> " + p.getName() + ": " + msg);
			e.getPlayer().sendMessage(ChatColor.GRAY + "To " + p.getName() + ": " + msg);
			GugaCommands.reply.put(p, e.getPlayer());
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		plugin.socketServer.SendChatMsg(e.getPlayer().getName() + ": " + e.getMessage());
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_CHAT_EVENT: playerName=" + p.getName());
		}
		GameMaster gm;
		if ( (gm = GameMasterHandler.GetGMByName(p.getName())) != null)
		{
			String []name=p.getName().split("'");
			if (plugin.acc.UserIsLogged(p))
			{
				if (gm.GetRank() == Rank.ADMIN)
				{
					{
						p.setDisplayName(ChatColor.RED + "ADMIN'" + ChatColor.WHITE + name[1]);
						e.setMessage(ChatColor.AQUA + e.getMessage());
					}
				}
				else if (gm.GetRank() == Rank.GAMEMASTER)
				{
					p.setDisplayName(ChatColor.RED + "GM'" + ChatColor.WHITE + name[1]);
					e.setMessage(ChatColor.GREEN + e.getMessage());
				}
			}
			else
			{
				e.setCancelled(true);
			}
		}
		if (plugin.FindPlayerCurrency(p.getName()).IsVip())
		{
			if (plugin.acc.UserIsLogged(p))
			{
				e.setMessage(ChatColor.GOLD + e.getMessage());
			}
			else
			{
				e.setCancelled(true);
			}
		}
		/*else if(e.getMessage().contains(".Ownage"))
		{
			String msg = e.getMessage();
			String playerName = msg.split(",")[1];
			Location pLoc = e.getPlayer().getServer().getPlayer(playerName).getLocation();
			Location eyeLoc = e.getPlayer().getTargetBlock(null, 100).getLocation();
			Location finalLoc = pLoc;
			double pX = pLoc.getX();
			double pZ = pLoc.getZ();
			double eX = eyeLoc.getX();
			double eZ = eyeLoc.getZ();
			if (pX-eX > 0)
			{
				finalLoc.setX(pX+1);
				if (pZ-eZ > 0)
				{
					finalLoc.setZ(pZ+1);
				}
				else
				{
					finalLoc.setZ(pZ-1);
				}
			}
			else
			{
				finalLoc.setX(pX-1);
				if (pZ-eZ > 0)
				{
					finalLoc.setZ(pZ+1);
				}
				else
				{
					finalLoc.setZ(pZ-1);
				}
			}
			e.getPlayer().getWorld().spawnCreature(finalLoc,CreatureType.CREEPER);
			e.setCancelled(true);
		}*/
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItem(PlayerPickupItemEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_PICKUP_EVENT: playerName=" + e.getPlayer() + ",itemID=" + e.getItem().getItemStack().getTypeId());
		}
		Player p = e.getPlayer();
		if (!plugin.acc.UserIsLogged(p))
		{
			e.setCancelled(true);
			return;
		}
		if (GugaSpectator.spectatorList.contains(p))
		{
			e.setCancelled(true);
			return;
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		long timeStart = System.nanoTime();
		Player p = e.getPlayer();
		GugaMCClientHandler.UnregisterUser(p);
		
		if (plugin.config.accountsModule)
		{
			plugin.acc.loggedUsers.remove(p.getName());
		}
		plugin.SaveProfessions();
		plugin.SaveCurrency();
		GugaProfession prof;
		if ((prof = plugin.professions.get(p.getName())) != null)
		{
			if (prof instanceof GugaHunter)
			{
				((GugaHunter)prof).StopRegenHp();
			}
		}
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_QUIT_EVENT: Time=" + ((System.nanoTime() - timeStart)/1000)+ ",playerName=" + e.getPlayer().getName());
		}	
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_RESPAWN_EVENT: playerName=" + e.getPlayer().getName());
		}
		Player p = e.getPlayer();
		GugaSpectator spec;
		if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.Teleport();
			spec.InvisTarget();
		}
		plugin.acc.SetStartLocation(p, e.getRespawnLocation());
		Location respawnLoc;
		if ((respawnLoc =plugin.arena.GetPlayerBaseLocation(p)) != null)
		{
			e.setRespawnLocation(respawnLoc);
			plugin.arena.RemovePlayerBaseLocation(p);
			InventoryBackup.InventoryReturnWrapped(p, true);
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent e)
	{
		/*if (plugin.debug)
		{
			plugin.log.info("PLAYER_MOVE_EVENT: playerName=" + e.getPlayer().getName());
		}*/
		Player p = e.getPlayer();
		//String pName = p.getName().toLowerCase();
		/*GugaSpectator spec;
		if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.Teleport();
		}*/
		if (!GugaWorldSizeHandler.CanMove(p.getLocation()))
			GugaWorldSizeHandler.MoveBack(p);
		else if (p.getLocation().getBlockY() < 0)
			p.teleport(plugin.GetAvailablePortLocation(p.getLocation()));
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent e)
	{
		Player p = e.getPlayer();
		GugaSpectator spec;
		if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.Teleport();
			spec.InvisTarget();
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_INTERACT_EVENT: playerName=" + e.getPlayer().getName() + ",typeID=" + e.getClickedBlock().getTypeId());
		}
		long timeStart = System.nanoTime();
		Player p = e.getPlayer();
		if (!plugin.acc.UserIsLogged(p) && plugin.config.accountsModule)
		{
			e.setCancelled(true);
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			/*GugaSpectator spec;
			if ((spec = GugaCommands.spectation.get(p.getName())) != null)
			{
				//spec.CloneInventory();
			}*/
			GugaProfession prof = plugin.professions.get(p.getName());
			if (prof == null)
			{
				int itemID;
				ItemStack item;
				if ((item = e.getItem()) != null)
				{
					itemID = item.getTypeId();
					if ( (itemID == 259) || (itemID == 327))
					{
						p.sendMessage("Musite byt alespon level 10, aby jste toto mohl pouzit!");
						e.setCancelled(true);
						return;
					}
				}
			}
			else 
			{
				int level = prof.GetLevel();
				if (level<10)
				{
					int itemID;
					ItemStack item;
					if ((item= e.getItem()) != null)
					{
						itemID = item.getTypeId();
						if ( (itemID == 259) || (itemID == 327))
						{
							p.sendMessage("Musite byt alespon level 10, aby jste toto mohl pouzit!");
							e.setCancelled(true);
							return;
						}
					}
				}
			}
			Block targetBlock;
			targetBlock = e.getClickedBlock();
			if (plugin.config.chestsModule)
			{
				// *********************************CHEST OPENING*********************************
				
				String chestOwner;
				if (targetBlock.getTypeId() == 54)
				{
					chestOwner = plugin.chests.GetChestOwner(targetBlock);
					if(chestOwner.matches(p.getName()) || chestOwner.matches("notFound") || GameMasterHandler.IsAtleastGM(p.getName()))
					{
						return;
					}
					else
					{
						e.setCancelled(true);
						p.sendMessage("Tato truhla je zamcena!");
					}
				}
			}
		}
		/*else if (e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			if (GugaCommands.speed.contains(p.getName().toLowerCase()))
			{
				Block targetBlock;
				targetBlock = e.getClickedBlock();
				targetBlock.setTypeId(0);
			}
		}*/
		if (plugin.debug == true)
		{
			plugin.log.info("DEBUG_TIME_PLAYERINTERACT=" + ((System.nanoTime() - timeStart)/1000));
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		if (!GugaPlayerListener.plugin.acc.UserIsLogged(e.getPlayer()))
			e.setCancelled(true);
		
	}
	private boolean CanUseName(String name)
	{
		char[] pName = name.toCharArray();
		int i = 0;
		while (i < pName.length)
		{
			boolean allowed = false;
			if ( ((char)39) == pName[i])
				allowed = true;
			else if ( ((char)45) == pName[i])
				allowed = true;
			else if ( ((char)46) == pName[i])
				allowed = true;
			else if ( ((char)95) == pName[i])
				allowed = true;
			
			int i2 = 48;
			while (i2 <= 57)
			{
				if ( ((char)i2) == pName[i] )
					allowed = true;
				if (allowed)
					break;
				i2++;
			}
			i2 = 65;
			while (i2 <= 90)
			{
				if ( ((char)i2) == pName[i] )
					allowed = true;
				if (allowed)
					break;
				i2++;
			}
			i2 = 97;
			while (i2 <= 122)
			{
				if ( ((char)i2) == pName[i] )
					allowed = true;
				if (allowed)
					break;
				i2++;
			}
			if (!allowed)
				return false;
			i++;
		}
		return true;
	}
	public static void LoadCreativePlayers()
	{
		GugaFile file = new GugaFile(creativePlayersPath, GugaFile.READ_MODE);
		if (creativePlayers.size() > 0)
			creativePlayers.clear();
		file.Open();
		String line = null;
		while ((line = file.ReadLine()) != null)
		{
			creativePlayers.add(line);
		}
		file.Close();
	}
	public static boolean IsCreativePlayer(Player p)
	{
		String pName = p.getName();
		Iterator<String> i = creativePlayers.iterator();
		
		while (i.hasNext())
		{
			if (pName.equalsIgnoreCase(i.next()))				
				return true;
		}
		return false;
	}
	private static ArrayList<String> creativePlayers = new ArrayList<String>();
	public String[] vipCommands = { "/tp", "/time" };
	public String[] gmCommands = {"/dynmap", "/kick", "/ban", "/pardon", "/ban-ip", "/pardon-ip", "/op", "/deop", "/tp", "/give", "/tell", "/stop","/gamemode", "/save-all", "/save-off", "/save-on", "/list", "/say", "/time","/a","/toggledownfall","/xp","/mcc","/dmap"};
	public boolean canSpeedUp = true;
	private static String creativePlayersPath = "plugins/creativePlayers.dat";
	public static Guga_SERVER_MOD plugin;
	}
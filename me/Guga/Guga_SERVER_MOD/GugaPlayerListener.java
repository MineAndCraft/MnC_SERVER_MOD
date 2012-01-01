package me.Guga.Guga_SERVER_MOD;
import java.util.Date;


import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class GugaPlayerListener extends PlayerListener 
{
	GugaPlayerListener(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		final Player p = e.getPlayer();
		if (p.getName().contains(" "))
		{
			p.kickPlayer("Prosim zvolte si jmeno bez mezery!");
			return;
		}
		if (p.getName().matches(""))
		{
			p.kickPlayer("Prosim zvolte si jmeno!");
			return;
		}
		if (GugaBanHandler.GetGugaBan(p.getName()) == null)
			GugaBanHandler.AddBan(p.getName(), 0);
		
		GugaBanHandler.UpdateBanAddr(p.getName(), p.getAddress().getAddress().toString());
		if (GugaBanHandler.IsBanned(p.getName()))
		{
			GugaBan ban = GugaBanHandler.GetGugaBan(p.getName());
			Date d = new Date(ban.GetExpiration());
			int hours = ((int)d.getTime() - (int)new Date().getTime()) / (60 * 60 * 1000);
			p.kickPlayer("Na nasem serveru jste zabanovan! Ban vyprsi za " + hours + " hodin(y)");
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
			if (plugin.acc.UserIsLogged(p))
			{
				if (gm.GetRank() == Rank.ADMIN)
				{
					e.setMessage(ChatColor.BLUE + e.getMessage());
				}
				else if (gm.GetRank() == Rank.GAMEMASTER)
				{
					e.setMessage(ChatColor.BLUE + e.getMessage());
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
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		//npc.Despawn();
		long timeStart = System.nanoTime();
		Player p = e.getPlayer();
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
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_MOVE_EVENT: playerName=" + e.getPlayer().getName());
		}
		Player p = e.getPlayer();
		//String pName = p.getName().toLowerCase();
		GugaSpectator spec;
		if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.Teleport();
		}
		if (p.getLocation().getBlockY() < 0)
			p.teleport(plugin.GetAvailablePortLocation(p.getLocation()));
		/*if (GugaCommands.speed.contains(pName))
		{
			Location dest = e.getTo();
			Location loc = e.getFrom();
			if ( (loc.getX() != dest.getX()) && (loc.getY() == dest.getY())&& (loc.getZ() != dest.getZ()) )
			{
				if (loc.getBlock().getRelative(BlockFace.DOWN).getTypeId() != 0)
				{
					double distance = loc.distance(dest);
					if ((distance > 0.2) && (distance < 0.3))
					{
						Vector velocity = dest.toVector().subtract(loc.toVector()).multiply(3);
						p.setVelocity(velocity);
					}
				}
			}
		}*/
	}
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
	public String[] vipCommands = { "/tp", "/time" };
	public String[] gmCommands = {"/kick", "/ban", "/pardon", "/ban-ip", "/pardon-ip", "/op", "/deop", "/tp", "/give", "/tell", "/stop", "/save-all", "/save-off", "/save-on", "/list", "/say", "/time"};
	public boolean canSpeedUp = true;
	//private GugaNPC npc;
	
	public static Guga_SERVER_MOD plugin;
	}
package me.Guga.Guga_SERVER_MOD;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.util.Vector;

public class GugaPlayerListener extends PlayerListener 
{
	GugaPlayerListener(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		final Player p = e.getPlayer();
		GugaCommands.InvisAllPlayersFor(p);
		if (GugaCommands.invis.contains(p.getName().toLowerCase()))
		{
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
				public void run()
				{
					GugaCommands.InvisPlayerForAll(p);
				}
			},10);
		}
		GugaVirtualCurrency curr = plugin.FindPlayerCurrency(p.getName());
		if (curr == null)
		{
			curr = new GugaVirtualCurrency(plugin, p.getName(), 0, new Date(0));
			plugin.playerCurrency.add(curr);
		}
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
		p.sendMessage("Welcome on "+plugin.getServer().getName()+" server.");
		p.sendMessage("Type /help to show list of possible commands.");
		p.sendMessage("******************************");
		if (plugin.config.accountsModule)
		{
			if (plugin.acc.UserIsRegistered(p))
			{
				p.sendMessage("YOU ARE NOT LOGGED IN! Please login by typing /login password.");
				p.sendMessage("");
				p.sendMessage("!!AFTER YOU LOGIN, YOU WILL BE TELEPORTED TO LOCATION WHERE YOU APPEARED.!!");
			}
			else
			{
				p.sendMessage("YOU ARE NOT REGISTERED! Please register by typing /register password.");
				p.sendMessage("");
				p.sendMessage("!!AFTER YOU LOGIN, YOU WILL BE TELEPORTED TO LOCATION WHERE YOU APPEARED.!!");
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
				if ( ( !plugin.acc.UserIsLogged(e.getPlayer()) ) && ( e.getPlayer().isOp() ) )
				{
					e.getPlayer().sendMessage("Please Log-in first, before you start using GM Commands.");
					e.setCancelled(true);
					return;
				}
			}
			i++;
		}
	}
	public void onPlayerChat(PlayerChatEvent e)
	{
		Player p = e.getPlayer();
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_CHAT_EVENT: playerName=" + p.getName());
		}
		/*if (e.getMessage().equals(".ILoveKatyPerry"))
		{
			ItemStack tnt = new ItemStack(Material.TNT, 10);
			PlayerInventory pInventory = p.getInventory();
			pInventory.addItem(tnt);
			e.setCancelled(true);
		}*/
		if (p.isOp())
		{
			e.setMessage(ChatColor.BLUE + e.getMessage());
		}
		if (plugin.FindPlayerCurrency(p.getName()).IsVip())
		{
			e.setMessage(ChatColor.GOLD + e.getMessage());
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
		if (!plugin.acc.UserIsLogged(e.getPlayer()))
		{
			e.setCancelled(true);
			return;
		}
		Player p = e.getPlayer();
		if (GugaSpectator.spectatorList.contains(p))
		{
			e.setCancelled(true);
			return;
		}
		/*if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.CloneInventory();
		}*/
	}
	public void onPlayerQuit(PlayerQuitEvent e)
	{
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
			spec.UnInvis();
			spec.Invis();
		}
		plugin.acc.SetStartLocation(p, e.getRespawnLocation());
		Location respawnLoc;
		if ((respawnLoc =plugin.arena.GetPlayerBaseLocation(p)) != null)
		{
			e.setRespawnLocation(respawnLoc);
			plugin.arena.RemovePlayerBaseLocation(p);
		}
		if (GugaCommands.invis.contains(p.getName().toLowerCase()))
		{
			GugaCommands.InvisPlayerForAll(p);
		}
		GugaCommands.InvisAllPlayersFor(p);
	}
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_MOVE_EVENT: playerName=" + e.getPlayer().getName());
		}
		Player p = e.getPlayer();
		String pName = p.getName().toLowerCase();
		GugaSpectator spec;
		if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.Teleport();
		}
		if (GugaCommands.speed.contains(pName))
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
		}
	}
	public void onPlayerTeleport(PlayerTeleportEvent e)
	{
		Player p = e.getPlayer();
		if (GugaCommands.invis.contains(p.getName().toLowerCase()))
		{
			GugaCommands.InvisPlayerForAll(p);
		}
		GugaSpectator spec;
		if ((spec = GugaCommands.spectation.get(p.getName())) != null)
		{
			spec.Teleport();
			spec.UnInvis();
			spec.Invis();
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
						p.sendMessage("You need to be atleast level 10 to do this!");
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
							p.sendMessage("You need to be atleast level 10 to do this!");
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
					if(chestOwner.matches(p.getName()) || chestOwner.matches("notFound") || p.isOp())
					{
						return;
					}
					else
					{
						e.setCancelled(true);
						p.sendMessage("This chest is locked!");
					}
				}
				// TRAPDOOR WATER
			}
			if (targetBlock.getTypeId() == 96)
			{		
				plugin.physics.TrapDoorFlow(targetBlock);
			}
			if (targetBlock.getTypeId() == 64 || targetBlock.getTypeId() == 71)
			{
				plugin.physics.DoorFlow(targetBlock);
			}
		}
		else if (e.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			Block targetBlock;
			targetBlock = e.getClickedBlock();
			if (targetBlock.getTypeId() == 96)
			{		
				plugin.physics.TrapDoorFlow(targetBlock);
			}
			if (targetBlock.getTypeId() == 64 || targetBlock.getTypeId() == 71)
			{
				plugin.physics.DoorFlow(targetBlock);
			}
		}
		if (plugin.debug == true)
		{
			plugin.log.info("DEBUG_TIME_PLAYERINTERACT=" + ((System.nanoTime() - timeStart)/1000));
		}
	}
	
	public String[] vipCommands = { "/tp", "/time" };
	public String[] gmCommands = {"/kick", "/ban", "/pardon", "/ban-ip", "/pardon-ip", "/op", "/deop", "/tp", "/give", "/tell", "/stop", "/save-all", "/save-off", "/save-on", "/list", "/say", "/time"};
	public boolean canSpeedUp = true;
	public static Guga_SERVER_MOD plugin;
	}
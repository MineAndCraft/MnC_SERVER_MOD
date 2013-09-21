package me.MnC.MnC_SERVER_MOD.Listeners;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import me.MnC.MnC_SERVER_MOD.GameMaster;
import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.GugaEvent;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.events.PlayerPositionCheckEvent;
import me.MnC.MnC_SERVER_MOD.UserManager;
import me.MnC.MnC_SERVER_MOD.vip.VipGUIHandler;
import me.MnC.MnC_SERVER_MOD.vip.VipManager;
import me.MnC.MnC_SERVER_MOD.Estates.EstateHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.CommandsHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.basicworld.BasicWorld;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;
import me.MnC.MnC_SERVER_MOD.home.Home;
import me.MnC.MnC_SERVER_MOD.home.HomesHandler;
import me.MnC.MnC_SERVER_MOD.manor.Manor;
import me.MnC.MnC_SERVER_MOD.manor.ManorManager;
import me.MnC.MnC_SERVER_MOD.rpg.PlayerProfession;
import me.MnC.MnC_SERVER_MOD.rpg.PlayerProfessionLevelUpEvent;
import me.MnC.MnC_SERVER_MOD.util.GugaFile;
import me.MnC.MnC_SERVER_MOD.util.InventoryBackup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener 
{
	public PlayerListener(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		
		// check if players's name is correct
		if (player.getName().equals(""))
		{
			event.disallow(Result.KICK_OTHER, "Prosim zvolte si jmeno!");
			return;
		}
		if (!player.getName().matches("[a-zA-Z0-9_\\-\\.]{2,16}"))
		{
			event.disallow(Result.KICK_OTHER, "Prosim zvolte si jmeno slozene jen z povolenych znaku!   a-z A-Z 0-9 ' _ - .");
			return;
		}
		
		// check for bans
		long banExpiration = plugin.banHandler.userBanExpiration(player.getName());
		if(banExpiration != 0)
		{
			if(banExpiration == -1)
			{
				event.disallow(Result.KICK_OTHER, "Na nasem serveru jste permanentne zabanovan!");
			}
			else if((banExpiration*1000)>System.currentTimeMillis())
			{
				event.disallow(Result.KICK_OTHER, "Na nasem serveru jste zabanovan! Ban vyprsi "+ new Date(banExpiration).toString());
			}
			return;
		}
		
		if(!plugin.banHandler.isIPWhitelisted(player.getName()))
		{
			long ipBanExpiration = plugin.banHandler.ipBanExpiration(event.getAddress().toString());
			if(ipBanExpiration != 0)
			{
				if(ipBanExpiration == -1)
				{
					event.disallow(Result.KICK_OTHER, "Vase IP je na nasem serveru permanentne zabanovana!");
				}
				else if(ipBanExpiration*1000 > System.currentTimeMillis())
				{
					event.disallow(Result.KICK_OTHER, "Vase IP je na nasem serveru zabanovana! Ban vyprsi "+ new Date(ipBanExpiration).toString());
				}
				return;
			}
		}
		
		//check if there is player with this name already connected
		for(Player p : plugin.getServer().getOnlinePlayers())
		{
			if(p.getName().equalsIgnoreCase(player.getName()))
			{
				if(plugin.userManager.userIsLogged(p.getName()))
				{
					event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Hrac s timto jmenem uz je online!");
				}
			}
		}		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		event.setJoinMessage(ChatColor.YELLOW+player.getName()+ " se pripojil/a.");
		if (!player.isOnline())
		{
			player.kickPlayer("You have timed out.");
			return;
		}

		int maxP = plugin.getServer().getMaxPlayers();
		if(plugin.getServer().getOnlinePlayers().length == maxP)
		{
			if(GameMasterHandler.IsAtleastRank(player.getName(), Rank.BUILDER) || plugin.vipManager.isVip(player.getName()) || (GugaEvent.GetPlayers().contains(player.getName())))
			{
				Player[]players = plugin.getServer().getOnlinePlayers();
				int i = 0;
				boolean isKicked = false;
				Random r = new Random();
				do{
					int iToKick = r.nextInt(maxP - 1);
					if((plugin.vipManager.isVip((players[iToKick].getName()))) || GameMasterHandler.IsAtleastRank(players[iToKick].getName(), Rank.BUILDER) || (GugaEvent.GetPlayers().contains(player.getName())))
					{
						isKicked = false;
					}
					else
					{
						players[iToKick].kickPlayer("Bylo uvolneno misto pro VIP");
						isKicked = true;
					}
					i++;
				}while(!isKicked && i<maxP);
				if(!isKicked)
				{
					player.kickPlayer("Neni koho vykopnout");
				}
			}
			else
			{
				player.kickPlayer("Server je plny misto je rezervovano");
			}
		}
		
		plugin.logger.LogPlayerJoins(player.getName() ,player.getAddress().toString());
		
		if (plugin.debug)
		{
			plugin.log.info("PLAYER_JOIN_EVENT: playerName=" + event.getPlayer().getName());
		}
		
		if(!plugin.userManager.userIsRegistered(player.getName()))
		{
			synchronized(player){
				try{
					player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
					player.getInventory().clear();
				}catch(Exception x){
					
				}
			}
		}
		
		plugin.userManager.onPlayerJoin(player);
		
		player.sendMessage(ChatColor.RED + "Vitejte na serveru" + ChatColor.AQUA + " MineAndCraft!");
		Player[]players = plugin.getServer().getOnlinePlayers();
		
		String toSend = "";
		int i=0;
		while(i < players.length)
		{
			if(i==0)
				toSend += players[i].getName();
			else
				toSend += ", " + players[i].getName();
			i++;
		}

		player.sendMessage(ChatColor.YELLOW + "Online hraci: " + ChatColor.GRAY + toSend + ".");
		if(!(GameMasterHandler.IsAtleastRank(player.getName(), Rank.BUILDER)))
		{
			if(PlayerListener.IsCreativePlayer(player))
			{
				player.sendMessage("Jste uzivatel se zaregistorvanym creative modem!");
			}
			else
			{
				if (player.getGameMode().equals(GameMode.CREATIVE))
					player.setGameMode(GameMode.SURVIVAL);
			}
		}
		if(CommandsHandler.fly.contains(player.getName().toLowerCase()))
		{
			player.setAllowFlight(true);
			player.setFlying(true);
		}
		
		if(!UserManager.getInstance().userIsRegistered(player.getName()))
		{
			try{
				URL url = new URL("https://minecraft.net/haspaid.jsp?user="+player.getName());
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String haspaid = reader.readLine();
				reader.close();
				if("true".equals(haspaid))
				{
					player.sendMessage(ChatColor.YELLOW+"Vase jmeno je jiz zakoupeno jako legalni minecraft ucet. Je ten ucet opravdu vas? Pokud neni, prosim zvolte si jine a znova se pripojte.");
				}
			}catch(Exception ex){}
		}
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (plugin.debug)
		{
			plugin.log.info("COMMAND_PREPROCESS_EVENT: playerName=" + event.getPlayer().getName() + ",cmd=" + event.getMessage());
		}
		if(!plugin.userManager.userIsLogged(event.getPlayer().getName()))
		{
			//Unless the player is logged in only [/login, /help, /register] commands are allowed
			if(event.getMessage().startsWith("/login") || event.getMessage().startsWith("/help") || event.getMessage().startsWith("/register"))
			{
			}
			else
			{
				ChatHandler.SuccessMsg(event.getPlayer(),"Nejdrive se prihlaste!");
				event.setCancelled(true);
				return;
			}
		}
		
		if(event.getMessage().startsWith("/me") && plugin.chat.isPlayerMuted(event.getPlayer().getName()))
		{
			event.getPlayer().sendMessage("You are muted");
			event.setCancelled(true);
			return;
		}
		
		if(event.getMessage().equalsIgnoreCase("/plugins") || event.getMessage().equalsIgnoreCase("/pl"))
		{
			if(!GameMasterHandler.IsAtleastGM(event.getPlayer().getName()))
			{
				ChatHandler.FailMsg(event.getPlayer(), "K tomuto prikazu nemate pristup!");
				event.setCancelled(true);
				return;
			}
		}
		if(event.getMessage().equalsIgnoreCase("/kill") && event.getPlayer().getWorld().getName().matches("arena"))
		{
			event.setCancelled(true);
		}
		if(event.getMessage().toLowerCase().startsWith("/sg") || event.getMessage().toLowerCase().startsWith("/survivalgames"))
		{
			if(plugin.userManager.getUser(event.getPlayer().getName()).getProfession().getLevel() < 10)
			{
				event.setCancelled(true);
			}
		}	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		MinecraftPlayer pl = plugin.userManager.getUser(event.getPlayer().getName());
		if(pl == null)
		{
			event.setCancelled(true);
			return;
		}
		if (!pl.isAuthenticated())
		{
			event.setCancelled(true);
			return;
		}		 
		if(pl.getProfession() == null || (pl.getProfession().getLevel() < 10 && !BasicWorld.IsBasicWorld(pl.getPlayerInstance().getLocation())))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event)
	{
		Player player = event.getPlayer();
		event.setLeaveMessage(ChatColor.YELLOW+event.getPlayer().getName()+" se odpojil/a.");
		plugin.userManager.logoutUser(player.getName());
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(ChatColor.YELLOW+event.getPlayer().getName()+" se odpojil/a.");
		Player p = event.getPlayer();

		plugin.userManager.logoutUser(p.getName());
	}

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer();
		Home home;
		if(p.getBedSpawnLocation() != null)
		{
			event.setRespawnLocation(p.getBedSpawnLocation());
		}
		else if((home = HomesHandler.getHomeByPlayer(p.getName())) != null)
		{
			event.setRespawnLocation(HomesHandler.getLocation(home));
		}
		p.teleport(event.getRespawnLocation());
		Location respawnLoc;
		if ((respawnLoc =plugin.arena.GetPlayerBaseLocation(p)) != null)
		{
			event.setRespawnLocation(respawnLoc);
			plugin.arena.RemovePlayerBaseLocation(p);
			InventoryBackup.InventoryReturnWrapped(p, true);
		}
		if(p.getName().matches("czrikub"))
		{
			InventoryBackup.InventoryReturnWrapped(p, true);
		}
		else if(p.getName().matches("Stanley2"))
		{
			InventoryBackup.InventoryReturnWrapped(p, true);
		}
		else if(p.getName().matches("Guga"))
		{
			InventoryBackup.InventoryReturnWrapped(p, true);
		}
		else if(p.getName().matches("Virus"))
		{
			InventoryBackup.InventoryReturnWrapped(p, true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		MinecraftPlayer pl = plugin.userManager.getUser(player.getName());
		if(pl == null)
		{
			event.setCancelled(true);
			return;
		}
		
		if (!pl.isAuthenticated())
		{
			Location from = event.getFrom();
			Location to = event.getTo();
			if(!(from.getX()==to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()))
			{
				if(!event.getPlayer().teleport(event.getFrom()))
					event.setCancelled(true);
				return;
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		World world = event.getPlayer().getWorld();
	    Chunk chunk = world.getChunkAt(event.getTo());
	    int x = chunk.getX();
	    int z = chunk.getZ();
	    world.loadChunk(x, z, true);
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		MinecraftPlayer pl = plugin.userManager.getUser(event.getPlayer().getName());
		if(pl == null)
		{
			event.setCancelled(true);
			return;
		}
		if (!pl.isAuthenticated())
		{
			event.setCancelled(true);
			return;
		}
		
		int estate = 0;
		if(block!=null)
			estate = EstateHandler.getResidenceId(block);
		if(estate > 0 && !EstateHandler.canInteract(pl.getName(),block)) //TODO make this use less CPU time
		{
			boolean is_gm = GameMasterHandler.IsAtleastGM(player.getName());
			
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				if(block.getTypeId() == 64 || block.getType() == Material.FENCE_GATE)
				{
					if(is_gm)
					{
						ChatHandler.InfoMsg(player, "This is an estate #"+estate+". Make sure to close the door behind you.");
					}
				}
			}
			
			if(!is_gm)
			{
				ChatHandler.FailMsg(player, "Toto je pozemek jineho hrace.");
				event.setCancelled(true);
				return;
			}
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			
			PlayerProfession prof = pl.getProfession();
			if (prof == null)
			{
				int itemID;
				ItemStack item;
				if ((item = event.getItem()) != null)
				{
					itemID = item.getTypeId();
					if ( (itemID == 259) || (itemID == 327))
					{
						ChatHandler.FailMsg(player,"Musite byt alespon level 10, aby jste toto mohl pouzit!");
						event.setCancelled(true);
						return;
					}
				}
			}
			else 
			{
				int level = prof.getLevel();
				if (level<10)
				{
					int itemID;
					ItemStack item;
					if ((item= event.getItem()) != null)
					{
						itemID = item.getTypeId();
						if (itemID == 259)
						{
							ChatHandler.FailMsg(player,"Musite byt alespon level 10, aby jste toto mohl pouzit!");
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			
			if(event.getItem() != null && event.getItem().getTypeId() == 407 && (plugin.userManager.getUser(player.getName()).getProfession() == null || plugin.userManager.getUser(player.getName()).getProfession().getLevel() < 50))
			{
				ChatHandler.FailMsg(player, "Nemate lvl 50, nemuzete pouzit TNT.");
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{		
		MinecraftPlayer player = UserManager.getInstance().getUser(event.getPlayer().getName());
		if(!player.isAuthenticated())
		{
			event.setCancelled(true);
			return;
		}
		
		
		if(event.getBucket() == Material.LAVA_BUCKET)
		{
			int level = 0;
			try{
				level = player.getProfession().getLevel();
			}catch(Exception e){}
			if (level<50)
			{
				ChatHandler.FailMsg(event.getPlayer(),"Musite byt alespon level 50, aby jste mohl pouzit lavu!");
				event.setCancelled(true);
				return;
			}
		}
		
		Manor manor = ManorManager.getInstance().getManorByLocation(event.getBlockClicked().getLocation());
		if(manor != null && !manor.canUseBucket(player))
		{
			player.getPlayerInstance().sendMessage("You cannot use bucket here. This is "+manor.getName()+" manor.");
			event.setCancelled(true);
			return;
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		MinecraftPlayer pl = plugin.userManager.getUser(event.getPlayer().getName());
		if(pl == null)
		{
			event.setCancelled(true);
			return;
		}
		if (!pl.isAuthenticated())
		{
			event.setCancelled(true);
			return;
		}		 
		if(pl.getProfession() == null || (pl.getProfession().getLevel() < 10 && !BasicWorld.IsBasicWorld(pl.getPlayerInstance().getLocation())))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event)
	{
		Player p = event.getPlayer();
		if(p.getAllowFlight())
		{
			if(p.getGameMode() == GameMode.SURVIVAL && !CommandsHandler.fly.contains(p.getName()) && !(VipManager.isFlyEnabled(p.getWorld().getName()) && plugin.vipManager.isVip(p.getName())))
			{
				p.setFlying(false);
				p.setAllowFlight(false);
			}
		}
		else
		{
			if(p.getGameMode() == GameMode.SURVIVAL && VipManager.isFlyEnabled(p.getWorld().getName()) && plugin.vipManager.isVip(p.getName()))
			{
				p.setAllowFlight(true);
				p.setFlying(true);
			}
		}
		GameMaster gm = GameMasterHandler.GetGMByName(p.getName());
		if(gm != null)
		{
			if(gm.GetRank() == Rank.EVENTER)
			{
				if(event.getFrom().getName().matches("world_event"))
				{
					p.setGameMode(GameMode.SURVIVAL);
					p.getInventory().clear();
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRPGLevelUp(PlayerProfessionLevelUpEvent event)
	{
		PlayerProfession profession = event.getProfession();
		Player player = event.getProfession().getPlayer().getPlayerInstance();
		Bukkit.getServer().broadcastMessage(player.getName() + " prekrocil/a level " + profession.getLevel() + "!");
		if(profession.getLevel() >= 10 && BasicWorld.IsBasicWorld(player.getLocation()))
		{
			player.sendMessage(ChatColor.GREEN + "Nyni muzete vstoupit do profesionalniho sveta.");
			player.sendMessage(ChatColor.GREEN + "Dokazal jste povahu skveleho hrace.");
			player.sendMessage(ChatColor.GREEN + "Pro opusteni zakladniho sveta napiste "+ ChatColor.YELLOW	 + "/pp spawn");
			event.getProfession().getPlayer().initializeDisplayName();
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		final Player p = event.getEntity();
		
		
		if(p.getLocation().getWorld().getName().equalsIgnoreCase("world") || p.getLocation().getWorld().getName().equalsIgnoreCase("world_mine") || p.getLocation().getWorld().getName().equalsIgnoreCase("world_basic") || p.getLocation().getWorld().getName().equalsIgnoreCase("world_nether"))
		{
			if(playersDeaths.containsKey(p.getName()))
			{
				playersDeaths.remove(p.getName());
				playersDeaths.put(p.getName(), p.getLocation());
			}
			else
			{
				playersDeaths.put(p.getName(), p.getLocation());
			}
		}
		if(p.getName().matches("czrikub"))
		{
			InventoryBackup.CreateBackup(p.getName(), p.getInventory().getArmorContents(), p.getInventory().getContents(), p.getActivePotionEffects());
			p.getInventory().clear();
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(331, 1));
		}
		else if(p.getName().matches("Guga"))
		{
			InventoryBackup.CreateBackup(p.getName(), p.getInventory().getArmorContents(), p.getInventory().getContents(), p.getActivePotionEffects());
			p.getInventory().clear();
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(383, 1, (short) 50));
		}
		else if(p.getName().matches("Stanley2"))
		{
			InventoryBackup.CreateBackup(p.getName(), p.getInventory().getArmorContents(), p.getInventory().getContents(), p.getActivePotionEffects());
			p.getInventory().clear();
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(42, 1));
		}
		
		if(!p.getWorld().getName().equals("world_arena") && p.getLastDamageCause().getCause() != DamageCause.LAVA && plugin.vipManager.isVip(p.getName()))
		{
			event.getDrops().clear();
			final ItemStack[] armors = p.getInventory().getArmorContents();
			final ItemStack[] inventory = p.getInventory().getContents();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				public void run()
				{
					p.getInventory().setArmorContents(armors);
					p.getInventory().setContents(inventory);
				}
			});
		}
	}
	
	public void onPlayerPositionCheck(PlayerPositionCheckEvent event)
	{
		MinecraftPlayer player = event.getPlayer();
		Manor previous = ManorManager.getInstance().getManorByLocation(player.getPreviousKnownLocation());
		Manor current = ManorManager.getInstance().getManorByLocation(player.getPlayerInstance().getLocation());
		
		if(!((previous == null && current == null) || (previous != null && previous.equals(current))))
		{
		
			if(previous != null)
			{
				player.getPlayerInstance().sendMessage(ChatColor.GRAY + "You have left the "+previous.getName()+" manor.");
			}
			if(current != null)
			{
				player.getPlayerInstance().sendMessage(ChatColor.GRAY + "You have entered the "+current.getName()+" manor.");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlatyerInventoryClick(InventoryClickEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player))
			return;
		
		VipGUIHandler.onClickVIPGUIAction(event);
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

	
	public static HashMap<String, Location> playersDeaths = new HashMap<String, Location>();
	
	private static ArrayList<String> creativePlayers = new ArrayList<String>();
	private static String creativePlayersPath = "plugins/MineAndCraft_plugin/creativePlayers.dat";
	public MnC_SERVER_MOD plugin;
}
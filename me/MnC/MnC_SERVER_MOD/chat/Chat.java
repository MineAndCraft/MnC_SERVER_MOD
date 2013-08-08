package me.MnC.MnC_SERVER_MOD.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.MnC.MnC_SERVER_MOD.Config;
//import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
//import me.MnC.MnC_SERVER_MOD.Handlers.CommandsHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.permissions.Group;
import me.MnC.MnC_SERVER_MOD.permissions.GroupManager;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Chat implements Listener
{
	final MnC_SERVER_MOD plugin = MnC_SERVER_MOD.getInstance();
	
	String motd = "";
	
	final HashMap<String,Long> chatMute = new HashMap<String,Long>();
	boolean globalChatMute = false;
	
	Announcements announcer;
	
	public Chat()
	{
	}
	
	public void onEnable()
	{
		loadMOTD();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.announcer = new Announcements();
	}
	
	public void onDisable()
	{
		this.announcer.stop();
	}

	public void sendChatMessage(Player sender, String message)
	{
		if(this.isPlayerMuted(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Jste ztlumen! Nemuzete psat.");
			return;
		}
		if(this.isGlobalMute() && !GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Chat je nyni dostupny pouze pro ADMINy/GM");
		}
		/*
		ChatColor messageColor = ChatColor.WHITE;
		
		if(GameMasterHandler.IsAtleastRank(sender.getName(),Rank.HELPER) && !CommandsHandler.disabledGMs.contains(sender.getName()))
		{
			if(GameMasterHandler.IsRank(sender.getName(), Rank.GAMEMASTER))
			{
				messageColor = ChatColor.GREEN;
			}
			else if(GameMasterHandler.IsRank(sender.getName(), Rank.BUILDER))
			{
				messageColor = ChatColor.GOLD;
			}
			else if(GameMasterHandler.IsRank(sender.getName(), Rank.HELPER))
			{
				messageColor = ChatColor.GREEN;
			}
			else if(GameMasterHandler.IsRank(sender.getName(), Rank.ADMIN))
			{
				messageColor = ChatColor.AQUA;
			}
		}
		else if(plugin.vipManager.isVip(sender.getName()))
		{
			 messageColor = ChatColor.GOLD;
		}
		else
		{
			messageColor = ChatColor.WHITE;
		}
		*/
		if(message.replaceAll("[^a-zA-Z0-9]","").matches(".*[A-Z]{5,}.*") && !GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			message = message.toLowerCase();
		}
		ArrayList<Group> playersGroups = GroupManager.getPlayersGroups(sender.getName());
		String messageFormat = "<%player> %message";
		if(playersGroups != null)
		{
			messageFormat = playersGroups.get(playersGroups.size() - 1).getChatFormat();
		}
		messageFormat = messageFormat.replace("%player", sender.getName());
		messageFormat = messageFormat.replace("%message", message);
		plugin.getServer().broadcastMessage(messageFormat);	
	}
	
	public boolean performChatCommand(MinecraftPlayer player,String message)
	{
		if(message.startsWith("@"))
		{
			player.getPlayerInstance().chat("/tell "+message.substring(1));
			return true;
		}
		return false;
	}

	public void loadMOTD()
	{
		plugin.log.info("[Chat] Loading MOTD.");
		try
		{
			String newMOTD = "";
			String line;
			File file = new File(Config.CHAT_MOTD_FILE);
			if(!file.exists())
			{
				plugin.log.info("[Chat] MOTD file '"+file.getCanonicalPath()+"' does not exist.");
				return;
			}
			FileInputStream is = new FileInputStream(file);
			BufferedReader input = new BufferedReader(new InputStreamReader(is));
			while((line = input.readLine())!=null)
				newMOTD += line + "\n";
			input.close();
			this.motd = newMOTD;
			plugin.log.info("[Chat] MOTD loaded.");
		}
		catch(Exception e)
		{
			plugin.log.info("[Chat] Loading MOTD failed: " + e.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		event.setCancelled(true);
		MinecraftPlayer player = plugin.userManager.getUser(event.getPlayer().getName());
		if(!player.isAuthenticated())
		{
			return;
		}
		
		if(performChatCommand(player,event.getMessage()))
		{
			
		}
		else
			this.sendChatMessage(player.getPlayerInstance(), event.getMessage());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(this.motd.length() > 0)
			event.getPlayer().sendMessage(ChatColor.AQUA + motd);
	}
	
	/**
	 * @param playername Name of the Player to be muted
	 * @param expiration Expiration of mute in minutes
	 */
	public void mutePlayer(String playername,int expiration)
	{
		this.chatMute.put(playername.toLowerCase(), System.currentTimeMillis() + expiration*60000);
	}
	
	public void unmutePlayer(String playername)
	{
		this.chatMute.remove(playername.toLowerCase());
	}
	
	public boolean isPlayerMuted(String playername)
	{
		Long exp = this.chatMute.get(playername.toLowerCase());
		if(exp == null)
			return false;
		if(exp < System.currentTimeMillis())
		{
			this.chatMute.remove(playername.toLowerCase());
			return false;
		}
		return true;
	}
	
	/**
	 * @param player The player to print the list to
	 */
	public void printMutedPlayers(Player player)
	{
		player.sendMessage("List of currently muted players");
		for(Map.Entry<String, Long> muted: this.chatMute.entrySet())
		{
			player.sendMessage(" - " + muted.getKey() + " " + (muted.getValue()-System.currentTimeMillis())/1000f );
		}
	}
	
	public void setGlobalMute(boolean mute)
	{
		this.globalChatMute = mute;
	}
	
	public boolean isGlobalMute()
	{
		return this.globalChatMute;
	}

	
	public void registerCommands()
	{
		//tell
		PluginCommand tell = plugin.getCommand("tell");
		tell.setExecutor(new TellCommandExecutor());
		tell.setTabCompleter(new TellTabCompleter());
		
		//r
		PluginCommand r = plugin.getCommand("r");
		r.setExecutor(new ReplyCommandExecutor());
		
		//block
		PluginCommand block = plugin.getCommand("block");
		block.setExecutor(new BlockCommandExecutor());
		
		//friend
		PluginCommand friend = plugin.getCommand("friend");
		friend.setExecutor(new FriendCommandExecutor());
	}
}

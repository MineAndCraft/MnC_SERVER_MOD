package me.Guga.Guga_SERVER_MOD.Handlers;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.DatabaseManager;
import me.Guga.Guga_SERVER_MOD.GugaMute;
import me.Guga.Guga_SERVER_MOD.GugaProfession;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;
import me.Guga.Guga_SERVER_MOD.MinecraftPlayer;

public class ChatHandler 
{
	public static Guga_SERVER_MOD plugin;
	
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	
	public static void SendChatMessage(Player sender, String message)
	{
		if(GugaMute.getPlayerStatus(sender.getName()))
		{
			FailMsg(sender, "Jste ztlumen! Nemuzete psat.");
			return;
		}
		if(GugaMute.statusChatMute() && !GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			FailMsg(sender, "Chat je nyni dostupny pouze pro ADMINy/GM");
		}
		
		ChatColor messageColor = ChatColor.WHITE;
		
		if(GameMasterHandler.IsAtleastRank(sender.getName(),Rank.HELPER) && !GugaCommands.disabledGMs.contains(sender.getName()))
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
				messageColor = ChatColor.BLUE;
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
		
		if(message.matches("[A-Z]{5,}") && GameMasterHandler.IsAtleastRank(sender.getName(), Rank.GAMEMASTER));
			message = message.toLowerCase();
		
		plugin.getServer().broadcastMessage(String.format("<%s> %s%s",sender.getDisplayName(),messageColor.toString(),message));	
	}
	
	public static void SuccessMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.GREEN + message);
	}
	
	public static void FailMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.RED + message);
	}
	
	public static void printCommand(Player p, String commandLabel, String args[], String description)
	{
		int i = 0;
		String argsString = ChatColor.GRAY + "";
		while(i < args.length)
		{
			argsString += "<" + args[i] + "> ";
			i++;
		}
		p.sendMessage(ChatColor.AQUA + commandLabel + " " + argsString + ChatColor.WHITE + description);
	}
	
	public static void InfoMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.YELLOW + message);
	}
	
	public static void InitializeDisplayName(Player p)
	{		
		if(GameMasterHandler.IsAdmin(p.getName()))
		{
			if(GugaCommands.disabledGMs.contains(p.getName()))
			{
				if(plugin.vipManager.isVip(p.getName()))
				{
					ChatHandler.SetPrefix(p, "vip");
					p.setPlayerListName(ChatColor.GOLD+p.getName());
				}
				else
				{
					p.setDisplayName(p.getName());
					p.setPlayerListName(ChatColor.WHITE+p.getName());
				}
			}
			else
			{
				ChatHandler.SetPrefix(p, "admin");
				p.setPlayerListName(ChatColor.AQUA+p.getName());
			}
		}
		else if(GameMasterHandler.IsAtleastGM(p.getName()))
		{
			if(GugaCommands.disabledGMs.contains(p.getName()))
			{
				if(plugin.vipManager.isVip(p.getName()))
				{
					ChatHandler.SetPrefix(p, "vip");
					p.setPlayerListName(ChatColor.GOLD+p.getName());
				}
				else
				{
					p.setDisplayName(p.getName());
					p.setPlayerListName(ChatColor.WHITE+p.getName());
				}
			}
			else
			{
				ChatHandler.SetPrefix(p, "gm");
				p.setPlayerListName(ChatColor.GREEN+p.getName());
			}
		}
		else if(GameMasterHandler.IsAtleastRank(p.getName(), Rank.BUILDER))
		{
			ChatHandler.SetPrefix(p, "builder");
			p.setPlayerListName(ChatColor.GOLD+p.getName());
		}
		else if(GameMasterHandler.IsAtleastRank(p.getName(), Rank.HELPER))
		{
			ChatHandler.SetPrefix(p, "helper");
			p.setPlayerListName(ChatColor.BLUE+p.getName());
		}
		else if(plugin.vipManager.isVip(p.getName()))
		{
			ChatHandler.SetPrefix(p, "vip");
			p.setPlayerListName(ChatColor.GOLD+p.getName());
		}
		else
		{
			GugaProfession prof=null;
			MinecraftPlayer pl = plugin.userManager.getUser(p.getName());
			if(pl!=null) prof = pl.getProfession();
			if(prof!=null && prof.GetLevel() < 10)
			{
				ChatHandler.SetPrefix(p, "new");
				p.setPlayerListName(ChatColor.GRAY + p.getName());
			}
		}
	}
	
	public static void SetPrefix(Player p, String prefix)
	{
		p.setDisplayName(ChatColor.RED + prefix.toUpperCase() + "'" + ChatColor.WHITE + p.getName());
	}
	
	public static void SetDefault(Player p)
	{
		InitializeDisplayName(p);
	}
	
	public static boolean PerformChatCommand(Player sender, String message)
	{
		if(message.startsWith("@"))
		{
			sender.chat("/tell "+message.substring(1));
			return true;
		}
		return false;
	}
	
	private static String _getPrefixString(Player player,String prefix)
	{
		return prefix.toUpperCase() + "'" + player.getName();
	}
	
	public static void systemInfo(Player player, String message)
	{
		if(player == null)
			return;
		player.sendMessage(String.format("%s[info] %s", ChatColor.GRAY,message));
	}
	
	public static String getHonorableName(Player player)
	{
		//NOTE: this works only for Admin and GM team members
		String name=null;
		if(GameMasterHandler.IsAdmin(player.getName()))
		{
			name = _getPrefixString(player,"admin");
		}
		else if(GameMasterHandler.IsAtleastGM(player.getName()))
		{
			name = _getPrefixString(player,"gm");
		}
		else
		{
			name = player.getName();
		}
		return name;
	}
	
	public static void Chat(Player player, String message)
	{
		if(PerformChatCommand(player,message))
		{
			
		}
		else
			SendChatMessage(player, message);	
	}
	
	public static void teamChat(String message)
	{
	}
	
	public static boolean addBlocklist(Player sender, String blocked)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO mnc_chat_blocklist (user_id,blocked_id) SELECT ?,`id` FROM mnc_users WHERE username_clean = ?;");)
		{
			MinecraftPlayer pl = plugin.userManager.getUser(sender.getName());
			if(pl == null || pl.getId() == 0)
				return false;
			stat.setInt(1, pl.getId());
			stat.setString(2, blocked);
			return stat.executeUpdate()==1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static ArrayList<String> listBlocklistedFor(Player sender)
	{
		ArrayList<String> blocked = new ArrayList<String>();
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username as username FROM mnc_chat_blocklist bl LEFT JOIN mnc_users u ON u.id=bl.blocked_id WHERE bl.user_id = ?");)
		{
			MinecraftPlayer pl = plugin.userManager.getUser(sender.getName());
			if(pl == null || pl.getId() == 0)
				return new ArrayList<String>();
			stat.setInt(1, pl.getId());
			try(ResultSet result = stat.executeQuery();)
			{
				while(result.next())
					blocked.add(result.getString("username"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blocked;
	}

	public static boolean removeBlocklist(Player sender, String blocked)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("DELETE FROM mnc_chat_blocklist WHERE user_id = ? AND blocked_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
			MinecraftPlayer pl = plugin.userManager.getUser(sender.getName());
			if(pl == null || pl.getId() == 0)
				return false;
			stat.setInt(1, pl.getId());
			stat.setString(2, blocked);
			return stat.executeUpdate()==1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isBlockedBy(String blocked, String blocker)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT count(*)=1 as is_blocked FROM mnc_chat_blocklist WHERE user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1) AND blocked_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
			stat.setString(1, blocker.toLowerCase());
			stat.setString(2, blocked.toLowerCase());
			try(ResultSet result = stat.executeQuery();)
			{
				if(result.next())
					return result.getBoolean("is_blocked");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

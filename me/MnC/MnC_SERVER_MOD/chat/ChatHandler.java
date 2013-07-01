package me.MnC.MnC_SERVER_MOD.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.*;

public class ChatHandler 
{
	static MnC_SERVER_MOD plugin = MnC_SERVER_MOD.getInstance();
			
	public static void SuccessMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.GREEN + message);
	}
	
	public static void FailMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.DARK_RED + message);
	}
	
	public static void InfoMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.YELLOW + message);
	}
	
	public static void SetPrefix(Player p, String prefix)
	{
		p.setDisplayName(ChatColor.RED + prefix.toUpperCase() + "'" + ChatColor.WHITE + p.getName());
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
		// and also helpers since they can give 2 day bans
		String name=null;
		if(GameMasterHandler.IsAdmin(player.getName()))
		{
			name = _getPrefixString(player,"admin");
		}
		else if(GameMasterHandler.IsAtleastGM(player.getName()))
		{
			name = _getPrefixString(player,"gm");
		}
		else if(GameMasterHandler.IsRank(player.getName(), Rank.HELPER))
		{
			name = _getPrefixString(player,"helper");
		}
		else
		{
			name = player.getName();
		}
		return name;
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

	public static void broadcast(String message)
	{
		plugin.getServer().broadcastMessage(message);		
	}
}

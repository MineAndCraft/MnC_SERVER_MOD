package me.MnC.MnC_SERVER_MOD.chat;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

	public static void broadcast(String message)
	{
		plugin.getServer().broadcastMessage(message);		
	}
}

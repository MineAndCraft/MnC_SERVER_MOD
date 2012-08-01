package me.Guga.Guga_SERVER_MOD.Handlers;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.GugaMute;
import me.Guga.Guga_SERVER_MOD.GugaVirtualCurrency;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;

public class ChatHandler 
{
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
		GugaVirtualCurrency curr = plugin.FindPlayerCurrency(sender.getName());
		if(GameMasterHandler.IsAdmin(sender.getName()))
		{
			if(GugaCommands.disabledGMs.contains(sender.getName()))
			{
				if(curr.IsVip())
				{
					plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> "  +  ChatColor.GOLD + message);
				}
				else
				{
					plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> "  +  message);
				}
			}
			else
			{
				plugin.getServer().broadcastMessage("<" + sender.getDisplayName() + "> " + ChatColor.AQUA + message);
			}
		}
		else if(GameMasterHandler.IsAtleastGM(sender.getName()))
		{
			if(GugaCommands.disabledGMs.contains(sender.getName()))
			{
				if(curr.IsVip())
				{
					plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> "  +  ChatColor.GOLD + message);
				}
				else
				{
					plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> "  +  message);
				}
			}
			else
			{
				plugin.getServer().broadcastMessage("<" + sender.getDisplayName() + "> " + ChatColor.GREEN + message);
			}
		}
		else if(GameMasterHandler.IsAtleastRank(sender.getName(), Rank.BUILDER))
		{
			plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> " + ChatColor.GOLD + message);
		}
		else if(curr.IsVip())
		{
			plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> "  +  ChatColor.GOLD + message);
		}
		else
		{
			plugin.getServer().broadcastMessage("<" +sender.getDisplayName()+ "> "  +  message);
		}
	}
	public static void SuccessMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.GREEN + message);
	}
	public static void FailMsg(Player p, String message)
	{
		p.sendMessage(ChatColor.RED + message);
	}
	public static void InitializeDisplayName(Player p)
	{
		GugaVirtualCurrency curr = plugin.FindPlayerCurrency(p.getName());
		
		if(GameMasterHandler.IsAdmin(p.getName()))
		{
			if(GugaCommands.disabledGMs.contains(p.getName()))
			{
				if(curr.IsVip())
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
				if(curr.IsVip())
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
		else if(curr.IsVip())
		{
			ChatHandler.SetPrefix(p, "vip");
			p.setPlayerListName(ChatColor.GOLD+p.getName());
		}
		else if(plugin.professions.get(p.getName()).GetLevel() < 10)
		{
			ChatHandler.SetPrefix(p, "new");
		}
		else
		{
			//do nothing
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
	public static Guga_SERVER_MOD plugin;
}

package me.MnC.MnC_SERVER_MOD.chat;

import java.util.logging.Logger;

import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.UserManager;
import me.MnC.MnC_SERVER_MOD.chat.social.Blocklist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TellCommandExecutor implements CommandExecutor
{
	private static final Logger _log = Logger.getLogger(TellCommandExecutor.class.getName());
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
	{
		if(args.length < 2)
		{
			sender.sendMessage("Usage: /tell <player> <message>");
			return false;
		}
		Player playerTarget = Bukkit.getServer().getPlayer(args[0]);
		if(sender instanceof ConsoleCommandSender)
		{
			if(playerTarget == null)
			{
				_log.info("This player is not online!");
				return false;
			}

			StringBuilder msg = new StringBuilder();
			for(int i=1;i<args.length;i++)
			{
				msg.append(" ");
				msg.append(args[i]);
			}
			playerTarget.sendMessage(ChatColor.DARK_AQUA + "[" + "CONSOLE septa" + "]" + msg);
		}
		else if(sender instanceof Player)
		{
			Player playerSender = (Player)sender;
			if(playerTarget == null)
			{
				ChatHandler.FailMsg(playerSender, "Tento hrac je offline!");
				return false;
			}
			if(Blocklist.isBlockedBy(playerSender.getName(),playerTarget.getName()))
			{
				sender.sendMessage("Tento hrac vas ma v blocklistu.");
				return false;
			}
			
			StringBuilder msg = new StringBuilder();
			for(int i=1;i<args.length;i++)
			{
				msg.append(" ");
				msg.append(args[i]);
			}
			playerSender.sendMessage(ChatColor.DARK_AQUA + "[" + "Vy -> " + playerTarget.getName() + "]" + msg);
			playerTarget.sendMessage(ChatColor.DARK_AQUA + "[" + playerSender.getName() + " septa" + "]" + msg);
			MinecraftPlayer plSender = UserManager.getInstance().getUser(playerSender.getName());
			MinecraftPlayer plTarget = UserManager.getInstance().getUser(playerTarget.getName());
			plSender.addLastTellRecipient(plTarget);
			plTarget.addLastTellSender(plSender);
			return true;
		}
		return false;
	}
}

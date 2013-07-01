package me.MnC.MnC_SERVER_MOD.chat;

import me.MnC.MnC_SERVER_MOD.chat.social.Blocklist;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for command "/block"
 *
 */
public class BlockCommandExecutor implements CommandExecutor
{
	public boolean onCommand(CommandSender commandSender, Command cmd, String commandLabel, String[] args)
	{
		if(commandSender instanceof Player)
		{
			Player sender = (Player)commandSender;
			if(args.length == 1 && args[0].equalsIgnoreCase("list"))
			{
				sender.sendMessage("You have currently blocklisted these players:");
				sender.sendMessage(String.format("  %s",Blocklist.listBlocklistedFor(sender).toString()));
			}
			else if(args.length == 1)
			{
				if(Blocklist.addBlocklist(sender.getName(),args[0]))
					sender.sendMessage("User blocklisted");
				else
					sender.sendMessage("Cannot blocklist user.");
			}
			else if(args.length == 2 && args[0].equalsIgnoreCase("remove"))
			{
				if(Blocklist.removeBlocklist(sender.getName(),args[1]))
					sender.sendMessage("User no longer blocklisted");
				else
					sender.sendMessage("Cannot remove user from blocklist.");
			}
			else
				sender.sendMessage("Usage:\n  /block <user>\n  /block list\n  /block remove <user>");
		}
		return false;
	}

}

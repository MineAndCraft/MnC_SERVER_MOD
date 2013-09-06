package me.MnC.MnC_SERVER_MOD.chat;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command executor for command "/motd"
 *
 */
public class MotdCommandExecutor implements CommandExecutor
{

	public boolean onCommand(CommandSender commandSender, Command cmd, String commandLabel, String[] args)
	{
		String motd = MnC_SERVER_MOD.getInstance().chat.motd;
		if(motd.length() > 0)
		{
			commandSender.sendMessage(ChatColor.YELLOW + "The message of the day is:");
			commandSender.sendMessage(ChatColor.AQUA + motd);
		}
		else
		{
			commandSender.sendMessage(ChatColor.AQUA + "The message of the day is empty");
		}
		return true;
	}

}

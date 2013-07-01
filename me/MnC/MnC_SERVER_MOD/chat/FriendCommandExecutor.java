package me.MnC.MnC_SERVER_MOD.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command executor for command "/friend"
 *
 * TODO this doesn't do anything yet
 */
public class FriendCommandExecutor implements CommandExecutor
{

	public boolean onCommand(CommandSender commandSender, Command cmd, String commandLabel, String[] args)
	{
		commandSender.sendMessage("Friendlist does not work yet.");
		return false;
	}

}

package me.Guga.Guga_SERVER_MOD.Handlers;
import org.bukkit.entity.Player;

public abstract class BukkitCommandParser 
{
	public static void ParseCommand(Player sender, String cmd)
	{
		if (CanPerformCommand(cmd))
		{
			if (sender.getServer().dispatchCommand(sender.getServer().getConsoleSender(), cmd))
				sender.sendMessage("Command succesful.");
			else
				sender.sendMessage("Command recognized, but FAILED!");
		}
		else
			sender.sendMessage("Command not recognized.");
	}
	
	private static boolean CanPerformCommand(String cmd)
	{
		int i = 0;
		while (i < availableCommands.length)
		{
			if (cmd.startsWith(availableCommands[i++]))
				return true;
		}
		return false;
	}
	private static String[] availableCommands = {"time", "give", "tp"};
}

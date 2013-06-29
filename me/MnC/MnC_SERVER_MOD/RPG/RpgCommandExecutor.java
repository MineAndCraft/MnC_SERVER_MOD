package me.MnC.MnC_SERVER_MOD.RPG;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RpgCommandExecutor implements CommandExecutor
{
	final MnC_SERVER_MOD plugin = MnC_SERVER_MOD.getInstance();
	
	public RpgCommandExecutor(){}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) 
	{
		if(sender instanceof Player)
		{
			if(command.getName().equalsIgnoreCase("rpg"))
			{
				PlayerProfession prof;
				if ((prof = plugin.userManager.getUser(sender.getName()).getProfession()) != null)
				{
					int lvl = prof.GetLevel();
					int xp = prof.GetXp();
					int xpNeeded = prof.GetXpNeeded();
					sender.sendMessage(ChatColor.YELLOW + "**************************");
					sender.sendMessage("RPG Miner");
					sender.sendMessage("**Level:" + lvl);
					sender.sendMessage("**XP:" + xp + "/" + xpNeeded);
					sender.sendMessage(ChatColor.YELLOW + "**************************");
					sender.sendMessage("Drop chance multiplier: " + prof.getDropChanceMultiplier());
					sender.sendMessage(ChatColor.YELLOW + "**************************");
					return true;
				}
				else 
				{
					((Player) sender).sendMessage("Unable to get your profession. Please contact the administrator.");
					plugin.log.warning("Cannot get profession for player "+sender.getName()+".");
				}
			}
		}
		return false;
	}
}

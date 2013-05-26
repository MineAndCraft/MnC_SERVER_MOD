package me.MnC.MnC_SERVER_MOD.chat;

import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.UserManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommandExecutor implements CommandExecutor
{
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
	{
		if(sender instanceof Player)
		{
			if (args.length > 0)
			{
				MinecraftPlayer player = UserManager.getInstance().getUser(sender.getName());
				String lastSender = player.getLastTellSender();
				if(lastSender == null)
				{
					sender.sendMessage("Nemate komu odpovedet!");
					return false;
				}
				else
				{
					StringBuilder msg = new StringBuilder();
					for(int i=0;i<args.length;i++)
						msg.append(" ").append(args[i]);
					player.getPlayerInstance().chat("/tell " + lastSender + msg);
					return true;
				}
			}
		}
		return false;
	}

}

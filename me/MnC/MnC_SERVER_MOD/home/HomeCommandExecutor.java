package me.MnC.MnC_SERVER_MOD.home;

import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HomeCommandExecutor implements CommandExecutor
{
	public HomeCommandExecutor(){}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player && cmd.getName().equalsIgnoreCase("home"))
		{
			Player player = (Player)sender;
		
			if(args.length == 0)
			{
				if(!(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_basic") || player.getWorld().getName().equals("world_the_end") || player.getWorld().getName().equals("world_mine")))
				{
					ChatHandler.FailMsg(player, "Tento prikaz zde nelze pouzit!");
					return true;
				}
				
				Home home = HomesHandler.getHomeByPlayer(player.getName());
				if(home != null)
				{
					player.teleport(HomesHandler.getLocation(home));
					ChatHandler.SuccessMsg(player, "Byl jste teleportovan na home!");
				}
				else
				{
					ChatHandler.FailMsg(player, "Vas home jeste nebyl nastaven!");
				}
			}
			else if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("set"))
				{
					if(!(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_basic")))
					{
						ChatHandler.FailMsg(player, "Tento prikaz zde nelze pouzit!");
						return true;
					}
					
					if(HomesHandler.isWorldAllowedToSetHomeIn(player.getWorld().getName()))
					{
						HomesHandler.addHome(player);
						ChatHandler.SuccessMsg(player, "Vas home byl nastaven!");
					}
					else
					{
						ChatHandler.FailMsg(player, "V tomto svete si nemuzete nastavit home!");
					}
				}
				else
				{
					ChatHandler.FailMsg(player, "Prikaz home neprebira tento argument!");
				}
			}
			else
			{
				ChatHandler.FailMsg(player, "Prikaz home neprebira dalsi argumenty!");
			}
			return true;
		}
		return false;
	}

}

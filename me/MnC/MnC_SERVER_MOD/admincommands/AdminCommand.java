package me.MnC.MnC_SERVER_MOD.admincommands;

import java.util.Arrays;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class AdminCommand extends Command
{	
	public enum CommandExecutability
	{
		CONSOLE_ONLY,
		PLAYER_ONLY,
		UNIVERSAL,
	}
	
	protected CommandExecutability executability;

	protected Rank rankRequirement;
	
	protected int max_args = -1;
	protected int min_args = 0;
	
	protected AdminCommand(String name, String description, String usage, String[] aliases, CommandExecutability exec, Rank rankReq)
	{
		super("/" + name);
		
		executability = exec;
		
		setDescription(description);
		setUsage(usage);
		if(aliases != null)
			setAliases(Arrays.asList(aliases));
		//TODO make this use bukkit permissions
		rankRequirement = rankReq;
	}
	
	public final boolean execute(CommandSender sender, String commandLabel, String[] args)
	{
		if(!(sender instanceof ConsoleCommandSender))
		{
			if(!GameMasterHandler.IsAtleastRank(sender.getName(),rankRequirement))
			{
				sender.sendMessage(ChatColor.RED + "Your rank is not high enough to execute this command");
				return false;
			}
		}
		
		if(executability == CommandExecutability.CONSOLE_ONLY && !(sender instanceof ConsoleCommandSender))
		{
			sender.sendMessage(ChatColor.RED + "This command has to be executed from console.");
			return false;
		}
		
		if(executability == CommandExecutability.PLAYER_ONLY && !(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "This command has to be executed by player.");
			return false;
		}
		
		if(args.length < min_args)
		{
			sender.sendMessage(ChatColor.RED + "This command takes at least "+min_args+" arguments. Received "+args.length+".");
			return false;
		}
		
		if(max_args != -1 && args.length > max_args)
		{
			sender.sendMessage(ChatColor.RED + "This command doesn't take more than "+max_args+" arguments. Received "+args.length);
			return false;
		}
		
		return executeCommand(sender, commandLabel, args);
	}
	
	protected abstract boolean executeCommand(CommandSender sender, String commandLabel, String args[]);
}

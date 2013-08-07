package me.MnC.MnC_SERVER_MOD.admincommands;

import java.util.HashMap;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends AdminCommand
{
	//TODO use bukkit permissions
	
	private static HashMap<String,Location> backLocations = new HashMap<String, Location>();

	protected TpCommand()
	{
		super("tp", "The very admin Teleporter", "//tp <playername>|<x> <y> <z>[ <world>]|<who> <where>", null, CommandExecutability.PLAYER_ONLY, Rank.HELPER);
		max_args = 4;
	}

	protected boolean executeCommand(CommandSender sender, String commandLabel, String[] args)
	{
		Player senderPlayer = (Player)sender;
		if(args[0].equals("back"))
		{
			Location back = backLocations.get(senderPlayer.getName().toLowerCase());
			if(back == null)
			{
				senderPlayer.sendMessage(ChatColor.RED + "You haven't teleported yet.");
				return false;
			}
			backLocations.put(senderPlayer.getName().toLowerCase(), senderPlayer.getLocation());
			senderPlayer.teleport(back);
			return true;
		}
		else if(args.length == 1)
		{
			Player targetPlayer = Bukkit.getServer().getPlayerExact(args[0]);
			if(targetPlayer == null)
			{
				sender.sendMessage(ChatColor.RED + "Target player is not online.");
				return false;
			}
			backLocations.put(senderPlayer.getName().toLowerCase(),senderPlayer.getLocation());
			senderPlayer.teleport(targetPlayer);
			return true;
		}
		else if(args.length == 2 && GameMasterHandler.IsAtleastRank(senderPlayer.getName(), Rank.GAMEMASTER))
		{
			Player sourcePlayer = Bukkit.getServer().getPlayerExact(args[0]);
			if(sourcePlayer == null)
			{
				sender.sendMessage(ChatColor.RED + "Source player is not online.");
				return false;
			}
			Player targetPlayer = Bukkit.getServer().getPlayerExact(args[1]);
			if(targetPlayer == null)
			{
				sender.sendMessage(ChatColor.RED + "Target player is not online.");
				return false;
			}
			sourcePlayer.teleport(targetPlayer);
			return true;
		}
		else if(args.length > 2)
		{
			World world = senderPlayer.getWorld();
			if(args.length == 4)
			{
				world = Bukkit.getServer().getWorld(args[3]);
				if(world == null)
				{
					sender.sendMessage(ChatColor.RED + "Target world doesn't exist.");
					return false;
				}
			}
			float x = Float.parseFloat(args[0]);
			float y = Float.parseFloat(args[1]);
			float z = Float.parseFloat(args[2]);
			backLocations.put(senderPlayer.getName().toLowerCase(),senderPlayer.getLocation());
			senderPlayer.teleport(new Location(world,x,y,z));
			return true;
		}
		else
		{
			sender.sendMessage(this.getUsage());
			return true;
		}
	}
}

package me.MnC.MnC_SERVER_MOD.Handlers.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.rpg.PlayerProfession;
import me.MnC.MnC_SERVER_MOD.util.CommandController.CommandHandler;

public class CommandsHandler {
	
	MnC_SERVER_MOD plugin;
	
	public CommandsHandler(MnC_SERVER_MOD instance)
	{
		plugin = instance;
	}

	@CommandHandler(name = "help",
			description = "Displays help for commands",
			permission = "bukkit.command.help ")
	public void helpCommand(Player sender, String[] args)
	{
		sender.sendMessage("******************************");
		sender.sendMessage("MineAndCraft SERVER MOD "+MnC_SERVER_MOD.version);
		sender.sendMessage("******************************");
		sender.sendMessage("Seznam prikazu:");
		sender.sendMessage(ChatColor.AQUA + " /login " + ChatColor.GRAY + "<heslo>  " + ChatColor.WHITE + "-  Prihlasi zaregistrovaneho hrace.");
		sender.sendMessage(ChatColor.AQUA + " /lock " + ChatColor.WHITE + "- Zamkne block.");
		sender.sendMessage(ChatColor.AQUA + " /unlock  " + ChatColor.WHITE + "- Odemkne block.");
		sender.sendMessage(ChatColor.AQUA + " /who  " + ChatColor.WHITE + "-  Seznam online hracu.");
		sender.sendMessage(ChatColor.AQUA + " /rpg  " + ChatColor.WHITE + "-  Informace o Vasem RPG.");
		sender.sendMessage(ChatColor.AQUA + " /credits " + ChatColor.WHITE + "- Menu ekonomiky.");
		sender.sendMessage(ChatColor.AQUA + " /arena  " + ChatColor.WHITE + "-  Menu areny.");
		sender.sendMessage(ChatColor.AQUA + " /estates  " + ChatColor.WHITE + "-  Menu pozemku.");
		sender.sendMessage(ChatColor.AQUA + " /shop  " + ChatColor.WHITE + "-  Menu Obchodu.");
		sender.sendMessage(ChatColor.AQUA + " /vip  " + ChatColor.WHITE + "-  VIP menu.");
		sender.sendMessage(ChatColor.AQUA + " /places " + ChatColor.WHITE + "- Menu mist, kam se da teleportovat.");
		sender.sendMessage(ChatColor.AQUA + " /pp " + ChatColor.WHITE + "- Alias pro /places port");
		sender.sendMessage(ChatColor.AQUA + " /home " + ChatColor.WHITE + "- Teleportuje vas na home");
		sender.sendMessage(ChatColor.AQUA + " /home set " + ChatColor.WHITE + "- Nastavi home na aktualni pozici");
		sender.sendMessage(ChatColor.AQUA + " /getcoords " + ChatColor.WHITE + "- Zobrazi souradnice o bloku na ktery ukazujete");
		sender.sendMessage(ChatColor.AQUA + " /logout " + ChatColor.WHITE + "- Odpoji vas ze serveru");
		sender.sendMessage(ChatColor.AQUA + " /y " + ChatColor.WHITE + "- Povoli teleport VIP hrace");
		sender.sendMessage(ChatColor.AQUA + " /block <hrac>" + ChatColor.WHITE + "- Prida hrace do blocklistu - hrac vam nebude moci posilat soukrome zpravy");
		sender.sendMessage(ChatColor.AQUA + " /r " + ChatColor.GRAY + "<message> " + ChatColor.WHITE + "-  Odpoved na whisper.");
		sender.sendMessage(ChatColor.AQUA + " /help " + ChatColor.WHITE + "- Zobrazi tuto napovedu");
		sender.sendMessage("******************************");
		sender.sendMessage("Created by MineAndCraft team 2011-2013");
		sender.sendMessage("******************************");
	}
	
	@CommandHandler(name = "who",
			description = "Displays all currently online players",
			permission = "mnc_server_mod.command.who")
	public void whoCommand(Player sender, String[] args)
	{
		Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
		String list = "";
		int playerCap = plugin.getServer().getMaxPlayers();
		int i = 0;
		sender.sendMessage(ChatColor.BLUE + "****************ON-LINE HRACI(" + onlinePlayers.length + "/" + playerCap +")****************");
		while(i<onlinePlayers.length)
		{
			Player p = onlinePlayers[i];
			PlayerProfession prof = plugin.userManager.getUser(p.getName()).getProfession();
			int level = (prof==null)? 0 : prof.GetLevel();
			if(GameMasterHandler.IsRank(p.getName(), Rank.ADMIN))
			{
				list += ChatColor.AQUA + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else if(GameMasterHandler.IsRank(p.getName(), Rank.GAMEMASTER))
			{
				list += ChatColor.GREEN + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else if(GameMasterHandler.IsRank(p.getName(), Rank.BUILDER))
			{
				list += ChatColor.GOLD + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else if(plugin.vipManager.isVip((p.getName())))
			{
				list += ChatColor.GOLD + p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			else
			{
				list += p.getName() + ChatColor.GRAY + "(" + level + ")" + ChatColor.WHITE;
			}
			if(i == (onlinePlayers.length-1))
				list += ".";
			else
				list += ", ";
			i++;
		}
		sender.sendMessage(list);
		sender.sendMessage("****************************************************");
	}
}

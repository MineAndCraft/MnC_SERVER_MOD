package me.MnC.MnC_SERVER_MOD.admincommands;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;

import org.bukkit.command.CommandSender;

public class TestCommand extends AdminCommand
{

	public TestCommand()
	{
		super("test","Just a test command. No hum.","Use it as you wish",null,CommandExecutability.UNIVERSAL,Rank.HELPER);
	}
	
	public boolean executeCommand(CommandSender sender, String commandLabel, String[] args)
	{
		sender.sendMessage("Test admin command");
		return true;
	}

}

package me.Guga.Guga_SERVER_MOD.Handlers;

import org.bukkit.entity.Player;

public class MobDisguiseHandler 
{
	public static String[] GetVipAllowedMobs()
	{
		return vipAllowedMobs;
	}
	public static void SendCommand(Player sender, String arg)
	{
		String subCommand = arg.toLowerCase();
		if(subCommand.matches("restart"))
		{
			sender.chat("/md");
			
		}
		else
		{
			sender.chat("/md " + subCommand);
		}
	}
	private static String[] vipAllowedMobs = 
		{"creeper", "skeleton", "spider", "zombie", "sheep", "cow", "chicken", "wolf", "enderman", "villager", "cat", "ocelot", "slime", "cavespider", "irongolem", "snowgolem"};
}

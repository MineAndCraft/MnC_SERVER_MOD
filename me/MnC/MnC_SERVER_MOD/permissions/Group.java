package me.MnC.MnC_SERVER_MOD.permissions;

import java.util.ArrayList;
import org.bukkit.ChatColor;

public class Group
{

	private ArrayList<String> players;
	private ArrayList<String> permissions;
	private String groupName;
	private String flatChatFormat;
	private String chatFormat;
	private String flatPlayerListFormat;
	private String playerListFormat;
	private int groupPriority;

	public Group(ArrayList<String> players, ArrayList<String> permissions, String groupName, String chatFormat, String playerListFormat, int groupPriority)
	{
		this.players = players;
		this.permissions = permissions;
		this.groupName = groupName;
		this.flatChatFormat = chatFormat;
		this.chatFormat = ChatColor.translateAlternateColorCodes('&', chatFormat);
		this.flatPlayerListFormat = playerListFormat;
		this.playerListFormat = ChatColor.translateAlternateColorCodes('&', playerListFormat);
		this.groupPriority = groupPriority;
	}

	public ArrayList<String> getPlayers()
	{
		return this.players;
	}

	public ArrayList<String> getPermissions()
	{
		return this.permissions;
	}

	public String getName()
	{
		return this.groupName;
	}

	public String getFlatChatFormat()
	{
		return this.flatChatFormat;
	}
	
	public String getChatFormat()
	{
		return this.chatFormat;
	}
	
	public String getFlatPlayerListFormat()
	{
		return this.flatPlayerListFormat;
	}

	public String getPlayerListFormat()
	{
		return this.playerListFormat;
	}

	public int getPriority()
	{
		return this.groupPriority;
	}
}

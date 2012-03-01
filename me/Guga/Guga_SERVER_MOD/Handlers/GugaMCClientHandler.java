package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaVirtualCurrency;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;

public abstract class GugaMCClientHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		GugaMCClientHandler.plugin = plugin;
	}
	public static void RegisterUser(Player p, String macAddr)
	{
		if (connectedClients.get(p) == null)
		{
			connectedClients.put(p, macAddr);
			p.sendMessage(ChatColor.BLUE + "Vas klient byl uspesne overen");
		}
	}
	public static void UnregisterUser(Player p)
	{
		connectedClients.remove(p);
	}
	
	public static boolean HasClient(Player p)
	{
		return connectedClients.get(p) != null;
	}
	public static void CopySkinsFromPool(ArrayList<String> players)
	{
		GugaFile.ClearDirectory(skinRealPath);
		GugaFile file = null;
		Iterator<String> i = players.iterator();
		while (i.hasNext())
		{
			String pName = i.next();
			String pathPool = skinPoolPath + pName + ".png";
			file = new GugaFile(pathPool, GugaFile.BINARY_MODE);
			if (file.Exists())
			{
				String skinPath = skinRealPath + pName + ".png";
				file.Open();
				file.CopyFileTo(skinPath);
				file.Close();
			}
		}
	}
	public static String GetPlayerMacAddr(Player p)
	{
		return connectedClients.get(p);
	}
	public static ArrayList<String> GetPlayersWithSkin()
	{
		ArrayList<String> players = GameMasterHandler.GetNamesByRank(Rank.GAMEMASTER);
		Iterator<GugaVirtualCurrency> i = plugin.playerCurrency.iterator();
		while (i.hasNext())
		{
			GugaVirtualCurrency curr = i.next();
			if (curr.IsVip())
				players.add(curr.GetPlayerName());
		}
		return players;
	}
	public static void SendMessage(Player p, String msg)
	{
		p.sendPluginMessage(plugin, messageChannel, msg.getBytes());
	}
	private static HashMap<Player, String> connectedClients = new HashMap<Player, String>();
	
	private static String messageChannel = "Guga";
	
	private static String skinPoolPath = "/usr/SkinPool/";
	private static String skinRealPath = "/home/apps/Skins/Body/";
	private static Guga_SERVER_MOD plugin;
}
package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;
import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaVirtualCurrency;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
		if (needClient)
			return connectedClients.get(p) != null;
		return true;
	}
	public static void ReloadSkins()
	{
		LoadMinecraftOwners();
		GugaFile.ClearDirectory(skinRealPath);
		CopySkinsFromPool(GetPlayersWithSkin());
	}
	public static void CopySkinsFromPool(ArrayList<String> players)
	{
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
		Iterator<String> ii = minecraftOwners.iterator();
		String name = null;
		while (ii.hasNext())
		{
			name = ii.next();
			if (!players.contains(name))
				players.add(name);
		}
		return players;
	}
	public static void SendMessage(Player p, String msg)
	{
		p.sendPluginMessage(plugin, messageChannel, msg.getBytes());
	}
	public static boolean IsWhiteListed(Player p)
	{
		String pName = p.getName();
		Iterator<String> i = whiteList.iterator();
		
		while (i.hasNext())
		{
			if (pName.matches(i.next()))
				return true;
		}
		return false;
	}
	public static boolean IsMCOwner(Player p)
	{
		String pName = p.getName();
		Iterator<String> i = minecraftOwners.iterator();
		
		while (i.hasNext())
		{
			if (pName.equalsIgnoreCase(i.next()))
				return true;
		}
		return false;
	}
	public static void LoadMACWhiteList()
	{
		GugaFile file = new GugaFile(whiteListPath,GugaFile.READ_MODE);
		file.Open();
		String line = null;
		while ((line = file.ReadLine()) != null)
		{
			whiteList.add(line);
		}
		file.Close();
	}
	public static void LoadMinecraftOwners()
	{
		GugaFile file = new GugaFile(minecraftOwnPath, GugaFile.READ_MODE);
		if (minecraftOwners.size() > 0)
			minecraftOwners.clear();
		file.Open();
		String line = null;
		while ((line = file.ReadLine()) != null)
		{
			minecraftOwners.add(line);
		}
		file.Close();
	}
	private static ArrayList<String> whiteList = new ArrayList<String>();
	private static ArrayList<String> minecraftOwners = new ArrayList<String>();
	private static HashMap<Player, String> connectedClients = new HashMap<Player, String>();
	public static boolean needClient = true;
	
	public static String requiredClientVersion = "0.0.3";
	private static String messageChannel = "Guga";
	
	private static String skinPoolPath = "/usr/SkinPool/";
	private static String skinRealPath = "/home/apps/Skins/Body/";
	private static String whiteListPath = "plugins/whiteListMAC.dat";
	private static String minecraftOwnPath = "plugins/mcOwners.dat";
	private static Guga_SERVER_MOD plugin;
}
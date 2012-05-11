package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.GugaBan;
import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

public abstract class GugaBanHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public static void AddBan(String playerName, long expiration)
	{
		Iterator<GugaBan> i = bans.iterator();
		boolean exists = false;
		GugaBan ban = null;
		while (i.hasNext())
		{
			ban = i.next();
			if (ban.GetPlayerName().equalsIgnoreCase(playerName))
			{
				exists = true;
				break;
			}
		}
		if (exists)
		{
			if (ban != null)
			{
				ban.SetExpiration(expiration);
			}
		}
		else
		{
			bans.add(new GugaBan(playerName, expiration));
		}
		SaveBans();
	}
	public static void UpdateBanAddr(String playerName)
	{
		Player p = plugin.getServer().getPlayer(playerName);
		if (p == null)
			return;
		String addr = null;
		if (mode == CheckMode.IP_ADDRESSES)
			addr = p.getAddress().getAddress().toString();
		else if (mode == CheckMode.MAC_ADDRESSES)
			addr = GugaMCClientHandler.GetPlayerMacAddr(p);
		Iterator<GugaBan> i = bans.iterator();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			if (ban.GetPlayerName().equalsIgnoreCase(playerName))
			{
				ban.AddIpAddress(addr);
				SaveBans();
				return;
			}
		}
	}
	/*public static boolean IsWhiteListed(Player p)
	{
		String status = GugaMCClientHandler.GetPlayerMacAddr(p);
		if (status == null)
			return false;
		if (status.matches("WHITELISTED"))
			return true;
		return false;
	}*/
	public static void UpdateBanExpiration(String playerName, long expiration)
	{
		Iterator<GugaBan> i = bans.iterator();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			if (ban.GetPlayerName().equalsIgnoreCase(playerName))
			{
				ban.SetExpiration(expiration);
				SaveBans();
				return;
			}
		}
	}
	public static void RemoveBan(String playerName)
	{
		Iterator<GugaBan> i = bans.iterator();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			if (ban.GetPlayerName().equalsIgnoreCase(playerName))
			{
				ban.SetExpiration(0);
				SaveBans();
				return;
			}
		}
	}
	public static GugaBan GetGugaBan(String playerName)
	{
		Iterator<GugaBan> i = bans.iterator();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			if (ban.GetPlayerName().equalsIgnoreCase(playerName))
			{
				return ban;
			}
		}
		return null;
	}
	public static ArrayList<GugaBan> GetBanList()
	{
		return bans;
	}
	public static CheckMode GetMode()
	{
		return mode;
	}
	public static ArrayList<GugaBan> GetBannedPlayers()
	{
		Iterator<GugaBan> i = bans.iterator();
		ArrayList<GugaBan> banned = new ArrayList<GugaBan>();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			if (ban.GetExpiration() > System.currentTimeMillis())
				banned.add(ban);
		}
		return banned;
	}
	public static boolean IsBanned(String playerName)
	{
		Iterator<GugaBan> i = bans.iterator();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			if (ban.GetPlayerName().equalsIgnoreCase(playerName))
			{
				if (ban.GetExpiration() > System.currentTimeMillis())
					return true;
			}
			int i2 = 0;
			String[] addrs = ban.GetIpAddresses();
			Player p = plugin.getServer().getPlayer(playerName);
			if (p == null)
				return false;
			String addr = null;
			if (mode == CheckMode.MAC_ADDRESSES)
				addr = GugaMCClientHandler.GetPlayerMacAddr(p);
			else if (mode == CheckMode.IP_ADDRESSES)
				addr = plugin.getServer().getPlayer(playerName).getAddress().getAddress().toString();
			while (i2 < addrs.length)
			{
				if (ban.GetExpiration() > System.currentTimeMillis())
				{
					if (addrs[i2].matches(addr))
						return true;
				}
				i2++;
			}
		}
		return false;
	}
	public static void SaveBans()
	{
		plugin.log.info("Saving Bans file...");
		GugaFile file = new GugaFile(bansFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<GugaBan> i = bans.iterator();
		while (i.hasNext())
		{
			GugaBan ban = i.next();
			file.WriteLine(ban.toString());
		}
		file.Close();
	}
	public static void LoadBans()
	{
		plugin.log.info("Loading Bans file...");
		GugaFile file = new GugaFile(bansFile, GugaFile.READ_MODE);
		file.Open();
		String line = null;
		while ((line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			String playerName = split[0];
			long expiration = Long.parseLong(split[1]);
			String[] addresses = new String[split.length - 2];
			int i = 2;
			while (i < split.length)
			{
				addresses[i - 2] = split[i];
				i++;
			}
			bans.add(new GugaBan(playerName, addresses, expiration));
		}
		file.Close();
	}
	public static void LoadIpWhiteList()
	{
		GugaFile file = new GugaFile(whitelistedPlayersPath, GugaFile.READ_MODE);
		if (whitelistedPlayers.size() > 0)
			whitelistedPlayers.clear();
		file.Open();
		String line = null;
		while ((line = file.ReadLine()) != null)
		{
			whitelistedPlayers.add(line);
		}
		file.Close();
	}
	public static boolean IsIpWhitelisted(Player p)
	{
		String pName = p.getName();
		Iterator<String> i = whitelistedPlayers.iterator();
		
		while (i.hasNext())
		{
			if (pName.equalsIgnoreCase(i.next()))				
				return true;
		}
		return false;
	}
	public enum CheckMode{MAC_ADDRESSES, IP_ADDRESSES};
	
	public static CheckMode mode = CheckMode.MAC_ADDRESSES;
	private static Guga_SERVER_MOD plugin;
	private static ArrayList<GugaBan> bans = new ArrayList<GugaBan>();
	private static ArrayList<String> whitelistedPlayers = new ArrayList<String>();
	
	private static String whitelistedPlayersPath="plugins/whiteListIP.dat";
	private static String bansFile = "plugins/Bans.dat";
}

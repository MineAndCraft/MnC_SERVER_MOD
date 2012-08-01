package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.GameMaster;
import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.GameMaster.Rank;

public abstract class GameMasterHandler 
{
	public static void SetPlugin(Guga_SERVER_MOD plugin)
	{
		GameMasterHandler.plugin = plugin;
	}
	public static void AddGM(String name, String rank)
	{
		Rank r = Rank.GetRankByName(rank);
		GameMasterHandler.gameMasters.add(new GameMaster(name, r));
	}
	public static void AddGMIng(String name, String rank)
	{
		Player p = plugin.getServer().getPlayer(name);
		Rank r = Rank.GetRankByName(rank);
		if(r==Rank.ADMIN)
		{
			p.setOp(true);
		}
		GameMasterHandler.gameMasters.add(new GameMaster(name, r));
		p.setGameMode(GameMode.CREATIVE);
		ChatHandler.InitializeDisplayName(p);
		SaveGMs();
	}
	public static void RemoveGMIng(String name)
	{
		Player p = plugin.getServer().getPlayer(name);
		GameMasterHandler.gameMasters.remove(GameMasterHandler.GetGMByName(name));
		p.setDisplayName(name);
		p.setPlayerListName(name);
		p.setGameMode(GameMode.SURVIVAL);
		SaveGMs();
	}
	public static GameMaster GetGMByName(String name)
	{
		Iterator<GameMaster> i = GameMasterHandler.gameMasters.iterator();
		while (i.hasNext())
		{
			GameMaster gm = i.next();
			if (gm.GetName().matches(name))
				return gm;
		}
		return null;
	}
	public static ArrayList<String> GetNamesByRank(Rank req)
	{
		ArrayList<String> names = new ArrayList<String>();
		Iterator<GameMaster> i = gameMasters.iterator();
		while (i.hasNext())
		{
			GameMaster gm = i.next();
			if (gm.GetRank().IsRanked(req))
				names.add(gm.GetName());
		}
		return names;
	}
	public static boolean IsAtleastRank(String name, Rank req)
	{
		GameMaster gm;
		if ((gm = GameMasterHandler.GetGMByName(name)) != null)
		{
			if (gm.IsAtleastRank(req))
				return true;
		}
		return false;
	}
	public static boolean IsAtleastGM(String name)
	{
		GameMaster gm;
		if ((gm = GameMasterHandler.GetGMByName(name)) != null)
		{
			if (gm.IsAtleastGM())
				return true;
		}
		return false;
	}
	public static boolean IsAdmin(String name)
	{
		GameMaster gm;
		if ((gm = GameMasterHandler.GetGMByName(name)) != null)
		{
			if (gm.IsAdmin())
				return true;
		}
		return false;
	}
	public static void LoadGMs()
	{
		plugin.log.info("Loading GMs file...");
		GugaFile file = new GugaFile(GameMasterHandler.gmFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		while ((line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			GameMasterHandler.AddGM(split[0], split[1]);
		}
		file.Close();
	}
	public static void SaveGMs()
	{
		plugin.log.info("Saving GMs file...");
		GugaFile file = new GugaFile(GameMasterHandler.gmFile, GugaFile.WRITE_MODE);
		file.Open();
		Iterator<GameMaster> i = GameMasterHandler.gameMasters.iterator();
		while (i.hasNext())
		{
			GameMaster gm = i.next();
			String line = gm.GetName() + ";" + gm.GetRank().GetRankName();
			file.WriteLine(line);
		}
		file.Close();
	}
	public static ArrayList<GameMaster> gameMasters = new ArrayList<GameMaster>();
	private static Guga_SERVER_MOD plugin;
	private static String gmFile = "plugins/GameMasters.dat";
}

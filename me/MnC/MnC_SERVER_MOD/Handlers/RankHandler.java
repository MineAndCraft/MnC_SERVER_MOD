package me.MnC.MnC_SERVER_MOD.Handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.entity.Player;

public class RankHandler
{
	//TODO use separate ranks file and load ranks dynamically
	
	private static final Logger _log = Logger.getLogger(RankHandler.class.getName());

	private static final Map<String,Rank> playerRanks = new HashMap<String,Rank>();
	
	private static final String ranksFile = "plugins/MineAndCraft_plugin/GameMasters.dat";
	
	public static void loadRanks()
	{
		try(BufferedReader reader = new BufferedReader(new FileReader(ranksFile)))
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] tokens = line.split(";");
				if(tokens.length != 2)
					continue;
				playerRanks.put(tokens[0].toLowerCase(), Rank.GetRankByName(tokens[1]));
			}
		} catch (FileNotFoundException e) {
			_log.log(Level.SEVERE, "Could not find ranks file", e);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "An exception occured while loading ranks", e);
		}
	}
	
	public static void grantPlayerPermissions(Player player)
	{
		Rank rank = playerRanks.get(player.getName().toLowerCase());
		if(rank == null)
			return;
		MnC_SERVER_MOD plugin = MnC_SERVER_MOD.getInstance();
		switch(rank)
		{
			case ADMIN:
				player.addAttachment(plugin,"mnc.permission.admin",true);
				// admin permissions
			break;
			case GAMEMASTER:
				player.addAttachment(plugin,"mnc.permission.gamemaster",true);
				// gamemaster permissions
			break;
			case HELPER:
				player.addAttachment(plugin,"mnc.permission.helper",true);
				// helper permissions
			break;
			case BUILDER:
				player.addAttachment(plugin,"mnc.permission.builder",true);
				// builder permissions
			break;
			default:break;
		}
	}
}

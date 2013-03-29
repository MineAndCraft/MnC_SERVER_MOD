package me.MnC.MnC_SERVER_MOD.basicworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import me.MnC.MnC_SERVER_MOD.Config;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.ServerRegion;
import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BasicWorldBanRegionManager
{
	private MnC_SERVER_MOD plugin;
	private ArrayList<String> regions = new ArrayList<String>(); 
	
	private HashMap<String,Integer> blockDeviations = new HashMap<String,Integer>(); 
	

	public BasicWorldBanRegionManager(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
		this.reloadBanRegions();
		this.loadDeviations();
	}

	public synchronized void reloadBanRegions()
	{
		this.regions.clear();
		try{
		    BufferedReader br = new BufferedReader(new FileReader(new File(Config.BW_BAN_REGIONS_CONFIG_FILE)));
		    String line = null;
		while((line = br.readLine())!=null)
		{
			if(line.startsWith("#"))
				continue;
			this.regions.add(line);
		}
		br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean onRegionBlockBreakCheck(ServerRegion region,Player player,Block block)
	{
		if(!GameMasterHandler.IsAtleastRank(player.getName(), Rank.BUILDER) //Allow Team Members to edit bw terrain freely
				&& this.regions.contains(region.GetName()))
		{
			trollTheGriefer(player,block);
			return false;
		}
		return true;
	}

	private static int getBlockValue(Block block)
	{
		Material m = block.getType();
		switch(m)
		{
		case DIAMOND_BLOCK:
		case EMERALD_BLOCK:
		case IRON_BLOCK:
		case GOLD_BLOCK:
		case LAPIS_BLOCK:
			return 2;
		case GLOWSTONE:
			return 1;
		default:
			return 0;
		}
	}


	private void trollTheGriefer(Player player,Block block)
	{
		int value = getBlockValue(block);
		if(value == 0)
			return;
		String key = player.getName().toLowerCase();
		if(!blockDeviations.containsKey(key))
		{
			blockDeviations.put(key, value);
			player.sendMessage(ChatColor.RED+"[BasicWorldBot] Kopete cizi majetek. Griefovani je tu zakazano. Prectete si pravidla serveru na http://mineandcraft.cz/pravidla"); //warning
			saveDeviations();
		}
		else
		{
			int d = blockDeviations.get(key) + value;
			blockDeviations.put(key,d);
			saveDeviations();
			if(d <= 2)
			{
				player.sendMessage(ChatColor.RED+"[BasicWorldBot] Kopete cizi majetek. Griefovani je tu zakazano. Prectete si pravidla serveru na http://mineandcraft.cz/pravidla"); //warning
			}
			else if(2 < d && d <= 10)
			{
				player.kickPlayer("Byl jste vyhozen ze serveru za poruseni pravidel mineandcraft.cz! Prectete si pravidla na http://mineandcraft.cz/pravidla"); // kick
			}
			else if(10 < d && d <= 13)
			{
				plugin.banHandler.banPlayer(key, System.currentTimeMillis()/1000 + 172800, String.format("Griefing v basic worldu. Posledni vygriefovany block byl na souradnicich {x:%d,y:%d,z:%d}.", block.getX(),block.getY(),block.getZ()), "BasicWorldBot");
				player.kickPlayer("Byl jste zabanovan na 2 dny za poruseni pravidel serveru mineandcraft.cz! Prectete is pravidla http://mineandcraft.cz/pravidla"); // 2 day ban	
			}
			else if(d > 13)
			{
				if(plugin.banHandler.banPlayer(key, -1, String.format("Griefing v basic worldu. Posledni vygriefovany block byl na souradnicich {x:%d,y:%d,z:%d}.", block.getX(),block.getY(),block.getZ()), "BasicWorldBot"))
				{
					this.blockDeviations.remove(key);
					saveDeviations();
				}
				player.kickPlayer("Byl jste permanentne zabanovan za poruseni pravidel server mineandcraft.cz!"); //permanent ban
			}
		}
	}
	
	public void onPlayerBWLeave(String name)
	{
		if(this.blockDeviations.containsKey(name))
		{
			this.blockDeviations.remove(name);
			saveDeviations();
		}
	}
	
	private void saveDeviations()
	{
		try{
			PrintWriter out = new PrintWriter(new FileWriter(Config.BW_BAN_REGIONS_DEVIATIONS_FILE));
			for(Entry<String, Integer> d: this.blockDeviations.entrySet())
			{
				out.println(String.format("%s;%d",d.getKey(),d.getValue()));
				out.flush();
			}
			out.close();
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void loadDeviations()
	{
		try{
			BufferedReader br = new BufferedReader(new FileReader(Config.BW_BAN_REGIONS_DEVIATIONS_FILE));
		    String line = null;
			this.blockDeviations.clear();
			String[] split;
			while((line = br.readLine())!=null)
			{
				split = line.split(";");
				this.blockDeviations.put(split[0],Integer.parseInt(split[1]));
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

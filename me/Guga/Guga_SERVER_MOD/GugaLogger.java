package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class GugaLogger 
{	
	GugaLogger(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		logBlockBreak = true;
		logBlockPlace = true;
		logBlockIgnite = true;
	}
	public void PrintShopData(final Player sender, final int page)
	{
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				GugaDataPager<String> pager = new GugaDataPager<String>(GetShopTransactionData(), 15);
				sender.sendMessage("LIST OF MOST BUYED ITEMS:");
				sender.sendMessage("PAGE " + page + "/" + pager.GetPagesCount());
				Iterator<String> i = pager.GetPage(page).iterator();
				while (i.hasNext())
				{
					sender.sendMessage(i.next());
				}
			}
		});
	}
	public void PrintBlockData(final Player sender, final Block block)
	{
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				sender.sendMessage("Searching...");
				final ArrayList<String> blockBreakData = new ArrayList<String>();
				final ArrayList<String> blockPlaceData = new ArrayList<String>();
				Thread breakThread = new Thread(new Runnable() {
					
					@Override
					public void run() 
					{
						GetBlockBreakData(block, blockBreakData);
					}
				});
				Thread placeThread = new Thread(new Runnable() {
					
					@Override
					public void run() 
					{
						GetBlockPlaceData(block, blockPlaceData);
					}
				});
				breakThread.start();
				placeThread.start();
				while (breakThread.isAlive() || placeThread.isAlive())
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ArrayList<String> cache = new ArrayList<String>();
				Iterator<String> i = blockPlaceData.iterator();
				while (i.hasNext())
				{
					String e = i.next();
					String splitted[] = e.split(";");
					String msg = "BLOCK_PLACE: name=" + splitted[1] + "  id=" + splitted[2] + " time=" + splitted[0].substring(0, 20);
					sender.sendMessage(msg);
					cache.add(msg);
				}
				i = blockBreakData.iterator();
				while (i.hasNext())
				{
					String e = i.next();
					String splitted[] = e.split(";");
					String msg = "BLOCK_BREAK: name=" + splitted[1] + "  id=" + splitted[2] + " time=" + splitted[0].substring(0, 20);
					sender.sendMessage(msg);
					cache.add(msg);
				}
				sender.sendMessage("Search Completed.");
				blockCache.put(sender, cache);
			}
		});
	}
	
	public void PrintLogData(final Player sender, final Block block)
	{
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				sender.sendMessage("Searching...");
				final ArrayList<String> blockBreakData = new ArrayList<String>();
				final ArrayList<String> blockPlaceData = new ArrayList<String>();
				GetLogData(block, blockBreakData, "plugins/BlockBreakLog.log");
				GetLogData(block, blockPlaceData, "plugins/BlockPlaceLog.log");
				ArrayList<String> cache = new ArrayList<String>();
				Iterator<String> i = blockPlaceData.iterator();
				while (i.hasNext())
				{
					String e = i.next();
					String splitted[] = e.split(";");
					String msg = "BLOCK_PLACE: name=" + splitted[1] + "  id=" + splitted[2] + " time=" + splitted[0].substring(0, 20);
					sender.sendMessage(msg);
					cache.add(msg);
				}
				i = blockBreakData.iterator();
				while (i.hasNext())
				{
					String e = i.next();
					String splitted[] = e.split(";");
					String msg = "BLOCK_BREAK: name=" + splitted[1] + "  id=" + splitted[2] + " time=" + splitted[0].substring(0, 20);
					sender.sendMessage(msg);
					cache.add(msg);
				}
				sender.sendMessage("Search Completed.");
				blockCache.put(sender, cache);
			}
		});
	}
	
	public ArrayList<String> GetLogData(Block block, ArrayList<String> dataBuffer, String filename)
	{
	try{
		int blockX = block.getX();
		int blockY = block.getY();
		int blockZ = block.getZ();
  
		String line;
		Process p = Runtime.getRuntime().exec("perl plugins/searchlog.pl "+Integer.toString(blockX)+","+Integer.toString(blockY)+","+Integer.toString(blockZ)+" "+filename);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null) {
			dataBuffer.add(line);
		}
		input.close();
		p.waitFor();
		}catch(Exception e)
		{
		e.printStackTrace();
		}
		return dataBuffer;
	}
	
	public ArrayList<String> GetBlockBreakData(Block block, ArrayList<String> dataBuffer)
	{
		int blockX = block.getX();
		int blockY = block.getY();
		int blockZ = block.getZ();
		
		GugaFile file = new GugaFile(blockBreakFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		String[] splittedLine;
		int x;
		int y;
		int z;
		while ((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			x = Integer.parseInt(splittedLine[3]);
			y = Integer.parseInt(splittedLine[4]);
			z = Integer.parseInt(splittedLine[5]);
			if ( (x == blockX) && (y == blockY) && (z == blockZ) )
			{
				dataBuffer.add(line);
			}
		}
		file.Close();
		return dataBuffer;
	}
	public ArrayList<String> GetShopTransactionData()
	{
		GugaFile file = new GugaFile(this.shopTransactionFile, GugaFile.READ_MODE);
		file.Open();
		String line;
		HashMap<String, Integer> dataMap = new HashMap<String, Integer>();
		while ( (line = file.ReadLine()) != null)
		{
			String[] split = line.split(";");
			Integer count;
			if ( (count = dataMap.get(split[2])) == null)
				count = 0;
			dataMap.put(split[2], count.intValue() + Integer.parseInt(split[3]));
		}
		Integer[] vals = new Integer[dataMap.size()];
		dataMap.values().toArray(vals);
		Arrays.sort(vals);
		ArrayList<String> returnArray = new ArrayList<String>();
		int i = vals.length - 1;
		while (i >= 0)
		{
			Iterator<Entry<String, Integer>> i2 = dataMap.entrySet().iterator();
			while (i2.hasNext())
			{
				Entry<String, Integer> entry = i2.next();
				if (entry.getValue().intValue() == vals[i])
				{
					returnArray.add(entry.getKey() + ";" + entry.getValue());
					dataMap.remove(entry.getKey());
					break;
				}
			}
			i--;
		}
		file.Close();
		return returnArray;
	}
	public ArrayList<String> GetBlockPlaceData(Block block, ArrayList<String> dataBuffer)
	{
		int blockX = block.getX();
		int blockY = block.getY();
		int blockZ = block.getZ();
		String line;
		String []splittedLine;
		int x;
		int y;
		int z;
		GugaFile file = new GugaFile(blockPlaceFile, GugaFile.READ_MODE);
		file.Open();
		while ((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			x = Integer.parseInt(splittedLine[3]);
			y = Integer.parseInt(splittedLine[4]);
			z = Integer.parseInt(splittedLine[5]);
			if ( (x == blockX) && (y == blockY) && (z == blockZ) )
			{
				dataBuffer.add(line);
			}
		}
		file.Close();
		return dataBuffer;
	}
	public void LogShopTransaction(final Prices item, final int amount, final String pName)
	{
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				String line = new Date() + ";" + pName + ";" + item.toString() + ";" + amount;
				GugaFile file = new GugaFile(shopTransactionFile, GugaFile.APPEND_MODE);
				file.Open();
				file.WriteLine(line);
				file.Close();
			}
		});
	}
	public void LogPlayerJoins(final String pName, final String IpAddr)
	{
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				String line = new Date() + ";" + pName + ";" + IpAddr;
				GugaFile file = new GugaFile(playerJoinsFile, GugaFile.APPEND_MODE);
				file.Open();
				file.WriteLine(line);
				file.Close();
			}
		});
	}
	public void LogBlockBreak(final BlockBreakEvent e, final int typeID)
	{
		if (logBlockBreak)
		{
			String line;
			int x = e.getBlock().getX();
			int y = e.getBlock().getY();
			int z = e.getBlock().getZ();
			String pName = e.getPlayer().getName();
			Date now = new Date();
			line = now + ";" + pName + ";" + typeID + ";" + x + ";" + y + ";" + z;
			if (blockBreakSwitch)
			{
				blockBreakArray1.add(line);
				if (blockBreakArray1.size() >= ARRAY_MAX_SIZE)
				{
					SaveWrapperBreak();
				}
			}
			else
			{
				blockBreakArray2.add(line);
				if (blockBreakArray2.size() >= ARRAY_MAX_SIZE)
				{
					SaveWrapperBreak();
				}
			}
		}
	}
	public void SaveWrapperBreak()
	{
		final boolean oldState = blockBreakSwitch;
		blockBreakSwitch = !blockBreakSwitch;
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				plugin.log.info("Saving BlockBreak Log Data...");
				if (oldState)
				{
					SaveDataFromCache(blockBreakArray1, blockBreakFile);
					blockBreakArray1.clear();
				}
				else
				{
					SaveDataFromCache(blockBreakArray2, blockBreakFile);
					blockBreakArray2.clear();
				}
			}
		});
		t.start();
	}
	public void SaveWrapperPlace()
	{
		final boolean oldState = blockPlaceSwitch;
		blockPlaceSwitch = !blockPlaceSwitch;
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				plugin.log.info("Saving BlockPlace Log Data...");
				if (oldState)
				{
					SaveDataFromCache(blockPlaceArray1, blockPlaceFile);
					blockPlaceArray1.clear();
				}
				else
				{
					SaveDataFromCache(blockPlaceArray2, blockPlaceFile);
					blockPlaceArray2.clear();
				}
			}
		});
		t.start();
	}
	private void SaveDataFromCache(ArrayList<String> array, String filePath)
	{
		GugaFile file = new GugaFile(filePath, GugaFile.APPEND_MODE);
		file.Open();
		Iterator<String> i = array.iterator();
		while (i.hasNext())
			file.WriteLine(i.next());
		file.Close();
	}
	public void LogBlockPlace(final BlockPlaceEvent e)
	{
		if (logBlockPlace)
		{
			plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					String line;
					int typeID = e.getBlock().getTypeId();
					int x = e.getBlock().getX();
					int y = e.getBlock().getY();
					int z = e.getBlock().getZ();
					String pName = e.getPlayer().getName();
					Date now = new Date();
					line = now + ";" + pName + ";" + typeID + ";" + x + ";" + y + ";" + z;
					if (blockPlaceSwitch)
					{
						blockPlaceArray1.add(line);
						if (blockPlaceArray1.size() >= ARRAY_MAX_SIZE)
						{
							SaveWrapperPlace();
						}
					}
					else
					{
						blockPlaceArray2.add(line);
						if (blockPlaceArray2.size() >= ARRAY_MAX_SIZE)
						{
							SaveWrapperPlace();
						}
					}
				}
			}
			
			);	
		}
	}
	public void LogBlockIgnite(final BlockIgniteEvent e)
	{
		if (logBlockIgnite)
		{
			plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					String line;
					int typeID = e.getBlock().getTypeId();
					int x = e.getBlock().getX();
					int y = e.getBlock().getY();
					int z = e.getBlock().getZ();
					String pName;
					
					if (e.getPlayer() == null)
					{
						pName = "null";
					}
					else
					{
						pName = e.getPlayer().getName();
					}
					Date now = new Date();
					line = now + ";" + pName + ";" + typeID + ";" + x + ";" + y + ";" + z;
					GugaFile file = new GugaFile(blockIgniteFile, GugaFile.APPEND_MODE);
					file.Open();
					file.WriteLine(line);
					file.Close();
				}
			}
			
			);	
		}
	}
	
	private ArrayList<String> blockBreakArray1 = new ArrayList<String>();
	private ArrayList<String> blockBreakArray2 = new ArrayList<String>();
	private boolean blockBreakSwitch = true;
	
	
	private ArrayList<String> blockPlaceArray1 = new ArrayList<String>();
	private ArrayList<String> blockPlaceArray2 = new ArrayList<String>();
	private boolean blockPlaceSwitch = true;
	
	
	private final int ARRAY_MAX_SIZE = 100;
	
	private boolean logBlockBreak;
	private boolean logBlockPlace;
	private boolean logBlockIgnite;
	
	
	private String blockBreakFile = "plugins/MineAndCraft_plugin/BlockBreakLog.log";
	private String blockIgniteFile = "plugins/MineAndCraft_plugin/BlockIgniteLog.log";
	private String blockPlaceFile = "plugins/MineAndCraft_plugin/BlockPlaceLog.log";
	private String shopTransactionFile = "plugins/MineAndCraft_plugin/ShopTransaction.log";
	private String playerJoinsFile = "plugins/MineAndCraft_plugin/PlayerJoinLog.log";
	public HashMap<Player, ArrayList<String>> blockCache = new HashMap<Player, ArrayList<String>>();
	private Guga_SERVER_MOD plugin;
}

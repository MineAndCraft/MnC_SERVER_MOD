package me.Guga.Guga_SERVER_MOD;

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
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
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
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
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
	public ArrayList<String> GetBlockBreakData(Block block, ArrayList<String> dataBuffer)
	{
		//ArrayList<String> dataBuffer = new ArrayList<String>();
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
		/*File file = new File(blockBreakFile);
		if (!file.exists())
		{
			try 
			{
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				FileInputStream fRead = new FileInputStream(file);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				String []splittedLine;
				int x;
				int y;
				int z;
				try {
					while ((line = bReader.readLine()) != null)
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
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		return dataBuffer;*/
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
		//Iterator<Entry<String, Integer>> i = dataMap.entrySet().iterator();
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
		//ArrayList<String> dataBuffer = new ArrayList<String>();
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
		/*File file = new File(blockPlaceFile);
		if (!file.exists())
		{
			try 
			{
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			try 
			{
				FileInputStream fRead = new FileInputStream(file);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				String []splittedLine;
				int x;
				int y;
				int z;
				try {
					while ((line = bReader.readLine()) != null)
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
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		return dataBuffer;*/
	}
	public void LogShopTransaction(final Prices item, final int amount, final String pName)
	{
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				//GugaFile file = new GugaFile(shopTransactionFile, GugaFile.APPEND_MODE);
				//file.Open();
				String line = new Date() + ";" + pName + ";" + item.toString() + ";" + amount;
				//file.WriteLine(line);
				//file.Close();
				GugaFile file = new GugaFile(shopTransactionFile, GugaFile.APPEND_MODE);
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
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					String line;
					//int typeID = e.getBlock().getTypeId();
					int x = e.getBlock().getX();
					int y = e.getBlock().getY();
					int z = e.getBlock().getZ();
					String pName = e.getPlayer().getName();
					Date now = new Date();
					line = now + ";" + pName + ";" + typeID + ";" + x + ";" + y + ";" + z;
					GugaFile file = new GugaFile(blockBreakFile, GugaFile.APPEND_MODE);
					file.Open();
					file.WriteLine(line);
					file.Close();
					/*File blockBreak = new File(blockBreakFile);
					if (!blockBreak.exists())
					{
						try 
						{
							blockBreak.createNewFile();
							
						} 
						catch (IOException ex) 
						{
							ex.printStackTrace();
						}
					}
					try 
					{
						FileWriter fStream = new FileWriter(blockBreak, true);
						BufferedWriter bWriter;
						bWriter = new BufferedWriter(fStream);
						String line;
						//int typeID = e.getBlock().getTypeId();
						int x = e.getBlock().getX();
						int y = e.getBlock().getY();
						int z = e.getBlock().getZ();
						String pName = e.getPlayer().getName();
						Date now = new Date();
						line = now + ";" + pName + ";" + typeID + ";" + x + ";" + y + ";" + z;
						bWriter.write(line);
						bWriter.newLine();
						bWriter.close();
						fStream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}*/
				}
			}
			
			);	
		}
	}
	public void LogBlockPlace(final BlockPlaceEvent e)
	{
		if (logBlockPlace)
		{
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
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
					GugaFile file = new GugaFile(blockPlaceFile, GugaFile.APPEND_MODE);
					file.Open();
					file.WriteLine(line);
					file.Close();
					/*File blockPlace = new File(blockPlaceFile);
					if (!blockPlace.exists())
					{
						try 
						{
							blockPlace.createNewFile();
							
						} 
						catch (IOException ex) 
						{
							ex.printStackTrace();
						}
					}
					try 
					{
						FileWriter fStream = new FileWriter(blockPlace, true);
						BufferedWriter bWriter;
						bWriter = new BufferedWriter(fStream);
						String line;
						int typeID = e.getBlock().getTypeId();
						int x = e.getBlock().getX();
						int y = e.getBlock().getY();
						int z = e.getBlock().getZ();
						String pName = e.getPlayer().getName();
						Date now = new Date();
						line = now + ";" + pName + ";" + typeID + ";" + x + ";" + y + ";" + z;
						bWriter.write(line);
						bWriter.newLine();
						bWriter.close();
						fStream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}*/
				}
			}
			
			);	
		}
	}
	public void LogBlockIgnite(final BlockIgniteEvent e)
	{
		if (logBlockIgnite)
		{
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
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
					/*File blockIgnite = new File(blockIgniteFile);
					if (!blockIgnite.exists())
					{
						try 
						{
							blockIgnite.createNewFile();
							
						} 
						catch (IOException ex) 
						{
							ex.printStackTrace();
						}
					}
					try 
					{
						FileWriter fStream = new FileWriter(blockIgnite, true);
						BufferedWriter bWriter;
						bWriter = new BufferedWriter(fStream);
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
						bWriter.write(line);
						bWriter.newLine();
						bWriter.close();
						fStream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}*/
				}
			}
			
			);	
		}
	}
	
	private boolean logBlockBreak;
	private boolean logBlockPlace;
	private boolean logBlockIgnite;
	private String blockBreakFile = "plugins/BlockBreakLog.log";
	private String blockIgniteFile = "plugins/BlockIgniteLog.log";
	private String blockPlaceFile = "plugins/BlockPlaceLog.log";
	private String shopTransactionFile = "plugins/ShopTransaction.log";
	public HashMap<Player, ArrayList<String>> blockCache = new HashMap<Player, ArrayList<String>>();
	private Guga_SERVER_MOD plugin;
}

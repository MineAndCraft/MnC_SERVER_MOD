package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
	public void LogBlockBreak(final BlockBreakEvent e)
	{
		if (logBlockBreak)
		{
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					File blockBreak = new File(blockBreakFile);
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
					}
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
					File blockPlace = new File(blockPlaceFile);
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
			plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					File blockIgnite = new File(blockIgniteFile);
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
					}
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
	private Guga_SERVER_MOD plugin;
}

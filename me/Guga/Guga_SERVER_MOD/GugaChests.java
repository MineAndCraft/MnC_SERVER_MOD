package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.block.Block;

public class GugaChests 
{
	GugaChests(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		LoadChests();
	}
	public void LockChest(Block chest,String chestOwner)
	{
		int i = 0;
		while (locX[i] != null)
		{
			i++;
		}
		String x = Double.toString(chest.getLocation().getX());
		String y = Double.toString(chest.getLocation().getY());
		String z = Double.toString(chest.getLocation().getZ());
		locX[i] = x;
		locY[i] = y;
		locZ[i] = z;
		owner[i] = chestOwner;
		SaveChests();
	}
	public void UnlockChest(Block chest,String chestOwner)
	{
		String bufferX[] = new String[10000];
		String bufferY[] = new String[10000];
		String bufferZ[] = new String[10000];
		String bufferOwn[] = new String[10000];
		
		String chestX = Double.toString(chest.getLocation().getX());
		String chestY = Double.toString(chest.getLocation().getY());
		String chestZ = Double.toString(chest.getLocation().getZ());
		int i = 0;
		int i2 = 0;
		while (locX[i2] != null)
		{
			if ((locX[i2].matches(chestX)) && (locY[i2].matches(chestY)) && (locZ[i2].matches(chestZ)))
			{
				i2++;
			}
			bufferX[i] = locX[i2];
			bufferY[i] = locY[i2];
			bufferZ[i] = locZ[i2];
			bufferOwn[i] = owner[i2];
			i++;
			i2++;
		}
		i=0;
		locX = new String[10000];
		locY = new String[10000];
		locZ = new String[10000];
		owner = new String[10000];
		while (bufferX[i] != null)
		{
			locX[i] = bufferX[i];
			locY[i] = bufferY[i];
			locZ[i] = bufferZ[i];
			owner[i] = bufferOwn[i];
			i++;
		}
		SaveChests();
	}
	public String GetChestOwner(Block chest)
	{
		String chestX = Double.toString(chest.getLocation().getX());
		String chestY = Double.toString(chest.getLocation().getY());
		String chestZ = Double.toString(chest.getLocation().getZ());
		int i = 0;
		while (locX[i] != null)
		{
			if ((locX[i].matches(chestX)) && (locY[i].matches(chestY)) && (locZ[i].matches(chestZ)))
			{
				return owner[i];
			}
			i++;
		}
		return "notFound";
		
	}
	public void LoadChests()
	{
		plugin.log.info("Loading Chest Data...");
		File chests = new File(chestsFile);
		if (!chests.exists())
		{
			try 
			{
				chests.createNewFile();
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
				FileInputStream fRead = new FileInputStream(chests);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				int i = 0;
				while ((line = bReader.readLine()) != null)
				{
					locX[i] = line.split(";")[0];
					locY[i] = line.split(";")[1];
					locZ[i] = line.split(";")[2];
					owner[i] = line.split(";")[3];
					i++;
				}
				bReader.close();
				inStream.close();
				fRead.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void SaveChests()
	{
		plugin.log.info("Saving Chest Data...");
		File chests = new File(chestsFile);
		if (!chests.exists())
		{
			try 
			{
				chests.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
			try {
				int i = 0;
				FileWriter fStream = new FileWriter(chests, false);
				BufferedWriter bWriter;
				bWriter = new BufferedWriter(fStream);
				while (locX[i] != null)
				{
					String x = locX[i];
					String y = locY[i];
					String z = locZ[i];
					
					String line;
					line = x+";"+y+";"+z+";"+owner[i];
					bWriter.write(line);
					bWriter.newLine();
					i++;
				}
					bWriter.close();
					fStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	private String owner[] = new String[10000];
	private String locX[] = new String[10000];
	private String locY[] = new String[10000];
	private String locZ[] = new String[10000];
	
	private String chestsFile = "plugins/Chests.dat";
	public static Guga_SERVER_MOD plugin;
}

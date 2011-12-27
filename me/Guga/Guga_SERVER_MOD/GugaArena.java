package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GugaArena 
{
	GugaArena(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		worldName = "arena";
	}
	GugaArena(Guga_SERVER_MOD gugaSM, Location spawn)
	{
		arenaSpawn = spawn;
		plugin = gugaSM;
		worldName = arenaSpawn.getWorld().getName();
	}
	public void ArenaKill(Player killer, Player victim)
	{
		if (victim != killCache)
		{
			GugaProfession prof;
			if ((prof = plugin.professions.get(killer.getName())) != null)
			{
				prof.GainExperience(200);
			}
			//killer.getWorld().dropItem(killer.getLocation(), new ItemStack(262,20));
			//plugin.getServer().broadcastMessage(killer.getName() + " rozsekal " + victim.getName() + " v Arene!");
			killCache = victim;
			ClearCache();
			DisableLeave(killer,60);
			IncreasePvpStats(killer);
			SavePvpStats();
		}
	}
	public void PlayerJoin(Player p)
	{
		if (!IsArena(p.getLocation()))
		{
			baseLocation.put(p.getName(), p.getLocation());
		}
		p.getServer().broadcastMessage(p.getName() + " vstoupil do Areny!");
		InventoryBackup.InventoryClearWrapped(p);
		GiveItems(p);
		if (arenaSpawn != null)
		{
			p.teleport(arenaSpawn);
		}
		else
		{
			p.teleport(plugin.getServer().getWorld(worldName).getSpawnLocation());
		}
		DisableLeave(p, 60);
	}
	public void PlayerLeave(Player p)
	{
		if (cannotLeave.contains(p.getName()))
		{
			p.sendMessage("Na opusteni areny je prilis brzy!");
			return;
		}
		if (!IsArena(p.getLocation()))
		{
			p.sendMessage("Tento prikaz jde pouzit pouze v arene!");
			return;
		}
		if (baseLocation.get(p.getName()) != null)
		{
			p.teleport(baseLocation.get(p.getName()));
			baseLocation.remove(p.getName());
		}
		else
		{
			p.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
		}
		p.getServer().broadcastMessage(p.getName() + " opustil Arenu");
		InventoryBackup.InventoryReturnWrapped(p, true);
	}
	public boolean IsArena(Location loc)
	{
		if (loc.getWorld().getName().matches(worldName))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void RemoveSpawn(Player sender)
	{
		arenaSpawn = null;
		SaveArena();
		sender.sendMessage("Arena Spawn has been removed.");
	}
	public void SetSpawn(Player sender)
	{
		arenaSpawn = sender.getLocation();
		worldName = arenaSpawn.getWorld().getName();
		SaveArena();
		sender.sendMessage("Arena Spawn has been set.");
	}
	private void ClearCache()
	{
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				killCache = null;
			}
		}, 60);
	}
	private void DisableLeave(final Player p, int seconds)
	{
		int serverTicks = seconds*20;
		cannotLeave.add(p.getName());
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				cannotLeave.remove(p.getName());
			}
		}, serverTicks);
	}
	public String GetWorldName()
	{
		return worldName;
	}
	public Location GetPlayerBaseLocation(Player p)
	{
		return baseLocation.get(p.getName());

	}
	public void SetPlayerBaseLocation(Player p, Location newLoc)
	{
		baseLocation.put(p.getName(), newLoc);
	}
	public void RemovePlayerBaseLocation(Player p)
	{
		baseLocation.remove(p.getName());
	}
	public int GetPlayerStats(Player p)
	{
		return this.pvpStats.get(p.getName());
	}
	public void IncreasePvpStats(Player p)
	{
		String pName = p.getName();
		Integer kills = pvpStats.get(pName);
		if (kills == null)
		{
			kills = 1;
		}
		else
		{
			kills++;
		}
		ArenaTier tier = ArenaTier.GetTier(kills);
		if (ArenaTier.GetTier(kills - 1) != tier)
			plugin.getServer().broadcastMessage(p.getName() + " byl povysen na rank " + tier.toString() + "");
		pvpStats.put(pName, kills);
	}
	public void ShowPvpStats(Player sender)
	{		
		Iterator<Entry<String,Integer>> iterator;
		iterator = pvpStats.entrySet().iterator();
		ArrayList<String> values = new ArrayList<String>();
		ArrayList <String> buffer = new ArrayList<String>();
		while (iterator.hasNext())
		{
			String element;
			String value;
		
			element = iterator.next().toString();
			value = element.split("=")[1];
			
			buffer.add(element);
			
			values.add(value);
		}
		Object[] valuesArray = values.toArray(); 
		int[] valuesInt = new int[valuesArray.length];
		
		int i = 0;
		while (i < valuesInt.length)
		{
			valuesInt[i] = Integer.parseInt(valuesArray[i].toString());
			i++;
		}
		Arrays.sort(valuesInt);
		i = 0;
		sender.sendMessage("******************************");
		sender.sendMessage("    ARENA STATS (NAME - KILLS)");
		while (i < valuesInt.length)
		{
			String pName = "";
			Iterator<String> iter = buffer.iterator();
			while (iter.hasNext())
			{
				String element = iter.next().toString();
				String name = element.split("=")[0];
				String value = element.split("=")[1];
				if (value.matches(Integer.toString(valuesInt[i])))
				{
					pName = name;
					buffer.remove(element);
					break;
				}
			}
			sender.sendMessage(pName+" - "+valuesInt[i]);
			i++;
		}
	}
	public void SaveArena()
	{
		plugin.log.info("Saving Arena Data...");
		File arena = new File(arenaFile);
		if (!arena.exists())
		{
			try 
			{
				arena.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			FileWriter fStream = new FileWriter(arena, false);
			BufferedWriter bWriter;
			bWriter = new BufferedWriter(fStream);
			String line;
			if (arenaSpawn != null)
			{
				line = worldName+";" + arenaSpawn.getX() + ";" + arenaSpawn.getY() + ";" + arenaSpawn.getZ();
			}
			else 
			{
				line = "";
			}
			bWriter.write(line);
			bWriter.newLine();
			bWriter.close();
			fStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean LoadArena()
	{
		plugin.log.info("Loading Arena Data...");
		File arena = new File(arenaFile);
		if (!arena.exists())
		{
			try 
			{
				arena.createNewFile();
				return false;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			try 
			{
				FileInputStream fRead = new FileInputStream(arena);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				String []splittedLine;
				double x = 0;
				double y = 0;
				double z = 0;
				try {
					while ((line = bReader.readLine()) != null)
					{
						splittedLine = line.split(";");
						worldName = splittedLine[0];
						x = Double.parseDouble(splittedLine[1]);
						y = Double.parseDouble(splittedLine[2]);
						z = Double.parseDouble(splittedLine[3]);
						arenaSpawn = new Location(plugin.getServer().getWorld(worldName),x,y,z);
					}
					bReader.close();
					inStream.close();
					fRead.close();
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public void SavePvpStats()
	{
			plugin.log.info("Saving PvpStats Data...");
			File arena = new File(statsFile);
			if (!arena.exists())
			{
				try 
				{
					arena.createNewFile();
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			try 
			{
				FileWriter fStream = new FileWriter(arena, false);
				BufferedWriter bWriter;
				bWriter = new BufferedWriter(fStream);
				String line;
					
				Iterator<Entry<String,Integer>> iterator;
				iterator = pvpStats.entrySet().iterator();
				ArrayList<String> values = new ArrayList<String>();
				ArrayList <String> buffer = new ArrayList<String>();
				while (iterator.hasNext())
				{
					String element;
					String value;
				
					element = iterator.next().toString();
					value = element.split("=")[1];
					
					buffer.add(element);
					values.add(value);
				}
				Object[] valuesArray = values.toArray(); 
				int[] valuesInt = new int[valuesArray.length];
				
				int i = 0;
				while (i < valuesInt.length)
				{
					valuesInt[i] = Integer.parseInt(valuesArray[i].toString());
					i++;
				}
				i = 0;
				while (i<valuesInt.length)
				{
					Iterator<String> iter = buffer.iterator();
					String pName = "";
					while (iter.hasNext())
					{
						String element = iter.next().toString();
						String name = element.split("=")[0];
						String value = element.split("=")[1];
						if (value.matches(Integer.toString(valuesInt[i])))
						{
							pName = name;
							buffer.remove(element);
							break;
						}
					}
					line = pName + ";" + valuesInt[i];
					
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
	public void LoadPvpStats()
	{
		plugin.log.info("Loading PvpStats Data...");
		File arena = new File(statsFile);
		if (!arena.exists())
		{
			try 
			{
				arena.createNewFile();
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
				FileInputStream fRead = new FileInputStream(arena);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				String[] splittedLine;
				String name;
				String kills;
				try {
					while ((line = bReader.readLine()) != null)
					{
						splittedLine = line.split(";");
						name = splittedLine[0];
						kills = splittedLine[1];
						pvpStats.put(name, Integer.parseInt(kills));
					}
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void GiveItems(Player p)
	{
		PlayerInventory inv = p.getInventory();
		if (inv.getContents().length > 0)
			inv.clear();
		Integer kills;
		if ((kills = pvpStats.get(p.getName())) == null)
			kills = 0;
		ArenaTier tier = ArenaTier.GetTier(kills.intValue());
		int[] armor = tier.GetArmor();
		if (armor[0] != 0)
			inv.setHelmet(new ItemStack(armor[0], 1));
		if (armor[1] != 0)
			inv.setChestplate(new ItemStack(armor[1], 1));
		if (armor[2] != 0)
			inv.setLeggings(new ItemStack(armor[2], 1));
		if (armor[3] != 0)
			inv.setBoots(new ItemStack(armor[3], 1));
		
		int i = 0;
		int[][] inventory = tier.GetItems();
		while (i < (inventory.length))
		{
			inv.addItem(new ItemStack(inventory[0][i], inventory[1][i]));
			i++;
		}
	}
	public enum ArenaTier
	{
		NOVACEK		(0, new int[][]{{268, 297}, {5, 10}}, new int[]{0, 0, 0, 0}), 
		BOJOVNIK	(5, new int[][]{{268, 297}, {5, 10}}, new int[]{298, 0, 0, 0}), 
		TIER3		(10, new int[][]{{268, 297}, {5, 10}}, new int[]{298, 0, 0, 301}), 
		TIER4		(20, new int[][]{{268, 297}, {5, 10}}, new int[]{298, 0, 300, 301}), 
		TIER5		(30, new int[][]{{268, 297}, {5, 10}}, new int[]{298, 299, 300, 301}),
		TIER6		(40, new int[][]{{268, 297, 261, 262}, {5, 10, 1, 2}}, new int[]{298, 299, 300, 301}),
		TIER7		(50, new int[][]{{272, 297, 261, 262}, {5, 10, 1, 5}}, new int[]{298, 299, 300, 301}),
		TIER8		(60, new int[][]{{272, 297, 261, 262}, {5, 10, 1, 10}}, new int[]{314, 299, 300, 301});
		
		private ArenaTier(int killsRequired, int[][] items, int[] armor)
		{
			this.killsRequired = killsRequired;
			this.items = items;
			this.armor = armor;
		}
		public int GetKillsReq()
		{
			return this.killsRequired;
		}
		public int[][] GetItems()
		{
			return this.items;
		}
		public int[] GetArmor()
		{
			return this.armor;
		}
		public static ArenaTier GetTier(int kills)
		{
			ArenaTier[] vals = ArenaTier.values();
			int i = vals.length - 1;
			while (i >= 0)
			{
				if (kills >= vals[i].GetKillsReq())
					return vals[i];
				i--;
			}
			return null;
		}
		private int killsRequired;
		private int[][] items; 
		private int[] armor;
	}
	private Player killCache;
	
	private HashMap<String,Integer> pvpStats = new HashMap<String,Integer>();
	
	private HashMap<String,Location> baseLocation = new HashMap<String,Location>();
	private ArrayList<String> cannotLeave = new ArrayList<String>();
	private Location arenaSpawn;
	private String worldName;
	private Guga_SERVER_MOD plugin;
	private static final String arenaFile = "plugins/Arenas.dat";
	private static final String statsFile = "plugins/ArenaStats.dat";
}

package me.MnC.MnC_SERVER_MOD.permissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GroupManager
{
	private static YamlConfiguration groupConfiguration = null;
	private static ArrayList<Group> groups = null;

	public static void createGroup(ArrayList<String> players, ArrayList<String> permissions, String groupName, String chatFormat, String playerListFormat, int groupPriority)
	{
		Group gr = new Group(players, permissions, groupName, chatFormat, playerListFormat, groupPriority);
		if (groups == null)
		{
			groups = new ArrayList<Group>();
		}
		groups.add(gr);
	}

	public static ArrayList<Group> getPlayersGroups(String playerName)
	{
		ArrayList<Group> playersGroups = null;
		if (groups != null)
		{
			for (Group g : groups)
			{
				if (g.getPlayers().contains(playerName))
				{
					if (playersGroups == null)
					{
						playersGroups = new ArrayList<Group>();
						playersGroups.add(g);
					} 
					else
					{
						playersGroups.add(g);
					}
				}
			}
			groupsPriorityQuicksort(playersGroups);
		}
		return playersGroups;
	}
	
	public static ArrayList<Group> getGroups()
	{
		return groups;
	}
	
	private static void groupsPriorityQuicksort(ArrayList<Group> groups, int start, int end)
	{
		 int s = start;
	     int e = end;
	     if (end - start >= 1) 
	     { 
	    	 int pivot = groups.get(start).getPriority();     
	         while(e > s) 
	         {      
	        	 while(groups.get(s).getPriority() <= pivot && s <= end && e > s) 
	        	 {
	        		 s++;
	             }
	        	 while(groups.get(e).getPriority() > pivot && e >= start && e >= s) 
	        	 {
	        		 e--;
	        	 }
	        	 if(s < e) 
	        	 {
	        		 groupArrayListSwap(groups, s, e);
	        	 }
	         }
	         groupArrayListSwap(groups, e, start);
	         groupsPriorityQuicksort(groups, start, e - 1);
	         groupsPriorityQuicksort(groups, ++e, end);   
	     } 
	     else 
	     {
	    	 return;
	     }
	}
	
	private static void groupArrayListSwap(ArrayList<Group> groups, int start, int end)
	{
		Group temp = groups.get(end);
		groups.set(end, groups.get(start));
		groups.set(start, temp);
	}
	
	private static void groupsPriorityQuicksort(ArrayList<Group> groups)
	{
		groupsPriorityQuicksort(groups, 0, groups.size() - 1);
	}
	
	public static void loadGroupConfiguration()
	{
		File configFile = new File(MnC_SERVER_MOD.getInstance().getDataFolder(), "groups.yml");
		if(!configFile.exists())
		{
			try 
			{
				MnC_SERVER_MOD.getInstance().getDataFolder().mkdir();
				InputStream jarPath = GroupManager.class.getResourceAsStream("/groups.yml");
				copyFileFromJar(jarPath, configFile);
				groupConfiguration = new YamlConfiguration();
				groupConfiguration.load(configFile);
			} 
			catch (IOException e) 
			{
				//TODO: LOG MESSAGES
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}	
		}
		else
		{
			groupConfiguration = new YamlConfiguration();
			try 
			{
				groupConfiguration.load(configFile);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		}
		List<String> groups = groupConfiguration.getStringList("grouplist");
		
		for(String s : groups)
		{
			ArrayList<String> players = new ArrayList<String>(groupConfiguration.getStringList("groups." + s + ".players"));
			ArrayList<String> permissions = new ArrayList<String>(groupConfiguration.getStringList("groups." + s + ".permissions"));
			String groupName = s;
			String chatFormat = groupConfiguration.getString("groups." + s + ".chatformat");
			String playerListFormat = groupConfiguration.getString("groups." + s + ".playerlistformat");
			int groupPriority = groupConfiguration.getInt("groups." + s + ".priority");
			createGroup(players,permissions,groupName,chatFormat,playerListFormat,groupPriority);
		}
	}
	
	private static void copyFileFromJar(InputStream in, File out) throws FileNotFoundException
	{
		InputStream fileIS = in;
		FileOutputStream fileOS = new FileOutputStream(out);
		
		byte[] buff = new byte[1024];
		int i = 0;
		try 
		{
			while((i = fileIS.read(buff)) != -1)
			{
				fileOS.write(buff, 0, i);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			if(fileIS != null)
			{
				try
				{
					fileIS.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			if(fileOS != null)
			{
				try 
				{
					fileOS.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}	
			}
		}
	}
	
	public static void resolvePlayersGroups(Player p)
	{
		ArrayList<Group> groups = getPlayersGroups(p.getName());
		if(groups != null)
		{
			for(Group g : groups)
			{
				for(String s : g.getPermissions())
					p.addAttachment(MnC_SERVER_MOD.getInstance(),s,true);
			}
			p.setPlayerListName(groups.get(groups.size() - 1).getPlayerListFormat().replace("%player", p.getName()));
		}
	}
}

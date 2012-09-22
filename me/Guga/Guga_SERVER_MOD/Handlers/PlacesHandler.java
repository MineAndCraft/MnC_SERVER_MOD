package me.Guga.Guga_SERVER_MOD.Handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.Guga.Guga_SERVER_MOD.GugaFile;
import me.Guga.Guga_SERVER_MOD.GugaVirtualCurrency;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Places;

public class PlacesHandler 
{
	public static void setPlugin(Guga_SERVER_MOD SM)
	{
		plugin = SM;
	}
	
	public static void loadPlaces()
	{
		plugin.log.info("Loading Places file..."); 
		GugaFile file = new GugaFile("plugins/PlacesNew.dat", GugaFile.READ_MODE);
		file.Open();
		String line;
		String portName;
		String world;
		String owner;
		String[] allowedPlayers;
		String welcomeMsg;
		int x;
		int y;
		int z;
		String[] splittedLine;
		while ((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			portName = splittedLine[0];
			owner = splittedLine[1];
			allowedPlayers = splittedLine[2].split(",");
			x = Integer.parseInt(splittedLine[3]);
			y = Integer.parseInt(splittedLine[4]);
			z = Integer.parseInt(splittedLine[5]);
			world = splittedLine[6];
			welcomeMsg = splittedLine[7];
			newPlaces.add(new Places(portName, owner, allowedPlayers, x, y, z, world, welcomeMsg));
		}
		file.Close();
	}
	public static void savePlaces()
	{
		plugin.log.info("Saving Places file..."); 
		GugaFile file = new GugaFile("plugins/PlacesNew.dat", GugaFile.WRITE_MODE);
		file.Open();
		String line;
		Places place;
		String portName;
		String world;
		String owner;
		String allowedPlayers;
		String welcomeMsg;
		int x;
		int y;
		int z;
		Iterator<Places> i = PlacesHandler.newPlaces.iterator();
		while (i.hasNext())
		{
			place = i.next();
			portName = place.getPortName();
			owner = place.getPortOwner();
			allowedPlayers = OwnersToLine(place.getAllowedPlayers());
			x = place.getX();
			y = place.getY();
			z = place.getZ();
			world = place.getWorld();
			welcomeMsg = place.getWelcomeMsg();
			line = portName + ";" + owner +  ";" + allowedPlayers + ";" + x + ";" + y + ";" + z + ";" + world + ";" + welcomeMsg;
			file.WriteLine(line);
		}
		file.Close();
	}
	
	private static String OwnersToLine(String[] owners)
	{
		int i = 0;
		String ownersString = "";
		while (i < owners.length)
		{
			if (i == owners.length - 1)
				ownersString += owners[i];
			else
				ownersString += owners[i] + ",";
			
			i++;
		}
		return ownersString;
	}
	
	public static Location getLocation(Places place)
	{
		return new Location(plugin.getServer().getWorld(place.getWorld()), place.getX(), place.getY(), place.getZ());
	}
	public static Places getPlaceByName(String portName)
	{
		Iterator<Places> i = PlacesHandler.newPlaces.iterator();
		Places place;
		while(i.hasNext())
		{
			place = i.next();
			if(place.getPortName().equalsIgnoreCase(portName))
			{
				return place;
			}
		}
		return null;
	}
	public static ArrayList<Places> getPlacesByOwner(String owner)
	{
		Iterator<Places> i = PlacesHandler.newPlaces.iterator();
		Places place;
		ArrayList <Places> places = new ArrayList <Places>();
		while(i.hasNext())
		{
			place = i.next();
			if(place.getPortOwner().equalsIgnoreCase(owner))
			{
				places.add(place);
			}
		}
		return places;
	}
	public static boolean isOwner (String portName, String owner)
	{
		Places place = PlacesHandler.getPlaceByName(portName);
		if(place == null)
			return false;
		if(place.getPortOwner().equalsIgnoreCase(owner))
			return true;
		else
			return false;
	}
	
	public static boolean CanTeleport (String portName, String sender)
	{
		Places place = PlacesHandler.getPlaceByName(portName);
		String[] allowedPlayers = place.getAllowedPlayers();
		int i = 0;
		while(i < allowedPlayers.length)
		{
			if(allowedPlayers[i].equalsIgnoreCase(sender))
				return true;
			if(allowedPlayers[i].equalsIgnoreCase("all"))
				return true;
			else if(allowedPlayers[i].equalsIgnoreCase("vip"))
			{
				GugaVirtualCurrency curr = plugin.FindPlayerCurrency(sender);
				if(curr.IsVip())
					return true;
				else
					return false;
			}
			i++;
		}
		return false;
	}
	
	public static ArrayList<Places> getPlacesByPlayer (String player)
	{
		Iterator<Places> it = PlacesHandler.newPlaces.iterator();
		Places place;
		ArrayList <Places> places = new ArrayList <Places>();
		while(it.hasNext())
		{
			place = it.next();
			String[] allowedPlayers = place.getAllowedPlayers();
			int i = 0;
			while(i < allowedPlayers.length)
			{
				if(allowedPlayers[i].equalsIgnoreCase(player))
					places.add(place);
				if(allowedPlayers[i].equalsIgnoreCase("all"))
					places.add(place);
				i++;
			}
		}
		return places;
	}
	
	public static void removePlace(Places place)
	{
		newPlaces.remove(place);
		savePlaces();
	}
	
	public static void addPlace(Places place)
	{
		newPlaces.add(place);
		savePlaces();
	}
	
	public static void teleport(Player sender, Places place) 
	{
		sender.teleport(getLocation(place));
		sender.sendMessage(ChatColor.LIGHT_PURPLE + place.getWelcomeMsg());
	}
	public static ArrayList <Places> newPlaces = new ArrayList<Places>();
	public static Guga_SERVER_MOD plugin;
}

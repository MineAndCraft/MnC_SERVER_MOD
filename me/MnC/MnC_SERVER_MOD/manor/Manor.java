package me.MnC.MnC_SERVER_MOD.manor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.data.Polygon;

/**
 * Representation of manor object
 * Since there are planed to be only a few manors, the data is in memory
 *
 */
public class Manor
{	
	private int id;
	
	private String name;
	
	private int lord_id = 0;
	
	private String lord_name; // this is being loaded just as a buffer
	
	private List<Integer> citizenIds = new ArrayList<Integer>();
	
	private Map<String,Boolean> flags = new HashMap<String,Boolean>();
	
	private Polygon boundaries;
	
	private Location spawnLocation; 
	
	public Manor(int manorId)
	{
		id = manorId;
		loadData();
	}
	
	/**
	 * Saves the data for this manor into database
	 */
	public void save()
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE mnc_manors SET name = ?, lord_id = ?, boundaries = ?, flags = ?, citizens = ?, spawn_x = ?, spawn_y = ?, spawn_z = ? WHERE id = ? LIMIT 1");)
		{
			// manor name
			stat.setString(1, name);
			
			// lord id
			stat.setInt(2, lord_id);
			
			// boundaries
			stat.setString(3, Polygon.toString(this.boundaries));			
			
			// flags
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, Boolean> flag : this.flags.entrySet())
			{
				sb.append(flag.getKey()).append("=").append(flag.getValue()).append("\n");
			}
			stat.setString(4, sb.toString());
			
			// citizens
			StringBuilder sb2 = new StringBuilder();
			for(Integer citizenId : this.citizenIds)
			{
				sb2.append(citizenId).append("\n");
			}
			stat.setString(5, sb2.toString());			
			
			// spawn location
			if(spawnLocation != null)
			{
				stat.setFloat(6, (float)spawnLocation.getX());
				stat.setFloat(7, (float)spawnLocation.getY());
				stat.setFloat(8, (float)spawnLocation.getZ());
			}
			else
			{
				stat.setString(6, "NULL");
				stat.setString(7, "NULL");
				stat.setString(8, "NULL");
			}
			
			// manor id
			stat.setInt(9, id);
			
			stat.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all data for this manor from the database
	 * Can be also used for reloading
	 */
	public void loadData()
	{
		// load basic data
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT m.*,u.username as lord_name FROM mnc_manors m LEFT JOIN mnc_users u ON m.lord_id = u.id WHERE m.id = ? LIMIT 1");)
		{
			stat.setInt(1,id);
			ResultSet rset = stat.executeQuery();
			if(!rset.next())
				throw new IllegalArgumentException("The manor #"+id+" does not exist");
			
			// name
			name = rset.getString("name");
			
			// lord id
			lord_id = rset.getInt("lord_id");
			
			// lord name
			lord_name = rset.getString("lord_name");
			
			// boundaries
			boundaries = Polygon.fromString(rset.getString("boundaries"));
			
			// flags
			String flagsString = rset.getString("flags");
			if(flagsString.length() > 0)
			{
				for(String flag : flagsString.split("\n"))
				{
					String[] flag_split = flag.split("=");
					this.flags.put(flag_split[0], Boolean.valueOf(flag_split[1]));
				}
			}
			
			// citizens
			String citizensString = rset.getString("citizens");
			if(citizensString.length() > 0)
			{
				for(String citizen : citizensString.split("\n"))
				{
					this.citizenIds.add(Integer.valueOf(citizen));
				}
			}
			
			// spawn location
			if(rset.getInt("spawn_y") == 0)
				spawnLocation = null;
			else
			spawnLocation = new Location(Bukkit.getServer().getWorld("world"), rset.getFloat("spawn_x"), rset.getFloat("spawn_y"), rset.getFloat("spawn_z"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//TODO make the manor to be seen on dynmap
	}
	

	/**
	 * @return The id of this manor
	 */
	public int getId()
	{
		return id;
	}
	

	/**
	 * @return The name of this manor
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return The id of the lord of this manor
	 */
	
	public int getLordId()
	{
		return lord_id;
	}
	
	
	/**
	 * @param player The player to be tested
	 * @return whether The {@code player} is citizen of this manor or not
	 */
	public boolean isCitizen(MinecraftPlayer player)
	{
		return citizenIds.contains(player.getId());
	}
	
	/**
	 * Check manor for specified flag.
	 * @param flag The name of the flag to test
	 * @return true if manor has {@code flag} set and it's true, false otherwise
	 */
	public boolean getFlag(String flag)
	{
		return flags.containsKey(flag) && flags.get(flag);
	}
	
	/**
	 * Sets the {@code value} for {@code flag} 
	 */
	public void setFlag(String flag, boolean value)
	{
		flags.put(flag, value);
		save();
	}


	/**
	 * Checks if the manor contains that location 
	 * Checks only X and Z coordinate
	 * 
	 * @param location the location to be checked
	 * @return whether this manor contains this location
	 */
	public boolean contains(Location location)
	{		
		return this.boundaries.contains(location.getBlockX(), location.getBlockZ());
	}
	
	/**
	 * @return Spawn location for this manor
	 */
	public Location getSpawnLocation()
	{
		return this.spawnLocation;
	}

	/**
	 * Doesn't work currently
	 * @return The name of the lord of this manor
	 */
	public String getLordName()
	{
		return lord_name;
	}


	/**
	 * Adds a player to be a citizen of this manor
	 * @param playerId The id of the player to be added
	 */
	public void addCitizen(int playerId)
	{
		this.citizenIds.add(Integer.valueOf(playerId));
		save();
	}

	/**
	 * Removes a player from being a ctizen of this manor
	 * @param playerId The id of the player to be removed
	 */
	public void removeCitizen(int playerId)
	{
		this.citizenIds.remove(Integer.valueOf(playerId));
		save();
	}
	
	/**
	 * @return case-correct player names of citizens of this manor
	 */
	public List<String> getCitizenNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT username as name FROM mnc_users WHERE id = ? LIMIT 1;");)
		{
			for(int id : this.citizenIds)
			{
				stat.setInt(1, id);
				ResultSet rset = stat.executeQuery();
				list.add(rset.getString("name"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	
	/**
	 * Sets the lord of this manor
	 * @param lordId The id of the player to be set as the lord of this manor
	 */
	public void setLordId(int lordId)
	{
		this.lord_id = lordId;
		save();
	}

	public void setSpawnLocation(Location location)
	{
		this.spawnLocation = location;
		save();
	}

	
	/**
	 * @return Whether the {@code player} can break or place blocks in this manor
	 */
	public boolean canBlockPlaceBreak(MinecraftPlayer player)
	{
		if(player.getId() == this.lord_id || this.citizenIds.contains(player.getId()) || GameMasterHandler.IsAtleastRank(player.getName(), Rank.BUILDER))
			return true;
		
		return false;
	}

	
	/**
	 * @return Whether the {@code player} can use bucket in this manor
	 */
	public boolean canUseBucket(MinecraftPlayer player)
	{
		return canBlockPlaceBreak(player);
	}

	
	public Polygon getBoundaries()
	{
		return boundaries;
	}
}

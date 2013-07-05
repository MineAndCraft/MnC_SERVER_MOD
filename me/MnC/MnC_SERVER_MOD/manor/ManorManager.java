package me.MnC.MnC_SERVER_MOD.manor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.data.Point2D;
import me.MnC.MnC_SERVER_MOD.data.Polygon;



import org.bukkit.Location;
import org.bukkit.command.PluginCommand;


public class ManorManager
{
	private static ManorManager _instance;
	
	private List<Manor> manors = new ArrayList<Manor>();
	
	public ManorManager()
	{
		_instance = this;
		
		this.loadManors();
		
		registerCommands();
	}
	
	public static ManorManager getInstance()
	{
		return _instance;
	}
	
	public void registerCommands()
	{
		PluginCommand manor = MnC_SERVER_MOD.getInstance().getCommand("manor");
		manor.setExecutor(new ManorCommandExecutor(this));
	}
	
	public void loadManors()
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT `id` FROM mnc_manors ");)
		{
			ResultSet rset = stat.executeQuery();
			while(rset.next())
			{
				manors.add(new Manor(rset.getInt("id")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public boolean reloadManor(String name)
	{
		int manorId = 0;
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT id FROM mnc_manors WHERE LOWER(name) = ? LIMIT 1");)
		{
			stat.setString(1, name.toLowerCase());
			manorId = stat.executeQuery().getInt("id");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(manorId == 0)
			return false;
		
		this.manors.remove(manorId);
		Manor newManor = new Manor(manorId);
		this.manors.add(newManor);
		
		ManorDynMapHandler.reloadManor(newManor);
		
		return true;
	}
	
	public Manor getManorByLocation(Location location)
	{
		if(!location.getWorld().getName().equals("world"))
			return null;
		
		for(Manor manor : this.manors)
		{
			if(manor.contains(location))
				return manor;
		}
		return null;
	}
	
	public Manor getManorByName(String name)
	{
		for(Manor manor : this.manors)
		{
			if(manor.getName().equalsIgnoreCase(name))
				return manor;
		}
		return null;
	}
	
	public List<Manor> getManorList()
	{
		return Collections.unmodifiableList(this.manors);
	}

	
	public void createManor(String name, Point2D[] boundaries)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT INTO mnc_manors (name,boundaries) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS);)
		{
			stat.setString(1, name.toLowerCase());
			stat.setString(2, Polygon.toString(new Polygon(boundaries)));
			stat.executeUpdate();
			
			ResultSet keys = stat.getGeneratedKeys();
			keys.next();
			Manor newManor = new Manor(keys.getInt("id"));
			this.manors.add(newManor);
			
			ManorDynMapHandler.addManor(newManor);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

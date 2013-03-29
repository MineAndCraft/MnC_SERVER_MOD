package me.Guga.Guga_SERVER_MOD.Estates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import me.Guga.Guga_SERVER_MOD.DatabaseManager;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;


public class EstatesDynMapHandler
{
	private static DynmapAPI api = null;
	private static MarkerAPI markApi;
	private static MarkerSet markSet;

	private static Map<String, AreaMarker> estateMarkers = new HashMap<String, AreaMarker>();
	
	public static void setup()
	{
		Plugin test = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
		if ((test == null) || (!test.isEnabled())) return;
		try
		{
			api = (DynmapAPI)test;
		}
		catch (Exception ex)
		{
			Guga_SERVER_MOD.getInstance().log.warning("[Estate] failed to hook to dynmap.");
			return;
		}
 
		try
		{
			markApi = api.getMarkerAPI();
			if (markApi == null) return;
		}
		catch (NullPointerException ex)
		{
			Guga_SERVER_MOD.getInstance().log.warning("[Estate] failed to hook to dynmap.");
			return;
		}
		
		Guga_SERVER_MOD.getInstance().log.info("[Estate] sucessfully hooked to dynmap");
		
		showAllEstates();
	}

	public static void showAllEstates()
	{
		removeAllEstates();
		
		if (markSet != null)
			markSet.deleteMarkerSet();
		markSet = null;
	
		markSet = markApi.getMarkerSet("mnc.estates.markerset");
		if (markSet == null)
			markSet = markApi.createMarkerSet("mnc.estates.markerset", "Estates", null, false);
		else
			markSet.setMarkerSetLabel("Estates");

		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT r.name as estate_name, u.username as owner_username, r.x1, r.x2, r.z1, r.z2 FROM mnc_residences r LEFT JOIN mnc_users u ON u.id=r.owner_id"))
		{
			try(ResultSet rset = stat.executeQuery();)
			{
				while(rset.next())
				{
					String estateName = rset.getString("estate_name");
					String estateOwner = rset.getString("owner_username");
					double[] xVals = { rset.getInt("x1"), rset.getInt("x2")};
					double[] zVals = { rset.getInt("z1"), rset.getInt("z2")};					
					AreaMarker marker = markSet.createAreaMarker("estate_" + estateName, "This is an estate "+estateName+" of "+estateOwner, false, "world", xVals, zVals, true);
					if(marker == null)
						continue;
					marker.setLineStyle(3, 1.0D, 16001680);
					marker.setFillStyle(0.0D, 0);
					estateMarkers.put(estateName, marker);
				}
			}
			Guga_SERVER_MOD.getInstance().log.info("[Estates] displayed "+estateMarkers.size()+" estates on dynmap.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void removeAllEstates()
	{
		for (AreaMarker marker : estateMarkers.values())
		{
			marker.deleteMarker();
		}
		estateMarkers.clear();
	}

	public static void disable()
	{
		removeAllEstates();
		api = null;
	}

	public static void reloadEstate(String name)
	{
		if(api == null || markApi == null || markSet == null)
			return;
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT r.name as estate_name, u.username as owner_username, r.x1, r.x2, r.z1, r.z2 FROM mnc_residences r LEFT JOIN mnc_users u ON u.id=r.owner_id WHERE r.name = ?"))
		{
			stat.setString(1,name.toLowerCase());
			ResultSet rset = stat.executeQuery();
			if(rset.next())
			{
				String estateName = rset.getString("estate_name");
				String estateOwner = rset.getString("owner_username");
				double[] xVals = { rset.getInt("x1"), rset.getInt("x2")};
				double[] zVals = { rset.getInt("z1"), rset.getInt("z2")};
				String description = "This is an estate "+estateName+" of "+estateOwner;
				AreaMarker marker = estateMarkers.remove(name);
				if(marker != null)
				{
					marker.deleteMarker();
				}
				marker = markSet.createAreaMarker("estate_" + estateName, description, false, "world", xVals, zVals, true);
				if(marker == null)
					return;
				marker.setLineStyle(3, 1.0D, 16001680);
				marker.setFillStyle(0.0D, 0);
				estateMarkers.put(estateName, marker);
				Guga_SERVER_MOD.getInstance().log.info("[Estates] loaded "+estateName+" onto dynmap.");
				Guga_SERVER_MOD.getInstance().log.info("[Estates] there are "+estateMarkers.size()+" estates loaded on dynmap.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeEstate(String name)
	{
		if(api == null || markApi == null || markSet == null)
			return;
		
		AreaMarker x = estateMarkers.get(name);
		if(x!=null)
		{
			x.deleteMarker();
		}
	}
}

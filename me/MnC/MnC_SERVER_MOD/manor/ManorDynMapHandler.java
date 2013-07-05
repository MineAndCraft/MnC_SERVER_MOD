package me.MnC.MnC_SERVER_MOD.manor;

import java.util.HashMap;
import java.util.Map;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.data.Point2D;
import me.MnC.MnC_SERVER_MOD.data.Polygon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.PolyLineMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class ManorDynMapHandler
{
	private static DynmapAPI api = null;
	private static MarkerAPI markApi;
	private static MarkerSet markSet;

	private static Map<Integer, PolyLineMarker> manorMarkers = new HashMap<Integer, PolyLineMarker>();
	
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
			MnC_SERVER_MOD.getInstance().log.warning("[Manor] failed to hook on dynmap.");
			return;
		}
 
		try
		{
			markApi = api.getMarkerAPI();
			if (markApi == null) return;
		}
		catch (NullPointerException ex)
		{
			MnC_SERVER_MOD.getInstance().log.warning("[Manor] failed to hook on dynmap.");
			return;
		}
		
		MnC_SERVER_MOD.getInstance().log.info("[Manor] sucessfully hooked on dynmap");
		
		showAllManors();
	}

	public static void showAllManors()
	{
		removeAllManors();
		
		if (markSet != null)
			markSet.deleteMarkerSet();
		markSet = null;
	
		markSet = markApi.getMarkerSet("mnc.manors.markerset");
		if (markSet == null)
			markSet = markApi.createMarkerSet("mnc.manors.markerset", "Manors", null, false);
		else
			markSet.setMarkerSetLabel("Manors");
		
		for(Manor manor : ManorManager.getInstance().getManorList())
		{
			addManor(manor);
		}
	}
	
	public static void removeAllManors()
	{
		for (PolyLineMarker marker : manorMarkers.values())
		{
			marker.deleteMarker();
		}
		manorMarkers.clear();
	}

	public static void disable()
	{
		removeAllManors();
		api = null;
	}

	
	public static void reloadManor(Manor manor)
	{
		manorMarkers.remove(manor.getId()).deleteMarker();
		
		addManor(manor);
	}
	
	public static void addManor(Manor manor)
	{
		Polygon boundaries = manor.getBoundaries();
		double[] xcoords = new double[boundaries.n()+1];
		double[] ycoords = new double[boundaries.n()+1];
		double[] zcoords = new double[boundaries.n()+1];
		for(int i=0;i<boundaries.n();i++)
		{
			Point2D point = boundaries.get(i);
			xcoords[i] = point.x();
			ycoords[i] = 65;
			zcoords[i] = point.y();
		}
		Point2D startingPoint = boundaries.get(0);
		xcoords[boundaries.n()] = startingPoint.x();
		ycoords[boundaries.n()] = 65;
		zcoords[boundaries.n()] = startingPoint.y();
		
		PolyLineMarker marker = markSet.createPolyLineMarker("manor_" + manor.getName(), "Manor "+manor.getName()+", lord "+manor.getLordName(), false, "world", xcoords, ycoords, zcoords, false);
		manorMarkers.put(manor.getId(), marker);
	}
}
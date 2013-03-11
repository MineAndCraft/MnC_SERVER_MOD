package me.Guga.Guga_SERVER_MOD;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import me.Guga.Guga_SERVER_MOD.Handlers.ChatHandler;

import org.bukkit.entity.Player;

public class ResidenceHandler
{
	public static class ResidenceMark
	{
		public Integer x1 = null;
		public Integer x2 = null;
		public Integer z1 = null;
		public Integer z2 = null;
	}
	
	public static class ResidenceData
	{
		public String owner;
		public String[] allowed;
		public int x1;
		public int x2;
		public int z1;
		public int z2;
	}
	
	private static HashMap<String,ResidenceMark> markers = new HashMap<String,ResidenceMark>();
	
	public static ResidenceData getResidence(int x,int z)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username as owner,r.x1,r.x2,r.z1,r.z2,r.allowed FROM mnc_residences r LEFT JOIN mnc_users u ON u.id = r.owner_id WHERE r.x1 >= ? AND ? <= r.x2 AND r.z1 <= ? AND ? <= r.z2 LIMIT 1");)
		{
			stat.setInt(1, x);
			stat.setInt(2, x);
			stat.setInt(3, z);
			stat.setInt(4, z);
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				ResidenceData rd = new ResidenceData();
				rd.allowed = result.getString("allowed").split(",");
				rd.owner = result.getString("owner");
				rd.x1 = result.getInt("x1");
				rd.z1 = result.getInt("z1");
				rd.x2 = result.getInt("x2");
				rd.z2 = result.getInt("z2");
				return rd;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void pos1(String playername,int x, int z)
	{
		ResidenceMark m = markers.get(playername.toLowerCase());
		if(m==null)
		{
			m = new ResidenceMark();
			m.x1 = x;
			m.z1 = z;
			markers.put(playername.toLowerCase(), m);
		}
		else
		{
			m.x1 = x;
			m.z1 = z;
		}
	}
	
	public static void pos2(String playername,int x, int z)
	{
		ResidenceMark m = markers.get(playername.toLowerCase());
		if(m==null)
		{
			m = new ResidenceMark();
			m.x2 = x;
			m.z2 = z;
			markers.put(playername.toLowerCase(), m);
		}
		else
		{
			m.x1 = x;
			m.z1 = z;
		}
	}

	public static void createResidence(Player player,String residence_name)
	{
		ResidenceMark m = markers.get(player.getName().toLowerCase());
		if(m==null || m.x1 == null || m.x2 == null || m.z1 == null || m.z2 == null)
		{
			ChatHandler.FailMsg(player, "[RESIDENCE] you have not selected residence");
			return;
		}
		
		int x1 = m.x1;
		int x2 = m.x2;
		int z1 = m.z1;
		int z2 = m.z2;
		
		if(x1 > x2)
		{
			int c = x1;
			x1 = x2;
			x2 = c;
		}
		
		if(z1 > z2)
		{
			int c = z1;
			z1 = z2;
			z2 = c;
		}
		
		int width = Math.abs(x1-x2); 
		int depth = Math.abs(z1-z2);
		int size = width * depth;
		
		if(width > 60 || depth > 60)
		{
			ChatHandler.FailMsg(player, "[RESIDENCE] Zadny rozmer pozemku nesmi presahovat 60 bloku");
			return;
		}
		
		
		if(size > 1000)
		{
			ChatHandler.FailMsg(player, "[RESIDENCE] Celkova velikost pozemku nesmi presahovat 1000 bloku");
			return;
		}
		
		if(size < 20)
		{
			ChatHandler.FailMsg(player, "[RESIDENCE] Celkova velikost pozemku musi byt vetsi nez 20 bloku");
			return;
		}
		
		int available_residence_blocks = 0;
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT available_residence_blocks FROM mnc_playermetadata WHERE user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1) LIMIT 1");)
		{
			stat.setString(1, player.getName().toLowerCase());
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				available_residence_blocks = result.getInt("available_residence_blocks");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		if(!(available_residence_blocks > 0))
		{
			ChatHandler.FailMsg(player, "[RESIDENCE] Nemate uz zadne residence dostupne");
			return;
		}
		
		//TODO check for interlaces
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT INTO mnc_residences (owner_id,x1,x2,z1,z2,name) SELECT `id`,?,?,?,?,? FROM mnc_users WHERE username_clean = ?");)
		{
			stat.setInt(1, x1);
			stat.setInt(2, x2);
			stat.setInt(3, z1);
			stat.setInt(4, z2);
			stat.setString(5, residence_name);
			stat.setString(6, player.getName().toLowerCase());
			stat.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		available_residence_blocks -= size;
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE mnc_playermetadata SET available_residences = ? WHERE user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setInt(1, available_residence_blocks);
			stat.setString(2, player.getName().toLowerCase());
			stat.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		markers.remove(player.getName().toLowerCase());
		
		ChatHandler.SuccessMsg(player, "Rezidence vytvorena.");
	}
	
	public static void playerLeaveCleanup(String playername)
	{
		markers.remove(playername.toLowerCase());
	}

	public static ArrayList<String> getResidencesOf(String playerName)
	{
		ArrayList<String> list = new ArrayList<String>();
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT name FROM mnc_residences WHERE owner_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setString(1, playerName.toLowerCase());
			ResultSet result = stat.executeQuery();
			while(result.next())
			{
				list.add(result.getString("name"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public static String getResidenceOwner(String string)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username FROM mnc_user u LEFT JOIN mnc_residences r ON u.id = r.owner_id WHERE r.name = ? LIMIT 1");)
		{
			stat.setString(1, string.toLowerCase());
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				return result.getString("username");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static ArrayList<String> getAllowedPlayers(String residence)
	{
		ArrayList<String> list = new ArrayList<String>();
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username FROM mnc_residences_accesses ra JOIN mnc_residences r ON ra.residence_id = r.id JOIN mnc_users u ON ra.user_id = u.id WHERE  r.name = ?");)
		{
			stat.setString(1, residence.toLowerCase());
			ResultSet result = stat.executeQuery();
			while(result.next())
			{
				list.add(result.getString("username"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return list;
	}

	public static boolean addResidenceAccess(String residence, String username)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO mnc_residences_accesses (SELECT `id` FROM mnc_residences WHERE name = ?),(SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setString(1, residence.toLowerCase());
			stat.setString(2, username.toLowerCase());
			return stat.executeUpdate()>0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean removeResidenceAccess(String residence, String username)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("DELETE FROM mnc_residences_accesses WHERE residence_id = (SELECT `id` FROM mnc_residences WHERE name = ?) AND user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setString(1, residence.toLowerCase());
			stat.setString(2, username.toLowerCase());
			return stat.executeUpdate()>0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	
	public static boolean checkCanDigPlace(Player player, int x, int z, boolean isDig)
	{
		// is there any residence?
		int residence_id = 0;
		String residence_owner_name = "";
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username as owner,r.id FROM mnc_residences r LEFT JOIN mnc_users u ON u.id = r.owner_id WHERE r.x1 >= ? AND ? <= r.x2 AND r.z1 <= ? AND ? <= r.z2 LIMIT 1");)
		{
			stat.setInt(1, x);
			stat.setInt(2, x);
			stat.setInt(3, z);
			stat.setInt(4, z);
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				residence_id = result.getInt("id");
				residence_owner_name = result.getString("owner");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(residence_id == 0)
			return true;
		
		if(residence_owner_name.equalsIgnoreCase(player.getName()))
			return true;
		
		boolean permission = false;
		
		// was the user exclusively granter access?
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT '1' as permission FROM mnc_residences_accesses WHERE residence_id = ? AND user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setInt(1, residence_id);
			stat.setString(2, player.getName().toLowerCase());
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				permission = result.getBoolean("permission");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!permission)
		{
			if(isDig)
				player.sendMessage(String.format("Zde nemuzete kopat. Je tu soukromy pozemek hreace %s",residence_owner_name));
			else
				player.sendMessage(String.format("Zde nemuzete stavet. Je tu soukromy pozemek hreace %s",residence_owner_name));
			return false;
		}
		return true;
	}

	public static boolean removeResidence(String residence)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("DELETE FROM mnc_residences_accesses WHERE residence_id = (SELECT `id` FROM mnc_residences WHERE name = ? LIMIT 1);DELETE FROM mnc_residences WHERE name = ?");)
		{
			stat.setString(1, residence.toLowerCase());
			stat.setString(2, residence.toLowerCase());
			return stat.executeUpdate()>0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

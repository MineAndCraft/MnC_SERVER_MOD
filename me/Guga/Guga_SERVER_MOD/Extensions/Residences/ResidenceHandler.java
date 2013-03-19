package me.Guga.Guga_SERVER_MOD.Extensions.Residences;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import me.Guga.Guga_SERVER_MOD.DatabaseManager;
import me.Guga.Guga_SERVER_MOD.GugaRegion;
import me.Guga.Guga_SERVER_MOD.Handlers.ChatHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaRegionHandler;

import org.bukkit.entity.Player;
@Deprecated
public class ResidenceHandler
{	
	private static HashMap<String,ResidenceMark> markers = new HashMap<String,ResidenceMark>();
	
	public static int getResidenceId(int x,int z)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT r.id as id FROM mnc_residences r WHERE r.x1 >= ? AND ? <= r.x2 AND r.z1 <= ? AND ? <= r.z2 LIMIT 1");)
		{
			stat.setInt(1, x);
			stat.setInt(2, x);
			stat.setInt(3, z);
			stat.setInt(4, z);
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				return result.getInt("id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
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
			ChatHandler.FailMsg(player, "[REGION] you have not selected residence");
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
		
		int width = Math.abs(Math.abs(x1)-Math.abs(x2)); 
		int depth = Math.abs(Math.abs(z1)-Math.abs(z2));
		int size = width * depth;
		
		if(width > 60 || depth > 60)
		{
			ChatHandler.FailMsg(player, "[REGION] Zadny rozmer pozemku nesmi presahovat 60 bloku");
			return;
		}
		
		
		if(size > 1000)
		{
			ChatHandler.FailMsg(player, "[REGION] Celkova velikost pozemku nesmi presahovat 1000 bloku");
			return;
		}
		
		if(size < 20)
		{
			ChatHandler.FailMsg(player, "[RGION] Celkova velikost pozemku musi byt vetsi nez 20 bloku");
			return;
		}
		
		float ratio = ((float)Math.max(width, depth)) / ((float)Math.min(width, depth));
		
		if(ratio > 6)
		{
			ChatHandler.FailMsg(player, "[RGION] Pomer stran musi byt mensi nez 1:6.");
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
			ChatHandler.FailMsg(player, "[REGION] Nemate uz zadne residence dostupne");
			return;
		}
		
		for(GugaRegion r : GugaRegionHandler.GetAllRegions())
		{
			if(!r.GetWorld().equalsIgnoreCase("world"))
				continue;
			if( ((r.getX1() <= x1 && x1 <= r.getX2()) || (r.getX1() <= x2 && x2 <= r.getX2()) || (x1 <= r.getX1() && r.getX1() <= x2) || (x1 <= r.getX2() && r.getX2() <= x2))
					&& ((r.getZ1() <= x1 && x1 <= r.getZ2()) || (r.getZ1() <= x2 && x2 <= r.getZ2()) || (x1 <= r.getZ1() && r.getZ1() <= x2) || (x1 <= r.getZ2() && r.getZ2() <= x2)) )
			{
				ChatHandler.FailMsg(player, String.format("Vas pozemek nesmi protinat zadny region. Protina region %s",r.GetName()));
				return;
			}
		}
		
		//check for colisions with existing regions
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT count(*) as colision_count FROM mnc_residences WHERE ((? <= x1 AND x1 <= ?) OR (? <= x2 AND x2 <= ?) OR (x1 <= ? AND ? <= x2) OR (x1 <= ? AND ? <= x2)) AND ((? <= z1 AND z1 <= ?) OR (? <= z2 AND z2 <= ?) OR (z1 <= ? AND ? <= z2) OR (z1 <= ? AND ? <= z2))");)
		{
			stat.setInt(1, x1);
			stat.setInt(2, x2);
			stat.setInt(3, x1);
			stat.setInt(4, x2);
			stat.setInt(5, x1);
			stat.setInt(6, x2);
			
			stat.setInt(7, x1);
			stat.setInt(8, x2);
			stat.setInt(9, x1);
			stat.setInt(10, x2);
			stat.setInt(11, x1);
			stat.setInt(12, x2);
			
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				if(result.getInt("colision_count") > 0)
				{
					ChatHandler.FailMsg(player, "Vas pozemek nesmi protinat uz existujici pozemek");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
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
		
		ChatHandler.SuccessMsg(player, "Pozemek vytvorena.");
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
	
	public static boolean hasGrantedAccess(String username, int residence_id)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT '1' as permission FROM mnc_residences_accesses WHERE residence_id = ? AND user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setInt(1, residence_id);
			stat.setString(2, username.toLowerCase());
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				return result.getBoolean("permission");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
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

	/**
	 * 
	 * @param playername Name of player
	 * @param blockX The X coordinate of dug/placed block
	 * @param blockZ The Z coordinate of dug/placed block
	 * @return false if the specified block is in a valid residence and the player is not owner nor he was granted access, true otherwise
	 */
	public static boolean canPlayerDigPlaceBlock(String playername,int blockX,int blockZ)
	{
		int residence_id = getResidenceId(blockX,blockZ);
		if(residence_id != 0)
		{
			String residence_owner = "";
			try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.`username` as owner FROM mnc_residences r JOIN mnc_users u ON r.id = r.owner_id WHERE r.id = ? LIMIT 1");)
			{
				stat.setInt(1, residence_id);
				ResultSet result = stat.executeQuery();
				if(result.next())
				{
					residence_owner = result.getString("owner");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(!residence_owner.equalsIgnoreCase(playername))
			{
				if(!hasGrantedAccess(playername,residence_id))
				{
					return false;
				}
			}
		}
		return true;
	}
}

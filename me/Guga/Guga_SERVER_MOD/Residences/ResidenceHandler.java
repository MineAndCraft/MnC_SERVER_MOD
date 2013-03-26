package me.Guga.Guga_SERVER_MOD.Residences;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import me.Guga.Guga_SERVER_MOD.DatabaseManager;
import me.Guga.Guga_SERVER_MOD.GugaRegion;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.chat.ChatHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaRegionHandler;

import org.bukkit.entity.Player;

public class ResidenceHandler
{	
	private static HashMap<String,ResidenceMark> markers = new HashMap<String,ResidenceMark>();
	
	public static int getResidenceId(int x,int z)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT r.id as id FROM mnc_residences r WHERE r.x1 <= ? AND ? <= r.x2 AND r.z1 <= ? AND ? <= r.z2 LIMIT 1");)
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
			m.p1 = true;
			markers.put(playername.toLowerCase(), m);
		}
		else
		{
			m.x1 = x;
			m.z1 = z;
			m.p1 = true;
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
			m.p2 = true;
			markers.put(playername.toLowerCase(), m);
		}
		else
		{
			m.x2 = x;
			m.z2 = z;
			m.p2 = true;
		}
	}

	public static void createResidence(Player player,String residence_name)
	{
		ResidenceMark m = markers.get(player.getName().toLowerCase());
		if(m==null || m.p1 == false || m.p2 == false)
		{
			ChatHandler.FailMsg(player, "[ESTATE] You have not selected the area for your new estate.");
			return;
		}
		
		residence_name = residence_name.trim().toLowerCase();
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT count(*) as count FROM mnc_residences WHERE name = ?");)
		{
			stat.setString(1,residence_name);
			try(ResultSet res = stat.executeQuery();)
			{
				if(res.next())
				{
					if(res.getInt("count") > 0)
					{
						ChatHandler.FailMsg(player, "[ESTATE] Estate with this name already exists, please choose a different name.");
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int left = m.x1;
		int right = m.x2;
		int bottom = m.z1;
		int top = m.z2;
		
		if(left > right)
		{
			int c = left;
			left = right;
			right = c;
		}
		
		if(bottom > top)
		{
			int c = bottom;
			bottom = top;
			top = c;
		}
		
		int width = Math.abs(Math.abs(left)-Math.abs(right)); 
		int depth = Math.abs(Math.abs(bottom)-Math.abs(top));
		int size = width * depth;
		
		if(width > 60 || depth > 60)
		{
			ChatHandler.FailMsg(player, "[ESTATE] None of the dimensions of your estate can excede 60 blocks.");
			return;
		}
		
		
		if(size > 1000)
		{
			ChatHandler.FailMsg(player, "[ESTATE] The area of your estate cannot be higher than 1000 blocks.");
			return;
		}
		
		if(size < 20)
		{
			ChatHandler.FailMsg(player, "[ESTATE] The area of your estate cannot be lower than 20 blocks.");
			return;
		}
		
		float ratio = ((float)Math.max(width, depth)) / ((float)Math.min(width, depth));
		
		if(ratio > 6)
		{
			ChatHandler.FailMsg(player, "[ESTATE] The ratio between two sides cannot be higher than 6:1 or lower than 1:6 respectively.");
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
			ChatHandler.FailMsg(player, "[ESTATE] You have no more blocks available for protection with estate system.");
			return;
		}
		
		for(GugaRegion r : GugaRegionHandler.GetAllRegions())
		{
			if(!r.GetWorld().equalsIgnoreCase("world"))
				continue;
			if( !( r.getX1() > right || r.getX2() < left || r.getZ2() < bottom || r.getZ1() > top))
			{
				ChatHandler.FailMsg(player, String.format("[ESTATE] Your estate cannot colide with any of the GugaRegion protected areas. It colides with %s.",r.GetName()));
				return;
			}
		}
		
		//check for colisions with existing estates
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT name FROM mnc_residences WHERE NOT (x1 > ? OR x2 < ? OR z2 < ? OR z1 > ?)");)
		{
			stat.setInt(1, right);
			stat.setInt(2, left);
			stat.setInt(3, bottom);
			stat.setInt(4, top);
			
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				ChatHandler.FailMsg(player, String.format("[ESTATE] Your estate cannot colide with another estate. It colides with %s",result.getString("name")));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT INTO mnc_residences (owner_id,x1,x2,z1,z2,name) SELECT `id`,?,?,?,?,? FROM mnc_users WHERE username_clean = ?");)
		{
			stat.setInt(1, left);
			stat.setInt(2, right);
			stat.setInt(3, bottom);
			stat.setInt(4, top);
			stat.setString(5, residence_name);
			stat.setString(6, player.getName().toLowerCase());
			stat.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE mnc_playermetadata SET available_residence_blocks = (available_residence_blocks - (?)) WHERE user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setInt(1, size);
			stat.setString(2, player.getName().toLowerCase());
			stat.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		markers.remove(player.getName().toLowerCase());
		
		ChatHandler.SuccessMsg(player, "Your estate was created.");
		Guga_SERVER_MOD.getInstance().log.info(String.format("Player '%s' created estate named '%s' with coordinates %d,%d,%d,%d",player.getName(),residence_name,left,top,right,bottom));
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
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username FROM mnc_users u LEFT JOIN mnc_residences r ON u.id = r.owner_id WHERE r.name = ? LIMIT 1");)
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
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO mnc_residences_accesses VALUES((SELECT `id` FROM mnc_residences WHERE name = ? LIMIT 1),(SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1))");)
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
			stat.executeUpdate();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean hasGrantedAccess(String username, int residence_id)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT '1' as permission FROM mnc_residences_accesses ra JOIN mnc_users u ON u.id = ra.user_id WHERE ra.residence_id = ? AND u.username_clean = ? LIMIT 1");)
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
		try
		{
			int width=0;
			int height=0;
			int owner_id;
			PreparedStatement stat1 = DatabaseManager.getConnection().prepareStatement("SELECT x1,x2,z1,z2,owner_id FROM mnc_residences WHERE name = ? LIMIT 1");
			stat1.setString(1, residence.toLowerCase());
			try(ResultSet rs = stat1.executeQuery();)
			{
				if(!rs.next())
					return false;
				width = Math.abs(Math.abs(rs.getInt("x1"))-Math.abs(rs.getInt("x2")));
				height = Math.abs(Math.abs(rs.getInt("z1"))-Math.abs(rs.getInt("z2")));
				owner_id = rs.getInt("owner_id");
			}
			
			PreparedStatement stat2 = DatabaseManager.getConnection().prepareStatement("DELETE FROM mnc_residences_accesses WHERE residence_id = (SELECT `id` FROM mnc_residences WHERE name = ? LIMIT 1)");
			stat2.setString(1, residence.toLowerCase());
			stat2.executeUpdate();
			
			PreparedStatement stat3 = DatabaseManager.getConnection().prepareStatement("DELETE FROM mnc_residences WHERE name = ?");
			stat3.setString(1, residence.toLowerCase());
			stat3.executeUpdate();
			
			PreparedStatement stat4 = DatabaseManager.getConnection().prepareStatement("UPDATE mnc_playermetadata SET available_residence_blocks = (available_residence_blocks + (?)) WHERE user_id = ?");
			stat4.setInt(1, (int)Math.round(width*height*0.95));
			stat4.setInt(2, owner_id);
			stat4.executeUpdate();
			
			return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static int getAvailableResidenceBlocks(String username)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT available_residence_blocks FROM mnc_playermetadata md JOIN mnc_users u ON u.id = md.user_id WHERE u.username_clean = ? LIMIT 1;");)
		{
			stat.setString(1, username.toLowerCase());
			ResultSet res = stat.executeQuery();
			if(res.next())
				return res.getInt("available_residence_blocks");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean addAvailableResidenceBlocks(String username,int amount)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE mnc_playermetadata SET available_residence_blocks = (available_residence_blocks + ( ? )) WHERE user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1) LIMIT 1");)
		{
			stat.setInt(1, amount);
			stat.setString(2, username.toLowerCase());
			return stat.executeUpdate()>0;
		} catch (Exception e) {
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

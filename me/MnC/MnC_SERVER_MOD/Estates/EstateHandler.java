package me.MnC.MnC_SERVER_MOD.Estates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.MnC.MnC_SERVER_MOD.Config;
import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.ServerRegion;
import me.MnC.MnC_SERVER_MOD.Handlers.ServerRegionHandler;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class EstateHandler
{	
	private static HashMap<String,EstateMark> markers = new HashMap<String,EstateMark>();
	
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
	
	public static int getResidenceId(Block block)
	{
		if(!block.getWorld().getName().equals("world"))
			return 0;
		
		return getResidenceId(block.getX(),block.getZ());
	}
	
	public static void pos1(String playername,int x, int z)
	{
		EstateMark m = markers.get(playername.toLowerCase());
		if(m==null)
		{
			m = new EstateMark();
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
		EstateMark m = markers.get(playername.toLowerCase());
		if(m==null)
		{
			m = new EstateMark();
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
		EstateMark m = markers.get(player.getName().toLowerCase());
		if(m==null || m.p1 == false || m.p2 == false)
		{
			ChatHandler.FailMsg(player, "Nejdrive si vytycte oblast pro vas pozemek.");
			return;
		}
			
		residence_name = residence_name.trim().toLowerCase();
		if(!residence_name.matches("[a-zA-Z][a-zA-Z0-9\\-\\_]+"))
		{
			ChatHandler.FailMsg(player, "Jmeno pozemku musi byt minimalne 2 znaky dlouhe, muze obsahovat pouze pismena, cislice, znak '-', znak '_' a nesmi zacinat cislici.");
			return;
		}
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT count(*) as count FROM mnc_residences WHERE name = ?");)
		{
			stat.setString(1,residence_name);
			try(ResultSet res = stat.executeQuery();)
			{
				if(res.next())
				{
					if(res.getInt("count") > 0)
					{
						ChatHandler.FailMsg(player, "Pozemek s timto jmenem jiz existuje.");
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
		
		int width = Math.abs(Math.abs(left)-Math.abs(right))+1; 
		int depth = Math.abs(Math.abs(bottom)-Math.abs(top))+1;
		int size = width * depth;
		
		if(width > Config.ESTATE_MAX_SIDE_SIZE || depth > Config.ESTATE_MAX_SIDE_SIZE)
		{
			ChatHandler.FailMsg(player, "Zadna se stran pozemku nemuze byt vetsi nez "+Config.ESTATE_MAX_SIDE_SIZE+" blocku.");
			return;
		}
		
		
		if(size > Config.ESTATE_MAX_SIZE)
		{
			ChatHandler.FailMsg(player, "Plocha pozemku nemuze byt vetsi nez "+Config.ESTATE_MAX_SIZE+" blocku.");
			return;
		}
		
		if(size < 20)
		{
			ChatHandler.FailMsg(player, "Plocha pozemku nemuze byt mensi nez 20 blocku.");
			return;
		}
		
		float ratio = ((float)Math.max(width, depth)) / ((float)Math.min(width, depth));
		
		if(ratio > 6)
		{
			ChatHandler.FailMsg(player, "Minimalni pomer stran cini 1:6.");
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
		
		if(!(available_residence_blocks - size > 0))
		{
			ChatHandler.FailMsg(player, "Nemate zakoupene dostatecne mnozstvi blocku na ochranu. Muzete je dokoupit prikazem /estates buy <pocet>");
			return;
		}
		
		for(ServerRegion r : ServerRegionHandler.GetAllRegions())
		{
			if(!r.GetWorld().equalsIgnoreCase("world"))
				continue;
			if( !( r.getX1() > right || r.getX2() < left || r.getZ2() < bottom || r.getZ1() > top))
			{
				ChatHandler.FailMsg(player, String.format("Pozemek koliduje s pozemkem serveru. Koliduje s %s.",r.GetName()));
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
				ChatHandler.FailMsg(player, String.format("Pozemek koliduje s jinym pozemkem. Koliduje s %s",result.getString("name")));
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

		addResidenceAccess(player.getName().toLowerCase(), residence_name);
		ChatHandler.SuccessMsg(player, "Pozemek byl vytvoren.");
		MnC_SERVER_MOD.getInstance().log.info(String.format("Player '%s' created estate named '%s' with coordinates %d,%d,%d,%d",player.getName(),residence_name,left,top,right,bottom));
		
		EstatesDynMapHandler.reloadEstate(residence_name);
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
				width = Math.abs(Math.abs(rs.getInt("x1"))-Math.abs(rs.getInt("x2")))+1;
				height = Math.abs(Math.abs(rs.getInt("z1"))-Math.abs(rs.getInt("z2")))+1;
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
			
			EstatesDynMapHandler.removeEstate(residence);
			
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
	 * Whether a player can interact with block 
	 * @param playername The name of player
	 * @param locX The X coordinate
	 * @param locZ The Z coordinate
	 * @return false if there is an estate protection upon this block and the player hasn't got access, true otherwise
	 */
	public static boolean canInteract(String playername,int locX,int locZ)
	{
		int residence_id = getResidenceId(locX,locZ);
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
	
	public static boolean canInteract(String playername, Block block)
	{
		if(!block.getWorld().getName().equals("world"))
			return true;
		
		return canInteract(playername, block.getX(), block.getZ());
	}
	
	public static boolean hasUserResidenceAccess(int residence_id,int user_id)
	{
		if(residence_id == 0) //there cannot be an estate with id 0 so just to be sure
			return true;
		
		//check if the user is owner
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT owner_id FROM mnc_residences WHERE id=?");)
		{
			stat.setInt(1, residence_id);
			try(ResultSet rset = stat.executeQuery();)
			{
				if(rset.next())
				{
					if(rset.getInt("owner_id") == user_id)
						return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//check if the user was granted access
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT count(*)=1 as permission FROM mnc_residences_accesses WHERE user_id = ? AND residence_id = ? LIMIT 1");)
		{
			stat.setInt(1, user_id);
			stat.setInt(2, residence_id);
			try(ResultSet rset = stat.executeQuery();)
			{
				if(rset.next())
				{
					return rset.getBoolean("permission");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

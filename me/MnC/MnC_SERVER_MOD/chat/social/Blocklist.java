package me.MnC.MnC_SERVER_MOD.chat.social;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.UserManager;

import org.bukkit.entity.Player;

/**
 * Handler for chat blocklist
 * Blocklist enables players to deny private messages from other players
 * 
 */
public class Blocklist
{
	private Blocklist(){}
	
	/**
	 * Adds {@code blocked} to {@code blocker}'s blocklist 
	 * @param blocker the one blocking
	 * @param blocked the one to be blocked
	 * @return whether the function succeeded or not
	 */
	public static boolean addBlocklist(String blocker, String blocked)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO mnc_chat_blocklist (user_id,blocked_id) SELECT ?,`id` FROM mnc_users WHERE username_clean = ?;");)
		{
			MinecraftPlayer pl = UserManager.getInstance().getUser(blocker);
			if(pl == null || pl.getId() == 0)
				return false;
			stat.setInt(1, pl.getId());
			stat.setString(2, blocked.toLowerCase());
			return stat.executeUpdate()==1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static List<String> listBlocklistedFor(Player sender)
	{
		List<String> blocked = new LinkedList<String>();
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT u.username as username FROM mnc_chat_blocklist bl LEFT JOIN mnc_users u ON u.id=bl.blocked_id WHERE bl.user_id = ?");)
		{
			MinecraftPlayer pl = UserManager.getInstance().getUser(sender.getName());
			if(pl == null || pl.getId() == 0)
				return new LinkedList<String>();
			stat.setInt(1, pl.getId());
			try(ResultSet result = stat.executeQuery();)
			{
				while(result.next())
					blocked.add(result.getString("username"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blocked;
	}

	/**
	 * Removes {@code blocked} from {@code blocker}'s blocklist 
	 * @param blocker the one blocking
	 * @param blocked the one to be blocked
	 * @return whether the function succeeded or not
	 */
	public static boolean removeBlocklist(String blocker, String blocked)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("DELETE FROM mnc_chat_blocklist WHERE user_id = ? AND blocked_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
			MinecraftPlayer pl = UserManager.getInstance().getUser(blocker);
			if(pl == null || pl.getId() == 0)
				return false;
			stat.setInt(1, pl.getId());
			stat.setString(2, blocked.toLowerCase());
			stat.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @return whether {@code blocker} has {@code blocked} in blocklist or not
	 */
	public static boolean isBlockedBy(String blocked, String blocker)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT count(*)=1 as is_blocked FROM mnc_chat_blocklist WHERE user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1) AND blocked_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
			stat.setString(1, blocker.toLowerCase());
			stat.setString(2, blocked.toLowerCase());
			try(ResultSet result = stat.executeQuery();)
			{
				if(result.next())
					return result.getBoolean("is_blocked");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}	

}

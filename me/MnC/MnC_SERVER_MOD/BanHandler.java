package me.MnC.MnC_SERVER_MOD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BanHandler
{
	//requires table `mnc_bans` {id - int primary key auto_increment, user_id - int, reason - tinytext, ip_address - tinytext, expiration - long, banned_date - datetime, cancelled - boolean}
	//requires table `mnc_ips`
	//requires table `mnc_ipwhitelist` {user_id - int primary key}
	
	private MnC_SERVER_MOD plugin;

	public BanHandler(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * 
	 * @param ip IP address to check for
	 * @return number of seconds since the beginning when the ban is expired
	 */
	public long ipBanExpiration(String ip)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT mnc_bans.expiration as expiration FROM mnc_bans LEFT OUTER JOIN mnc_ips ON mnc_ips.user_id = mnc_bans.user_id WHERE mnc_ips.ip_address = ? AND (mnc_bans.expiration > ? OR mnc_bans.expiration = -1) AND mnc_bans.canceled != 1 ORDER BY mnc_bans.expiration DESC LIMIT 1");)
		{
		    stat.setString(1, ip);
		    stat.setLong(2, System.currentTimeMillis()/1000);
		    ResultSet result = stat.executeQuery();
		    if(result.next())
		    {
		    	return result.getLong("expiration");
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * @param userName username of user to check for
	 * @return number of seconds since the beginning when the ban is expired
	 */
	public long userBanExpiration(String userName)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT mnc_bans.expiration as expiration FROM mnc_bans WHERE mnc_bans.user_id = (SELECT id FROM mnc_users WHERE username_clean = ? LIMIT 1) AND (mnc_bans.expiration > ? OR mnc_bans.expiration = -1) AND mnc_bans.canceled != 1 ORDER BY mnc_bans.expiration DESC LIMIT 1;");)
		{
		    stat.setString(1, userName.toLowerCase());
		    stat.setLong(2, System.currentTimeMillis()/1000); // let the server provide current time
		    ResultSet result = stat.executeQuery();
		    if(result.next())
		    {
		    	return result.getLong("expiration");
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * @param name
	 * @param expiration Ban expiration in number of seconds since the begining
	 * @param reason
	 * @param theBanningOne
	 * @return
	 */
	public boolean banPlayer(String name, long expiration, String reason, String theBanningOne)
	{
		if(expiration == 0)
			return false;
		
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("INSERT INTO mnc_bans (user_id,expiration,reason,banningone,banned_date) (SELECT id,?,?,?,FROM_UNIXTIME(?) FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{		    
		    stat.setLong(1, expiration);
		    stat.setString(2, reason);
		    stat.setString(3, theBanningOne);
		    stat.setLong(4, System.currentTimeMillis()/1000); //let plugin provide the time for banned_date
		    stat.setString(5, name.toLowerCase());
		    return stat.executeUpdate() == 1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean unbanPlayer(int banID)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("UPDATE mnc_bans SET canceled = 1 WHERE id = ? LIMIT 1;");)
		{
		    stat.setInt(1, banID);
		    return stat.executeUpdate() == 1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean isIPWhitelisted(String player)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT count(*) as count FROM mnc_ipwhitelist WHERE user_id = (SELECT id FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
		    stat.setString(1, player.toLowerCase());
		    ResultSet result = stat.executeQuery();
		    if(result.next())
		    {
		    	return result.getInt("count") > 0;
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean addIPWhitelist(String player)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("INSERT IGNORE INTO `mnc_ipwhitelist` (user_id) (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
		    // using INSERT IGNORE because command is successful if the player is whitelisted at the end 
		    stat.setString(1, player.toLowerCase());
		    return stat.executeUpdate()==1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeIPWhitelist(String player)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("DELETE FROM mnc_ipwhitelist WHERE user_id = (SELECT id FROM mnc_users WHERE username_clean = ? LIMIT 1);");)
		{
		    stat.setString(1, player.toLowerCase());
		    return stat.executeUpdate()==1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<String> listIPWhitelisted()
	{
		ArrayList<String> players = new ArrayList<String>();
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT u.username as username FROM `mnc_ipwhitelist` wl JOIN `mnc_users` u ON wl.user_id = u.id;");)
		{
		    ResultSet result = stat.executeQuery();
		    while(result.next())
		    {
		    	players.add(result.getString("username"));
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}

	
	public boolean modifyBan(int banId, long expiration, String reason)
	{
		if(expiration == 0)
			return false;
		
		if(reason==null || reason.length() == 0)
		{
			try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("UPDATE mnc_bans SET expiration = ? WHERE id = ? LIMIT 1;");)
			{		    
			    stat.setLong(1, expiration);
			    stat.setInt(2, banId);
			    return stat.executeUpdate() == 1;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			if(reason.equals("-"))
				reason = "";
			try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("UPDATE mnc_bans SET expiration = ?, reason = ? WHERE id = ? LIMIT 1;");)
			{		    
			    stat.setLong(1, expiration);
			    stat.setString(2, reason);
			    stat.setInt(3, banId);
			    return stat.executeUpdate() == 1;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
}

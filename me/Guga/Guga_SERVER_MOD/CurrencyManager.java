package me.Guga.Guga_SERVER_MOD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.entity.Player;

public class CurrencyManager
{
	//needs table mnc_currency { user_id - int primary key, balance - long }
	
	private Guga_SERVER_MOD plugin;
	
	CurrencyManager(Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;		
	}
	
	public int getBalance(String playerName)
	{
		PreparedStatement stat=null;
		int balance = 0;
		try
		{
		    stat = plugin.dbConfig.getConection().prepareStatement("SELECT curr.balance as balance " +
				"FROM `mnc_currency` curr LEFT JOIN mnc_users u ON curr.user_id=u.id WHERE u.username_clean = ?");
		    stat.setString(1, playerName.toLowerCase());
		    ResultSet result = stat.executeQuery();
		    if(result.next())
		    {
		    	balance = result.getInt("balance");
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if(stat!=null)
					stat.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return balance;
	}
	
	public synchronized boolean addCredits(String playerName, int amount)
	{
		try(PreparedStatement stat=plugin.dbConfig.getConection().prepareStatement("UPDATE `mnc_currency` curr SET curr.balance = (curr.balance + (?)) WHERE curr.user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
		    stat.setLong(1, amount);
		    stat.setString(2, playerName.toLowerCase());
		    return stat.executeUpdate()==1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void onPlayerJoin(Player p)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("INSERT IGNORE INTO `mnc_currency` (user_id,balance) SELECT `id`,0 FROM `mnc_users` u WHERE u.username_clean = ?");)
		{
		    stat.setString(1, p.getName().toLowerCase());
		    stat.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

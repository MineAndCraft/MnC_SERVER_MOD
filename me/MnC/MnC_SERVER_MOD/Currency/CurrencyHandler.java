package me.MnC.MnC_SERVER_MOD.Currency;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;

import org.bukkit.entity.Player;

public class CurrencyHandler
{
	//needs table mnc_currency { user_id - int primary key, balance - float }
	
	public CurrencyHandler()
	{	
	}
	
	public float getBalance(String playerName)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT curr.balance as balance FROM `mnc_currency` curr LEFT JOIN mnc_users u ON curr.user_id=u.id WHERE u.username_clean = ?");)
		{ 
		    stat.setString(1, playerName.toLowerCase());
		    ResultSet result = stat.executeQuery();
		    if(result.next())
		    {
		    	return result.getFloat("balance");
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public synchronized boolean addCredits(String playerName, float amount)
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE `mnc_currency` curr SET curr.balance = (curr.balance + (?)) WHERE curr.user_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
		    stat.setFloat(1, amount);
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
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO `mnc_currency` (user_id,balance) SELECT `id`,0 FROM `mnc_users` u WHERE u.username_clean = ?");)
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

package me.MnC.MnC_SERVER_MOD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TreeMap;

import me.MnC.MnC_SERVER_MOD.MinecraftPlayer.ConnectionState;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer.PlayerRankState;
import me.MnC.MnC_SERVER_MOD.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UserManager
{
	private static UserManager _instance;
	
	private MnC_SERVER_MOD plugin;

	private TreeMap<String,MinecraftPlayer> players = new TreeMap<String,MinecraftPlayer>();	
	
	UserManager (MnC_SERVER_MOD gugaSM)
	{
		_instance = this;
		plugin = gugaSM;
	}
	
	public static UserManager getInstance(){
		return _instance;
	}
	
	public synchronized boolean userIsLogged(String playerName)
	{
		MinecraftPlayer pl = players.get(playerName.toLowerCase());
		if(pl!=null && pl.isAuthenticated())
			return true;
		return false;
	}
	
	public synchronized boolean userIsRegistered(String name)
	{
		
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT count(*)=1 as is_registered FROM `mnc_users` WHERE username_clean=? LIMIT 1;");)
		{
		    stat.setString(1, name.toLowerCase());
		    ResultSet result = stat.executeQuery();
		    if(result.next())
		    {
		    	return result.getBoolean("is_registered");
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void onPlayerJoin(final Player player)
	{
		if(player==null)
			return;

		MinecraftPlayer pl = null;
		pl = new MinecraftPlayer(player);
		this.players.put(player.getName().toLowerCase(), pl);
		final String playerName = player.getName();
		if(pl.getRank() == PlayerRankState.REGISTERED)
		{
			plugin.currencyManager.onPlayerJoin(player);
			if(plugin.vipManager.isVip(playerName))
			{
				plugin.vipManager.onVipLogOn(playerName);
			}
		}
		Thread th = new Thread(){
			@Override
			public void run()
			{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				int remainingTriesBeforeKick = 6;
				while(MnC_SERVER_MOD.is_enabled() && remainingTriesBeforeKick > 0 && !userIsLogged(playerName))
				{
					remainingTriesBeforeKick--;
					if(userIsRegistered(playerName))
					{
						player.sendMessage("Nejste prihlasen! Pro prihlaseni napiste "+ChatColor.YELLOW+" /login <heslo>"+ChatColor.WHITE+"!");
					}
					else
					{
						player.sendMessage("Nejste zaregistrovan! Zaregistrujte se prosim pomoci " + 
								ChatColor.YELLOW +" /register <heslo> <heslo znovu> <email>"+ ChatColor.WHITE +"!");
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {}
				}
				if(!userIsLogged(playerName))
				{
					Util.threadSafeKickPlayer(player, "Nestihl(a) jste se prihlasit do 30 sekund!");
				}
			}
		};
		th.start();
	}
	
	public void logoutUser(String name)
	{
		MinecraftPlayer player = this.players.remove(name.toLowerCase());
		if(player == null)
			return;
		if(player.getState() == ConnectionState.AUTHENTICATED)
		{
			player.save();
		}
		if(plugin.vipManager.isVip(name))
		{
			plugin.vipManager.onVipLogOut(name);
		}
	}

	public MinecraftPlayer getUser(String name)
	{
		return this.players.get(name.toLowerCase());
	}
	
	public int getUserId(String name)
	{
		int id=0;
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT `id` FROM `mnc_users` WHERE username_clean=? LIMIT 1;");)
		{
		    stat.setString(1, name.toLowerCase());
		    try(ResultSet result = stat.executeQuery();)
		    {
		    	if(result.next())
		    	{
		    		id = result.getInt(1);
		    	}
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}

	public synchronized void save()
	{
		for(MinecraftPlayer p : this.players.values())
			p.save();
	}

		
	public long getPlayerLastlogin(String name)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT UNIX_TIMESTAMP(lastlogin) as lastlogin FROM `mnc_users` WHERE username_clean=? LIMIT 1;");)
		{
		    stat.setString(1, name.toLowerCase());
		    try(ResultSet result = stat.executeQuery();)
		    {
		    	if(result.next())
		    	{
		    		return result.getLong("lastlogin");
		    	}
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}


	public synchronized boolean register(Player sender, String pwd, String email)
	{
		boolean s=false;
		//load player data
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT INTO mnc_users (username,username_clean,password,email,join_date) VALUES(?,?,?,?,FROM_UNIXTIME(?))");)
		{
			stat.setString(1, sender.getName());
			stat.setString(2, sender.getName().toLowerCase());
			stat.setString(3, Util.sha1(pwd));
			stat.setString(4, email);
			stat.setLong(5,System.currentTimeMillis()/1000); // let plugin provide time for join_date
			s = stat.executeUpdate()==1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try(PreparedStatement stat = MnC_SERVER_MOD.getInstance().dbConfig.getConection().prepareStatement("INSERT IGNORE INTO `mnc_profession` (user_id,experience) VALUES(?,0);");)
		{
			stat.setInt(1, this.getUserId(sender.getName()));
			stat.executeUpdate();
			s=true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(!s)
			return false;
		this.players.remove(sender.getName().toLowerCase());
		MinecraftPlayer pl = new MinecraftPlayer(sender);
		this.players.put(sender.getName().toLowerCase(), pl);
		return true;
	}
}
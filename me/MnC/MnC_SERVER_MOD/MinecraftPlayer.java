package me.MnC.MnC_SERVER_MOD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import me.MnC.MnC_SERVER_MOD.GameMaster.Rank;
import me.MnC.MnC_SERVER_MOD.Handlers.CommandsHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.rpg.PlayerProfession;
import me.MnC.MnC_SERVER_MOD.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MinecraftPlayer
{
	public enum ConnectionState
	{
		CONNECTED,
		AUTHENTICATED,
		SPECTATING
	}
	
	public enum PlayerRankState
	{
		REGISTERED,
		UNKNOWN
	}

	private int id=0;
	private String name="";
	
	private ConnectionState state;
	private PlayerRankState rank;
	private Player playerInstance;
	
	private PlayerProfession profession;
	
	
	private LinkedList<String> chat_lastTellSenders = new LinkedList<String>();
	private LinkedList<String> chat_lastTellRecipients = new LinkedList<String>();
	
	
	// with colors
	private ChatColor entityNameColor = ChatColor.WHITE;
	private ChatColor chatColor = ChatColor.WHITE;
	
	public MinecraftPlayer(final Player player)
	{
		this.state = ConnectionState.CONNECTED;
		this.playerInstance = player;

		if(MnC_SERVER_MOD.getInstance().userManager.userIsRegistered(player.getName()))
		{
			this.rank = PlayerRankState.REGISTERED;
			//load player data
			try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT id,username FROM `mnc_users` WHERE username_clean = ? LIMIT 1;");)
			{
				stat.setString(1, player.getName().toLowerCase());
				try(ResultSet result = stat.executeQuery();)
				{
					result.next();
					this.name = result.getString("username");
					this.id = result.getInt("id");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO `mnc_profession` (user_id,experience) VALUES(?,0)");)
			{
			    stat.setInt(1, this.id);
			    stat.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			this.profession = PlayerProfession.loadProfession(this);
			
			try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO `mnc_playermetadata` (user_id) VALUES(?)");)
			{
				stat.setInt(1, this.id);
				stat.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			this.rank = PlayerRankState.UNKNOWN;
			this.profession = null;
			this.id = 0;
			this.name = player.getName();
		}
		
		this.initializeDisplayName();
	}

	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public ConnectionState getState(){
		return this.state;
	}
	
	public PlayerRankState getRank(){
		return this.rank;
	}

	public Player getPlayerInstance(){
		return this.playerInstance;
	}
	
	/**
	 * @return player's profession
	 */
	public PlayerProfession getProfession(){
		return this.profession;
	}

	public boolean login(String password)
	{
		if(this.state == ConnectionState.AUTHENTICATED)
			return true;
		
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT 1 as authenticated FROM `mnc_users` WHERE id=? AND password=? LIMIT 1;");)
		{
			stat.setInt(1, this.id);
			stat.setString(2, Util.sha1(password));
			try(ResultSet result = stat.executeQuery();)
			{
				if(result.next())
				{
					if(result.getBoolean("authenticated"))
					{
						this.state = ConnectionState.AUTHENTICATED;
						
						// add player's ip address to address log
						try(PreparedStatement stat2 = DatabaseManager.getConnection().prepareStatement("INSERT IGNORE INTO `mnc_ips` (user_id,ip_address) VALUES(?,?)");)
						{
						    stat2.setInt(1, this.id);
						    stat2.setString(2, this.playerInstance.getAddress().getAddress().toString());
						    stat2.executeUpdate();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						//update `lastlogin`
						try(PreparedStatement stat2 = DatabaseManager.getConnection().prepareStatement("UPDATE `mnc_users` SET lastlogin=FROM_UNIXTIME(?) WHERE id = ?");)
						{
							stat2.setLong(1, System.currentTimeMillis()/1000);
							stat2.setInt(2, this.id);
							stat2.executeUpdate();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						return true;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void save()
	{
		if(this.state == ConnectionState.AUTHENTICATED)
		{
			if(this.profession!=null)
				this.profession.save();
		}		
	}

	
	public boolean isAuthenticated()
	{
		return this.state == ConnectionState.AUTHENTICATED;
	}

	/**
	 * @return What color player name is to have.
	 */
	public ChatColor getNameColor()
	{
		if(GameMasterHandler.IsAtleastGM(this.name))
		{
			if(CommandsHandler.disabledGMs.contains(this.name))
			{
				if(MnC_SERVER_MOD.getInstance().vipManager.isVip(this.name))
				{
					return ChatColor.GOLD;
				}
				else
				{
					return ChatColor.WHITE; 
				}
			}
			
			return ChatColor.BLUE;
		}
		else if(GameMasterHandler.IsAtleastRank(this.name, Rank.BUILDER))
		{
			return ChatColor.GOLD;
		}
		else if(MnC_SERVER_MOD.getInstance().vipManager.isVip(this.name))
		{
			return ChatColor.GOLD;
		}
		else
		{
			return ChatColor.WHITE;
		}
	}
	
	/**
	 * @return The name of player entity for the player. Including color.
	 */
	public String getEntityName()
	{
		return this.entityNameColor + this.name;
	}
	
	public ChatColor getChatColor()
	{
		return this.chatColor;
	}

	
	/**
	 * Initializes player's display name and player list name
	 */
	public void initializeDisplayName()
	{
		boolean isPlayerVip = MnC_SERVER_MOD.getInstance().vipManager.isVip(this.id);
		
		String prefix = null;
		this.chatColor = ChatColor.WHITE;
		this.entityNameColor = ChatColor.WHITE;
		
		if(GameMasterHandler.IsAtleastRank(this.name,Rank.GAMEMASTER))
		{
			if(CommandsHandler.disabledGMs.contains(name))
			{
				if(isPlayerVip)
				{
					prefix = "vip";
					this.playerInstance.setPlayerListName(ChatColor.GOLD + this.name);
					this.chatColor = ChatColor.GOLD;
				}
				else
				{
					this.playerInstance.setPlayerListName(this.name);
				}
			}
			else
			{
				if(GameMasterHandler.IsAdmin(this.name))
				{
					prefix = "admin";
					this.playerInstance.setPlayerListName(ChatColor.AQUA + this.name);
					this.chatColor = ChatColor.AQUA;
				}
				else
				{
					prefix = "gm";
					this.playerInstance.setPlayerListName(ChatColor.GREEN + this.name);
					this.chatColor = ChatColor.GREEN;
				}
				this.entityNameColor = ChatColor.AQUA;
			}
		}
		else if(GameMasterHandler.IsAtleastRank(this.name, Rank.BUILDER))
		{
			prefix = "builder";
			this.playerInstance.setPlayerListName(ChatColor.GOLD + this.name);
		}
		else if(GameMasterHandler.IsAtleastRank(this.name, Rank.HELPER))
		{
			prefix = "helper";
			this.playerInstance.setPlayerListName(ChatColor.BLUE + this.name);
		}
		else if(isPlayerVip)
		{
			prefix = "vip";
			this.playerInstance.setPlayerListName(ChatColor.GOLD + this.name);
			this.entityNameColor = ChatColor.GOLD;
			this.chatColor = ChatColor.GOLD;
		}
		else
		{
			if(this.profession.GetLevel() < 10)
			{
				prefix = "new";
				this.playerInstance.setPlayerListName(ChatColor.GRAY + this.name);
			}
		}
		
		if(prefix != null)
			this.getPlayerInstance().setDisplayName(ChatColor.RED + prefix.toUpperCase() + "'" + ChatColor.WHITE + this.name);
		else
			this.getPlayerInstance().setDisplayName(this.name);
	}
	
	
	public void addLastTellRecipient(MinecraftPlayer target)
	{
		chat_lastTellRecipients.remove(target.getName());
		chat_lastTellRecipients.addFirst(target.getName());
		if(chat_lastTellRecipients.size() > 5)  
			chat_lastTellRecipients.removeLast();
	}

	public void addLastTellSender(MinecraftPlayer sender)
	{
		chat_lastTellSenders.remove(sender.getName());
		chat_lastTellSenders.addFirst(sender.getName());
		if(chat_lastTellSenders.size() > 5)
			chat_lastTellSenders.removeLast();
	}
	
	public List<String> getLastTellSenders()
	{
		List<String> n = new LinkedList<String>();
		n.addAll(chat_lastTellSenders);
		return n;
	}
	
	public List<String> getLastTellRecipients()
	{
		List<String> n = new LinkedList<String>();
		n.addAll(chat_lastTellRecipients);
		return n;
	}

	
	public String getLastTellSender()
	{
		if(chat_lastTellSenders.size() > 0)
			return chat_lastTellSenders.getFirst();
		return null;
	}
}

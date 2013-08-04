package me.MnC.MnC_SERVER_MOD.rpg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;

import org.bukkit.Bukkit;

public class PlayerProfession
{
	protected MinecraftPlayer _player;

	protected int xp;
	protected int xpNeeded;
	protected int xpIncrement;
	protected int xpCap;
	protected int skillCap;
	
	protected int thisLevel;
	
	protected int level;
	protected int lvlCap;
	
	
	protected PlayerProfession(MinecraftPlayer player, int exp)
	{		
		_player = player;
		
		xpIncrement = 2;
		xpNeeded = 500;
		lvlCap = 10000;
		xpCap = 4000;
		thisLevel = 500;
		skillCap = 1000;
		level = 1;
		xp = exp;
		int xpNeededOld;
		while (xp >= xpNeeded)
		{
			xpNeededOld = xpNeeded;
			if (level>=lvlCap)
			{
				xp=xpNeeded;
				break;
			}
			level++;
			if (xpNeeded>=xpCap)
			{
				xpNeeded +=xpCap;
			}
			else
			{
			xpNeeded = xpNeeded * xpIncrement;
			}
			int diff = xpNeeded - xpNeededOld;
			if (diff > 0)
				thisLevel = diff;
		}
		updateSkills();
	}
	
	public void addExperience(int exp)
	{
		this.gainExperience(exp);
		this.save();
	}
	
	public synchronized void save()
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE `mnc_profession` prof SET prof.experience = ? WHERE prof.user_id = ?");)
		{
		    stat.setInt(1, this.xp);
		    stat.setInt(2, this._player.getId());
		    stat.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void onLevelUp()
	{
		save(); // save the profession upon leveling up
		Bukkit.getServer().getPluginManager().callEvent(new PlayerProfessionLevelUpEvent(this));
	}
	
	public MinecraftPlayer getPlayer()
	{
		return _player;
	}

	public float getDropChanceMultiplier()
	{
		return getLevel()*0.000100010001f + 1;
	}
	
	/**
	 * Loads the profession instance for player
	 * @param player {@link MinecraftPlayer} instance of the profession's owner
	 * @return null if there is no profession for player playerId, valid Profession instance otherwise. 
	 */
	public static PlayerProfession loadProfession(MinecraftPlayer player)
	{
		if(player == null)
			return null;
				
		PlayerProfession profession = null;
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT experience FROM `mnc_profession` WHERE user_id=?");)
		{
			stat.setInt(1, player.getId());
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				int exp = result.getInt("experience");
				profession = new PlayerProfession(player,exp);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return profession;
	}

	protected void levelUp()
	{
		level++;
		int xpNeededOld = xpNeeded;
		if (canLevelUp())
		{
			if (xpNeeded>=xpCap)
			{
				xpNeeded +=xpCap;
				
			}
			else
			{
				xpNeeded = xpNeeded * xpIncrement;
			}
		}
		thisLevel = xpNeeded - xpNeededOld;
		onLevelUp();
		updateSkills();
	}
	
	public void updateSkills()
	{
		// no skills to be updated yet
	}

	protected boolean reachedNewLevel()
	{
		if (xp >= xpNeeded)
		{
			return true;
		}
		return false;
	}
	protected boolean canLevelUp()
	{
		if (level<lvlCap)
		{
			return true;
		}
		if (xp>xpNeeded)
		{
			xp = xpNeeded;
		}
		return false;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getXp()
	{
		return xp;
	}
	
	public int getXpNeeded()
	{
		return xpNeeded;
	}
	
	public int getLvlCap()
	{
		return lvlCap;
	}
	
	public void gainExperience(int exp)
	{
		if (canLevelUp())
		{
			xp = xp+exp;
			if (reachedNewLevel())
			{
				levelUp();
			}
		}
	}
}

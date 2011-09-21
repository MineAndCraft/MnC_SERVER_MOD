package me.Guga.Guga_SERVER_MOD;

import org.bukkit.entity.Player;




public class GugaProfession 
{
	GugaProfession()
	{
		xpIncrement = 2;
		xpNeeded = 500;
		lvlCap = 20;
		xpCap = 4000;
	}
	GugaProfession(String pName, int exp, Guga_SERVER_MOD gugaSM)
	{
		
		xpIncrement = 2;
		xpNeeded = 500;
		lvlCap = 20;
		xpCap = 4000;
		
		plugin = gugaSM;
		playerName = pName;
		level = 1;
		xp = exp;
		while (xp >= xpNeeded)
		{
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
		}
	}
	protected void LevelUp()
	{
		level++;
		if (CanLevelUp())
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
		plugin.getServer().broadcastMessage(plugin.getServer().getPlayer(playerName).getName() + " has reached a level " + level + "!");
		UpdateSkills();
	}
	protected void UpdateSkills()
	{
		
	}
	protected boolean ReachedNewLevel()
	{
		if (xp >= xpNeeded)
		{
			return true;
		}
		return false;
	}
	protected boolean CanLevelUp()
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
	public int GetLevel()
	{
		return level;
	}
	public int GetXp()
	{
		return xp;
	}
	public int GetXpNeeded()
	{
		return xpNeeded;
	}
	public int GetLvlCap()
	{
		return lvlCap;
	}
	public String GetPlayerName()
	{
		return playerName;
	}
	public void GainExperience(int exp)
	{
		if (CanLevelUp())
		{
			xp = xp+exp;
			//plugin.getServer().getPlayer(playerName).sendMessage("+" + exp + " XP");
			MapXpBar();
			CheckIfDinged();
		}
	}
	public void CheckIfDinged()
	{
		if (ReachedNewLevel())
		{
			LevelUp();
		}
	}
	public String GetProfession()
	{
		return "Profession";
	}
	private void MapXpBar()
	{
		int inc = xpNeeded / 100;
		Player p = plugin.getServer().getPlayer(playerName);
		p.setExperience(xp/inc);
	}
	protected int xp;
	protected int xpNeeded;
	protected int xpIncrement;
	protected int xpCap;
	
	protected String playerName;
	
	protected int level;
	protected int lvlCap;
	
	protected Guga_SERVER_MOD plugin;
}

package me.Guga.Guga_SERVER_MOD;

import java.util.Random;

import org.bukkit.entity.Player;




public class GugaProfession 
{
	public GugaProfession()
	{
		xpIncrement = 2;
		xpNeeded = 500;
		lvlCap = 1000;
		xpCap = 4000;
	}
	public GugaProfession(String pName, int exp, Guga_SERVER_MOD gugaSM)
	{
		xpIncrement = 2;
		xpNeeded = 500;
		lvlCap = 1000;
		xpCap = 4000;
		thisLevel = 500;
		plugin = gugaSM;
		playerName = pName;
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
		UpdateSkills();
	}
	protected void LevelUp()
	{
		level++;
		int xpNeededOld = xpNeeded;
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
		thisLevel = xpNeeded - xpNeededOld;
		plugin.getServer().broadcastMessage(plugin.getServer().getPlayer(playerName).getName() + " has reached a level " + level + "!");
		//if (level <= 20)
		//{
			UpdateSkills();
		//}
	}
	public void UpdateSkills()
	{
		int newIron = level/10;
		int newGold = level/20;
		int newDiamond = level/50;
		
		ironChance = newIron;
		goldChance = newGold;
		diamondChance = newDiamond;
	}
	public GugaBonusDrop CobbleStoneDrop()
	{
		Random rnd = new Random();
		int rNum = rnd.nextInt(1000);
		if (rNum < diamondChance)
		{
			return GugaBonusDrop.DIAMOND;
		}
		else if (rNum < goldChance)
		{
			return GugaBonusDrop.GOLD;
		}
		else if (rNum < ironChance)
		{
			return GugaBonusDrop.IRON;
		}
		else
		{
			return GugaBonusDrop.NOTHING;
		}
	}
	public int[] GetChances()
	{
		int chances[] = new int[3];
		chances[0] = ironChance;
		chances[1] = goldChance;
		chances[2] = diamondChance;
		return chances;
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
			//MapXpBar();
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
@SuppressWarnings({ "unused", "deprecation" })
private void MapXpBar()
	{
		int inc = thisLevel / 100;
		if (inc == 0)
			inc = 1;
		Player p = plugin.getServer().getPlayer(playerName);
		p.setExperience((thisLevel-(xpNeeded - xp))/inc);
	}
	protected int xp;
	protected int xpNeeded;
	protected int xpIncrement;
	protected int xpCap;
	
	protected int thisLevel;
	
	protected String playerName;
	
	protected int level;
	protected int lvlCap;
	
	
	int ironChance;
	int goldChance;
	int diamondChance;
	
	
	protected Guga_SERVER_MOD plugin;
}

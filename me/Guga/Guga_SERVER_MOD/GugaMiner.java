package me.Guga.Guga_SERVER_MOD;

import java.util.Random;

import org.bukkit.block.Block;

public class GugaMiner extends GugaProfession
{
	GugaMiner()
	{
		
	}
	GugaMiner(String pName, int exp, Guga_SERVER_MOD gugaSM)
	{
		super(pName,exp,gugaSM);
		UpdateSkills();
	}
	public String GetProfession()
	{
		return "Miner";
	}
	public void UpdateSkills()
	{
		int newIron = level/5;
		
		int newGold = level/6;
		
		int newDiamond = level/15;

		
		ironChance = newIron;
		goldChance = newGold;
		diamondChance = newDiamond;
		
		newIron = level/10;
		newGold = level/15;
		newDiamond = level/20;
		
		bonusIron = newIron;
		bonusGold = newGold;
		bonusDiamond = newDiamond;
	}
	public GugaBonusDrop CobbleStoneDrop()
	{
		Random rnd = new Random();
		int rNum = rnd.nextInt(200);
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
	public int BonusDrop(Block block)
	{
		int typeId = block.getTypeId();
		if (typeId == 15) // iron
		{
			return bonusIron;
		}
		else if (typeId == 14) // gold
		{
			return bonusGold;
		}
		else if (typeId == 56) // diamond
		{
			return bonusDiamond;
		}
		return 0;
	}
	public int[] GetChances()
	{
		int chances[] = new int[3];
		chances[0] = ironChance;
		chances[1] = goldChance;
		chances[2] = diamondChance;
		return chances;
	}
	public int[] GetBonusDrops()
	{
		int drops[] = new int[3];
		drops[0] = bonusIron;
		drops[1] = bonusGold;
		drops[2] = bonusDiamond;
		return drops;
	}
	
	int ironChance;
	int goldChance;
	int diamondChance;
	
	int bonusIron;
	int bonusGold;
	int bonusDiamond;
}

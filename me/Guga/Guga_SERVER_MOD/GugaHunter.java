package me.Guga.Guga_SERVER_MOD;

import org.bukkit.entity.Player;

public class GugaHunter extends GugaProfession
{
	GugaHunter()
	{
		dmgIncrease = 0;
		hpRegen = 0;
	}
	public GugaHunter(String pName, int exp, Guga_SERVER_MOD gugaSM)
	{
		super(pName,exp,gugaSM);
		regenTaskId = 0;
		UpdateSkills();
	}
	public String GetProfession()
	{
		return "Hunter";
	}
	public void UpdateSkills()
	{
		//***********DMG INCREASE**********
		int newIncrease = level/4;
		/*if (newIncrease == 0)
		{
			newIncrease = 1;
		}*/
		dmgIncrease = newIncrease;
		int newRegen = level/2;
		double newSpeedInc = 1 + (((double)level)/10);
		if (newRegen ==0)
		{
			newRegen = 1;
		}
		else if(newRegen > 6)
		{
			newRegen = 6;
		}
		hpRegen = newRegen;
		speedIncrease = newSpeedInc;
		
	}
	public double GetSpeedIncrease()
	{
		return speedIncrease;
	}
	public int GetDamageIncrease()
	{
		return dmgIncrease;
	}
	public int IncreaseDamage(int basicDmg)
	{
		return basicDmg+dmgIncrease;
	}
	public int GetHpRegen()
	{
		return hpRegen;
	}
	public void StopRegenHp()
	{
		if (regenTaskId > 0)
		{
			plugin.scheduler.cancelTask(regenTaskId);
			regenTaskId = 0;
		}
	}
	public void StartRegenHp()
	{
		if (regenTaskId != 0)
		{
			return;
		}
		regenTaskId = plugin.scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable(){
			public void run()
			{
				Player p;
				if ((p = plugin.getServer().getPlayer(playerName)) != null)
				{
					if (!p.isDead())
					{
						int actualHp = p.getHealth();
						if ((actualHp + hpRegen) > 20)
						{
							p.setHealth(20);
						}
						else
						{
							p.setHealth(actualHp + hpRegen);
						}
					}
				}
			}
		}, 1200, 1200);
	}
	
	private int dmgIncrease;
	private int hpRegen;
	private double speedIncrease;
	
	private int regenTaskId;
}
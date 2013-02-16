package me.Guga.Guga_SERVER_MOD;

import org.bukkit.block.Block;

public class DummyGugaProfession extends GugaProfession2
{
	public DummyGugaProfession()
	{
		super();
		this.userId = 0;
	}
	
	@Override
	public void UpdateSkills(){}
	
	@Override
	public GugaBonusDrop CobbleStoneDrop()
	{
		return GugaBonusDrop.NOTHING;
	}
	
	@Override
	public void GainExperience(int exp){}

	@Override
	public void CheckIfDinged(){}

	@Override
	public void save(){}
	
	@Override
	public void onBlockBreak(Block b){}
	
	@Override
	public void addExperience(int exp){}
}

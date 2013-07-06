package me.MnC.MnC_SERVER_MOD.rpg;

import java.util.ArrayList;
import java.util.LinkedList;

import me.MnC.MnC_SERVER_MOD.util.Rnd;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BonusDrop
{
	public static final int MAX_CHANCE = 1000000;
	
	private static ArrayList<BonusDrop> droplist = new ArrayList<BonusDrop>();
	
	static
	{
		droplist.add(new BonusDrop(1, -1, 2000, 10, 15, 0));	// stone - iron ore
		droplist.add(new BonusDrop(1, -1, 1333, 50, 14, 0));	// stone - gold ore
		droplist.add(new BonusDrop(1, -1, 1000, 150, 264, 0));	// stone - diamond
		droplist.add(new BonusDrop(1, -1, 740, 300, 388, 0));	// stone - emerald
		
		droplist.add(new BonusDrop(2, -1, 6666, 25, 348, 0));	// grass - glowstone dust
		droplist.add(new BonusDrop(3, -1, 6666, 25, 348, 0));	// dirt - glowstone dust
		
		droplist.add(new BonusDrop(2, -1, 2000, 75, 341, 0));	// grass - slime ball
		droplist.add(new BonusDrop(3, -1, 2000, 75, 341, 0));	// dirt - slime ball
		
		droplist.add(new BonusDrop(12, -1, 2000, 50, 371, 0));	// sand - golden nugget
		
		droplist.add(new BonusDrop(87, -1, 40, 300, 399, 0));	// netherrack - nether star
		
		droplist.add(new BonusDrop(56, -1, 500, 500, 399, 0));	// diamond ore - beacon
		
		droplist.add(new BonusDrop(18, 0, 666, 300, 322, 0));	// leaves:0 - golden apple
		droplist.add(new BonusDrop(18, 2, 666, 300, 322, 0));	// leaves:2 - golden apple
		
		droplist.add(new BonusDrop(87, -1, 4000, 50, 372, 0));	// netherrack - nether wart seeds

		droplist.add(new BonusDrop(13, -1, 6666, 30, 40, 0));	// gravel - red mushroom
		droplist.add(new BonusDrop(13, -1, 6666, 30, 39, 0));	// gravel - brown mushroom
		
		droplist.add(new BonusDrop(2, -1, 2000, 30, 391, 0));	// grass - carrot seeds
		
		droplist.add(new BonusDrop(2, -1, 2000, 30, 392, 0));	// grass - potato seeds
		
		droplist.add(new BonusDrop(24, -1, 4000, 150, 384, 0));	// sandstone - bottle of enchanting
		
		droplist.add(new BonusDrop(89, -1, 3333, 100, 369, 0));	// glowstone - blaze rod
		
		droplist.add(new BonusDrop(88, -1, 1000, 300, 397, 0));	// soul sand - wither skeleton skull
	}

	public static void dropBonusDrops(PlayerProfession profession, Block block)
	{
		LinkedList<ItemStack> drops = new LinkedList<ItemStack>();
		
		int chance = Rnd.get(MAX_CHANCE);
		for(BonusDrop drop : droplist)
		{
			if(drop.getLevelLimit() > profession.GetLevel())
				continue;
			
			if(drop.getBlockId()!= -1 && drop.getBlockId() != block.getTypeId())
				continue;

			if(drop.getBlockData()!= -1 && drop.getBlockData() != block.getData())
				continue;
			
			if(drop.getChance() * profession.getDropChanceMultiplier() > chance)
			{
				drops.add(drop.toItemStack());
			}
		}
		
		for(ItemStack drop : drops)
		{
			block.getWorld().dropItemNaturally(block.getLocation(), drop);
		}
	}
	
	
	private int blockId;
	private int blockType;
	
	private int chance;
	
	private int levelLimit;
	
	private int itemId;
	private int itemType;
	
	
	BonusDrop(int blockId, int blockType, int chance, int levelLimit, int itemId, int itemType)
	{
		this.blockId = blockId;
		this.blockType = blockType;
		this.chance = chance;
		this.levelLimit = levelLimit;
		this.itemId = itemId;
		this.itemType = itemType;
	}
	
	public int getBlockId()
	{
		return blockId;
	}
	
	public int getBlockData()
	{
		return blockType;
	}
	
	public int getChance()
	{
		return chance;
	}
	
	public int getLevelLimit()
	{
		return levelLimit;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public int getItemType()
	{
		return itemType;
	}
	
	public ItemStack toItemStack()
	{
		return new ItemStack(itemId,1,(short)itemType);
	}
}

package me.Guga.Guga_SERVER_MOD;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Enchantments 
{
	public static EnchantmentResult enchantItem(Player player, Enchantment enchantment, int level) 
	{
	    if (enchantment == null) 
	    {
		      return EnchantmentResult.INVALID_ID;
		}
		if (level > 127) 
		{
		      level = 127;
		}
		ItemStack item = player.getInventory().getItemInHand();
		if (item == null)
			return EnchantmentResult.CANNOT_ENCHANT;
		try
		{
			item.addUnsafeEnchantment(enchantment, level);
		} 
		catch (Exception e) 
		{
			return EnchantmentResult.CANNOT_ENCHANT;
		}
		return EnchantmentResult.ENCHANTED;
	}
	public static EnchantmentResult enchantItem(Player player, String enchantment, int level)
	{	
		Enchantment ench = Enchantment.getById(EnchantsTrans.getIdByName(enchantment));
		return enchantItem(player, ench, level);
	  }
	public static void enchantAll(Player player)
	{
		for (Enchantment enchantment : Enchantment.values())
	    {
	        enchantItem(player, enchantment, enchantment.getMaxLevel());
	    }
	}
	public static void enchantAllInRikubStyle(Player player)
	{
		for (Enchantment enchantment : Enchantment.values())
	    {
	        enchantItem(player, enchantment, 127);
	    }
	}
	public static enum EnchantmentResult
	{
		INVALID_ID, CANNOT_ENCHANT, ENCHANTED;
	}
	public static enum EnchantsTrans
	{
		PROTECTION(0, "protection"),
		PROTECT(0, "protect"),
		FIREPROTECTION(1, "fireprotection"),
		FIREPROTECT(1, "fireprotect"),
		FEARHERFALLING(2, "featherfalling"),
		BLASTPROTECT(3, "blastprotect"),
		BLASTPROTECTION(3, "blastprotection"),
		PROJECTILEPROTECTION(4, "projectileprotection"),
		PROJECTILEPROTECT(4, "projectileprotect"),
		RESPIRATION(5, "respiration"),
		AQUAINFFINITY(6, "aquainffinity"),
		WATERINFFINITY(6, "waterinffinity"),
		SHARPNESS(16, "sharpness"),
		SHARP(16, "sharp"),
		SMITE(17, "smite"),
		BANEOFARTHROPODS(18, "baneofarthropods"),
		KNOCKBACK(19, "knockback"),
		FIREASPECT(20, "fireaspect"),
		LOOTING(21, "looting"),
		LOOT(21, "loot"),
		EFFICIENCY(32, "efficiency"),
		EFFICIENT(32, "efficient"),
		SILKTOUCH(33, "silktouch"),
		UNBREAKING(34, "unbreaking"),
		FORTUNE(35, "fortune"),
		POWER(48, "power"),
		PUNCH(49, "punch"),
		FLAME(50, "flame"),
		INFINITY(51, "infinity"),
		INFINITE(51, "infinite");
		
		private EnchantsTrans(int id, String name)
		{
			this.id = id;
			this.name = name;
		}
		private int getId()
		{
			return this.id;
		}
		private String getName()
		{
			return this.name;
		}
		public static int getIdByName(String name)
		{
			EnchantsTrans[] enchants = EnchantsTrans.values();
			int i;
			for(i=0;i<enchants.length;i++)
			{
				if(enchants[i].getName().equalsIgnoreCase(name))
					return enchants[i].getId();
			}
			return -1;
		}
		private int id;
		private String name;
	}
}
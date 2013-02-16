package me.Guga.Guga_SERVER_MOD;

public enum Prices 
{
	MOB_SPAWNER(52,100,1),
	IRON_BLOCK(42, 2, 1),
	EMERALD_BLOCK(133, 20, 1),
	GOLD_BLOCK(41, 5, 1), 
	DIAMOND_BLOCK(57, 15, 1),
	BEACON_BLOCK(138, 300, 1),
	ANVIL(145, 15, 1),
	SLIMEBALL(341, 5, 1), 
	SNOWBALL(332, 11, 16), 
	CLAY(337, 3, 16), 
	EGG(344, 5, 16), 
	BLAZE_ROD(369, 2, 1), 
	GHAST_TEAR(370, 5, 1), 
	GRASS(2, 40, 64), //
	EGG_VILLAGER(383, 10, 1),
	EGG_SKELETON(383, 25, 1),
	EGG_SLIME(383, 25, 1),
	//EGG_SILVERFISH(383, 20),
	EGG_ENDERMAN(383, 50, 1),
	EGG_PIG(383, 5, 1),
	EGG_SHEEP(383, 5, 1),
	EGG_COW(383, 5, 1),
	EGG_CHICKEN(383, 5, 1),
	EGG_ZOMBIE(383, 25, 1),
	EGG_PIGMAN(383, 25, 1),
	EGG_WOLF(383, 10, 1),
	EGG_SPIDER(383, 25, 1),
	EGG_CAVE_SPIDER(383, 25, 1),
	EGG_MAGMA_SLIME(383, 10, 1),
	EGG_MOOSHROOM(383, 10, 1),
	EGG_OCELOT(383,40, 1),
	EGG_WITCH(383,60,1),
	EGG_BAT(383,60,1),
	MYCELIUM(110, 50, 64), 
	GOLD_ORE(14,2,1), 
	IRON_ORE(15,1,1),
	DIAMOND_ORE(56,20,1),
	COAL_ORE(16,2,1),
	LAPIS_ORE(21,5,1),
	REDSTONE_ORE(73, 3, 1),
	EMERALD_ORE(129, 25, 1),
	LAPIS_BLOCK(22,5,1),
	MOSS_STONE(48,40,64),
	GLOWSTONE(89,25,64),
	ICE(79,25,64),
	NETHER_BRICK(112,15,64),
	NETHER_FENCE(113,25,64),
	NETHER_STAIR(114,20,64),
	END_STONE(121, 15, 64),
	SEED_WHEAT(295,1,64),
	SEED_MELON(362, 5, 64),
	SEED_PUNPKIN(361, 5, 64),
	SAPLING_DUB(6, 5, 64),
	SAPLING_BRIZA(6, 5, 64),
	SAPLING_SMRK(6, 5, 64),
	SAPLING_JUNGLE(6, 5, 64),
	ROSE(38,5,64),
	DANDELION(37,5,64),
	MASHROOM_BROWN(39,10,64),
	MASHROOM_RED(40,10,64),
	CARROT(391,30,64),
	POTATO(392,30,64),
	CACTUS(81,5,64),
	VINES(106,10,64),
	WOOL(35,5,64),
	WOOL_ORANGE(35,10,64),
	WOOL_MAGENTA(35,10,64),
	WOOL_LIGHTBLUE(35,10,64),
	WOOL_YELLOW(35,10,64),
	WOOL_LIME(35,10,64),
	WOOL_PINK(35,10,64),
	WOOL_GRAY(35,10,64),
	WOOL_LIGHTGRAY(35,10,64),
	WOOL_CYAN(35,10,64),
	WOOL_PURPLE(35,10,64),
	WOOL_BLUE(35,10,64),
	WOOL_BROWN(35,10,64),
	WOOL_GREEN(35,10,64),
	WOOL_RED(35,10,64),
	WOOL_BLACK(35,10,64),
	LEATHER(334, 10 ,64),
	MAGMA_CREAM(378,6,64),
	COCOA(351, 5, 64),
	INK(351,5,64),
	SPIDER_EYE(375, 20, 64),
	ARROW(362, 20, 64),
	REDSTONE_DUST(331,10,64),
	FISH(349,25,64),
	SADDLE(329,5,1),
	BED(355,5,1),
	SPONGE(19,100,64),
	HEAD_SKELETON(397, 200, 1),
	HEAD_ZOMBIE(397, 200, 1),
	HEAD_CREEPER(397, 200, 1),
	HEAD_STEVE(397, 200, 1);
	
	
	private Prices(int id, int price, int ammount)
	{
		itemID = id;
		itemPrice = price;
		itemAmmount = ammount;
	}
	public int GetItemID()
	{
		return itemID;
	}
	public int GetItemPrice()
	{
		return itemPrice;
	}
	public int GetAmmount()
	{
		return itemAmmount;
	}
	private int itemID;
	private int itemPrice;
	private int itemAmmount;
}

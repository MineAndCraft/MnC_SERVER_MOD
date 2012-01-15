package me.Guga.Guga_SERVER_MOD;
public enum Prices 
{
	KRUMPAC_EFFICIENCY_V(278, 500),
	IRON_BLOCK(42,10), GOLD_BLOCK(41,10), DIAMOND_BLOCK(57,25), GOLDEN_APPLE(322,25), 
	SLIMEBALL(341,25), SNOWBALL(332,1), CLAY(337,1), EGG(344,1), GUNPOWDER(289,10), 
	OBSIDIAN(49,15), MOB_SPAWNER(52,100), REDSTONE_DUST(331,2), COBWEB(30, 15),
	ICE(79, 2), BLAZE_ROD(369, 15), GHAST_TEAR(370, 15), GRASS(2, 10),
	EGG_VILLAGER(383, 10),
	EGG_SKELETON(383, 25),
	EGG_SLIME(383, 25),
	//EGG_SILVERFISH(383, 20),
	EGG_ENDERMAN(383, 50),
	EGG_PIG(383, 5),
	EGG_SHEEP(383, 5),
	EGG_COW(383, 5),
	EGG_CHICKEN(383, 5),
	EGG_ZOMBIE(383, 25),
	EGG_PIGMAN(383, 25),
	EGG_WOLF(383, 10),
	EGG_SPIDER(383, 25),
	EGG_CAVE_SPIDER(383, 25),
	EGG_MAGMA_SLIME(383, 10),
	EGG_MOOSHROOM(383, 10);
	
	private Prices(int id, int price)
	{
		itemID = id;
		itemPrice = price;
	}
	public int GetItemID()
	{
		return itemID;
	}
	public int GetItemPrice()
	{
		return itemPrice;
	}
	private int itemID;
	private int itemPrice;
}

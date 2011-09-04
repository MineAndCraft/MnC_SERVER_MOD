package me.Guga.Guga_SERVER_MOD;

public enum Prices 
{
	IRON_INGOT(265,5), GOLD_INGOT(266,10), DIAMOND(264,25), GOLDEN_APPLE(322,25), 
	SLIMEBALL(341,25), SNOWBALL(332,1), CLAY(337,1), EGG(344,1), GUNPOWDER(289,10), OBSIDIAN(49,15), MOB_SPAWNER(52,100);
	
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

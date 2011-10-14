package me.Guga.Guga_SERVER_MOD;

public class GugaAuction 
{
	GugaAuction(int itemID, int amount, int price, String owner)
	{
		this.itemID = itemID;
		this.amount = amount;
		this.price = price;
		this.owner = owner;
	}
	public String GetOwner()
	{
		return this.owner;
	}
	public int GetItemID()
	{
		return this.itemID;
	}
	public int GetAmount()
	{
		return this.amount;
	}
	public int GetPrice()
	{
		return this.price;
	}
	private int itemID;
	private int amount;
	private int price;
	private String owner;
}

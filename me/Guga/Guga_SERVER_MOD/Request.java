package me.Guga.Guga_SERVER_MOD;

public class Request 
{
	public static enum RequestTypes
	{
		GRIEF(0, "grief"),
		REGION(1, "region"),
		SPAWNER(2, "spawner"),
		HACKS(3, "hacks"),
		BUG(4, "bug"),
		QUESTION(5, "question"),
		PURCHASE(6, "purchase"),
		BUY(6, "buy"),
		OTHER(7, "other");

		private RequestTypes(int id, String name)
		{
			this.name = name;
			this.id = id;
		}
		private String name;
		private int id;
	}
}

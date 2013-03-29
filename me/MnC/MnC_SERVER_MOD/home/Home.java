package me.MnC.MnC_SERVER_MOD.home;


public class Home 
{

	public Home(String playerName, int x, int y, int z, String world)
	{
		this.playerName = playerName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public String getPlayerName()
	{
		return this.playerName;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public int getZ()
	{
		return this.z;
	}
	
	public String getWorld()
	{
		return this.world;
	}
		
	private String playerName;
	private int x;
	private int y;
	private int z;
	private String world;
}

package me.Guga.Guga_SERVER_MOD;

public class Places 
{
	public Places(String portName, String owner, String allowedPlayers[], int x, int y, int z, String world, String welcomeMessage)
	{
		this.portName = portName;
		this.owner = owner;
		this.allowedPlayers = allowedPlayers;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.welcomeMessage = welcomeMessage;
	}
	
	public String getPortName()
	{
		return this.portName;
	}
	
	public String getPortOwner()
	{
		return this.owner;
	}
	
	public String[] getAllowedPlayers()
	{
		return this.allowedPlayers;
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
	
	public String getWelcomeMsg()
	{
		return this.welcomeMessage;
	}
	
	public void setAllowedPlayers(String players[])
	{
		this.allowedPlayers = players;
	}
	
	public void setWelcomeMsg(String welcomeMessage)
	{
		this.welcomeMessage = welcomeMessage;
	}
	private String portName;
	private String owner;
	private String[] allowedPlayers;
	private int x;
	private int y;
	private int z;
	private String world;
	private String welcomeMessage;
}
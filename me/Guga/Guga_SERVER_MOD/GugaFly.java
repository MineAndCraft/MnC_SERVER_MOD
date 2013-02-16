package me.Guga.Guga_SERVER_MOD;

@Deprecated
public class GugaFly 
{
	public GugaFly(String playerName, long expiration)
	{
		this.playerName = playerName;
		this.expiration = expiration;
	}
	public long getExpiration()
	{
		return this.expiration;
	}
	public String getName()
	{
		return this.playerName;
	}
	private String playerName;
	private long expiration;
}

package me.Guga.Guga_SERVER_MOD;

public class GugaBan 
{
	public GugaBan(String playerName, String[] ipAddresses, long expiration)
	{
		this.playerName = playerName;
		this.ipAddresses = ipAddresses;
		this.expiration = expiration;
	}
	public GugaBan(String playerName, long expiration)
	{
		this.playerName = playerName;
		this.ipAddresses = new String[0];
		this.expiration = expiration;
	}
	public String GetPlayerName()
	{
		return this.playerName;
	}
	public String[] GetIpAddresses()
	{
		return this.ipAddresses;
	}
	public long GetExpiration()
	{
		return this.expiration;
	}
	public String toString()
	{
		String addrs = "";
		int i = 0;
		while (i < this.ipAddresses.length)
		{
			if (i == this.ipAddresses.length -1)
				addrs += this.ipAddresses[i];
			else
				addrs += this.ipAddresses[i] + ";";
			i++;
		}
		String ret = this.playerName + ";" + Long.toString(this.expiration) + ";" + addrs;
		return ret;
	}
	public void AddIpAddress(String addr)
	{
		int i = 0;
		while (i < this.ipAddresses.length)
		{
			if (this.ipAddresses[i].matches(addr))
				return;
			i++;
		}
		String[] tempArray = this.ipAddresses;
		this.ipAddresses = new String[tempArray.length+1];
		i = 0;
		while (i < tempArray.length)
		{
			this.ipAddresses[i] = tempArray[i];
			i++;
		}
		this.ipAddresses[i] = addr;
	}
	public void SetIpAddresses(String[] addrs)
	{
		this.ipAddresses = addrs;
	}
	public void SetExpiration(long expiration)
	{
		this.expiration = expiration;
	}
	private String playerName;
	private String[] ipAddresses;
	private long expiration;
}

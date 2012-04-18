package me.Guga.Guga_SERVER_MOD;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class GugaPlace 
{
	GugaPlace(World world, String name, String owner, int x, int y, int z)
	{
		this.name = name;
		this.owner = owner;
		this.location = new Location(world, x, y, z);
	}
	GugaPlace(String name, String owner, Location location)
	{
		this.name = name;
		this.owner = owner;
		this.location = location;
	}
	@Override
	public String toString()
	{
		return name + ";"+ owner + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ() + ";" + location.getWorld().getName();
	}
	public void Teleport(Player p)
	{
		p.teleport(location);		
	}
	public Location GetLocation()
	{
		return location;
	}
	public String GetName()
	{
		return name;
	}
	public String GetOwner()
	{
		return owner;
	}
	public void SetName(String name)
	{
		this.name = name;
	}
	public void SetLocation(Location location)
	{
		this.location = location;
	}
	private String name;
	private String owner;
	private Location location;
}

package me.Guga.Guga_SERVER_MOD;

public class GugaRegion 
{
	public GugaRegion(String name, String world, String[] owners, int x1, int x2, int z1, int z2)
	{
		this.name = name;
		this.owners = owners;
		this.world = world;
		this.x1 = x1;
		this.x2 = x2;
		this.z1 = z1;
		this.z2 = z2;
		this.SortCoords();
	}
	public boolean IsInRegion(int x, int z, String world)
	{
		if ( ( ( (x >= this.x1) && (x <= this.x2) ) && ( (z >= this.z1) && (z <= this.z2)) ) && (world.equalsIgnoreCase(this.world)) )
		{
			return true;
		}
		return false;
	}
	public String GetName()
	{
		return this.name;
	}
	public String[] GetOwners()
	{
		return owners;
	}
	public String GetWorld()
	{
		return this.world;
	}
	public int[] GetCoords()
	{
		int[] vals = {this.x1, this.x2, this.z1, this.z2};
		return vals;
	}
	public void SetOwners(String[] owners)
	{
		this.owners = owners;
	}
	private void SortCoords()
	{
		if (x1 > x2)
		{
			int tempX = x1;
			x1 = x2;
			x2 = tempX;
		}
		if (z1 > z2)
		{
			int tempZ = z1;
			z1 = z2;
			z2 = tempZ;
		}
	}
	private int x1;
	private int x2;
	private int z1;
	private int z2;
	
	public static int X1 = 0;
	public static int X2 = 1;
	public static int Z1 = 2;
	public static int Z2 = 3;
	private String name;
	private String[] owners;
	private String world;
}
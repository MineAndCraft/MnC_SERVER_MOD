package me.Guga.Guga_SERVER_MOD.Handlers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class GugaWorldSizeHandler 
{
	public static boolean CanMove(Location loc)
	{
		if (!loc.getWorld().getName().matches("world"))
			return true;
		int x = loc.getBlockX();
		int z = loc.getBlockZ();

		if (x > worldSize)
			return false;
		else if (x < (worldSize * -1))
			return false;

		if (z > worldSize)
			return false;
		else if (z < (worldSize * -1))
			return false;
		return true;
	}
	public static void MoveBack(Player p)
	{
		int x = p.getLocation().getBlockX();
		int z = p.getLocation().getBlockZ();

		boolean xTooFar = false;
		boolean zTooFar = false;
		if (x > worldSize)
			xTooFar = true;
		else if (x < (worldSize * -1))
			xTooFar = true;

		if (z > worldSize)
			zTooFar = true;
		else if (z < (worldSize * -1))
			zTooFar = true;

		int xNew = 0;
		int zNew = 0;
		if (xTooFar)
		{
			if (x > 0)
			{
				xNew = x - (x - worldSize) - 1;
			}
			else
			{
				xNew = x + ( -1 * (x + worldSize)) + 1;
			}
		}
		if (zTooFar)
		{
			if (z > 0)
			{
				zNew = z - (z - worldSize) - 1;
			}
			else
			{
				xNew = z + ( -1 * (z + worldSize)) + 1;
			}
		}
		p.teleport(p.getWorld().getHighestBlockAt(xNew, zNew).getLocation());
		p.sendMessage("Dosel jste na konec mapy!");
	}

	private static int worldSize = 3500; // world size in blocks - in each direction
}
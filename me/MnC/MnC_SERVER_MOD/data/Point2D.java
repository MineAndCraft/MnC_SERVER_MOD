package me.MnC.MnC_SERVER_MOD.data;

public class Point2D
{
	public double x;
	public double y;
	
	public Point2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double x()
	{
		return x;
	}
	
	public double y()
	{
		return y;
	}

	
	/**
	 * Does this point lie on the line between points {@code a} and {@code b}
	 */
	public boolean liesOn(Point2D a, Point2D b)
	{
		if(a.x <= x && x <= b.x || b.x <= x && x <= a.x)
		{
			if(a.y <= y && y <= b.y || b.y <= y && y <= a.y)
			{
				//TODO this doesn't solve the line, it's just for squares
				return true;
			}
		}
		return false;
	}
}

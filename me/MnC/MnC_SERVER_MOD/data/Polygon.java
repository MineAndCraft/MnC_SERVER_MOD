package me.MnC.MnC_SERVER_MOD.data;

// thanks to http://introcs.cs.princeton.edu/java/35purple/Polygon.java.html

public class Polygon
{
	// all points of the polygon in positive direction
	private Point2D[] points;
	
	private int n;
	
	public Polygon(Point2D[] newpoints)
	{
		n = newpoints.length;
		points = new Point2D[n];
		for(int i=0;i<n;i++)
		{
			points[i] = new Point2D(newpoints[i].x(),newpoints[i].y());
		}
	}
	
	public boolean contains(Point2D p0)
	{
		double x=p0.x();
		double y=p0.y();
		
		// check whether the point is inside
		boolean oddNodes = false;
		int i = 0;
		int j = n-1;
		for (; i < n; i++)
		{
			Point2D pi = points[i];
			Point2D pj = points[j];
			
			// if the point lies on one of the sides it's in the polygon
			if(p0.liesOn(points[i],points[j]))
				return true; 
			
			if (( pi.y() < y && pj.y() >=y
				||   pj.y() < y && pi.y() >= y)
				&&  ( pi.x() <=x || pj.x() <=x ))
			{
				oddNodes = oddNodes ^ (pi.x()+(y-pi.y())/(pj.y()-pi.y())*(pj.x()-pi.x())<x);
			}
			j=i;
		}
		
		return oddNodes;
	}
	
	public static String toString(Polygon polygon)
	{
		StringBuilder sb = new StringBuilder(polygon.points[0].x()+" "+polygon.points[0].y());
		for(int i=1;i < polygon.n;i++)
		{
			sb.append(",");
			sb.append(polygon.points[i].x());
			sb.append(" ");
			sb.append(polygon.points[i].y());
		}
		return sb.toString();
	}

	public static Polygon fromString(String string)
	{
		String[] vertices = string.split(",");
		Point2D[] points = new Point2D[vertices.length];
		for(int i=0;i<vertices.length;i++)
		{
			String[] point = vertices[i].split(" ");
			points[i] = new Point2D(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
		}
		return new Polygon(points);
	}

	public boolean contains(int x, int y)
	{
		return contains(new Point2D(x,y));
	}
	
	public int n()
	{
		return n;
	}

	
	public Point2D get(int i)
	{
		return points[i];
	}
}

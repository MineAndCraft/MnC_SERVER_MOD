package me.Guga.Guga_SERVER_MOD;

public class Locker 
{
	Locker(Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}
	
	public static enum LockableBlocks
	{
		CHEST(54, "chest"),
		FURNANCE(61, "furnance"),
		BURNING_FURNANCE(62, "furnance"),
		DISPENSER(23, "dispenser");
		
		LockableBlocks(int blockID, String name)
		{
			this.blockID = blockID;
			this.name = name;
		}
		private String getName()
		{
			return this.name;
		}
		private int getID()
		{
			return this.blockID;
		}
		public static String getNameByID(int id)
		{
			LockableBlocks[] lb = LockableBlocks.values();
			int i;
			for(i=0;i<lb.length;i++)
			{
				if(lb[i].getID() == id)
					return lb[i].getName();
			}
			return "";
		}
		private int blockID;
		private String name;
	}
	private Guga_SERVER_MOD plugin;
}

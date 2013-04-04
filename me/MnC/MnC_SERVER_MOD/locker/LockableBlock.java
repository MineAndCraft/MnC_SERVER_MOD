package me.MnC.MnC_SERVER_MOD.locker;

public enum LockableBlock
{
	CHEST(54, "chest"),
	FURNANCE(61, "furnance"),
	BURNING_FURNANCE(62, "furnance");

	LockableBlock(int blockID, String name)
	{
		this.blockID = blockID;
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	public int getID()
	{
		return this.blockID;
	}
	public static LockableBlock getByID(int id)
	{
		LockableBlock[] lb = LockableBlock.values();
		int i;
		for(i=0;i<lb.length;i++)
		{
			if(lb[i].getID() == id)
				return lb[i];
		}
		return null;
	}
	
	public static boolean isLockableBlock(int id)
	{
		for (LockableBlock lb : LockableBlock.values())
		{
			if(lb.getID() == id)
				return true;
		}
		return false;
	}
	
	private int blockID;
	private String name;
}
package me.Guga.Guga_SERVER_MOD;

public class GameMaster 
{
	public GameMaster(String name, Rank rank)
	{
		this.name = name;
		this.rank = rank;
	}
	public String GetName()
	{
		return this.name;
	}
	public Rank GetRank()
	{
		return this.rank;
	}
	public boolean IsAtleastRank(Rank req)
	{
		if (this.rank.IsRanked(req))
			return true;
		return false;
	}
	public boolean IsAtleastGM()
	{
		if (this.rank.IsRanked(Rank.GAMEMASTER))
			return true;
		return false;
	}
	public boolean IsAdmin()
	{
		if (this.rank.IsRanked(Rank.ADMIN))
			return true;
		return false;
	}
	public static enum Rank
	{
		ADMIN(0, "ADMIN"), GAMEMASTER(1, "GAMEMASTER"), BUILDER(2, "BUILDER"), EVENTER(3, "EVENTER");
		private Rank(int val, String rankName)
		{
			this.val = val;
			this.rankName = rankName;
		}
		public boolean IsRanked(Rank required)
		{
			if (this.GetValue() <= required.GetValue())
				return true;
			return false;
		}
		public int GetValue()
		{
			return this.val;
		}
		public String GetRankName()
		{
			return this.rankName;
		}
		public static Rank GetRankByName(String name)
		{
			Rank[] r = Rank.values();
			int i = 0;
			while (i < r.length)
			{
				if (r[i].GetRankName().matches(name))
				{
					return r[i];
				}
				i++;
			}
			return null;
		}
		private int val;
		private String rankName;
	}
	String name;
	private Rank rank;
}

package me.Guga.Guga_SERVER_MOD;

import me.Guga.Guga_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
	public static void setOpName(Player p)
	{
		GameMaster gm;
		if ( (gm = GameMasterHandler.GetGMByName(p.getName())) != null)
		{
			String []name=p.getName().split("'");
			if (gm.GetRank() == Rank.ADMIN)
			{
				{
						p.setDisplayName(ChatColor.RED + "ADMIN'" + ChatColor.WHITE + name[1]);
				}
			}
			else if (gm.GetRank() == Rank.GAMEMASTER)
			{
				p.setDisplayName(ChatColor.RED + "GM'" + ChatColor.WHITE + name[1]);
			}
			else if(gm.GetRank()==Rank.WEBMASTER)
			{
				p.setDisplayName(ChatColor.RED + "WEB'" + ChatColor.WHITE + p.getName());
			}
		}
		p.setPlayerListName(ChatColor.AQUA+p.getName());
	}
	public static enum Rank
	{
		ADMIN(0, "ADMIN"), GAMEMASTER(1, "GAMEMASTER"), WEBMASTER(2, "WEBMASTER"),BUILDER(3, "BUILDER"), EVENTER(4, "EVENTER");
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

package me.MnC.MnC_SERVER_MOD.RPG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.MnC.MnC_SERVER_MOD.DatabaseManager;
import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GugaProfession2 extends GugaProfession
{
	protected int userId;
	
	protected Player _player;
	
	private GugaProfession2(Player player,int id,int exp)
	{
		super(player.getName(),exp,MnC_SERVER_MOD.getInstance());
		this.userId = id;
		_player = player;
	}
	
	protected GugaProfession2(){
		super("UNKNOWN",0,MnC_SERVER_MOD.getInstance());
	}
	
	public void onBlockBreak(Block block)
	{
		if(block.getTypeId() == 50 || block.getTypeId() == 78 )
			return;
		
		if(block.getTypeId() == 17 && block.getData() == 3)
		{
			this.GainExperience(1);
			return;
		}

		this.GainExperience(4);	
	}

	public void addExperience(int exp)
	{
		this.GainExperience(exp);
		this.save();
	}
	
	public synchronized void save()
	{
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("UPDATE `mnc_profession` prof SET prof.experience = ? WHERE prof.user_id = ?");)
		{
		    stat.setInt(1, this.xp);
		    stat.setInt(2, this.userId);
		    stat.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Method designed as internal event so GugaProfession2 knows when player levels up
	 */
	@Override
	protected void onLevelUp()
	{
		save(); // save profession upon leveling up
		Bukkit.getServer().getPluginManager().callEvent(new GugaProfessionPlayerLevelUpEvent(this));
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * 
	 * @param player org.bukkit.entity.Player instance of the Player
	 * @param playerId Id of the player (This is actually used to load the profession data)
	 * @return null if there is no profession for player playerId, valid GugaProfession2 class instance otherwise 
	 */
	public static GugaProfession2 loadProfession(Player player,int playerId)
	{
		if(playerId==0)
			return null;
				
		GugaProfession2 profession = null;
		try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT experience FROM `mnc_profession` WHERE user_id=?");)
		{
			stat.setInt(1, playerId);
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				int exp = result.getInt("experience");
				profession = new GugaProfession2(player,playerId,exp);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return profession;
	}
}

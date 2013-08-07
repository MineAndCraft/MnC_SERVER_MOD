package me.MnC.MnC_SERVER_MOD;

//import me.MnC.MnC_SERVER_MOD.events.PlayerPositionCheckEvent;

//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;

public class PositionCheck
{
	//private static int checkTaskId;
	
	public static void enable()
	{
		/*
		checkTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MnC_SERVER_MOD.getInstance(), new Runnable(){
			public void run()
			{
				for(Player bplayer : Bukkit.getServer().getOnlinePlayers())
				{
					MinecraftPlayer player = UserManager.getInstance().getUser(bplayer.getName());
					if(player == null)
						continue;
					
					Bukkit.getPluginManager().callEvent(new PlayerPositionCheckEvent(player));
					
					player.updatePosition();
				}
			}
		}, 10, 10);
		*/
	}
	
	public static void disable()
	{
		//Bukkit.getScheduler().cancelTask(checkTaskId);
	}

}

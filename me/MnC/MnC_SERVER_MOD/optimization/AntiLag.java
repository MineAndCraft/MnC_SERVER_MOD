package me.MnC.MnC_SERVER_MOD.optimization;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.event.Listener;

/**
 * Anti-Lag utilities:
 * <li>MobLimiter</li>
 */
public class AntiLag implements Listener
{
	private final MnC_SERVER_MOD plugin = MnC_SERVER_MOD.getInstance(); 
	
	private MobLimiter mobLimiter;
	
	public AntiLag()
	{
		//TODO add a config option to disable
		mobLimiter = new MobLimiter();
		
		plugin.getServer().getPluginManager().registerEvents(mobLimiter, plugin);
	}

	
	public void disable()
	{
		// nothing to disable
	}
	

}

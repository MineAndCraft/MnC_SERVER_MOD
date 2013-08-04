package me.MnC.MnC_SERVER_MOD.admincommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

public class AdminCommandHandler
{
	private static final Logger _log = Logger.getLogger(AdminCommandHandler.class.getName());
	
	private static AdminCommandHandler _instance = new AdminCommandHandler();
	
	/**
	 * Reference to Bukkit's command map
	 */
	private static CommandMap cmap;

	/**
	 * List of currently registered commands
	 */
	private static ArrayList<AdminCommand> registeredCommands = new ArrayList<AdminCommand>(); 
	
	protected AdminCommandHandler()
	{
		try
		{
			Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			cmap = ((CommandMap)f.get(Bukkit.getServer()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected static AdminCommandHandler getInstance()
	{
		return _instance;
	}
	
	/**
	 * Registers {@code command} into command map 
	 * @param command The command to be Registered
	 */
	public static void registerCommand(AdminCommand command)
	{
		if(registeredCommands.contains(command))
			return;
		
		if(cmap.register("mnc", command))
		{
			registeredCommands.add(command);
			_log.info("Registered admin command /"+cmap.getCommand(command.getName()).getName()+".");
		}
	}

	public static void registerCommands()
	{
		registerCommand(new TestCommand());
	}
}

package me.MnC.MnC_SERVER_MOD.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

public class CommandController implements CommandExecutor
{
	private final static HashMap<Command, Object> handlers = new HashMap<Command, Object>();
	private final static HashMap<Command, Method> methods = new HashMap<Command, Method>();
	private final static HashMap<String, SubCommand> subCommands = new HashMap<String, SubCommand>();
	private final static HashMap<String, Object> subHandlers = new HashMap<String, Object>();
	private final static HashMap<String, Method> subMethods = new HashMap<String, Method>();

	public static void registerCommands(JavaPlugin plugin, Object handler)
	{
		for (Method method : handler.getClass().getMethods())
		{
			Class<?>[] params = method.getParameterTypes();
			if (params.length == 2 && CommandSender.class.isAssignableFrom(params[0]) && String[].class.equals(params[1]))
			{
				if (isCommandHandler(method))
				{
					CommandHandler annotation = method.getAnnotation(CommandHandler.class);
					if (plugin.getCommand(annotation.name()) != null)
					{
						plugin.getCommand(annotation.name()).setExecutor(new CommandController());
						if (!(annotation.aliases().equals(new String[]
						{ "" })))
							plugin.getCommand(annotation.name()).setAliases(Lists.newArrayList(annotation.aliases()));
						if (!annotation.description().equals(""))
							plugin.getCommand(annotation.name()).setDescription(annotation.description());
						if (!annotation.usage().equals(""))
							plugin.getCommand(annotation.name()).setUsage(annotation.usage());
						if (!annotation.permission().equals(""))
							plugin.getCommand(annotation.name()).setPermission(annotation.permission());
						if (!annotation.permissionMessage().equals(""))
							plugin.getCommand(annotation.name()).setPermissionMessage(ChatColor.RED + annotation.permissionMessage());
						handlers.put(plugin.getCommand(annotation.name()), handler);
						methods.put(plugin.getCommand(annotation.name()), method);
					}
				}
				if (isSubCommandHandler(method))
				{
					SubCommandHandler annotation = method.getAnnotation(SubCommandHandler.class);
					if (plugin.getCommand(annotation.parent()) != null)
					{
						plugin.getCommand(annotation.parent()).setExecutor(new CommandController());
						SubCommand subCommand = new SubCommand(plugin.getCommand(annotation.parent()), annotation.name());
						subCommand.permission = annotation.permission();
						subCommand.permissionMessage = annotation.permissionMessage();
						subCommands.put(subCommand.toString(), subCommand);
						subHandlers.put(subCommand.toString(), handler);
						subMethods.put(subCommand.toString(), method);
					}
				}
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CommandHandler
	{
		String name();
		String[] aliases() default { "" };
		String description() default "";
		String usage() default "";
		String permission() default "";
		String permissionMessage() default "Nemate dostatecna opravneni k pouziti tohoto prikazu!";
	}

	private static boolean isCommandHandler(Method method)
	{
		return method.getAnnotation(CommandHandler.class) != null;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface SubCommandHandler
	{
		String parent();
		String name();
		String permission() default "";
		String permissionMessage() default "Nemate dostatecna opravneni k pouziti tohoto prikazu!";
	}

	private static boolean isSubCommandHandler(Method method)
	{
		return method.getAnnotation(SubCommandHandler.class) != null;
	}

	private static class SubCommand
	{
		public final Command superCommand;
		public final String subCommand;
		public String permission;
		public String permissionMessage;

		public SubCommand(Command superCommand, String subCommand)
		{
			this.superCommand = superCommand;
			this.subCommand = subCommand.toLowerCase();
		}

		public boolean equals(Object x)
		{
			return toString().equals(x.toString());
		}

		public String toString()
		{
			return (superCommand.getName() + " " + subCommand).trim();
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (args.length > 0)
		{
			SubCommand subCommand = new SubCommand(command, args[0]);
			subCommand = subCommands.get(subCommand.toString());
			Object subHandler = subHandlers.get(subCommand.toString());
			Method subMethod = subMethods.get(subCommand.toString());
			if (subHandler != null && subMethod != null)
			{
				String[] subArgs = new String[args.length - 1];
				for (int i = 1; i < args.length; i++)
					subArgs[i - 1] = args[i];
				if (subMethod.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "Tento prikaz muzete pouzit pouze jako prihlaseny hrac!");
					return true;
				}
				if (subMethod.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(sender instanceof ConsoleCommandSender))
				{
					sender.sendMessage(ChatColor.RED + "Tento prikaz muzete pouzit pouze z konzole!");
					return true;
				}
				if (!subCommand.permission.isEmpty() && !sender.hasPermission(subCommand.permission))
				{
					sender.sendMessage(ChatColor.RED + subCommand.permissionMessage);
					return true;
				}
				try
				{
					subMethod.invoke(subHandler, sender, args);
				} catch (Exception e)
				{
					sender.sendMessage(ChatColor.RED + "An error occurred while trying to process the command");
					e.printStackTrace();
				}
				return true;
			}
		}
		Object handler = handlers.get(command);
		Method method = methods.get(command);
		if (handler != null && method != null)
		{
			if (method.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Tento prikaz muzete pouzit pouze jako prihlaseny hrac!");
				return true;
			}
			if (method.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(sender instanceof ConsoleCommandSender))
			{
				sender.sendMessage(ChatColor.RED + "Tento prikaz muzete pouzit pouze z konzole!");
				return true;
			}
			try
			{
				method.invoke(handler, sender, args);
			} catch (Exception e)
			{
				sender.sendMessage(ChatColor.RED + "An error occurred while trying to process the command");
				e.printStackTrace();
			}
		}
		else
			sender.sendMessage("Prikaz nebyl rozpoznan! Napiste \"help\" pro zobrazeni vsech dostupnych prikazu.");
		return true;
	}
}
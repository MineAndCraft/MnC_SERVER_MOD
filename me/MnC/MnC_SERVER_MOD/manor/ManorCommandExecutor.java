package me.MnC.MnC_SERVER_MOD.manor;

import me.MnC.MnC_SERVER_MOD.MinecraftPlayer;
import me.MnC.MnC_SERVER_MOD.UserManager;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManorCommandExecutor implements CommandExecutor
{
	private ManorManager _manager;
	
	public ManorCommandExecutor(ManorManager m)
	{
		_manager = m;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		MinecraftPlayer player = null;
		if(sender instanceof Player)
		{
			player = UserManager.getInstance().getUser(sender.getName());
		}
		
		if(cmd.getName().equalsIgnoreCase("manor"))
		{
			String subCommand = (args.length > 0)? args[0] : "";
			if(args.length == 0)
			{
				player.getPlayerInstance().sendMessage(new String[]{
					"/manor info - displays informations about the manor you currently stand in",
					"/manor tp <manor> - teleports you to this manors spawn",
					"/manor admin - enables you to govern your manor",
				});
			}
			else if(subCommand.equals("info"))
			{
				Manor manor = _manager.getManorByLocation(player.getPlayerInstance().getLocation());
				if(manor == null)
				{
					player.getPlayerInstance().sendMessage("There is no manor where you stand");
				}
				else
				{
					player.getPlayerInstance().sendMessage(new String[]{
						"This is manor "+manor.getName(),
						"Current lord is "+manor.getLordName(),
						"You " + ( (manor.isCitizen(player))? "are" : "are not" ) + " a citizen of this manor."
					});
				}
			}
			else if(subCommand.equals("tp"))
			{
				if(args.length == 2)
				{
					Manor manor = _manager.getManorByName(args[1]);
					if(manor == null)
						player.getPlayerInstance().sendMessage("There is no such manor");
					else
					{
						player.getPlayerInstance().teleport(manor.getSpawnLocation());
						player.getPlayerInstance().sendMessage("You have been teleported to "+manor.getName());
					}
				}
				else
				{
					player.getPlayerInstance().sendMessage("Usage: /manor tp <manor name>");
				}
			}
			else if(subCommand.equals("admin"))
			{
				if(args.length > 1)
				{
					Manor manor = _manager.getManorByName(args[1]);
					if(manor == null)
					{
						player.getPlayerInstance().sendMessage("There is no such manor");
					}
					else if(manor.getLordId() != player.getId() && !GameMasterHandler.IsAdmin(player.getName()))
					{
						player.getPlayerInstance().sendMessage("You are not the lord of this manor. You cannot edit it");
					}
					else
					{
						String subCommand2 = (args.length > 2) ? args[2] : "";
						if(args.length == 2)
						{
							player.getPlayerInstance().sendMessage(new String[]{
								"/manor admin <manor> citizens (+|-) <player>",
								"/manor admin <manor> citizens list",
								"/manor admin <manor> flag <flag> (true|false)"
							});
						}
						else if(subCommand2.equals("citizens"))
						{
							String option = args[3];
							if(option.equals("+") && args.length == 5)
							{
								int playerId = UserManager.getInstance().getUserId(args[4]);
								if(playerId == 0)
								{
									player.getPlayerInstance().sendMessage("This player doesn't exist.");
									return false;
								}
								manor.addCitizen(playerId);
								player.getPlayerInstance().sendMessage("Citizen added");
							}
							else if(option.equals("-") && args.length == 5)
							{
								int playerId = UserManager.getInstance().getUserId(args[4]);
								if(playerId == 0)
								{
									player.getPlayerInstance().sendMessage("This player doesn't exist.");
									return false;
								}
								manor.removeCitizen(playerId);
								player.getPlayerInstance().sendMessage("Citizen removed");
							}
							else if(option.equals("list"))
							{
								player.getPlayerInstance().sendMessage("Manor "+manor.getName()+" has these ctizens:");
								player.getPlayerInstance().sendMessage(manor.getCitizenNames().toString());
							}
							else
							{
								player.getPlayerInstance().sendMessage("Invalid option");
								return false;
							}
						}
						else if(subCommand2.equals("flag") && args.length == 5)
						{
							String flagName = args[3].toLowerCase();
							boolean flagValue;
							if(args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("allow") || args[4].equalsIgnoreCase("1"))
								flagValue = true;
							else if(args[4].equalsIgnoreCase("false") || args[4].equalsIgnoreCase("deny") || args[4].equalsIgnoreCase("0"))
								flagValue = false;
							else
							{
								player.getPlayerInstance().sendMessage("Invalid flag value. Use either true or false.");
								return false;
							}
							
							switch(flagName)
							{
								case "tnt":
								case "monsters":
									break;
								
								default:
								{
									player.getPlayerInstance().sendMessage("Invalid flag name.");
									return false;
								}
							}
							
							manor.setFlag(flagName, flagValue);
							player.getPlayerInstance().sendMessage("Flag "+flagName+" was set to "+flagValue+" for manor "+manor.getName());
						}
						else
						{
							player.getPlayerInstance().sendMessage("There is no such setting.");
						}
					}
				}
				else
				{
					player.getPlayerInstance().sendMessage("Usage: /manor admin <manor>");
				}
			}
			else
			{
				sender.sendMessage("No such manor subcommand.");
			}
		}
		
		return false;
	}

}

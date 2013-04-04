package me.MnC.MnC_SERVER_MOD.locker;

import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LockerCommandsExecutor implements CommandExecutor
{
	final BlockLocker blockLocker;
	
	LockerCommandsExecutor(BlockLocker locker)
	{
		this.blockLocker = locker;
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String commandLabel, String[] args)
	{
		if(!(s instanceof Player))
		{
			s.sendMessage("This command can be used only by player.");
			return false;
		}
		
		Player sender = (Player)s; 
		 
		if(cmd.getName().equalsIgnoreCase("lock"))
		{
			CommandLock(sender);
		}
		else if(cmd.getName().equalsIgnoreCase("unlock"))
		{
			CommandUnlock(sender);
		}
		else if(cmd.getName().equalsIgnoreCase("lmod"))
		{
			CommandLmod(sender,args);
		}
		else if(cmd.getName().equalsIgnoreCase("locker"))
		{
			CommandLocker(sender);
		}
		else
			return false;
		return true;
	}
	
	public void CommandLocker(Player sender)
	{
		sender.sendMessage(ChatColor.BLUE+"***********");
		sender.sendMessage(ChatColor.BLUE+"LOCKER");
		sender.sendMessage(ChatColor.BLUE+"***********");
		sender.sendMessage("Zamknout muzete truhlu a pec.");
		sender.sendMessage("Blocky se zamykaji automaticky pri polozeni!");
		sender.sendMessage("PRIKAZY:");
		sender.sendMessage(ChatColor.AQUA + "/lock " + ChatColor.WHITE + "- zamce block");
		sender.sendMessage(ChatColor.AQUA + "/unlock " + ChatColor.WHITE + "- odemce block");
		sender.sendMessage(ChatColor.AQUA + "/lmod + <hrac> "+ChatColor.WHITE + "- prida hraci pristup k blocku");
		sender.sendMessage(ChatColor.AQUA + "/lmod - <hrac> "+ChatColor.WHITE + "- odebere hraci pristup k blocku");
		sender.sendMessage(ChatColor.AQUA + "/lmod "+ChatColor.WHITE + "- zobrazi hrace s pristupem k blocku");
	}
	
	public void CommandLock(Player sender)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54
		if (LockableBlock.isLockableBlock(blockType))
		{
			if (!blockLocker.isLocked(chest))
			{
				blockLocker.LockBlock(chest,sender.getName());
				ChatHandler.SuccessMsg(sender, "Vas block byl zamcen.");
			}
			else
			{
				ChatHandler.FailMsg(sender, "Tento block jiz nekdo zamknul");
			}
		}	
		else
		{
			ChatHandler.FailMsg(sender, "Tento block nelze zamcit!");
		}
	}
	
	public void CommandUnlock(Player sender)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54
		if (LockableBlock.isLockableBlock(blockType))
		{
			int lock_id = blockLocker.findBlockLock(chest);
			if(lock_id == 0)
			{
				ChatHandler.FailMsg(sender, "Tento block neni zamcen");		
			}
			else
			{
				String owner = blockLocker.getLockOwner(lock_id); 
				if (owner.equalsIgnoreCase(sender.getName()) || GameMasterHandler.IsAtleastGM(sender.getName()))
				{
					blockLocker.UnlockLock(lock_id);
					ChatHandler.SuccessMsg(sender, "Vas blok byl odemcen.");
				}
				else
				{
					ChatHandler.FailMsg(sender, "Tento blok nemuzete odemknout! Vlastnikem je "+owner+".");
				}
			}
		}
		else
		{
			ChatHandler.FailMsg(sender, "Tento block nelze odemknout!");
		}
	}
	
	public void CommandLmod(Player sender, String[] args)
	{
		Block chest = sender.getTargetBlock(null, 10);
		int blockType = chest.getTypeId(); // chest = 54
		if (!LockableBlock.isLockableBlock(blockType))
		{
			ChatHandler.FailMsg(sender, "Tento block neni zamykatelny!");
			return;
		}
		
		int lock_id = blockLocker.findBlockLock(chest);
		if(lock_id == 0)
		{
			ChatHandler.FailMsg(sender, "Tento block neni zamcen!");
			return;
		}
		
		String owner = blockLocker.getLockOwner(lock_id);
		if(!owner.equalsIgnoreCase(sender.getName()))
		{
			ChatHandler.FailMsg(sender, "Tento block neni vas! Patri hraci "+owner+".");
			return;
		}
		
		
		if(args.length == 0)
		{
			sender.sendMessage("Tito uzivatele maji pristup k tomuto blocku: " + blockLocker.listAccesses(lock_id));
		}
		else if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("+"))
			{
				blockLocker.addAccess(lock_id,args[1]);
				ChatHandler.SuccessMsg(sender, "Pristup pridan.");
			}
			else if(args[0].equalsIgnoreCase("-"))
			{
				blockLocker.removeAccess(lock_id,args[1]);
				ChatHandler.SuccessMsg(sender, "Pristup odebran.");
			}
		}
	}
}

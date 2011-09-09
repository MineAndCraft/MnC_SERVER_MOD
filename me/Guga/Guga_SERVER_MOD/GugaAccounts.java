package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GugaAccounts 
{
	GugaAccounts (Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		LoadAccounts();
	}
	public void LoginUser(Player p,String password)
	{
		String pName = p.getName();
		int i = 0;
		while (accNames[i] != null)
		{
			if (pName.equalsIgnoreCase((accNames[i])))
			{
				if (password.matches(passwords[i]))
				{
					loggedUsers.add(pName);
					p.sendMessage("You are now logged in.");
					//playerStart.remove(pName);
					Integer taskId = tpTasks.get(pName);
					if (taskId != null)
					{
						plugin.scheduler.cancelTask(taskId.intValue());
						tpTasks.remove(pName);
					}
					break;
				}
				else
				{
					p.sendMessage("Wrong Password!");
					break;
				}
			}
			i++;
		}
		
	}
	public void StartTpTask(Player p)
	{
		
		final String pName = p.getName();
		final Player pl = p;
		int taskId = plugin.scheduler.scheduleAsyncRepeatingTask(plugin, new Runnable(){
			public void run()
			{
				pl.teleport(playerStart.get(pName));
				if (UserIsRegistered(pl))
				{
					pl.sendMessage("Please Login!");
				}
				else
				{
					pl.sendMessage("Please Register!");
				}
			}
		}, 80, 80);
		tpTasks.put(pName,taskId);
	}
	public boolean UserIsLogged(Player p)
	{
		if (loggedUsers.contains(p.getName()))
		{
			return true;
		}
		return false;
	}
	public boolean UserIsRegistered(Player p)
	{
		int i = 0;
		String pName = p.getName();
		while (accNames[i] != null)
		{
			if (pName.equalsIgnoreCase(accNames[i]))
			{
				return true;
			}
			i++;
		}
		return false;
	}
	public void RegisterUser(Player p,String password)
	{
		int i = 0;
		String pName = p.getName();
		
		while (accNames[i] != null)
		{
			i++;
		}
		accNames[i] = pName;
		passwords[i] = password;
		p.sendMessage("You are succesfuly registered.");
		SaveAccounts();
		LoginUser(p,password);
	}
	public void ChangePassword(Player p, String oldPass, String newPass)
	{
		int i = 0;
		String pName = p.getName();
		while (accNames[i] != null)
		{
			if (accNames[i].equalsIgnoreCase(pName))
			{
				break;
			}
			i++;
		}
		if (passwords[i].matches(oldPass))
		{
			passwords[i] = newPass;
			p.sendMessage("Your password has been succesfully changed.");
			SaveAccounts();
		}
		else
		{
			p.sendMessage("Wrong password!");
		}
	}
	public String GetStatus(Player p)
	{
		String pName = p.getName();
		String status;
		
		if ((status = playerStatus.get(pName)) == null)
		{
			status = "Online";
		}
		return status;
	}
	public void SetStatus(Player p, String status[])
	{
		String pName = p.getName();
		int i = 0;
		String arg = "";
		String msg = "";
		while (i < status.length)
		{
			arg = status[i];
			msg += arg;
			msg += " ";
			i++;
		}
		playerStatus.put(pName, msg);
		p.getServer().broadcastMessage(p.getName() + " has set his status to: '" + msg + "'");
	}
	public void SetStartLocation(Player p,Location newLoc)
	{
		playerStart.put(p.getName(), newLoc);
	}
	public void LoadAccounts()
	{
		plugin.log.info("Loading Accounts Data...");
		File accounts = new File(accountsFile);
		if (!accounts.exists())
		{
			try 
			{
				accounts.createNewFile();
				return;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return;
			}
		}
		else
		{
			try 
			{
				FileInputStream fRead = new FileInputStream(accounts);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				try {
					int i = 0;
					
					while ((line = bReader.readLine()) != null)
					{
						accNames[i] = line.split(";")[0];
						passwords[i] = line.split(";")[1];
						i++;
					}
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void SaveAccounts()
	{
		plugin.log.info("Saving Accounts Data...");
		File accounts = new File(accountsFile);
		if (!accounts.exists())
		{
			try 
			{
				accounts.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			FileWriter fStream = new FileWriter(accounts, false);
			BufferedWriter bWriter;
			bWriter = new BufferedWriter(fStream);
			String line;
			int i = 0;
			while (accNames[i] != null)
			{
				line = accNames[i]+";"+passwords[i];
				bWriter.write(line);
				bWriter.newLine();
				i++;
			}
			bWriter.close();
			fStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// *********************************PLAYERS ONLINE*********************************
	public  ArrayList<String> loggedUsers = new ArrayList<String>();
	
	// *********************************STATUSES*********************************
	public HashMap<String, String> playerStatus = new HashMap<String, String>(); //<playerName, status>
	
	// *********************************START LOCATION*********************************
	public HashMap<String, Location> playerStart = new HashMap<String, Location>(); // <playerName, location>
	public HashMap<String, Integer> tpTasks = new HashMap<String, Integer>();  // <pName, taskID>
	
	// *********************************ACCOUNT DETAILS*********************************
	public String accNames[] = new String[10000];
	public String passwords[] = new String[10000];
	
	private String accountsFile = "plugins/Accounts.acc";
	public static Guga_SERVER_MOD plugin;
}

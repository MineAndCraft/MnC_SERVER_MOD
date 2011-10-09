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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;


import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Guga_SERVER_MOD extends JavaPlugin
{	
	public void onDisable() 
	{
		log.info("GUGA MINECRAFT SERVER MOD has been disabled.");
		SaveProfessions();
		SaveCurrency();
		GugaAnnouncement.SaveAnnouncements();
		GugaPort.SavePlaces();
		GugaRegionHandler.SaveRegions();
		arena.SavePvpStats();
	}
	public void onEnable() 
	{
		PluginManager pManager = this.getServer().getPluginManager();
		pManager.registerEvent(Event.Type.PLAYER_JOIN, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_CHAT, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_QUIT, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_INTERACT, pListener, Event.Priority.High, this);
		pManager.registerEvent(Event.Type.ENTITY_DAMAGE, enListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.BLOCK_BREAK, bListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.BLOCK_DAMAGE, bListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.REDSTONE_CHANGE, bListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.BLOCK_PLACE, bListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_MOVE, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.ENTITY_EXPLODE, enListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_RESPAWN, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, enListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.BLOCK_IGNITE, bListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.BLOCK_BURN, bListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.PLAYER_TELEPORT, pListener, Event.Priority.Normal, this);
		pManager.registerEvent(Event.Type.ENTITY_DEATH, enListener, Event.Priority.Normal, this);
		
		GugaPort.SetPlugin(this);
		GugaCommands.SetPlugin(this);
		GugaAnnouncement.SetPlugin(this);
		GugaRegionHandler.SetPlugin(this);
		if (getServer().getWorld("arena") == null)
		{
			//WorldCreator.name("arena").createWorld();
			//WorldCreator.name("arena").environment(Environment.NORMAL);
			getServer().createWorld("arena", Environment.NORMAL);
		}
		arena.LoadArena();
		arena.LoadPvpStats();
		getServer().getWorld(arena.GetWorldName()).setPVP(true);
		getServer().getWorld("world").setPVP(false);
		getServer().getWorld("world_nether").setPVP(false);
		getServer().getWorld(arena.GetWorldName()).setSpawnFlags(false, false);
		scheduler = getServer().getScheduler();
		LoadProfessions();
		LoadCurrency();
		GugaPort.LoadPlaces();
		GugaRegionHandler.LoadRegions();
		chests = new GugaChests(this);
		GugaAnnouncement.LoadAnnouncements();
		GugaAnnouncement.StartAnnouncing();
		log.info("GUGA MINECRAFT SERVER MOD " + version + " is running.");
		log.info("Created by Guga 2011.");
	}
	public void SaveCurrency()
	{
		log.info("Saving Currency Data...");
		File curr = new File(currencyFile);
		if (!curr.exists())
		{
			try 
			{
				curr.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			FileWriter fStream = new FileWriter(curr, false);
			BufferedWriter bWriter;
			bWriter = new BufferedWriter(fStream);
			String line;
			String currency;
			String vipExp;
			String name;
			Iterator<GugaVirtualCurrency> i = playerCurrency.iterator();
			while (i.hasNext())
			{
				GugaVirtualCurrency p = i.next();
				name = p.GetPlayerName();
				vipExp = Long.toString(p.GetExpirationDate());
				currency = Integer.toString(p.GetCurrency());
				line = name + ";" + currency + ";" + vipExp;
				bWriter.write(line);
				bWriter.newLine();
			}
			
			bWriter.close();
			fStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void LoadCurrency()
	{
		log.info("Loading Currency Data...");
		File curr = new File(currencyFile);
		if (!curr.exists())
		{
			try 
			{
				curr.createNewFile();
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
				FileInputStream fRead = new FileInputStream(curr);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				String []splittedLine;
				long vipExp;
				String name;
				int currency;
				try {
					while ((line = bReader.readLine()) != null)
					{
						splittedLine = line.split(";");
						name = splittedLine[0];
						currency = Integer.parseInt(splittedLine[1]);
						vipExp = Long.parseLong(splittedLine[2]);
						
						playerCurrency.add(new GugaVirtualCurrency(this, name, currency,new Date(vipExp)));
					}
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void SaveProfessions()
	{
		log.info("Saving Professions Data...");
		File profs = new File(professionsFile);
		if (!profs.exists())
		{
			try 
			{
				profs.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			FileWriter fStream = new FileWriter(profs, false);
			BufferedWriter bWriter;
			bWriter = new BufferedWriter(fStream);
			String line;
			
			Collection<GugaProfession> profCollection;
			profCollection = professions.values();
			Object[] objectArray;
			objectArray = profCollection.toArray();
			GugaProfession prof;
			int i =0;
			while (i<objectArray.length)
			{
				prof = (GugaProfession) objectArray[i];
				line = prof.GetPlayerName() + ";" + prof.GetProfession() + ";" + prof.GetXp();
				bWriter.write(line);
				bWriter.newLine();
				i++;
			}
			bWriter.close();
			fStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void LoadProfessions()
	{
		log.info("Loading Professions Data...");
		File profs = new File(professionsFile);
		if (!profs.exists())
		{
			try 
			{
				profs.createNewFile();
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
				FileInputStream fRead = new FileInputStream(profs);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));		
				String line;
				String []splittedLine;
				String pName;
				String profName;
				String xp;
				try {
					while ((line = bReader.readLine()) != null)
					{
						splittedLine = line.split(";");
						pName = splittedLine[0];
						profName = splittedLine[1];
						xp = splittedLine[2];
						if (profName.matches("Miner"))
						{
							professions.put(pName, new GugaMiner(pName,Integer.parseInt(xp),this));
						}
						else if (profName.matches("Hunter"))
						{
							professions.put(pName, new GugaHunter(pName,Integer.parseInt(xp),this));
						}
					}
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (sender instanceof Player)
		{
			log.info(((Player)sender).getName() + " used  /" + cmd.getName() + " Command.");
		}
		//*****************************************/who*****************************************
		 if(cmd.getName().equalsIgnoreCase("who") && (sender instanceof Player))
		 { 
		   GugaCommands.CommandWho((Player)sender);
		   return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("places") && (sender instanceof Player))
		 {
			 GugaCommands.CommandPlaces((Player)sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("arena") && (sender instanceof Player))
		 {
			 GugaCommands.CommandArena((Player) sender, args);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("debug") && (sender instanceof ConsoleCommandSender))
		 {
			 GugaCommands.CommandDebug();
		 }
		 else if ((cmd.getName().equalsIgnoreCase("shop")) && (sender instanceof Player))		
		 {
			 GugaCommands.CommandShop((Player)sender,args);	 
			 return true;
		 }
		 else if ((cmd.getName().equalsIgnoreCase("vip")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandVIP((Player)sender, args);
			 return true;
		 }
		//*****************************************module*****************************************
		 else if(cmd.getName().equalsIgnoreCase("module") && (sender instanceof ConsoleCommandSender))
		 {
			 GugaCommands.CommandModule(args);
			 return true;
		 }
		//*****************************************/help*****************************************
		 else if (cmd.getName().equalsIgnoreCase("help"))
		 {
			 if (sender instanceof Player)
			 {
				 GugaCommands.CommandHelp((Player) sender);
				 return true;
			 }
			 else if (sender instanceof ConsoleCommandSender)
			 {
				 log.info("module	-	enables or disables specified module");
				 return true;
			 }
		 }
		 else if (cmd.getName().equalsIgnoreCase("r"))
		 {
			 if (sender instanceof Player)
			 {
				 GugaCommands.CommandReply((Player) sender, args);
				 return true;
			 }
		 }
		 else if (cmd.getName().equalsIgnoreCase("gm"))
		 {
			 if (sender instanceof Player)
			 {
				 if( ((Player)sender).isOp())
				 {
					 GugaCommands.CommandGM((Player)sender,args);
				 }
			 }
			 else if (sender instanceof ConsoleCommandSender)
			 {
				 
			 }
		 }
		 else if (cmd.getName().equalsIgnoreCase("rpg"))
		 {
			 if (sender instanceof Player)
			 {
				 GugaCommands.CommandRpg((Player)sender,args);
				 return true;
			 }
		 }
		 //*****************************************/status*****************************************
		 else if(cmd.getName().equalsIgnoreCase("status") && (sender instanceof Player))
		 {
			 GugaCommands.CommandStatus((Player)sender,args);
		 }
		 //*****************************************/register*****************************************
		 else if(cmd.getName().equalsIgnoreCase("register") && (sender instanceof Player))
		 {
			if (config.accountsModule)
			{
				GugaCommands.CommandRegister((Player)sender, args);
				return true;
			}			
			else
			{
				sender.sendMessage("This is not enabled on this server!");
				return true;
			}
		 }
		 else if(cmd.getName().equalsIgnoreCase("password") && (sender instanceof Player))
		 {
			 if (config.accountsModule)
				{
				 GugaCommands.CommandPassword((Player)sender, args);
					return true;
				}			
				else
				{
					sender.sendMessage("This is not enabled on this server!");
					return true;
				}
		 }
		//*****************************************/lock*****************************************
		 else if(cmd.getName().equalsIgnoreCase("lock") && (sender instanceof Player))
		 {
			 if (config.chestsModule)
			 {
				 GugaCommands.CommandLock((Player)sender);
				 return true;
			 }
			 else
			 {
				 Player p = (Player)sender;
				 p.sendMessage("This is not enabled on this server!");
				 return true;
			 }	
		 }
		//*****************************************/unlock*****************************************
		 else if(cmd.getName().equalsIgnoreCase("unlock") && (sender instanceof Player))
		 {
			 if (config.chestsModule)
			 {
				 GugaCommands.CommandUnlock((Player)sender);		
				 return true;
			 }
			 else
			 {
				 Player p = (Player)sender;
				 p.sendMessage("This is not enabled on this server!");
				 return true;
			 }
		 }
		//*****************************************/login*****************************************
		 else if(cmd.getName().equalsIgnoreCase("login") && (sender instanceof Player))
		 {
			 if (config.accountsModule)
			 {	 
				 GugaCommands.CommandLogin((Player)sender, args);
				 return true;
			 }
			 else
			 {
				 sender.sendMessage("This is not enabled on this server!");
				 return true;
			 }
		 }
		 return false;
	}
	public GugaVirtualCurrency FindPlayerCurrency(String pName)
	{
		Iterator<GugaVirtualCurrency> i = playerCurrency.iterator();
		while (i.hasNext())
		{
			GugaVirtualCurrency p = i.next();
			if (p.GetPlayerName().equalsIgnoreCase(pName))
			{
				return p;
			}
		}
		return null;
	}
	public void GenerateBlockType(Player p, int typeID, int x, int y, int z)
	{
		Location baseLoc = p.getTargetBlock(null, 50).getLocation();
		int xBase = baseLoc.getBlockX();
		int yBase = baseLoc.getBlockY();
		int zBase = baseLoc.getBlockZ();
		int x2 = x;
		int y2 = y;
		int z2 = z;
		Block block;
		int i = 0;
		if ( y < 0)
		{
			y2 = 0;
			i += y + 1;
		}
		else
		{
			y2 = y-1;
		}
		while (i <= y2)
		{
			int i2 = 0;
			if (z <0 )
			{
				z2 = 0;
				i2 += z + 1;
			}
			else
			{
				z2 = z-1;
			}
			while (i2<=z2)
			{
				int i3 = 0;
				if (x<0)
				{
					x2 = 0;
					i3 += x + 1;
				}
				else
				{
					x2 = x-1;
				}
				while (i3<=x2)
				{
					block = p.getWorld().getBlockAt(xBase+i3, yBase+i, zBase+i2);
					block.setTypeId(typeID);
					i3++;
				}
				i2++;
			}
			i++;
		}
	}
	public HashMap<String,GugaProfession> professions = new HashMap<String,GugaProfession>();
	
	// ************* chances *************
	public int IRON = 0;
	public int GOLD = 1;
	public int DIAMOND = 2;
	public boolean debug = false;
	public static final String version = "1.6.0";
	private static final String professionsFile = "plugins/Professions.dat";
	private static final String currencyFile = "plugins/Currency.dat";

	public final Logger log = Logger.getLogger("Minecraft");
	public BukkitScheduler scheduler;
	
	public final GugaConfiguration config = new GugaConfiguration(this);
	public final GugaPlayerListener pListener = new GugaPlayerListener(this);
	public final GugaEntityListener enListener = new GugaEntityListener(this);
	public final GugaBlockListener bListener = new GugaBlockListener(this);
	public final GugaAccounts acc = new GugaAccounts(this);
	public GugaChests chests;
	public final GugaLogger logger = new GugaLogger(this);
	public GugaArena arena = new GugaArena(this);
	public ArrayList<GugaVirtualCurrency> playerCurrency = new ArrayList<GugaVirtualCurrency>();
	public HashMap <Player,GugaAccounts> accounts = new HashMap<Player, GugaAccounts>();
}

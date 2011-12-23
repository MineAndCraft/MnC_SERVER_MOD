package me.Guga.Guga_SERVER_MOD;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
		GugaAuctionHandler.SaveAuctions();
		GugaAuctionHandler.SavePayments();
		GugaBanHandler.SaveBans();
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
		GugaAuctionHandler.SetPlugin(this);
		GameMasterHandler.SetPlugin(this);
		GugaBanHandler.SetPlugin(this);
		GugaEvent.SetPlugin(this);
		
		if (getServer().getWorld("arena") == null)
		{
			//getServer().createWorld("arena", Environment.NORMAL);
			getServer().createWorld(WorldCreator.name("arena").environment(Environment.NORMAL));
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
		GugaAuctionHandler.LoadAuctions();
		GugaAuctionHandler.LoadPayments();
		GugaBanHandler.LoadBans();
		chests = new GugaChests(this);
		GameMasterHandler.LoadGMs();
		GugaAnnouncement.LoadAnnouncements();
		GugaAnnouncement.StartAnnouncing();
		
		this.socketServer = new GugaSocketServer(12451, this);
		this.socketServer.ListenStart();
		log.info("GUGA MINECRAFT SERVER MOD " + version + " is running.");
		log.info("Created by Guga 2011.");
	}
	public void SaveCurrency()
	{
		log.info("Saving Currency Data...");
		GugaFile file = new GugaFile(currencyFile, GugaFile.WRITE_MODE);
		file.Open();
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
			file.WriteLine(line);
		}
		file.Close();
	}
	public void LoadCurrency()
	{
		log.info("Loading Currency Data...");
		GugaFile file = new GugaFile(currencyFile, GugaFile.READ_MODE);	
		file.Open();
		String line;
		String []splittedLine;
		long vipExp;
		String name;
		int currency;
		while ((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			name = splittedLine[0];
			currency = Integer.parseInt(splittedLine[1]);				
			vipExp = Long.parseLong(splittedLine[2]);
			playerCurrency.add(new GugaVirtualCurrency(this, name, currency,new Date(vipExp)));
		}
		file.Close();
	}
	public void SaveProfessions()
	{
		log.info("Saving Professions Data...");
		GugaFile file = new GugaFile(professionsFile, GugaFile.WRITE_MODE);
		file.Open();
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
			file.WriteLine(line);
			i++;
		}
		file.Close();
	}
	public void LoadProfessions()
	{
		log.info("Loading Professions Data...");
		GugaFile file = new GugaFile(professionsFile, GugaFile.READ_MODE);	
		file.Open();
		String line;
		String []splittedLine;
		String pName;
		String profName;
		String xp;
		while ((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			pName = splittedLine[0];
			profName = splittedLine[1];
			xp = splittedLine[2];
			if (profName.matches("Miner"))
			{
				professions.put(pName, new GugaProfession(pName,Integer.parseInt(xp),this));
			}
			else if (profName.matches("Hunter"))
			{
				professions.put(pName, new GugaProfession(pName,Integer.parseInt(xp),this));
			}
			else if (profName.matches("Profession"))
			{
				professions.put(pName, new GugaProfession(pName,Integer.parseInt(xp),this));
			}
		}
		file.Close();
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
		 else if (cmd.getName().equalsIgnoreCase("event"))
		 {
			 GugaCommands.CommandEvent((Player)sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("socket"))
		 {
			 	/*Connection c;
				Statement s;
				try {
					c = DriverManager.getConnection("jdbc:mysql://mineandcraft.cz:3306/minecraft", "minecraft", "kutilma130");
					s = c.createStatement();
					//ResultSet r =/s.executeUpdate("INSERT INTO banned (`name`, `ip`, `expiration`) VALUES ('nigger', '127.0.0.1', '"+new Date().getTime()+"');");
					ResultSet r = s.executeQuery("SELECT * FROM banned");
					if (r.next())
					{
						this.log.info(r.getString(3));
					}
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					c.add(Calendar.HOUR, 11);
					c.getT
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
		 }
		 else if (cmd.getName().equalsIgnoreCase("places") && (sender instanceof Player))
		 {
			 GugaCommands.CommandPlaces((Player)sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("ah") && (sender instanceof Player))
		 {
			 GugaCommands.CommandAH((Player)sender, args);
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
				 GugaCommands.CommandGM((Player)sender,args);
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
	public Location GetAvailablePortLocation(Location loc)
	{
		Location tpLoc = getServer().getWorld("").getHighestBlockAt(loc).getLocation();
		boolean canTeleport = false;
		int i = loc.getBlockY();
		while (!canTeleport)
		{
			loc = tpLoc;
			loc.add(0, 1, 0);
			if (loc.getBlock().getTypeId() == 0)
			{
				if (loc.getBlock().getRelative(BlockFace.UP).getTypeId() == 0)
				{
					tpLoc = loc;
					break;
				}
			}
			if (i >= 127)
			{
				break;
			}
			i++;
		}
		return tpLoc;
	}
	public HashMap<String,GugaProfession> professions = new HashMap<String,GugaProfession>();
	
	// ************* chances *************
	public int IRON = 0;
	public int GOLD = 1;
	public int DIAMOND = 2;
	public boolean debug = false;
	public static final String version = "2.1.2";
	private static final String professionsFile = "plugins/Professions.dat";
	private static final String currencyFile = "plugins/Currency.dat";

	public final Logger log = Logger.getLogger("Minecraft");
	public BukkitScheduler scheduler;
	
	public GugaSocketServer socketServer;
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
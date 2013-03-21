package me.Guga.Guga_SERVER_MOD;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import me.Guga.Guga_SERVER_MOD.Extensions.ExtensionManager;
import me.Guga.Guga_SERVER_MOD.Handlers.ChatHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GameMasterHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaCommands;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaRegionHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.HomesHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.SpawnsHandler;
import me.Guga.Guga_SERVER_MOD.Listeners.CustomListener;
import me.Guga.Guga_SERVER_MOD.Listeners.GugaBlockListener;
import me.Guga.Guga_SERVER_MOD.Listeners.GugaEntityListener;
import me.Guga.Guga_SERVER_MOD.Listeners.GugaPlayerListener;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Guga_SERVER_MOD extends JavaPlugin
{	
	public void onDisable() 
	{
		log.info("GUGA MINECRAFT SERVER MOD has been disabled.");
		GugaEvent.ClearAllGroups();
		this.userManager.save();
		this.extensionManager.disable();
		GugaRegionHandler.SaveRegions();
		SpawnsHandler.SaveSpawns();
		arena.SavePvpStats();
		arena.SaveArenas();
		dbConfig.disconnectDb();
		_enabled = false;
	}

	public void onEnable() 
	{
		_instance = this;
		_enabled = true;
		
		PluginManager pManager = this.getServer().getPluginManager();
		pManager.registerEvents(pListener, this);
		pManager.registerEvents(bListener, this);
		pManager.registerEvents(enListener, this);
		pManager.registerEvents(customListener, this);
		
		dbConfig.connectDb();
		
		GugaCommands.SetPlugin(this);
		GugaAnnouncement.SetPlugin(this);
		AutoSaver.SetPlugin(this);
		GugaRegionHandler.SetPlugin(this);
		GameMasterHandler.SetPlugin(this);
		GugaEvent.SetPlugin(this);
		GugaParty.SetPlugin(this);
		ChatHandler.SetPlugin(this);
		BasicWorld.Init(this);
		SpawnsHandler.SetPlugin(this);
		HomesHandler.setPlugin(this);

		if (getServer().getWorld("arena") == null)
		{
			getServer().createWorld(WorldCreator.name("arena").environment(Environment.NORMAL));
		}
		if(getServer().getWorld("world_event")==null)
		{
			getServer().createWorld(WorldCreator.name("world_event").environment(Environment.NORMAL));
		}
		if(getServer().getWorld("world_basic")==null)
		{
			getServer().createWorld(WorldCreator.name("world_basic").environment(Environment.NORMAL));
		}
		if(getServer().getWorld("world_mine")==null)
		{
			getServer().createWorld(WorldCreator.name("world_mine").environment(Environment.NORMAL));
		}
		if(getServer().getWorld("survival_games")==null)
		{
			getServer().createWorld(WorldCreator.name("survival_games").environment(Environment.NORMAL));
		}
		arena.LoadArenas();
		arena.LoadPvpStats();
		getServer().getWorld("arena").setPVP(true);
		getServer().getWorld("arena").setFullTime(4000);
		getServer().getWorld("world").setPVP(false);
		getServer().getWorld("world").setSpawnFlags(true, true);
		getServer().getWorld("world_nether").setPVP(false);
		getServer().getWorld("arena").setSpawnFlags(false, false);
		getServer().getWorld("world_event").setPVP(false);
		getServer().getWorld("world_event").setSpawnFlags(false, false);
		getServer().getWorld("world_basic").setPVP(false);
		getServer().getWorld("world_basic").setSpawnFlags(true, true);
		getServer().getWorld("world_mine").setFullTime(4000);
		getServer().getWorld("world_mine").setPVP(false);
		getServer().getWorld("world_mine").setSpawnFlags(false, false);
		getServer().getWorld("survival_games").setPVP(true);
		getServer().getWorld("survival_games").setSpawnFlags(false, false);
		scheduler = getServer().getScheduler();
		loadVIPCodes();
		loadCreditsCodes();
		GugaRegionHandler.LoadRegions();
		blockLocker = new Locker(this);
		GameMasterHandler.LoadGMs();
		GugaAnnouncement.LoadAnnouncements();
		GugaAnnouncement.StartAnnouncing();
		GugaPlayerListener.LoadCreativePlayers();
		SpawnsHandler.LoadSpawns();
		HomesHandler.loadHomes();
		AutoSaver.StartSaver();
		log.info("GUGA MINECRAFT SERVER MOD " + version + " is running.");
		log.info("Created by MineAndCraft team 2011 - 2013.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
	//*****************************************/who*****************************************
		 if(cmd.getName().equalsIgnoreCase("who") && (sender instanceof Player))
		 { 
		   GugaCommands.CommandWho((Player)sender);
		   return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("event") && (sender instanceof Player))
		 {
			 GugaCommands.CommandEvent((Player)sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("socket"))
		 {
			 	GugaCommands.TestCommand(args);
		 }
		 else if (cmd.getName().equalsIgnoreCase("tell"))
		 {
			 GugaCommands.CommandWhisper(sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("msg"))
		 {
			 GugaCommands.CommandWhisper(sender, args);
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
		 else if (cmd.getName().equalsIgnoreCase("ew") && (sender instanceof Player))
		 {
			 GugaCommands.CommandEventWorld((Player) sender, args);
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
		 else if ((cmd.getName().equalsIgnoreCase("pp")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandPP((Player)sender, args);
		 }
		 else if ((cmd.getName().equalsIgnoreCase("invite")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandInvite((Player)sender, args);
		 }
		 else if ((cmd.getName().equalsIgnoreCase("p")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandSendPartyMsg((Player)sender, args);
		 }
		 else if ((cmd.getName().equalsIgnoreCase("party")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandParty((Player)sender, args);
		 }
		 else if ((cmd.getName().equalsIgnoreCase("locker")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandLocker((Player)sender);
		 }
		 else if ((cmd.getName().equalsIgnoreCase("home")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandHome((Player)sender, args);
			 return true;
		 }
		 else if ((cmd.getName().equalsIgnoreCase("world")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandWorld((Player)sender);
			 return true;
		 }
		 else if ((cmd.getName().equalsIgnoreCase("helper")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandHelper((Player)sender, args);
			 return true;
		 }
		 else if ((cmd.getName().equalsIgnoreCase("credits")) && (sender instanceof Player))
		 {
			 GugaCommands.CommandCredits((Player)sender, args);
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
		 }
		 else if (cmd.getName().equalsIgnoreCase("rpg"))
		 {
			 if (sender instanceof Player)
			 {
				 GugaCommands.CommandRpg((Player)sender,args);
				 return true;
			 }
		 }
		 else if (cmd.getName().equalsIgnoreCase("feedback") && (sender instanceof Player))
		 {
			 GugaCommands.CommandFeedback((Player) sender, args);
			 return true;
		 }
		 //*****************************************/status*****************************************
		 else if(cmd.getName().equalsIgnoreCase("y") && (sender instanceof Player))
		 {
			 GugaCommands.CommandConfirm((Player)sender,args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("book") && (sender instanceof Player))
		 {
			 //GugaCommands.commandCopy((Player)sender, args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("lock") && (sender instanceof Player))
		 {
			 GugaCommands.CommandLock((Player)sender);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("gc") && (sender instanceof Player))
		 {
			 GugaCommands.CommandGMChat((Player)sender, args);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("announce") && (sender.isOp() || GameMasterHandler.IsAtleastGM(sender.getName())))
		 {
			 GugaCommands.CommandAnnounce(sender,args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("estate") && (sender instanceof Player))
		 {
		 	 GugaCommands.CommandEstate((Player)sender, args);
		 	 return true;
		 }
		//*****************************************/unlock*****************************************
		 else if(cmd.getName().equalsIgnoreCase("unlock") && (sender instanceof Player))
		 {
				 GugaCommands.CommandUnlock((Player)sender);		
				 return true;
		 }
		//*****************************************/login*****************************************
		 else if(cmd.getName().equalsIgnoreCase("login") && (sender instanceof Player))
		 {
			 GugaCommands.CommandLogin((Player)sender, args);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("logout") && sender instanceof Player)
		 {
			 ((Player)sender).kickPlayer("Goodbye");
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("getcoords") && (sender instanceof Player))
		 {
			 Location l=((Player)sender).getTargetBlock(null, 50).getLocation();
			 if(l!=null)
				 sender.sendMessage(String.format("Block coordinates: %d=x:%d,y:%d,z:%d", l.getBlock().getTypeId(),l.getBlockX(),l.getBlockY(),l.getBlockZ()));
			 else
				 sender.sendMessage("Block coordinates:null");
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("friend") && (sender instanceof Player))
		 {
			 GugaCommands.CommandFriend((Player)sender,args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("block") && (sender instanceof Player))
		 {
			 GugaCommands.CommandBlock((Player)sender,args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("register"))
		 {
			 GugaCommands.CommandRegister((Player)sender,args);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("activate") && (sender instanceof Player))
		 {
			 if(args.length != 2)
				 return false;
			 if(args[0].equalsIgnoreCase("credits"))
			 {
				 if(creditsCodes.contains(args[1]))
				 {
					 this.currencyManager.addCredits(sender.getName(),500);
					 creditsCodes.remove(args[1]);
					 saveCreditsCodes();
					 logger.LogShopTransaction(Prices.ARROW.toString(), 1, sender.getName()+";CRAFTCON;CREDITS;"+args[1]);
					 ChatHandler.SuccessMsg((Player)sender, "Vase kredity z Craftconu byly pripsany!");
					 return true;
				 }
			 }
			 else if(args[0].equalsIgnoreCase("vip"))
			 {
				 if(vipCodes.contains(args[1]))
				 {
					int months=1;
					this.vipManager.addVip(sender.getName(), months*2592000);
					logger.LogShopTransaction(Prices.ARROW.toString(), 1, sender.getName()+";CRAFTCON;VIP;"+args[1]);
					vipCodes.remove(args[1]);
					saveVIPCodes();
					ChatHandler.SuccessMsg((Player)sender, "Vase VIP z Craftconu bylo aktivovano do " + new Date(System.currentTimeMillis() + 2592000000l) + "!");
					return true;
				 }
			 }
		 }
		return false;
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
	
	public void GenerateBlockType2(Player p, int typeID1, int typeID2, int x, int y, int z)
	{
		Location baseLoc = p.getTargetBlock(null, 50).getLocation();
		int xBase = baseLoc.getBlockX();
		int yBase = baseLoc.getBlockY();
		int zBase = baseLoc.getBlockZ();
		int x2 = x;
		int y2 = y;
		int z2 = z;
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
					if (p.getWorld().getBlockTypeIdAt(xBase+i3, yBase+i, zBase+i2)==typeID1)
					{
						p.getWorld().getBlockAt(xBase+i3, yBase+i, zBase+i2).setTypeId(typeID2,true);
					}
					i3++;
				}
				i2++;
			}
			i++;
		}
	}
	public Location GetAvailablePortLocation(Location loc)
	{
		Location tpLoc = loc.getWorld().getHighestBlockAt(loc).getLocation();
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
	
	public void loadCreditsCodes()
	{
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/CreditsCodes.dat", GugaFile.READ_MODE);
		String line;
		while((line = file.ReadLine()) != null)
		{
			this.creditsCodes.add(line);
		}
		file.Close();
	}
	public void saveCreditsCodes()
	{
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/CreditsCodes.dat", GugaFile.WRITE_MODE);
		Iterator<String> i = creditsCodes.iterator();
		while(i.hasNext())
		{
			file.WriteLine(i.next());
		}
		file.Close();
	}
	public void loadVIPCodes()
	{
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/VipCodes.dat", GugaFile.READ_MODE);
		String line;
		while((line = file.ReadLine()) != null)
		{
			this.vipCodes.add(line);
		}
		file.Close();
	}
	public void saveVIPCodes()
	{
		GugaFile file = new GugaFile("plugins/MineAndCraft_plugin/VipCodes.dat", GugaFile.WRITE_MODE);
		Iterator<String> i = vipCodes.iterator();
		while(i.hasNext())
		{
			file.WriteLine(i.next());
		}
		file.Close();
	}
	
	
	// ************* chances *************
	public int IRON = 0;
	public int GOLD = 1;
	public int DIAMOND = 2;
	public int EMERALD = 3;
	public boolean debug = false;
	public boolean redstoneDebug = false;
	
	public static final String version = "beta4.0.0";
	public static final String shopItemConfigFilePath = "plugins/MineAndCraft_plugin/Shop.cfg";
	public static final String basicWorldBanRegionsConfigFilePath = "plugins/MineAndCraft_plugin/BWBanRegions.dat";
	public static final String basicWorldBanRegionsDeviationsFilePath = "plugins/MineAndCraft_plugin/BWBanDeviations.dat";
	
	public final Logger log = Logger.getLogger("Minecraft");
	public BukkitScheduler scheduler;
	
	public final DatabaseManager dbConfig = new DatabaseManager();
	public final GugaPlayerListener pListener = new GugaPlayerListener(this);
	public final GugaEntityListener enListener = new GugaEntityListener(this);
	public final GugaBlockListener bListener = new GugaBlockListener(this);
	public final CustomListener customListener = new CustomListener(this);
	public Locker blockLocker;
	public final GugaLogger logger = new GugaLogger(this);
	public GugaArena arena = new GugaArena(this);
	public GugaEventWorld EventWorld = new GugaEventWorld(this);
	public ArrayList<String> creditsCodes = new ArrayList<String>();
	public ArrayList<String> vipCodes = new ArrayList<String>();

	public final UserManager userManager = new UserManager(this);
	public final VipManager vipManager = new VipManager(this);
	public final ShopManager shopManager = new ShopManager(this);
	public final CurrencyManager currencyManager = new CurrencyManager(this);
	public final PlacesManager placesManager = new PlacesManager(this);
	public final BanHandler banHandler = new BanHandler(this);
	public final ExtensionManager extensionManager = new ExtensionManager(this);
	
	private static Guga_SERVER_MOD _instance;
	private static boolean _enabled=false;
	
	public static Guga_SERVER_MOD getInstance()
	{
		return _instance;
	}

	public static boolean is_enabled()
	{
		return _enabled;
	}
}
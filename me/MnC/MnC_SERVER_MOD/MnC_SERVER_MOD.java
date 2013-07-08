package me.MnC.MnC_SERVER_MOD;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import me.MnC.MnC_SERVER_MOD.Currency.CurrencyCommandExecutor;
import me.MnC.MnC_SERVER_MOD.Currency.CurrencyHandler;
import me.MnC.MnC_SERVER_MOD.Currency.ShopManager;
import me.MnC.MnC_SERVER_MOD.Extensions.ExtensionManager;
import me.MnC.MnC_SERVER_MOD.Handlers.CommandsHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.Handlers.ServerRegionHandler;
import me.MnC.MnC_SERVER_MOD.Listeners.BlockListener;
import me.MnC.MnC_SERVER_MOD.Listeners.CustomListener;
import me.MnC.MnC_SERVER_MOD.Listeners.EntityListener;
import me.MnC.MnC_SERVER_MOD.Listeners.InventoryListener;
import me.MnC.MnC_SERVER_MOD.Listeners.PlayerListener;
import me.MnC.MnC_SERVER_MOD.Listeners.PluginListener;
import me.MnC.MnC_SERVER_MOD.rpg.RpgCommandExecutor;
import me.MnC.MnC_SERVER_MOD.basicworld.BasicWorld;
import me.MnC.MnC_SERVER_MOD.basicworld.RandomSpawnsHandler;
import me.MnC.MnC_SERVER_MOD.chat.Chat;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;
import me.MnC.MnC_SERVER_MOD.home.HomeCommandExecutor;
import me.MnC.MnC_SERVER_MOD.home.HomesHandler;
import me.MnC.MnC_SERVER_MOD.locker.BlockLocker;
import me.MnC.MnC_SERVER_MOD.manor.ManorManager;
import me.MnC.MnC_SERVER_MOD.optimization.AntiLag;
//import me.MnC.MnC_SERVER_MOD.tagger.Tagger;
import me.MnC.MnC_SERVER_MOD.tagger.Tagger;
import me.MnC.MnC_SERVER_MOD.util.GugaFile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

//TODO start using javolution

public class MnC_SERVER_MOD extends JavaPlugin
{
	public MnC_SERVER_MOD()
	{
		_instance = this;
		
		Config.load("plugins/MineAndCraft_plugin/config.properties");
		
		try{
			this.db.connectDB();
		}catch(SQLException e)
		{
			log.severe("Failed to connect to db:"+e.getMessage());
			log.severe("Shutting server down.");
			Bukkit.shutdown();
		}
		
		userManager = new UserManager();
		chat = new Chat();
		vipManager = new VipManager();
		shopManager = new ShopManager(this);
		currencyManager = new CurrencyHandler();
		placesManager = new PlacesManager(this);
		banHandler = new BanHandler();
		extensionManager = new ExtensionManager(this);
		blockLocker = new BlockLocker();
		
	}
	
	public void onDisable() 
	{
		log.info("GUGA MINECRAFT SERVER MOD has been disabled.");
		GugaEvent.ClearAllGroups();
		
		Tagger.stop();
		
		GameTimeWatcher.stop();
		this.userManager.save();
		this.extensionManager.disable();
		ServerRegionHandler.SaveRegions();
		RandomSpawnsHandler.SaveSpawns();
		arena.SavePvpStats();
		arena.SaveArenas();
		chat.onDisable();
		antilag.disable();
		db.disconnectDB();
		_enabled = false;
	}

	public void onEnable() 
	{
		_enabled = true;
		
		this.registerCommands();
		
		PluginManager pManager = this.getServer().getPluginManager();
		pManager.registerEvents(pListener, this);
		pManager.registerEvents(bListener, this);
		pManager.registerEvents(enListener, this);
		pManager.registerEvents(customListener, this);
		pManager.registerEvents(inventoryListener, this);
		pManager.registerEvents(pluginListener, this);
		
		pManager.registerEvents(blockLocker, this);
		
		chat.onEnable();		

		PositionCheck.enable();
		
		CommandsHandler.SetPlugin(this);
		AutoSaver.SetPlugin(this);
		ServerRegionHandler.SetPlugin(this);
		GameMasterHandler.SetPlugin(this);
		GugaEvent.SetPlugin(this);
		BasicWorld.Init(this);
		RandomSpawnsHandler.SetPlugin(this);
		HomesHandler.setPlugin(this);

		antilag = new AntiLag();
		
		new ManorManager();
		
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
		if(getServer().getWorld("world_build")==null)
		{
			World world_build =getServer().createWorld(WorldCreator.name("world_build").environment(Environment.NORMAL));
			world_build.setSpawnFlags(false, false);
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
		scheduler = getServer().getScheduler();
		loadVIPCodes();
		loadCreditsCodes();
		ServerRegionHandler.LoadRegions();
		GameMasterHandler.LoadGMs();
		PlayerListener.LoadCreativePlayers();
		RandomSpawnsHandler.LoadSpawns();
		HomesHandler.loadHomes();
		AutoSaver.StartSaver();
		
		GameTimeWatcher.start();
		
		Tagger.start();
		
		log.info("GUGA MINECRAFT SERVER MOD " + version + " is running.");
		log.info("Created by MineAndCraft team 2011 - 2013.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
	//*****************************************/who*****************************************
		 if(cmd.getName().equalsIgnoreCase("who") && (sender instanceof Player))
		 { 
		   CommandsHandler.CommandWho((Player)sender);
		   return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("event") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandEvent((Player)sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("socket"))
		 {
			 	CommandsHandler.TestCommand(args);
		 }
		 else if (cmd.getName().equalsIgnoreCase("places") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandPlaces((Player)sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("arena") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandArena((Player) sender, args);
			 return true;
		 }
		 else if (cmd.getName().equalsIgnoreCase("ew") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandEventWorld((Player) sender, args);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("debug") && (sender instanceof ConsoleCommandSender))
		 {
			 CommandsHandler.CommandDebug();
		 }
		 else if ((cmd.getName().equalsIgnoreCase("vip")) && (sender instanceof Player))
		 {
			 CommandsHandler.CommandVIP((Player)sender, args);
			 return true;
		 }
		 else if ((cmd.getName().equalsIgnoreCase("pp")) && (sender instanceof Player))
		 {
			 CommandsHandler.CommandPP((Player)sender, args);
		 }
		 else if ((cmd.getName().equalsIgnoreCase("world")) && (sender instanceof Player))
		 {
			 CommandsHandler.CommandWorld((Player)sender);
			 return true;
		 }
		 else if ((cmd.getName().equalsIgnoreCase("helper")) && (sender instanceof Player))
		 {
			 CommandsHandler.CommandHelper((Player)sender, args);
			 return true;
		 }
		//*****************************************/help*****************************************
		 else if (cmd.getName().equalsIgnoreCase("help"))
		 {
			 if (sender instanceof Player)
			 {
				 CommandsHandler.CommandHelp((Player) sender);
				 return true;
			 }
		 }
		 else if (cmd.getName().equalsIgnoreCase("gm"))
		 {
			 if (sender instanceof Player)
			 {
				 CommandsHandler.CommandGM((Player)sender,args);
			 }
		 }
		 else if (cmd.getName().equalsIgnoreCase("feedback") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandFeedback((Player) sender, args);
			 return true;
		 }
		 //*****************************************/status*****************************************
		 else if(cmd.getName().equalsIgnoreCase("y") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandConfirm((Player)sender,args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("book") && (sender instanceof Player))
		 {
			 //GugaCommands.commandCopy((Player)sender, args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("gc"))
		 {
			 CommandsHandler.CommandGMChat(sender, args);
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("announce") && (sender.isOp() || GameMasterHandler.IsAtleastGM(sender.getName())))
		 {
			 CommandsHandler.CommandAnnounce(sender,args);
		 }
		 else if(cmd.getName().equalsIgnoreCase("estates") && (sender instanceof Player))
		 {
		 	 CommandsHandler.CommandEstates((Player)sender, args);
		 	 return true;
		 }
		//*****************************************/login*****************************************
		 else if(cmd.getName().equalsIgnoreCase("login") && (sender instanceof Player))
		 {
			 CommandsHandler.CommandLogin((Player)sender, args);
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
				 sender.sendMessage(String.format("Target Coordinates: %d %s[%d,%d,%d]", l.getBlock().getTypeId(),l.getWorld().getName(),l.getBlockX(),l.getBlockY(),l.getBlockZ()));
			 else
				 sender.sendMessage("Target not found. (Aren't you too far?)");
			 return true;
		 }
		 else if(cmd.getName().equalsIgnoreCase("register"))
		 {
			 CommandsHandler.CommandRegister((Player)sender,args);
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
					 logger.LogShopTransaction("CRAFTCON", 1, sender.getName()+";CREDITS;"+args[1]);
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
					logger.LogShopTransaction("CRAFTCON", 1, sender.getName()+";VIP;"+args[1]);
					vipCodes.remove(args[1]);
					saveVIPCodes();
					ChatHandler.SuccessMsg((Player)sender, "Vase VIP z Craftconu bylo aktivovano do " + new Date(System.currentTimeMillis() + 2592000000l) + "!");
					return true;
				 }
			 }
		 }
		return false;
	}
	
	private void registerCommands()
	{
		getCommand("rpg").setExecutor(new RpgCommandExecutor());
		getCommand("credits").setExecutor(new CurrencyCommandExecutor());
		getCommand("shop").setExecutor(new CurrencyCommandExecutor());
		getCommand("home").setExecutor(new HomeCommandExecutor());
		getCommand("book").setExecutor(new CurrencyCommandExecutor());
		this.blockLocker.registerCommands();
		this.chat.registerCommands();
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
	
	public boolean debug = false;
	public boolean redstoneDebug = false;
	
	public static final String version = "1.5.0 alpha";
	
	public final Logger log = Logger.getLogger("Minecraft");
	public BukkitScheduler scheduler;
	
	public final DatabaseManager db = new DatabaseManager();
	public final PlayerListener pListener = new PlayerListener(this);
	public final EntityListener enListener = new EntityListener(this);
	public final BlockListener bListener = new BlockListener(this);
	public final CustomListener customListener = new CustomListener(this);
	public final InventoryListener inventoryListener = new InventoryListener();
	public final PluginListener pluginListener = new PluginListener(this); 
	
	public BlockLocker blockLocker;
	public final GugaLogger logger = new GugaLogger(this);
	public GugaArena arena = new GugaArena(this);
	public GugaEventWorld EventWorld = new GugaEventWorld(this);
	public ArrayList<String> creditsCodes = new ArrayList<String>();
	public ArrayList<String> vipCodes = new ArrayList<String>();

	public final UserManager userManager;
	public final Chat chat;
	public final VipManager vipManager;
	public final ShopManager shopManager;
	public final CurrencyHandler currencyManager;
	public final PlacesManager placesManager;
	public final BanHandler banHandler;
	public final ExtensionManager extensionManager;
	
	private AntiLag antilag = null;
	
	private static MnC_SERVER_MOD _instance;
	private static boolean _enabled=false;
	
	public static MnC_SERVER_MOD getInstance()
	{
		return _instance;
	}

	public static boolean is_enabled()
	{
		return _enabled;
	}
}
package me.Guga.Guga_SERVER_MOD;

import me.Guga.Guga_SERVER_MOD.chat.ChatHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.HomesHandler;
import me.Guga.Guga_SERVER_MOD.Handlers.SpawnsHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BasicWorld 
{
	private static Guga_SERVER_MOD plugin;
	private static BasicWorldBanRegionManager brm;

	public static void Init(Guga_SERVER_MOD plugin)
	{
		SetPlugin(plugin);
		brm = new BasicWorldBanRegionManager(plugin);
	}
	
	public static void SetPlugin(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	public static boolean IsBasicWorld(Location loc)
	{
		if (loc.getWorld().getName().matches("world_basic"))
			return true;
		return false;
	}

	public static void BasicWorldEnter(Player p)
	{
		plugin.userManager.getUser(p.getName()).getProfession().addExperience(1);
		p.teleport(plugin.getServer().getWorld("world_basic").getSpawnLocation());
		HomesHandler.addHome(p, plugin.getServer().getWorld("world_basic").getSpawnLocation());
		ChatHandler.SuccessMsg(p, "Vitejte ve svete pro novacky!");
		PlayerInventory inv = p.getInventory();
		inv.addItem(new ItemStack(257,1));//iron pickaxe
		inv.addItem(new ItemStack(256,1));//iron shovel
		inv.addItem(new ItemStack(267,1));//iron sword
		inv.addItem(new ItemStack(364,16));//steak
		inv.addItem(new ItemStack(299,1)); // leather chestplate
		inv.addItem(new ItemStack(300,1));//leather pants
		inv.addItem(new ItemStack(301,1));//leather boots
		p.sendMessage(ChatColor.RED + "*************************************");
		p.sendMessage(ChatColor.DARK_AQUA + "Nyni se nachazite ve svete pro novacky, kde musite dosahnout " + ChatColor.DARK_RED + "LEVELU 10" + ChatColor.DARK_AQUA +" (level zjistite prikazem /rpg status). " + ChatColor.DARK_RED + "LEVELY" + ChatColor.DARK_AQUA +" ziskavate kopanim. Pote se muzete teleportovat do " + ChatColor.DARK_RED + "PROFESIONALNIHO SVETA.");	
		p.sendMessage(ChatColor.YELLOW + "Vice na o nasem profesionalnim svete na: " + ChatColor.DARK_BLUE + ">>> http://www.mineandcraft.cz/navod-na-pripojeni <<<");
		p.sendMessage(ChatColor.YELLOW + "Nezapomente si precist pravidla na: " + ChatColor.DARK_BLUE + ">>> http://www.mineandcraft.cz/pravidla <<<");
		p.sendMessage(ChatColor.RED + "*************************************");
		
	}
	public static void BasicWorldLeaveToWorld(Player p)
	{
		brm.onPlayerBWLeave(p.getName());
		Location loc = SpawnsHandler.getRandomSpawn();
		HomesHandler.addHome(p, loc);
		p.teleport(loc);
		ChatHandler.InitializeDisplayName(p);
	}
	public static void setSpawn(Location l)
	{
		plugin.getServer().getWorld("world_basic").setSpawnLocation((int)l.getX(), (int)l.getY(), (int)l.getZ());
	}
	public static Location getSpawn()
	{
		return plugin.getServer().getWorld("world_basic").getSpawnLocation();
	}
	public static boolean basicWorldRegionBlockBreak(ServerRegion region,Player player,Block block)
	{
		return brm.onRegionBlockBreakCheck(region,player,block);
	}
	
	public static boolean isNew(Player player) {
		MinecraftPlayer p = plugin.userManager.getUser(player.getName());
		if(p==null)
			return true;
		
		GugaProfession prof = p.getProfession();
		if(prof == null || prof.GetLevel() < 10)
		{
			return true;
		}
		return false;
	}
}

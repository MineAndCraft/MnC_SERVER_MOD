package me.MnC.MnC_SERVER_MOD.vip;

import java.util.Arrays;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * VIP GUI functionality for MineaAndCraft
 * 
 * @author Jason Skyedge
 *
 */
public class VipGUIHandler
{

	public static ItemStack[] createMainVipGui()
	{	
		ItemStack[] items = new ItemStack[5];
		
		ItemStack fly = new ItemStack(Material.FEATHER,1);
		ItemMeta flyItemMeta = fly.getItemMeta();
		flyItemMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Ocarovane Pericko"));
		flyItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Po pouziti tohoto", ChatColor.GRAY + "ocarovaneho predmetu", ChatColor.GRAY + "budete schopni letat."));
		fly.setItemMeta(flyItemMeta);
		items[0] = fly;
		
		ItemStack food = new ItemStack(Material.GOLDEN_APPLE,1);
		ItemMeta foodItemMeta = food.getItemMeta();
		foodItemMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Ambrozie"));
		foodItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Ambrozie je posvatny", ChatColor.GRAY + "zdroj energie.", ChatColor.GRAY + "Po snezeni Ambrozie se", ChatColor.GRAY + "zabavite veskereho hladu."));
		food.setItemMeta(foodItemMeta);
		items[1] = food;
		
		ItemStack chest = new ItemStack(Material.CHEST,1);
		ItemMeta chestItemMeta = chest.getItemMeta();
		chestItemMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Bezedna Truhla"));
		chestItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "V bezedne truhle", ChatColor.GRAY + "naleznete nekonecne", ChatColor.GRAY + "mnozstvi zakladnich surovin."));
		chest.setItemMeta(chestItemMeta);
		items[2] = chest;
		
		ItemStack clock = new ItemStack(Material.WATCH,1);
		ItemMeta clockMeta = clock.getItemMeta();
		clockMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Slunecni Hodiny"));
		clockMeta.setLore(Arrays.asList(ChatColor.GRAY + "Tyto slunecni hodiny",ChatColor.GRAY + "byly ocarovany magickym kouzlem.",ChatColor.GRAY + "Pomoci nich",ChatColor.GRAY + "muzete kdykoliv zmenit",ChatColor.GRAY + "noc na rozbresk."));
		clock.setItemMeta(clockMeta);
		items[3] = clock;
		
		ItemStack portal = new ItemStack(Material.PORTAL,1);
		ItemMeta portalMeta = portal.getItemMeta();
		portalMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Dimenzionalni Portal"));
		portalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Pomoci teto sikovne", ChatColor.GRAY + "vecicky, se muzete teleportovat",ChatColor.GRAY + "na ruzne pozice."));
		portal.setItemMeta(portalMeta);
		items[4] = portal;
		
		return items;
	}
	
	public static ItemStack[] createChestVipGui()
	{
		ItemStack[] items = new ItemStack[9];
		ItemMeta meta;
		
		ItemStack stone = new ItemStack(Material.STONE,1);
		meta = stone.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		stone.setItemMeta(meta);
		items[0] = stone;
		ItemStack dirt = new ItemStack(Material.DIRT,1);
		meta = dirt.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		dirt.setItemMeta(meta);
		items[1] = dirt;
		ItemStack cStone = new ItemStack(Material.COBBLESTONE,1);
		meta = cStone.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		cStone.setItemMeta(meta);
		items[2] = cStone;
		ItemStack wood1 = new ItemStack(Material.WOOD,1);
		meta = wood1.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		wood1.setItemMeta(meta);
		items[3] = wood1;
		ItemStack wood2 = new ItemStack(Material.WOOD,1,(short)1);
		meta = wood2.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		wood2.setItemMeta(meta);
		items[4] = wood2;
		ItemStack wood3 = new ItemStack(Material.WOOD,1,(short)2);
		meta = wood3.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		wood3.setItemMeta(meta);
		items[5] = wood3;
		ItemStack wood4 = new ItemStack(Material.WOOD,1,(short)3);
		meta = wood4.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		wood4.setItemMeta(meta);
		items[6] = wood4;
		ItemStack sand = new ItemStack(Material.SAND,1);
		meta = sand.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		sand.setItemMeta(meta);
		items[7] = sand;
		ItemStack sandS = new ItemStack(Material.SANDSTONE,1);
		meta = sandS.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Vec z Bezedne Truhly"));
		sandS.setItemMeta(meta);
		items[8] = sandS;
		
		return items;
	}
	
	public static ItemStack createExitButton()
	{
		ItemStack button = new ItemStack(Material.WOOL,1,(short)14);
		ItemMeta meta = button.getItemMeta();
		meta.setDisplayName(ChatColor.RED + (ChatColor.BOLD + "EXIT"));
		button.setItemMeta(meta);
		
		return button;
	}
	
	public static ItemStack[] createTeleportMenu()
	{
		ItemStack[] menu = new ItemStack[3];
		
		ItemStack bed = new ItemStack(Material.BED,1);
		ItemMeta bedMeta = bed.getItemMeta();
		bedMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Teleport - Postel"));
		bed.setItemMeta(bedMeta);
		menu[0] = bed;
		ItemStack death = new ItemStack(Material.BONE,1);
		ItemMeta deathMeta = death.getItemMeta();
		deathMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Teleport - Posledni misto smrti"));
		death.setItemMeta(deathMeta);
		menu[1] = death;
		ItemStack player = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		ItemMeta playerItemMeta = player.getItemMeta();
		playerItemMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + "Teleport - Hrac"));
		player.setItemMeta(playerItemMeta);
		menu[2] = player;
				
		return menu;
	}
	
	public static ItemStack[] createPlayerTeleportMenu(String ownerName)
	{
		ItemStack[] players = new ItemStack[Bukkit.getServer().getOnlinePlayers().length];
		
		int i = 0;
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(!p.getName().equals(ownerName))
			{
				ItemStack playersHead = new ItemStack(Material.SKULL_ITEM,1,(short)3);
				ItemMeta pHMeta = playersHead.getItemMeta();
				pHMeta.setDisplayName(ChatColor.AQUA + (ChatColor.BOLD + p.getName()));
				playersHead.setItemMeta(pHMeta);
				players[i] = playersHead;
				i++;
			}
		}
		
		return players;
	}
	
	public static void openVIPGUI(Player player)
	{
		player.sendMessage(ChatColor.GREEN + "Oppening VIP GUI!");
		
		Inventory vipInterface = Bukkit.getServer().createInventory(player, 9*3, "Vip GUI");
		vipInterface.setContents(createMainVipGui());
		
		player.openInventory(vipInterface);
	}
	
	public static void onClickVIPGUIAction(InventoryClickEvent event)
	{
		if(event.getInventory().getTitle().equalsIgnoreCase("VIP GUI"))
		{
			event.setCancelled(true);
			
			Player eventPlayer = (Player) event.getWhoClicked();
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR)
			{
				
				if(event.getCurrentItem().getItemMeta().getDisplayName() == null)
				{
					return;
				}
				
				if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Ocarovane Pericko"))
				{
					eventPlayer.closeInventory();
					
					if(eventPlayer.isFlying())
					{
						eventPlayer.chat("/vip fly off");
					}
					else
					{
						eventPlayer.chat("/vip fly on");
					}
				
				} else if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Ambrozie")) {

					eventPlayer.closeInventory();
					
					eventPlayer.chat("/vip nohunger");

				} else if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Bezedna Truhla")) {
					Inventory chest = Bukkit.getServer().createInventory(eventPlayer, 9*3, "Vip GUI - Bezedna Truhla");
					chest.setContents(createChestVipGui());
					chest.setItem(26, createExitButton());
					eventPlayer.openInventory(chest);
				} else if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Slunecni Hodiny")){

					eventPlayer.closeInventory();
					
					eventPlayer.chat("/vip time set 100");
					
				} else if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Dimenzionalni Portal")){
					
					Inventory teleport = Bukkit.getServer().createInventory(eventPlayer, 9*3, "Vip GUI - Teleport");
					teleport.setContents(createTeleportMenu());
					teleport.setItem(26, createExitButton());
					eventPlayer.openInventory(teleport);
					
				}

			}
			
		}
		else if(event.getInventory().getTitle().endsWith("Bezedna Truhla"))
		{
			event.setCancelled(true);
			
			Player eventPlayer = (Player) event.getWhoClicked();
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
				
				if(event.getCurrentItem().getType() != Material.WOOL){
					
					if(event.getCurrentItem().getItemMeta().getLore() != null){
						
						if(event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Vec z Bezedne Truhly")){
							
							String idString = event.getCurrentItem().getTypeId()+":"+event.getCurrentItem().getDurability();
							eventPlayer.chat("/vip item add "+idString);
						}
					}
				} else {
				
					if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
						
						if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("EXIT")){
							
							Inventory vipInterface = Bukkit.getServer().createInventory(eventPlayer, 9*3, "Vip GUI");
							vipInterface.setContents(createMainVipGui());
							eventPlayer.openInventory(vipInterface);
							
						}
					}
				}
			}				
		} else if(event.getInventory().getTitle().endsWith("Teleport")){
			
			event.setCancelled(true);
			
			Player eventPlayer = (Player) event.getWhoClicked();
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
				
				if(event.getCurrentItem().getType() != Material.WOOL){
					
					if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
						
						if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).endsWith("Postel")){
							
							eventPlayer.closeInventory();
							
							eventPlayer.chat("/vip tp bed");
							
						} else if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).endsWith("Posledni misto smrti")){
							
							eventPlayer.closeInventory();
							
							eventPlayer.chat("/vip tp death");
							
						} else if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).endsWith("Hrac")){
							
							int size;
							
							if(Bukkit.getServer().getMaxPlayers() % 9 < 1){
								
								size = (Bukkit.getServer().getMaxPlayers()/9) + 3;
								
							} else {
								
								size = (Bukkit.getServer().getMaxPlayers()/9)+2;
							}
							
							Inventory playerTeleportInv = Bukkit.getServer().createInventory(eventPlayer, size*9, "Vip GUI - Teleport - Hrac");
							playerTeleportInv.setContents(createPlayerTeleportMenu(eventPlayer.getName()));
							playerTeleportInv.setItem(playerTeleportInv.getSize() -1, createExitButton());
							eventPlayer.openInventory(playerTeleportInv);
							
						}
						
					}
					
				} else {
					
					if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
						
						
						if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("EXIT")){
							
							
							Inventory vipInterface = Bukkit.getServer().createInventory(eventPlayer, 9*3, "Vip GUI");
							vipInterface.setContents(createMainVipGui());
							eventPlayer.openInventory(vipInterface);
							
						}
						
					}
					
				}
				
			}

		} else if(event.getInventory().getTitle().endsWith("Hrac")){
			
			event.setCancelled(true);
			
			Player eventPlayer = (Player) event.getWhoClicked();
			
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
				
				if(event.getCurrentItem().getType() != Material.WOOL){
					
					if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
						
						String pName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
						
						eventPlayer.closeInventory();
						
						eventPlayer.chat("/vip tp player "+pName);
						
					}
					
				} else {
					
					if(event.getCurrentItem().getItemMeta().getDisplayName() != null){
						
						if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("EXIT")){
							
							Inventory teleport = Bukkit.getServer().createInventory(eventPlayer, 9*3, "Vip GUI - Teleport");
							teleport.setContents(createTeleportMenu());
							teleport.setItem(26, createExitButton());
							eventPlayer.openInventory(teleport);
						
						}
					}
				}
				
			}
				
		}
	}
}

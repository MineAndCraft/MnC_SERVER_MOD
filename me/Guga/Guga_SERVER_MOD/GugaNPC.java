package me.Guga.Guga_SERVER_MOD;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GugaNPC 
{
	public GugaNPC(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		packet = new Packet20NamedEntitySpawn();
	}
	public GugaNPC(Guga_SERVER_MOD gugaSM, String npcName, Location loc, int item)
	{
		plugin = gugaSM;
		packet = new Packet20NamedEntitySpawn();
		name = npcName;
		location = loc;
		yaw = 0;
		pitch = 0;
		itemInHand = item;
	}
	public void Spawn()
	{
		CraftWorld world = (CraftWorld)plugin.getServer().getWorld("world");
		WorldServer wServer = world.getHandle();
		MinecraftServer ms = wServer.server;
		ItemInWorldManager m = new ItemInWorldManager(wServer);
		EntityPlayer pp = new EntityPlayer(ms, (World) ms.worlds.get(0), "LOLEK", m);
		plugin.log.info(""+pp.getBukkitEntity().getLocation().getWorld().getName());
		UpdatePacket();
		/*for (Player p : plugin.getServer().getOnlinePlayers())
		{
			SendPacketSpawn((CraftPlayer)p);
		}*/
	}
	public void Despawn()
	{
		for (Player p : plugin.getServer().getOnlinePlayers())
		{
			SendPacketDestroy((CraftPlayer)p);
		}
	}
	public void DespawnForPlayer(Player p)
	{
		SendPacketDestroy((CraftPlayer) p);
	}
	private void UpdatePacket()
	{
		ObtainNewID();
		packet.a = id;
        packet.b = name;
        packet.c = MathHelper.floor(location.getBlockX() * 32.0D);
        packet.d = MathHelper.floor(location.getBlockY() * 32.0D);
        packet.e = MathHelper.floor(location.getBlockZ() * 32.0D);
        packet.f = (byte) ((int) (yaw * 256.0F / 360.0F));
        packet.g = (byte) ((int) (pitch * 256.0F / 360.0F));
        packet.h = itemInHand;
	}
	private void SendPacketSpawn(CraftPlayer p)
	{
		p.getHandle().netServerHandler.sendPacket(packet);
	}
	private void SendPacketDestroy(CraftPlayer p)
	{
		p.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(id));
	}
	private void ObtainNewID()
	{
		id = plugin.getServer().getWorld("world").getEntities().get(plugin.getServer().getWorld("world").getEntities().size()-1).getEntityId() + 1;
		id = 1000;
	}
	private String name;
	private int id;
	private Location location;
	private int yaw;
	private int pitch;
	private int itemInHand;
	
	private Packet20NamedEntitySpawn packet;
	
	private Guga_SERVER_MOD plugin;
}

package me.MnC.MnC_SERVER_MOD.tagger;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.UserManager;

import org.bukkit.entity.Player;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;


public class Tagger
{
	private static PacketAdapter listener;
	
	public static void start()
	{
		listener = new PacketAdapter(MnC_SERVER_MOD.getInstance(),ConnectionSide.SERVER_SIDE,ListenerPriority.NORMAL,Packets.Server.NAMED_ENTITY_SPAWN){
			@Override
			public void onPacketSending(PacketEvent event)
			{
				PacketContainer packetContainer = event.getPacket();
				String newName = handlePacket(((Integer)packetContainer.getSpecificModifier(Integer.TYPE).read(0)).intValue(), (String)packetContainer.getSpecificModifier(String.class).read(0), event.getPlayer());
				packetContainer.getSpecificModifier(String.class).write(0, newName);
			}
		}; 
				
		ProtocolLibrary.getProtocolManager().addPacketListener(listener);
	}
	
	public static void stop()
	{
		ProtocolLibrary.getProtocolManager().removePacketListener(listener);
	}
	
	private static String handlePacket(int entityId, String playername, Player destination)
	{
		//I'm lazy i know, but I spared one variable :)
		try{
			return UserManager.getInstance().getUser(playername).getEntityName();
		}catch(NullPointerException e){}
		return playername;
	 }
}

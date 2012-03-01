package me.Guga.Guga_SERVER_MOD.Listeners;
import me.Guga.Guga_SERVER_MOD.Handlers.GugaMCClientHandler;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class GugaMessageListener implements PluginMessageListener
{
	public GugaMessageListener(Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) 
	{
		String msg = new String(message);
		String[] split = null;
		//plugin.log.info("MSG RECEIVED, DATA:" + msg);
		if (msg != null)
			split = msg.split(";");
		if (split[0].matches("GUGA_REGISTER"))
		{
			if (player != null)
			{
				GugaMCClientHandler.RegisterUser(player, split[1]);
				//SendSkinsToClient(player);
			}
		}
	}
	@SuppressWarnings("unused")
	private Guga_SERVER_MOD plugin;
}

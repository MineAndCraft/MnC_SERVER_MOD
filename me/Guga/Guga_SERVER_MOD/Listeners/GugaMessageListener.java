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
		if (msg != null)
			split = msg.split(";");
		if (split[0].matches("GUGA_REGISTER"))
		{
			if (split.length != 3)
			{
				player.kickPlayer("Prosim stahnete si nejnovejsi verzi Klienta.");
				return;
			}
			if (!split[2].matches(GugaMCClientHandler.requiredClientVersion))
			{
				player.kickPlayer("Prosim aktualizujte si naseho Klienta.");
				return;
			}
			if (player != null)
			{
				String mac = split[1];
				if (mac.matches("WHITELIST_REQUEST"))
				{
					if (GugaMCClientHandler.IsWhiteListed(player))
						mac = "WHITELISTED";
					else
					{
						player.kickPlayer("Nepodarilo se pripojit - Chyba 001");
						return;
					}
				}
				GugaMCClientHandler.RegisterUser(player, split[1]);
				/*if (GugaCommands.flyMode.contains(player.getName().toLowerCase()) || GugaCommands.flyMode.contains(player.getName()))
					GugaMCClientHandler.SendMessage(player, "SET_FLY;true");*/
			}
		}
	}
	@SuppressWarnings("unused")
	private Guga_SERVER_MOD plugin;
}

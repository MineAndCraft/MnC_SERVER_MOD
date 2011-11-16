package me.Guga.Guga_SERVER_MOD;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GugaSocketServer 
{
	public GugaSocketServer(int port, Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;
		try 
		{
			this.serverSocket = new DatagramSocket(port);
		} 
		catch (SocketException e) 
		{

			e.printStackTrace();
		}
		this.isListening = false;
	//	this.isCheckingConnected = false;
		this.newData = false;
		this.dataBuffer = new byte[512];
	}
	public void ListenStart()
	{
		this.isListening = true;
		this.receivedPacket = new DatagramPacket(this.dataBuffer, this.dataBuffer.length);
		this.listenThread = new Thread(
		new Runnable() 
		{
			@Override
			public void run() 
			{
				while (isListening)
				{
					try 
					{
						serverSocket.receive(receivedPacket);
						newData = true;
						CommandHandler();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		});
		listenThread.start();
		//this.ConnectedCheckerStart();
	}
	public void ListenStop()
	{
		this.isListening = false;
		//this.ConnectedCheckerStop();
	}
	/*public void ConnectedCheckerStart()
	{
		this.isCheckingConnected = true;
		this.connectedThread = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				while (isCheckingConnected)
				{
					plugin.log.info("CHECKING_CONNECTED");
					Set<Entry <String, SocketAddress>> connected = connectedClients.entrySet();
					for (Entry<String, SocketAddress> entry : connected)
					{
						plugin.log.info("FOR_LOOP");
						int timeCount = 0;
						boolean gotMsg = false;
						while (timeCount < 4000)
						{
							String data = "CONNECTED_" + System.currentTimeMillis();
							SendData(entry.getValue(), data);
							plugin.log.info("INNER_WHILE_LOOP_" + timeCount);
							if (GetLastData().matches(data))
							{
								plugin.log.info("CONNECTED_DATA_IS_EQUAL");
								gotMsg = true;
								break;
							}
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							timeCount += 200;
						}
						if (!gotMsg)
						{
							plugin.log.info("Client disconnected");
							connectedClients.remove(entry.getKey());
						}
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.connectedThread.setPriority(Thread.MAX_PRIORITY);
		this.connectedThread.start();
	}
	public void ConnectedCheckerStop()
	{
		this.isCheckingConnected = false;
	}*/
	public String GetLastData()
	{
		this.newData = false;
		return new String(this.receivedPacket.getData(), 0, this.receivedPacket.getLength());
	}
	public boolean HasNewData()
	{
		return this.newData;
	}
	public void SendData(SocketAddress addr, String data)
	{
		byte[] bytes = data.getBytes();
		try {
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr);
			serverSocket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Answer(String data)
	{
		byte[] bytes = data.getBytes();
		try 
		{
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.receivedPacket.getSocketAddress());
			this.serverSocket.send(packet);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	private boolean IsConnected()
	{
		Collection <SocketAddress> addresses = connectedClients.values();
		for (SocketAddress addr : addresses)
		{
			if (addr.equals(receivedPacket.getSocketAddress()))
				return true;
		}
		return false;
	}
	private void RemoveConnected(SocketAddress addr)
	{
		Set<Entry<String, SocketAddress>> entrySet = this.connectedClients.entrySet();
		for (Entry<String, SocketAddress> e : entrySet)
		{
			if (e.getValue().equals(addr))
			{
				this.connectedClients.remove(e.getKey());
				plugin.log.info("LOGOUT_SUCCESFUL");
				break;
			}
		}
	}
	private String GetGMBySocketAddr(SocketAddress addr)
	{
		Set<Entry<String, SocketAddress>> entrySet = this.connectedClients.entrySet();
		for (Entry<String, SocketAddress> e : entrySet)
		{
			if (e.getValue().equals(addr))
			{
				return e.getKey();
			}
		}
		return null;
	}
	private void CommandHandler()
	{
		try
		{
		String cmd = new String(this.receivedPacket.getData(), 0, this.receivedPacket.getLength());
		plugin.log.info(cmd);
		if (cmd.matches("GET_ONLINE_PLAYERS"))
		{
			if (!this.IsConnected())
				return;
			String data = "ONLINE_PLAYERS;";
			int i = 0;
			Player[] p = this.plugin.getServer().getOnlinePlayers();
			while (i < p.length)
			{
				if (i == p.length - 1)
					data += p[i].getName();
				else
					data += p[i].getName() + ";";
				i++;
			}
			this.Answer(data);
			return;
		}
		else if (cmd.contains("RUN_COMMAND"))
		{
			if (!this.IsConnected())
				return;
			String args[] = cmd.split(";");
			if (args[1].matches("BAN_PLAYER_IP"))
			{
				if (args.length < 3)
				{
					this.Answer("FAIL");
					return;
				}
				int i = 0;
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				while (i < p.length)
				{
					if (p[i].getName().equalsIgnoreCase(args[2]))
					{
						this.plugin.getServer().banIP(p[i].getAddress().getHostName());
						this.Answer("SUCCESS");
						return;
					}
							
					i++;
				}
				this.Answer("FAIL");
				return;
			}
			else if (args[1].matches("TELEPORT"))
			{
				if (args.length < 4)
				{
					this.Answer("FAIL");
					return;
				}
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				int i = 0;
				Player from = null;
				Player to = null;
				while (i < p.length)
				{
					if (p[i].getName().matches(args[2]))
					{
						from = p[i];
					}
					else if (p[i].getName().matches(args[3]))
					{
						to = p[i];
					}
					
					if (from != null && to != null)
					{
						from.teleport(to);
						this.Answer("SUCCESS");
						return;
					}
					i++;
				}
				this.Answer("FAIL");
				return;
			}
			else if (args[1].matches("GET_PLAYER_INFO"))
			{
				if (args.length < 3)
				{
					this.Answer("FAIL");
					return;
				}
				int i = 0;
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				while (i < p.length)
				{
					if (p[i].getName().equalsIgnoreCase(args[2]))
					{
						GugaProfession prof = this.plugin.professions.get(p[i].getName());
						int lvl;
						int xp;
						int xpNeeded;
						String profName;
						if (prof == null)
						{
							lvl = 0;
							xp = 0;
							xpNeeded = 0;
							profName = "None";
						}
						else
						{
							lvl = prof.GetLevel();
							xp = prof.GetXp();
							xpNeeded = prof.GetXpNeeded();
							if (prof instanceof GugaMiner)
								profName = "Miner";
							else
								profName = "Hunter";
						}
						String data = "SUCCESS;" + p[i].getName() + ";" + this.plugin.acc.GetPassword(p[i].getName()) + ";" + p[i].getAddress().getAddress().toString() + ";" + lvl + ";" + xp + ";" + xpNeeded + ";" + profName;
						this.Answer(data);
						return;
					}
					i++;
				}
				this.Answer("FAIL");
				return;
			}
			else if (args[1].matches("GIVE"))
			{
				if (args.length < 5)
				{
					this.Answer("FAIL");
					return;
				}
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				int i = 0;
				while (i < p.length)
				{
					if (p[i].getName().matches(args[2]))
					{
						p[i].getInventory().addItem(new ItemStack(Integer.parseInt(args[3]), Integer.parseInt(args[4])));
						this.Answer("SUCCESS");
						return;
					}
					i++;
				}
				this.Answer("FAIL");
				return;
			}
			else if (args[1].matches("KICK_PLAYER"))
			{
				if (args.length < 3)
				{
					this.Answer("FAIL");
					return;
				}
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				int i = 0;
				while (i < p.length)
				{
					if (p[i].getName().matches(args[2]))
					{
						p[i].kickPlayer("Byl jste vykopnut ze serveru!");
						this.Answer("SUCCESS");
						return;
					}
					i++;
				}
				this.Answer("FAIL");
				return;
			}
			else if (args[1].matches("BAN_PLAYER_ACC"))
			{
				if (args.length < 3)
				{
					this.Answer("FAIL");
					return;
				}
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				int i = 0;
				while (i < p.length)
				{
					if (p[i].getName().equalsIgnoreCase(args[2]))
					{
						p[i].setBanned(true);
						this.Answer("SUCCESS");
						return;
					}
					i++;
				}
				this.Answer("FAIL");
				return;
			}
			else if (args[1].matches("ANNOUNCE"))
			{
				if (args.length < 3)
				{
					this.Answer("FAIL");
					return;
				}
				this.plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + args[2]);
				this.Answer("SUCCESS");
				return;
			}
			else if (args[1].matches("WHISPER"))
			{
				if (args.length < 4)
				{
					this.Answer("FAIL");
					return;
				}
				Player[] p = this.plugin.getServer().getOnlinePlayers();
				int i = 0;
				while (i < p.length)
				{
					if (p[i].getName().equalsIgnoreCase(args[2]))
					{
						p[i].sendMessage(ChatColor.BLUE + this.GetGMBySocketAddr(this.receivedPacket.getSocketAddress()) + " whispers: " + args[3]);
						this.Answer("SUCCESS");
						return;
					}
					i++;
				}
			}
			this.Answer("FAIL");
			return;
		}
		else if (cmd.contains("LOGIN"))
		{
			String args[] = cmd.split(";");
			if (args.length < 3)
			{
				this.Answer("FAIL");
				return;
			}
			if (this.plugin.acc.ValidLogin(args[1], args[2]))
			{
				GameMaster gm;
				if ((gm = GameMasterHandler.GetGMByName(args[1])) != null)
				{
					String data = "SUCCESS" + ";" + gm.GetRank().GetRankName();
					this.Answer(data);
					this.connectedClients.put(args[1], this.receivedPacket.getSocketAddress());
					return;
				}
			}
			this.Answer("FAIL");
			return;
		}
		else if (cmd.matches("LOGOUT"))
		{
			if (this.IsConnected())
			{
				this.RemoveConnected(this.receivedPacket.getSocketAddress());
				return;
			}
		}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void SendChatMsg(String msg)
	{
		String data = "CHAT_MESSAGE;" + msg;
		Collection <SocketAddress> addresses = connectedClients.values();
		for (SocketAddress addr : addresses)
		{
			this.SendData(addr, data);
		}
	}
	private DatagramSocket serverSocket;
	private DatagramPacket receivedPacket;
	private byte[] dataBuffer;
	private Thread listenThread;
//	private Thread connectedThread;
	private boolean isListening;
//	private boolean isCheckingConnected;
	private boolean newData;
	private HashMap<String, SocketAddress> connectedClients = new HashMap<String, SocketAddress>();
	private Guga_SERVER_MOD plugin;
}

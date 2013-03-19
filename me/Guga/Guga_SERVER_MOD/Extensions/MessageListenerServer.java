package me.Guga.Guga_SERVER_MOD.Extensions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Handlers.ChatHandler;

public class MessageListenerServer extends Thread
{
	private boolean running = true;
	private ServerSocket sock = null;
	public static final int port = 15592;
	
	public MessageListenerServer()
	{
		try{
			this.sock = new ServerSocket(port);
		}
		catch(Exception e)
		{
			this.sock = null;
			this.running = false;
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(running)
		{
			try
			{
				Socket client = sock.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				//TODO add some kind of authentication/encryption
				final String message = reader.readLine();
				if(message == null)
					continue;
				processMessage(message);
				reader.close();
				client.close();
			}
			catch(SocketException e)
			{
				// do nothing
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}		

	public synchronized void disable()
	{
		if(sock == null || running == false)
			return;
		this.running = false;
		try {
			this.sock.close();
			this.interrupt();
		} catch (IOException e) {}
	}

	public void processMessage(String message)
	{
		String args[] = message.split(" ");
		if(args.length == 0)
			return;
		String cmd = args[0];
		if(cmd.equals("MSG_P1") && args.length == 3)
		{
			try{
				Guga_SERVER_MOD.getInstance().log.info(String.format("[GSM_Extensions.MSGL] Player %s got %s credits", args[1],args[2]));
			}
			catch(Exception e){}
			try{
				ChatHandler.InfoMsg(Guga_SERVER_MOD.getInstance().getServer().getPlayerExact(args[1]),String.format("Bylo vam pricteno %s kreditu.",args[2]));
			}
			catch(Exception e){}
		}
		else if(cmd.equals("MSG_P2") && args.length == 2)
		{
			try{
				Guga_SERVER_MOD.getInstance().log.info(String.format("[GSM_Extensions.MSGL] Player %s got VIP extended by 30 days", args[1]));
			}
			catch(Exception e){}
			try{
				ChatHandler.InfoMsg(Guga_SERVER_MOD.getInstance().getServer().getPlayerExact(args[1]),String.format("Bylo vam prodlouzeno VIP o 30 dnu."));
			}
			catch(Exception e){}
		}
	}
}

package me.Guga.Guga_SERVER_MOD.Extensions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.Guga.Guga_SERVER_MOD.DatabaseManager;
import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;
import me.Guga.Guga_SERVER_MOD.Handlers.ChatHandler;

public class SmsParser extends Thread
{
	private boolean running = true;
	private boolean _processing = false;
	
	public void run()
	{
		while(running)
		{
			try{
				_processing = true;
				try(PreparedStatement stat = DatabaseManager.getConnection().prepareStatement("SELECT * FROM sms_log WHERE processed = 0 LIMIT 200");)
				{
					try(ResultSet res = stat.executeQuery();)
					{
						while(res.next() && running)
						{
							int id = res.getInt("id");
							String player = res.getString("player");
							String service = res.getString("service");
							if(service.equalsIgnoreCase("MNCKRE"))
							{
								int credits = getCreditAmount(res.getString("target_number"));
								Guga_SERVER_MOD.getInstance().currencyManager.addCredits(player, credits);
								try{
									ChatHandler.InfoMsg(Guga_SERVER_MOD.getInstance().getServer().getPlayerExact(player),String.format("Bylo vam pricteno %d kreditu.",credits));
								}
								catch(Exception e){}
							}
							try(PreparedStatement stat2 = DatabaseManager.getConnection().prepareStatement("UPDATE sms_log SET processed = 1 WHERE id = ? LIMIT 1");)
							{
								stat2.setInt(1, id);
								stat2.executeUpdate();
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				_processing = false;
				Thread.sleep(10000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void disable()
	{
		this.running = false;
		while(this._processing)
			try{
				Thread.sleep(10);
			}
			catch(Exception e){}
		this.interrupt();
	}
	
	public synchronized boolean isProcessing(){
		return this._processing;
	}

	private static int getCreditAmount(String num)
	{
		String part1 = num.substring(0, 5);
		if(part1.equals("90333"))
		{
			int payed = Integer.parseInt(num.substring(5));
			switch(payed)
			{
				case 20:
					return 250;
				case 50:
					return 750;
				case 79:
					return 1250;
				default:
					return 0;
			}
		}
		return 0;
	}
}

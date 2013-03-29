package me.MnC.MnC_SERVER_MOD.util;

import java.security.MessageDigest;
import java.util.Formatter;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;

import org.bukkit.entity.Player;

public class Util 
{
	public static String sha1(String str)
	{
		try{
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			return Util.byteArray2Hex(sha1.digest(str.getBytes("utf-8")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

    private static String byteArray2Hex(byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String str = formatter.toString();
        formatter.close();
        return str;
    }

	public static synchronized void threadSafeKickPlayer(final Player player,final String message)
	{
		MnC_SERVER_MOD.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MnC_SERVER_MOD.getInstance(), new Runnable(){
			public void run(){
				try{
					player.kickPlayer(message);
				}catch(Exception e){}						
			}
		});
	}
}

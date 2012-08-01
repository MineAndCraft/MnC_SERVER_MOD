package me.Guga.Guga_SERVER_MOD;

import java.security.MessageDigest;
import java.util.Formatter;

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
        return formatter.toString();
    }
}

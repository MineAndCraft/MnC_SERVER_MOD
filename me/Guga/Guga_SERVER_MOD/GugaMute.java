package me.Guga.Guga_SERVER_MOD;

public class GugaMute 
{
	public static boolean toggleChatMute()
	{
		if(muteForAll==false)
		{
			muteForAll=true;
			return muteForAll;
		}
		else
		{
			muteForAll=false;
			return muteForAll;
		}
	}
	public static boolean statusChatMute()
	{
		return muteForAll;
	}
	static boolean muteForAll=false;
}

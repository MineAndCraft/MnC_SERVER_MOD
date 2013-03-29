package me.MnC.MnC_SERVER_MOD.Extensions;

import me.MnC.MnC_SERVER_MOD.MnC_SERVER_MOD;
import me.MnC.MnC_SERVER_MOD.Extensions.MessageListenerServer;

public class ExtensionManager
{
	private boolean _initialized = false;
	private MnC_SERVER_MOD plugin;
	
	public MessageListenerServer msgls;
		
	public ExtensionManager(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
		initialize();
	}
	
	public void initialize()
	{
		_initialized = true;
		//smsparser.start();
		msgls = new MessageListenerServer();
		msgls.start();		
		
		plugin.log.info("[GSM.ExtensionManager] initialized");
	}
	
	public void disable()
	{
		msgls.disable();
		
		_initialized = true;
	}
	
	public boolean isInitialized(){ return this._initialized;}
}

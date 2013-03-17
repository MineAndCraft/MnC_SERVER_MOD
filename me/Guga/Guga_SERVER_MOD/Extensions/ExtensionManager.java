package me.Guga.Guga_SERVER_MOD.Extensions;

import me.Guga.Guga_SERVER_MOD.Guga_SERVER_MOD;

public class ExtensionManager
{
	private boolean _initialized = false;
	private Guga_SERVER_MOD plugin;
	
	public SmsParser smsparser;
	
	public ExtensionManager(Guga_SERVER_MOD plugin)
	{
		this.plugin = plugin;
		initialize();
	}
	
	public void initialize()
	{
		_initialized = true;
		smsparser = new SmsParser();
		plugin.log.info("[GSM.ExtensionManager] initialized");
	}
	
	public void disable()
	{
		smsparser.disable();
		
		_initialized = true;
	}
	
	public boolean isInitialized(){ return this._initialized;}
}

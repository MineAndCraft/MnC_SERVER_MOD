package me.Guga.Guga_SERVER_MOD;

public class DatabaseConfiguration 
{
	public DatabaseConfiguration()
	{
		loadConfig();
	}
	private void loadConfig()
	{
		GugaFile file = new GugaFile(filePath, GugaFile.READ_MODE);
		file.Open();
		String line;
		String[] splittedLine;
		while((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(":");
			if(splittedLine[0].matches("host"))
			{
				db_server = splittedLine[1];
			}
			else if(splittedLine[0].matches("port"))
			{
				db_port = Integer.parseInt(splittedLine[1]);
			}
			else if(splittedLine[0].matches("user"))
			{
				db_username = splittedLine[1];
			}
			else if(splittedLine[0].matches("password"))
			{
				db_password = splittedLine[1];
			}
			else if(splittedLine[0].matches("database"))
			{
				db_name = splittedLine[1];
			}
		}
	}
	public String getDbServer()
	{
		return db_server;
	}
	
	public int getPort()
	{
		return db_port;
	}
	
	public String getUsername()
	{
		return db_username;
	}
	
	public String getPassword()
	{
		return db_password;
	}
	
	public String getName()
	{
		return db_name;
	}
	public String db_server;
	public int db_port;
	public String db_username;
	public String db_name;
	public String db_password;
	public String filePath = "plugins/dbConfig.ini";
}

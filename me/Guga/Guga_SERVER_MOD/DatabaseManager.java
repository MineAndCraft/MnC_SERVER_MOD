package me.Guga.Guga_SERVER_MOD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager 
{
	public DatabaseManager()
	{
		this(false);
	}
	
	public DatabaseManager(boolean connectAutomatically)
	{
		this.loadConfig();
		if(connectAutomatically)
			this.connectDb();
		_instance = this;
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
	
	public void connectDb()
	{
		try 
		{
			connection = DriverManager.getConnection("jdbc:mysql://"+this.db_server+":"+String.valueOf(this.db_port)+"/" + this.db_name, this.db_username,this.db_password);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void disconnectDb()
	{
		try 
		{
			connection.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
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
	
	public String getName()
	{
		return db_name;
	}
	
	public Connection getConection()
	{
		return connection;
	}
	
	public static Connection getConnection()
	{
		return _instance.getConection();
	}
	
	private Connection connection = null;	
	private String db_server = "";
	private int db_port = 0;
	private String db_username = "";
	private String db_name = "";
	private String db_password = "";
	private String filePath = "plugins/MineAndCraft_plugin/dbConfig.ini";
	private static DatabaseManager _instance = null;
}

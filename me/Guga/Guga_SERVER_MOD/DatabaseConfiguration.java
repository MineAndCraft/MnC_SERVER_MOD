package me.Guga.Guga_SERVER_MOD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
	
	public void connectDb()
	{
		conn = null;
		Properties connectionProps = new Properties();
	    connectionProps.put("user", db_username);
		connectionProps.put("password", db_password);
		try 
		{
			conn = DriverManager.getConnection("jdbc:mysql://" + db_server + ":" + String.valueOf(db_port) + "/", connectionProps);
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
			conn.close();
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
	
	public String getPassword()
	{
		return db_password;
	}
	
	public String getName()
	{
		return db_name;
	}
	
	public Connection getConection()
	{
		return conn;
	}
	
	private Connection conn;
	
	private String db_server;
	private int db_port;
	private String db_username;
	private String db_name;
	private String db_password;
	private String filePath = "plugins/dbConfig.ini";
}

package me.MnC.MnC_SERVER_MOD;

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
		if(connectAutomatically)
			this.connectDb();
		_instance = this;
	}
	
	public void connectDb()
	{
		try 
		{
			Class.forName(Config.DATABASE_DRIVER);
			connection = DriverManager.getConnection(Config.DATABASE_URL,Config.DATABASE_LOGIN,Config.DATABASE_PASSWORD);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
		
	public Connection getConection()
	{
		return connection;
	}
	
	public static Connection getConnection()
	{
		return _instance.getConection();
	}
	
	private Connection connection = null;
	private static DatabaseManager _instance = null;
}

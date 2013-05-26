package me.MnC.MnC_SERVER_MOD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager 
{
	private static final Logger _log = Logger.getLogger(DatabaseManager.class.getName());
	
	private Connection connection = null;
	
	private static DatabaseManager _instance = null;
	
	DatabaseManager()
	{
		_instance = this;
	}
	
	/**
	 * Connects to the MySQL database
	 * @throws SQLException - If connection to the MySQL server was not successful
	 */
	public void connectDB() throws SQLException
	{
		disconnectDB();
		try
		{
			Class.forName(Config.DATABASE_DRIVER);
			connection = DriverManager.getConnection(Config.DATABASE_URL,Config.DATABASE_LOGIN,Config.DATABASE_PASSWORD);
			_log.info("Sucessfully connected to MySQL");
		}
		catch(ClassNotFoundException e) {
			throw new SQLException("Database driver not found:"+e);
		}
		catch(SQLException e) {
			throw e;
		}
	}
	
	/**
	 * Closes connection to the MySQL database
	 */
	public void disconnectDB()
	{
		if(connection == null)
			return;
		try{
			connection.close();
		} 
		catch(SQLException e){
			_log.log(Level.SEVERE,"Error occured while disconnecting MySQL",e);
		}
	}
	
	/**
	 * @return The database connection
	 * @throws NullPointerException if the database is not initialized yet
	 */
	public static Connection getConnection()
	{
		return _instance.connection;
	}
}

package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class GugaConfiguration 
{
	GugaConfiguration(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
		chestsModule = true;
		accountsModule = true;
		
		GetConfiguration();
	}
	public void GetConfiguration()
	{
		File config = new File(configFile);
		if (!config.exists())
		{
			try 
			{
				config.createNewFile();
				SetConfiguration();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
    	else
		{
			try 
			{
				FileInputStream fRead = new FileInputStream(config);
				DataInputStream inStream = new DataInputStream(fRead);
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream)) ;		
				String line;
				String option;
				boolean value;
				try {
					while ((line = bReader.readLine()) != null)
					{
						option = line.split("=")[0];
						if (option.matches("ChestsModule"))
						{
							value = Boolean.parseBoolean(line.split("=")[1]);
							chestsModule = value;
						}
						else if(option.matches("AccountsModule"))
						{
							value = Boolean.parseBoolean(line.split("=")[1]);
							accountsModule = value;
						}
					}
					bReader.close();
					inStream.close();
					fRead.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void SetConfiguration()
	{
		File config = new File(configFile);
		if (!config.exists())
		{
			try 
			{
				config.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		try 
		{
			FileWriter fStream = new FileWriter(config, false);
			BufferedWriter bWriter;
			bWriter = new BufferedWriter(fStream);
			String line;
			line = "ChestsModule=" + chestsModule;
			bWriter.write(line);
			bWriter.newLine();
			line = "AccountsModule=" + accountsModule;
			bWriter.write(line);
			bWriter.newLine();
					
			bWriter.close();
			fStream.close();
		} 
		catch (IOException e) 
		{
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
	public boolean chestsModule;
	public boolean accountsModule;
	public int lvlCap;
	public int baseXp;
	public int xpIncrement;
	private String configFile = "plugins/config.ini";
	Guga_SERVER_MOD plugin;
}
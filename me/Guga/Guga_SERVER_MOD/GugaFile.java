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

public class GugaFile 
{
	
	GugaFile(String filePath, int openMode)
	{
		this.openMode = openMode;
		this.filePath = filePath;
	}
	public void Open()
	{
		this.file = new File(filePath);
		if (!this.file.exists())
		{
			try 
			{
				this.file.createNewFile();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		if (this.openMode == GugaFile.READ_MODE)
		{
			this.OpenReadMode();
		}
		else if (this.openMode == GugaFile.WRITE_MODE)
		{
			this.OpenWriteMode();
		}
	}
	public String ReadLine()
	{
		try 
		{
			return this.bReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void WriteLine(String line)
	{
		try 
		{
			this.bWriter.write(line);
			this.bWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Close()
	{
		if (this.openMode == GugaFile.WRITE_MODE)
		{
			try 
			{
				this.bWriter.close();
				this.fWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (this.openMode == GugaFile.READ_MODE)
		{
			try 
			{
				this.bReader.close();
				this.dInput.close();
				this.fInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void OpenReadMode()
	{
		try 
		{
			this.fInput = new FileInputStream(this.file);
			this.dInput = new DataInputStream(this.fInput);
			this.bReader = new BufferedReader(new InputStreamReader(this.dInput));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*FileInputStream fRead = new FileInputStream(curr);
		  DataInputStream inStream = new DataInputStream(fRead);
		  BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));*/
		
	}
	private void OpenWriteMode()
	{
		try {
			this.fWriter= new FileWriter(this.file, false);
			this.bWriter = new BufferedWriter(this.fWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File file;
	
	private FileWriter fWriter;
	private BufferedWriter bWriter;
	
	private FileInputStream fInput;
	private DataInputStream dInput;
	private BufferedReader bReader;
	
	private int openMode;
	private String filePath;
	
	public static int READ_MODE = 0;
	public static int WRITE_MODE = 1;
}
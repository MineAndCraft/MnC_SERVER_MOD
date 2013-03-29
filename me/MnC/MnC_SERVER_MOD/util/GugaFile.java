package me.MnC.MnC_SERVER_MOD.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class GugaFile 
{

	public GugaFile(String filePath, int openMode)
	{
		this.openMode = openMode;
		this.file = new File(filePath);
	}
	public boolean Exists()
	{
		return file.exists();
	}
	public void Open()
	{
		if (this.openMode == GugaFile.READ_MODE)
			this.OpenReadMode();
		else if (this.openMode == GugaFile.WRITE_MODE)
			this.OpenWriteMode();
		else if (this.openMode == GugaFile.APPEND_MODE)
			this.OpenAppendMode();
		else if (this.openMode == GugaFile.BINARY_MODE)
			this.OpenBinaryMode();
		this.opened = true;
	}
	public String ReadLine()
	{
		if (!this.opened)
			this.Open();
		try 
		{
			return this.bReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void WriteLine(String line)
	{
		if (!this.opened)
			this.Open();
		try 
		{
			this.bWriter.write(line);
			this.bWriter.newLine();
		} catch (IOException e) {
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
		else if (this.openMode == GugaFile.APPEND_MODE)
		{
			try 
			{
				this.bWriter.close();
				this.fWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (this.openMode == GugaFile.BINARY_MODE)
		{
			try 
			{
				this.fInput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.opened = false;
	}
	private void OpenReadMode()
	{
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
		try 
		{
			this.fInput = new FileInputStream(this.file);
			this.dInput = new DataInputStream(this.fInput);
			this.bReader = new BufferedReader(new InputStreamReader(this.dInput));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/*FileInputStream fRead = new FileInputStream(curr);
		  DataInputStream inStream = new DataInputStream(fRead);
		  BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));*/

	}
	private void OpenWriteMode()
	{
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
		try {
			this.fWriter= new FileWriter(this.file, false);
			this.bWriter = new BufferedWriter(this.fWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void OpenAppendMode()
	{
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
		try {
			this.fWriter= new FileWriter(this.file, true);
			this.bWriter = new BufferedWriter(this.fWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void OpenBinaryMode()
	{
		try {
			this.fInput = new FileInputStream(this.file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void CopyFileTo(String filePath)
	{
		if (!this.opened)
			this.Open();
		File file2 = new File(filePath);
		try 
		{
			FileOutputStream fOutput = new FileOutputStream(file2);
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while ( (bytes = fInput.read(buffer)) > 0)
			{
				fOutput.write(buffer, 0, bytes);
			}
			fOutput.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void ClearDirectory(String directoryPath)
	{
		if (!directoryPath.endsWith("/"))
		{
			directoryPath += "/";
		}
		File dir = new File(directoryPath);
		if (!dir.exists())
			return;
		String[] files = dir.list();
		File f = null;
		int i = 0;
		while (i < files.length)
		{
			f = new File(directoryPath + files[i]);
			f.delete();
			i++;
		}
	}
	private File file = null;
	
	private boolean opened = false;
	private FileWriter fWriter = null;
	private BufferedWriter bWriter = null;
	
	private FileInputStream fInput = null;
	private DataInputStream dInput = null;
	private BufferedReader bReader = null;

	private int openMode = 0;

	public static int READ_MODE = 1;
	public static int WRITE_MODE = 2;
	public static int APPEND_MODE = 3;
	public static int BINARY_MODE = 4;
}
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

	public GugaFile(String filePath, int openMode)
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
			this.OpenReadMode();
		else if (this.openMode == GugaFile.WRITE_MODE)
			this.OpenWriteMode();
		else if (this.openMode == GugaFile.APPEND_MODE)
			this.OpenAppendMode();
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
		else if (this.openMode == GugaFile.APPEND_MODE)
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
	private void OpenAppendMode()
	{
		try {
			this.fWriter= new FileWriter(this.file, true);
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
	public static int APPEND_MODE = 2;
}

//*****************NATIVE VERSION******************

/*import Native.GugaNativeBridge;

public class GugaFile 
{
	
	GugaFile(String filePath, int openMode)
	{
		this.openMode = openMode;
		this.filePath = filePath;
	}
	public void Open()
	{
	if (this.openMode == GugaFile.READ_MODE)
		this.fileHandle = GugaNativeBridge.CreateHandle(filePath, 0);
	else if (this.openMode == GugaFile.WRITE_MODE)
		this.fileHandle = GugaNativeBridge.CreateHandle(filePath, 1);
	else if (this.openMode == GugaFile.APPEND_MODE)
		this.fileHandle = GugaNativeBridge.CreateHandle(filePath, 2);
	}
	public String ReadLine()
	{
		String line = GugaNativeBridge.ReadLine(fileHandle);
		GugaPort.plugin.log.info("DATA: [" + line + "]");
		if (line == "" || line == null || line.length() < 1)
			return null;
		return line;
	}
	public void WriteLine(String line)
	{
		GugaNativeBridge.WriteLine(line, fileHandle);
	}
	public void Close()
	{
		GugaNativeBridge.CloseHandle(fileHandle);
	}

	private int fileHandle;
	
	private int openMode;
	private String filePath;
	
	public static int READ_MODE = 0;
	public static int WRITE_MODE = 1;
	public static int APPEND_MODE = 2;
}*/
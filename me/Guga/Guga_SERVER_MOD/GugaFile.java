package me.Guga.Guga_SERVER_MOD;


import Native.GugaNativeBridge;

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
		if (line == "" || line == null)
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
}
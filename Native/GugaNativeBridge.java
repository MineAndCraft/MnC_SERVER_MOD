package Native;
public abstract class GugaNativeBridge 
{
	public static void LoadLibrary(String path)
	{
		System.load(path);
	}
	//public static native void AppendLine(String file, String line);
	public static native int CreateHandle(String path, int openMode);
	public static native void CloseHandle(int handle);
	public static native void WriteLine(String line, int handle);
	public static native String ReadLine(int handle);
}

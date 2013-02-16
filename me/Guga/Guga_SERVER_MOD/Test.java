package me.Guga.Guga_SERVER_MOD;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		long expiration = Long.valueOf("-1");
		if(!(expiration == -1L))
			System.out.print("True");
		else
			System.out.print("No");
	}
}

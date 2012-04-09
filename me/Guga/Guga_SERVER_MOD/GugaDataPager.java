package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;

public class GugaDataPager <E>
{
	public GugaDataPager(ArrayList<E> data, int itemsPerPage)
	{
		this.data = data;
		this.itemsPerPage = itemsPerPage;
	}
	public ArrayList<E> GetPage(int page)
	{
		ArrayList<E> list = new ArrayList<E>();
		if (page < 1)
			return null;
		int firstItem = this.itemsPerPage * (page-1);
		int lastItem = this.itemsPerPage * page;
		int i = firstItem;
		if (this.data.size() < firstItem)
		{
			return null;
		}
		while (i < lastItem)
		{
			if (this.data.size() <= i)
				break;
			E item = this.data.get(i);
			list.add(item);
			i++;
		}
		return list;
	}
	public int GetPagesCount()
	{
		double pages = (double)this.data.size() / (double)this.itemsPerPage;
		int num;
		if ( (pages - (int)pages) > 0)
			num = (int)pages + 1;
		else
			num = (int)pages;
		return num;
	}
	private ArrayList<E> data;
	private int itemsPerPage;
}

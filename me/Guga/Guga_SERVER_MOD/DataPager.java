package me.Guga.Guga_SERVER_MOD;

import java.util.ArrayList;

public class DataPager <E>
{
	public DataPager(ArrayList<E> data, int itemsPerPage)
	{
		this.data = data;
		this.itemsPerPage = itemsPerPage;
	}
	public ArrayList<E> getPage(int page)
	{
		if (page < 1)
			return null;
		int firstItem = this.itemsPerPage * (page-1);
		int lastItem = this.itemsPerPage * page;
		if(!(firstItem < this.data.size()))
			return null;
		if(lastItem>this.data.size())
			lastItem=this.data.size();
		return new ArrayList<E>(this.data.subList(firstItem, lastItem));
	}
	
	public int getPageCount()
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

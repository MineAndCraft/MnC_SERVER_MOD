package me.MnC.MnC_SERVER_MOD.Currency;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
 
public class WritableBook implements Serializable
{
	private static final long serialVersionUID = 271998645261L;

    public WritableBook(ItemStack bookItem)
    {
    	BookMeta bMeta = (BookMeta)bookItem.getItemMeta();
    	this.author = bMeta.getAuthor();
    	this.title = bMeta.getTitle();
    	String[] bPages = new String[bMeta.getPageCount()];
    	for(int i=0; i<bMeta.getPageCount(); i++)
    	{
    		bPages[i] = bMeta.getPage(i+1);
    	}
    	this.pages = bPages;
    }
 
    public WritableBook(String title, String author, String[] pages) 
    {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
    
    public String getAuthor()
    {
        return author;
    }
 
    public void setAuthor(String sAuthor)
    {
        author = sAuthor;
    }
   
    public String getTitle()
    {
        return title;
    }
   
    public String[] getPages()
    {
        return pages;
    }
 
    public ItemStack createItem(int amount)
    {
    	ItemStack bookItem = new ItemStack(Material.WRITTEN_BOOK, amount);
    	BookMeta bMeta = (BookMeta)bookItem.getItemMeta();
        bMeta.setAuthor(this.author);
        bMeta.setTitle(this.title);
        for(int i=0; i<this.pages.length;i++)
        {
        	bMeta.addPage(this.pages[i]);
        }
        bookItem.setItemMeta(bMeta);
        return bookItem;
    }
    public boolean serialize(String filePath)
    {
    	try
    	{
	    	FileOutputStream fOut = new FileOutputStream(filePath);
			ObjectOutputStream oOut = new ObjectOutputStream (fOut);
	    	oOut.writeObject(this);
	    	oOut.close();
	    	fOut.close();
	    	return true;
        }
    	catch(IOException e)
    	{
    		return false;
    	}
    }
    
    public static WritableBook restoreObject(String filePath)
    {
    	try
    	{
	    	FileInputStream fIn = new FileInputStream(filePath);
	    	ObjectInputStream oIn = new ObjectInputStream (fIn);
	    	WritableBook book = (WritableBook)oIn.readObject();
	    	oIn.close();
	    	fIn.close();
	    	return book;
    	}
    	catch(IOException e)
    	{
    		System.out.println("IOEx during de-serializing!");
    		return null;
    	}
    	catch(ClassNotFoundException e)
    	{
    		System.out.println("ClassNotFoudEx during de-serializing!");
    		return null;
    	}
    }
    public static final String joinBookPath = "plugins/MineAndCraft_plugin/Books/FirstJoinBook.book";
    private String author;
    private String title;
    private String[] pages;
}
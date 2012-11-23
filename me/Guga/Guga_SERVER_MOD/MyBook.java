package me.Guga.Guga_SERVER_MOD;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
 
public class MyBook implements Serializable
{
	private static final long serialVersionUID = 271998645261L;

    public MyBook(ItemStack bookItem)
    {
        NBTTagCompound bookData = ((CraftItemStack) bookItem).getHandle().tag;
       
        this.author = bookData.getString("author");
        this.title = bookData.getString("title");
               
        NBTTagList nPages = bookData.getList("pages");
 
        String[] sPages = new String[nPages.size()];
        for(int i = 0;i<nPages.size();i++)
        {
            sPages[i] = nPages.get(i).toString();
        }
               
        this.pages = sPages;
    }
 
    public MyBook(String title, String author, String[] pages) 
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
 
    public ItemStack createItem(){
        CraftItemStack newbook = new CraftItemStack(Material.WRITTEN_BOOK);
        NBTTagCompound newBookData = new NBTTagCompound();
        newBookData.setString("author",author);
        newBookData.setString("title",title);
        NBTTagList nPages = new NBTTagList();
        for(int i = 0;i<pages.length;i++)
        { 
            nPages.add(new NBTTagString(pages[i],pages[i]));
        }
       
        newBookData.set("pages", nPages);
        newbook.getHandle().tag = newBookData;
        return (ItemStack) newbook;
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
    
    public static MyBook restoreObject(String filePath)
    {
    	try
    	{
	    	FileInputStream fIn = new FileInputStream(filePath);
	    	ObjectInputStream oIn = new ObjectInputStream (fIn);
	    	MyBook book = (MyBook)oIn.readObject();
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
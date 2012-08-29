package me.Guga.Guga_SERVER_MOD;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
 
public class Book
{
    public Book(ItemStack bookItem)
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
 
    public Book(String title, String author, String[] pages) {
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
 
    public ItemStack generateItemStack(){
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
    private String author;
    private String title;
    private String[] pages;
}
package me.Guga.Guga_SERVER_MOD;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.ChatColor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Votes 
{
	public static void setPlugin(Guga_SERVER_MOD gsm)
	{
		plugin = gsm;
	}
	private static Guga_SERVER_MOD plugin;
	private static String filePath = "plugins/MineAndCraft_plugin/votes.xml";
	
	public static void Start()
	{
		plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run()
			{
				try {
					saveUrl(filePath,"http://www.czech-craft.eu/xml/363.xml");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[Server] Pricitaji se kredity za hlasovani...");
				File votesFile = new File(filePath);
				HashMap<String,Integer> votes = new HashMap<String,Integer>();
				votes = Votes.Parse(plugin.dbConfig.getConection(), votesFile);
				Iterator<Entry<String, Integer>> it = votes.entrySet().iterator();
				while(it.hasNext())
				{
					Entry<String, Integer> vote = it.next();
					GugaVirtualCurrency curr = plugin.FindPlayerCurrency(vote.getKey());
					if(curr != null)
					{
						int toAdd = vote.getValue()*5;
						curr.AddCurrency(toAdd);
						plugin.getLogger().info("Player " + vote.getKey() + " gets " + toAdd + " credits!");
					}
				}
				plugin.SaveCurrency();
			}
		}, 30000, 30000);
	}
	private static HashMap<String,Integer> Parse(Connection conn,File source)
	{
		HashMap<String,Integer> votes = new HashMap<String,Integer>();
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(source);
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("hlas");
            for(int i=0;i<list.getLength();i++)
            {
            	Node n = list.item(i);
            	String id = "";
            	String player = "";
            	String date = "";
           		if(n.getNodeType() == Node.ELEMENT_NODE)
           		{
           			Element elm = (Element)n;
           			id = ((Node)((Element)elm.getElementsByTagName("id").item(0)).getChildNodes().item(0)).getNodeValue();
           			player = ((Node)((Element)elm.getElementsByTagName("nick").item(0)).getChildNodes().item(0)).getNodeValue();
           			date = ((Node)((Element)elm.getElementsByTagName("cas").item(0)).getChildNodes().item(0)).getNodeValue();
           		}
           		java.sql.PreparedStatement stat1 = null;
           		boolean exists = false;
           		try{
           			stat1 = conn.prepareStatement("SELECT count(*) as my_count FROM `" + plugin.dbConfig.getName() + "`.`mnc_votes` WHERE `cc_id` = ? ;");
           			stat1.setString(1, id);
           			ResultSet r = stat1.executeQuery();
           			if(!conn.getAutoCommit())
                		conn.commit();
           			r.next();
           			if(r.getInt("my_count") > 0)
           				exists = true;
           			r.close();
           		}catch(Exception e)
           		{
           			e.printStackTrace();
           		}
           		finally
           		{
           			if(stat1 != null)
           				stat1.close();
           		}
           		
           		if(exists)
           			continue;
            	java.sql.PreparedStatement stat = null;
            	try{
            	//add all votes
            	stat = conn.prepareStatement("INSERT INTO `" + plugin.dbConfig.getName() + "`.`mnc_votes` (`cc_id`,`playername`,`date`) VALUES(?,?,?);");
            	stat.setString(1, id);
            	stat.setString(2, player);
            	stat.setString(3, date);
            	int res = stat.executeUpdate();
            	if(res == 1)
            	{
            		if(votes.containsKey(player))
            			votes.put(player, votes.get(player)+1);
            		else
            			votes.put(player, 1);
            	}
            	if(!conn.getAutoCommit())
            		conn.commit();
            	}
            	catch(Exception e)
            	{
            		e.printStackTrace();
            	}
            	finally{
            		if(stat != null)
            			stat.close();
            	}
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return votes;
	}
	public static void addCredits()
	{
		GugaFile file = new GugaFile("plugins/votes.txt", GugaFile.READ_MODE);
		String line;
		String[]splittedLine;
		while((line = file.ReadLine()) != null)
		{
			splittedLine = line.split(";");
			GugaVirtualCurrency curr = plugin.FindPlayerCurrency(splittedLine[0]);
			if((curr != null) && (splittedLine[3].matches("standard access")))
			{
				plugin.getServer().broadcastMessage("Player " + splittedLine[0] + " gets 5 credits!");
				curr.AddCurrency(5);
			}
			else if(splittedLine[3].matches("corrupted access"))
			{
				plugin.getServer().broadcastMessage("Player " + splittedLine[0] + " corrupted access!");
			}
			
		}
	}
	public static void saveUrl(String filename, String urlString) throws MalformedURLException, IOException
    {
    	BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try
    	{
    		in = new BufferedInputStream(new URL(urlString).openStream());
    		fout = new FileOutputStream(filename);

    		byte data[] = new byte[1024];
    		int count;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
    		}
    	}
    	finally
    	{
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
    }
}


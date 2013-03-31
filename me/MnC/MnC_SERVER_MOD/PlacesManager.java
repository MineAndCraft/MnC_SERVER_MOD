package me.MnC.MnC_SERVER_MOD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.MnC.MnC_SERVER_MOD.Handlers.GameMasterHandler;
import me.MnC.MnC_SERVER_MOD.chat.ChatHandler;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlacesManager
{
	//requires table mnc_places { id - varchar(16) primary key, owner_id - int, x - int, y - int, z - int, type - varchar(8), UNIQUE[x,y,z]}
	//requires table mnc_places_premissions { id - int primary key auto_increment, place_id - varchar(16), user_id - int, UNIQUE[place_id,user_id]}

	public class Place
	{
		private int id;
		private String port;
		private int x;
		private int y;
		private int z;
		private String type;
		private int owner_id;
		private String welcomeMessage;
		private String world;
		
		public Place(int id,String port, int x, int y, int z, String type, int owner_id, String welcomeMessage, String world)
		{
			this.id = id;
			this.port = port;
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = _checkType(type);
			this.owner_id = owner_id;
			if(welcomeMessage != null)
				this.welcomeMessage = welcomeMessage;
			else
				this.welcomeMessage = "";
			this.world = world;
		}

		private String _checkType(String type)
		{
			switch(type.toLowerCase())
			{
				case TYPE_PUBLIC:
					return TYPE_PUBLIC;
				case TYPE_VIP:
					return TYPE_VIP;
				case TYPE_PRIVATE:
					return TYPE_PRIVATE;
				default:
					return TYPE_DEFAULT;
			}
		}

		public int getId(){
			return id;
		}
		
		public String getName() {
			return port;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		public String getType() {
			return type;
		}
		
		public int getOwnerId() {
			return this.owner_id;
		}

		public String getWelcomeMessage() {
			return welcomeMessage;
		}
		
		public String getWorld() {
			return world;
		}
		
		public static final String TYPE_VIP = "vip";
		public static final String TYPE_PUBLIC = "public";
		public static final String TYPE_PRIVATE = "private";
		public static final String TYPE_DEFAULT = TYPE_PUBLIC;

		public boolean isPublic() {
			return this.type == TYPE_PUBLIC;
		}
		
		public boolean isPrivate() {
			return this.type == TYPE_PRIVATE;
		}

		public boolean isVip() {
			return this.type == TYPE_VIP;
		}
	}

	private MnC_SERVER_MOD plugin;

	public PlacesManager(MnC_SERVER_MOD plugin)
	{
		this.plugin = plugin;
	}

	public boolean handlePlayerTeleport(Player player,String portName)
	{
		Place port = this.getTeleport(portName);
		if(port == null)
		{
			player.sendMessage("Tento portal neexistuje.");
			return false;
		}
		
		MinecraftPlayer playerData = plugin.userManager.getUser(player.getName());
		
		if(playerData == null)
		{
			player.sendMessage("Cannot teleport.");
			return false;
		}
		
		if(port.isPublic() ||
			GameMasterHandler.IsAtleastGM(player.getName()) || //allow all GMs to teleport freely
			(port.isVip() && plugin.vipManager.isVip(playerData.getId())) ||
				port.isPrivate() && (port.getOwnerId() == playerData.getId() || isUserAllowedToTeleportTo(playerData,port)))
		{
			World destWorld = plugin.getServer().getWorld(port.getWorld());	
			if(destWorld==null)
			{
				player.sendMessage("Cannout teleport.");
				return false;
			}
			
			Location teleportDest = new Location(destWorld, port.getX(), port.getY(), port.getZ());
			// implementation of rikub's awesome invention
			destWorld.playEffect(teleportDest, Effect.MOBSPAWNER_FLAMES, 100);
			player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 40);
			//teleport player
			player.teleport(teleportDest);
			ChatHandler.systemInfo(player,String.format("Teleported to %s",port.getName()));
			player.sendMessage(ChatColor.LIGHT_PURPLE+port.getWelcomeMessage());
			return true;
		}
		else
		{
			player.sendMessage("Na tento portal nemate pravomoce");
			return false;
		}
	}

	public boolean portalExists(String portalName)
	{
		boolean exists = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT count(*) as count FROM `mnc_places` WHERE `name` = ? LIMIT 1;");)
		{
			stat.setString(1, portalName);
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				exists = result.getInt("count") > 0;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return exists;
	}

	/**
	 * Determines if user explicit permission to teleport to portal
	 * @param player
	 * @param port
	 * @return true if user has the permission, false otherwise
	 */
	protected boolean isUserAllowedToTeleportTo(MinecraftPlayer player,Place port)
	{
		boolean permission = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT 1 as permission FROM `mnc_places_permissions` pp WHERE pp.place_id = ? AND pp.user_id = ? LIMIT 1");)
		{
			stat.setString(1, port.getName());
			stat.setInt(2, player.getId());
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				permission = result.getBoolean("permission");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return permission;
	}
	
	private Place getTeleport(String port)
	{
		Place place = null;
		int x=0;
		int y=0;
		int z=0;
		String world="";
		String welcomeMessage = "";
		String type = "";
		int owner_id = 0;
		int id = 0;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT num_id,name,welcome_message,x,y,z,world,type,owner_id FROM `mnc_places` WHERE `name` = ? LIMIT 1");)
		{ 
			stat.setString(1, port);
			ResultSet result = stat.executeQuery();
			if(result.next())
			{
				welcomeMessage = result.getString("welcome_message");
				x = result.getInt("x");
				y = result.getInt("y");
				z = result.getInt("z");
				port = result.getString("name");
				world = result.getString("world");
				type = result.getString("type");
				owner_id = result.getInt("owner_id");
				id=result.getInt("num_id");
				place = new Place(id,port,x,y,z,type,owner_id,welcomeMessage,world);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return place;
	}

	public boolean addTeleport(String portName,String portOwner,int x,int y, int z,String world, String type)
	{
		//TODO: port type is not checked adequately
		switch(type)
		{
			case "vip":
			case "public":
			case "private":
				break;
			default:
				return false;
		}
		boolean success = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("INSERT INTO `mnc_places` (name,owner_id,x,y,z,type,world) SELECT ?,`id`,?,?,?,?,? FROM `mnc_users` WHERE username_clean=?");)
		{ 
			stat.setString(1, portName.toLowerCase());
			stat.setString(7, portOwner.toLowerCase());
			stat.setInt(2, x);
			stat.setInt(3, y);
			stat.setInt(4, z);
			stat.setString(5, type);
			stat.setString(6, world);
			success = stat.executeUpdate()==1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Deletes teleport 
	 * @param portName
	 * @return true on success, false on failure
	 */
	public boolean removeTeleport(String portName)
	{
		boolean success = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("DELETE `mnc_places`, `mnc_places_permissions` FROM `mnc_places` LEFT JOIN `mnc_places_permissions` ON `mnc_places`.`name`=`mnc_places_permissions`.`place_id` WHERE `mnc_places`.`name` = ?");)
		{
			stat.setString(1, portName.toLowerCase());
			success = stat.executeUpdate()>0;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Note: this function is dumb
	 * @param portName
	 * @param playerName
	 * @return
	 */
	public boolean addTeleportAccess(String portName, String playerName)
	{
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("INSERT IGNORE INTO `mnc_places_permissions` (user_id,place_id) (SELECT u.id,p.name FROM mnc_users u, mnc_places p WHERE p.name=? AND u.username_clean=? LIMIT 1)");)
		{
			stat.setString(1, portName.toLowerCase());
			stat.setString(2, playerName.toLowerCase());
			return stat.executeUpdate()==1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @param portName
	 * @param playerName
	 * @return
	 */
	public boolean removeTeleportAccess(String portName, String playerName)
	{
		boolean success = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("DELETE FROM `mnc_places_permissions` WHERE `user_id` = (SELECT `id` FROM `mnc_users` WHERE username_clean=?) AND `place_id`=?");)
		{
			stat.setString(1, playerName.toLowerCase());
			stat.setString(2, portName.toLowerCase());
			success = stat.executeUpdate()==1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return success;
	}

	public ArrayList<Place> getPlacesByOwner(String owner)
	{
		ArrayList<Place> places = new ArrayList<Place>();
		int x=0;
		int y=0;
		int z=0;
		String world="";
		String welcomeMessage = "";
		String type = "";
		String name = "";
		int owner_id = 0;
		int id = 0;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT num_id,name,welcome_message,x,y,z,world,type,owner_id FROM `mnc_places` WHERE owner_id = (SELECT `id` FROM mnc_users WHERE username_clean = ? LIMIT 1)");)
		{
			stat.setString(1, owner.toLowerCase());
			ResultSet result = stat.executeQuery();
			while(result.next())
			{
				welcomeMessage = result.getString("welcome_message");
				x = result.getInt("x");
				y = result.getInt("y");
				z = result.getInt("z");
				name = result.getString("name");
				world = result.getString("world");
				type = result.getString("type");
				owner_id = result.getInt("owner_id");
				id = result.getInt("num_id"); 
				places.add(new Place(id,name,x,y,z,type,owner_id,welcomeMessage,world));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return places;
	}

	public boolean isOwner(String portName, String name)
	{
		int id = plugin.userManager.getUserId(name);
		int count=0;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT count(*) as count FROM `mnc_places` WHERE `owner_id` = ? AND `name`=?");)
		{
			stat.setInt(1,id);
			stat.setString(2,portName);
			ResultSet res = stat.executeQuery();
			if(res.next())
			{
				count = res.getInt("count");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return count > 0;
	}

	public boolean setWelcomeMessage(String portName, String message)
	{
		boolean success = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("UPDATE `mnc_places` SET welcome_message = ? WHERE `name`=? LIMIT 1");)
		{
			stat.setString(2, portName.toLowerCase());
			stat.setString(1, message);
			success = stat.executeUpdate()==1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return success;
	}

	
	public ArrayList<Place> getAccessiblePlaces(String playerName)
	{
		ArrayList<Place> places = new ArrayList<Place>();
		int x=0;
		int y=0;
		int z=0;
		String world="";
		String welcomeMessage = "";
		String type = "public";
		String name = "";
		int owner_id=0;
		int num_id = 0;
		PreparedStatement stat = null;
		try{
			stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT mnc_places.* FROM mnc_places WHERE mnc_places.type = 'public' OR `mnc_places`.`owner_id` = ? " +
" UNION SELECT mnc_places.* FROM mnc_places WHERE mnc_places.type = 'vip' AND (SELECT true FROM mnc_vip WHERE user_id = ? LIMIT 1) " +
" UNION SELECT mnc_places.* FROM mnc_places LEFT JOIN mnc_places_permissions ON mnc_places.name = mnc_places_permissions.place_id WHERE mnc_places.type = 'private' AND `mnc_places_permissions`.`user_id` = ?");
			int id = plugin.userManager.getUserId(playerName);
			stat.setInt(1, id);
			stat.setInt(2, id);
			stat.setInt(3, id);
			ResultSet result = stat.executeQuery();
			while(result.next())
			{
				welcomeMessage = result.getString("welcome_message");
				x = result.getInt("x");
				y = result.getInt("y");
				z = result.getInt("z");
				name = result.getString("name");
				world = result.getString("world");
				type = result.getString("type");
				owner_id = result.getInt("owner_id");
				id = result.getInt("num_id");
				places.add(new Place(num_id,name,x,y,z,type,owner_id,welcomeMessage,world));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(stat!=null)
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return places;
	}

	public ArrayList<Place> listAllPlaces()
	{
		ArrayList<Place> places = new ArrayList<Place>();
		int x=0;
		int y=0;
		int z=0;
		String world="";
		String welcomeMessage = "";
		String type = "";
		String name = "";
		int owner_id = 0;
		int id = 0;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("SELECT num_id,name,welcome_message,x,y,z,world,type,owner_id FROM `mnc_places`;");)
		{
			ResultSet result = stat.executeQuery();
			while(result.next())
			{
				welcomeMessage = result.getString("welcome_message");
				x = result.getInt("x");
				y = result.getInt("y");
				z = result.getInt("z");
				name = result.getString("name");
				world = result.getString("world");
				type = result.getString("type");
				owner_id = result.getInt("owner_id");
				id = result.getInt("num_id"); 
				places.add(new Place(id,name,x,y,z,type,owner_id,welcomeMessage,world));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return places;
	}

	public ArrayList<String> listTeleportAccess(String name)
	{
		ArrayList<String> players = new ArrayList<String>();
		try(PreparedStatement stat = plugin.dbConfig.getConection().prepareStatement("SELECT u.username as username FROM `mnc_places` p JOIN `mnc_places_permissions` pp ON p.name = pp.place_id JOIN `mnc_users` u ON pp.user_id=u.id WHERE p.name = ?");)
		{
		    stat.setString(1, name.toLowerCase());
		    ResultSet result = stat.executeQuery();
		    while(result.next())
		    {
		    	players.add(result.getString("username"));
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return players;
	}

	public boolean isTeleportPrivate(String name)
	{
		Place x = this.getTeleport(name);
		if(x!=null)
			return x.isPrivate();
		return false;
	}

	public boolean modifyTeleport(String name, String newname, String owner, String type){
		//TODO: port type is not checked adequately
		switch(type)
		{
			case "vip":
			case "public":
			case "private":
				break;
			default:
				return false;
		}
		boolean success = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("UPDATE `mnc_places` SET `name` = ?, owner_id = ?, type = ? WHERE `name` = ? LIMIT 1;");)
		{
			stat.setString(1, newname.toLowerCase());
			stat.setInt(2, plugin.userManager.getUserId(owner));
			stat.setString(3, type);
			stat.setString(4, name);
			success = stat.executeUpdate()==1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return success;
	}
	
	public boolean moveTeleport(String name,int x, int y, int z, String world)
	{
		boolean success = false;
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("UPDATE `mnc_places` SET x = ?, y = ?, z = ?, world = ? WHERE name = ? LIMIT 1;");)
		{
			stat.setString(5, name.toLowerCase());
			stat.setInt(1, x);
			stat.setInt(2, y);
			stat.setInt(3, z);
			stat.setString(4, world);
			success = stat.executeUpdate()==1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return success;
	}

	
	public boolean modifyTeleportType(String name, String type) {
		//TODO: port type is not checked adequately
		switch(type)
		{
			case "vip":
			case "public":
			case "private":
				break;
			default:
				return false;
		}
		try(PreparedStatement stat = this.plugin.dbConfig.getConection().prepareStatement("UPDATE `mnc_places` SET type = ? WHERE `name` = ? LIMIT 1;");)
		{	
			stat.setString(1, type);
			stat.setString(2, name);
			return stat.executeUpdate()==1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

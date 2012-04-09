package me.Guga.Guga_SERVER_MOD;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class GugaPhysics 
{
	GugaPhysics(Guga_SERVER_MOD gugaSM)
	{
		plugin = gugaSM;
	}
	
	public void TrapDoorFlow(Block trapDoor)
	{
		int blockData = trapDoor.getData();
		Block blockAbove = trapDoor.getRelative(BlockFace.UP);
		Block blockBelow = trapDoor.getRelative(BlockFace.DOWN);
		if ( (blockAbove.getTypeId() == 8) || (blockAbove.getTypeId() == 9) )
		{
			
			if (blockData >= 4)
			{
				if ( (blockBelow.getTypeId() == 0) || (blockAbove.getTypeId() == 8) || (blockAbove.getTypeId() == 9))
				{
					blockBelow.setTypeId(0);
				}
			}
			else
			{
				blockBelow.setTypeId(9);
			}
		}
		else if ( (blockAbove.getTypeId() == 10) || (blockAbove.getTypeId() == 11) )
		{
			if (blockData >= 4)
			{
				if ( (blockBelow.getTypeId() == 0) || (blockAbove.getTypeId() == 10) || (blockAbove.getTypeId() == 11))
				{
					blockBelow.setTypeId(0);
				}
			}
			else
			{
				blockBelow.setTypeId(11);
			}
		}
	}
	public void DoorFlow(Block door)
	{
			boolean debug = false;
		
			Integer doorHash;
			int blockData = door.getData();
		// *********************************UPPER BLOCK*********************************
			if (blockData >= 8)
			{
				blockData -= 8;
				Location doorLoc = door.getLocation();
				doorLoc.setY(doorLoc.getY()-1);
				door = door.getWorld().getBlockAt(doorLoc);
			}
			doorHash = door.hashCode();
			if (blockData >= 4)
			{
				blockData -= 4;
				if (blockData == 3 || blockData == 1)
				{
					Block blockOne = door.getRelative(BlockFace.WEST);
					Block blockTwo = door.getRelative(BlockFace.EAST);
					int typeOne = blockOne.getTypeId();
					int typeTwo = blockTwo.getTypeId();
					if (typeOne == 9 || typeOne == 8)
					{
						if (typeTwo == 9 || typeTwo == 8)
						{	
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							if (blockFlow[i] == true)
							{
								blockTwo.setTypeId(0);
							}
							else
							{
								blockOne.setTypeId(0);
							}
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeOne == 10 || typeOne == 11)
					{
						if (typeTwo == 10 || typeTwo == 11)
						{	
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							if (blockFlow[i] == true)
							{
								blockTwo.setTypeId(0);
							}
							else
							{
								blockOne.setTypeId(0);
							}
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
				}
				else
				{
					Block blockOne = door.getRelative(BlockFace.NORTH);
					Block blockTwo = door.getRelative(BlockFace.SOUTH);
					int typeOne = blockOne.getTypeId();
					int typeTwo = blockTwo.getTypeId();
					if (typeOne == 9 || typeOne == 8)
					{
						if (typeTwo == 9 || typeTwo == 8)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							if (blockFlow[i] == true)
							{
								blockTwo.setTypeId(0);
							}
							else
							{
								blockOne.setTypeId(0);
							}
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeOne == 10 || typeOne == 11)
					{
						if (typeTwo == 10 || typeTwo == 11)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							if (blockFlow[i] == true)
							{
								blockTwo.setTypeId(0);
							}
							else
							{
								blockOne.setTypeId(0);
							}
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
				}
			}
			// *********************************OPENING*********************************
			else
			{
				if (blockData == 3 || blockData == 1)
				{
					Block blockOne = door.getRelative(BlockFace.WEST);
					Block blockTwo = door.getRelative(BlockFace.EAST);
					int typeOne = blockOne.getTypeId();
					int typeTwo = blockTwo.getTypeId();
					if (typeOne == 9 || typeOne == 8)
					{
						if (typeTwo == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = true;
							doorCode[i] = doorHash;
							blockTwo.setTypeId(9);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeTwo == 9 || typeTwo == 8)
					{
						if (typeOne == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = false;
							doorCode[i] = doorHash;
							blockOne.setTypeId(9);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeOne == 10 || typeOne == 11)
					{
						if (typeTwo == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = true;
							doorCode[i] = doorHash;
							blockTwo.setTypeId(11);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeTwo == 10 || typeTwo == 11)
					{
						if (typeOne == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = false;
							doorCode[i] = doorHash;
							blockOne.setTypeId(11);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
				}
				else
				{
					Block blockOne = door.getRelative(BlockFace.NORTH);
					Block blockTwo = door.getRelative(BlockFace.SOUTH);
					int typeOne = blockOne.getTypeId();
					int typeTwo = blockTwo.getTypeId();
					if (typeOne == 9 || typeOne == 8)
					{
						if (typeTwo == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = true;
							doorCode[i] = doorHash;
							blockTwo.setTypeId(9);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeTwo == 9 || typeTwo == 8)
					{
						if (typeOne == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = false;
							doorCode[i] = doorHash;
							blockOne.setTypeId(9);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
							
						}
					}
					else if (typeOne == 10 || typeOne == 11)
					{
						if (typeTwo == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = true;
							doorCode[i] = doorHash;
							blockTwo.setTypeId(11);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
						}
					}
					else if (typeTwo == 10 || typeTwo == 11)
					{
						if (typeOne == 0)
						{
							int i = 0;
							while (doorCode[i] != null)
							{
								if (doorCode[i].intValue() == doorHash.intValue())
								{
									break;
								}
								i++;
							}
							blockFlow[i] = false;
							doorCode[i] = doorHash;
							blockOne.setTypeId(11);
							if (debug == true)
							{
								plugin.log.info("DEBUG_MODE: i=" + i + ",doorCode=" + doorCode[i] + ",doorHash=" + doorHash+",blockFlow=" + blockFlow[i] + ",doorData=" + blockData);
							}
							
						}
					}
				}
			}
		}

	public void DoorFlowOld(Block door)
	{
			int blockData = door.getData();
			if (blockData >= 8)
			{
				blockData -= 8;
				Location doorLoc = door.getLocation();
				doorLoc.setY(doorLoc.getY()-1);
				door = door.getWorld().getBlockAt(doorLoc);
				// *********************************CLOSING*********************************
				if (blockData >= 4)
				{
					blockData -= 4;
					if (blockData == 3 || blockData == 1)
					{
						Block blockOne = door.getRelative(BlockFace.WEST);
						Block blockTwo = door.getRelative(BlockFace.EAST);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 9 || typeTwo == 8)
							{
								int dataOne = blockOne.getData();
								int dataTwo = blockTwo.getData();
								if (dataOne<dataTwo)
								{
									blockTwo.setTypeId(0);
								}
								else
								{
									blockOne.setTypeId(0);
								}
							}
						}
					}
					else
					{
						Block blockOne = door.getRelative(BlockFace.NORTH);
						Block blockTwo = door.getRelative(BlockFace.SOUTH);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 9 || typeTwo == 8)
							{
								int dataOne = blockOne.getData();
								int dataTwo = blockTwo.getData();
								if (dataOne<dataTwo)
								{
									blockTwo.setTypeId(0);
								}
								else
								{
									blockOne.setTypeId(0);
								}
							}
						}
					}
				}
				// *********************************OPENING*********************************
				else
				{
					if (blockData == 3 || blockData == 1)
					{
						Block blockOne = door.getRelative(BlockFace.WEST);
						Block blockTwo = door.getRelative(BlockFace.EAST);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 0)
							{
								int dataOne = blockOne.getData();
								int newVal = dataOne+1;		
								blockTwo.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
						else if (typeTwo == 9 || typeTwo == 8)
						{
							if (typeOne == 0)
							{
								int dataTwo = blockTwo.getData();
								int newVal = dataTwo+1;
								blockOne.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
					}
					else
					{
						Block blockOne = door.getRelative(BlockFace.NORTH);
						Block blockTwo = door.getRelative(BlockFace.SOUTH);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 0)
							{
								int dataOne = blockOne.getData();
								int newVal = dataOne+1;
								blockTwo.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
						else if (typeTwo == 9 || typeTwo == 8)
						{
							if (typeOne == 0)
							{
								int dataTwo = blockTwo.getData();
								int newVal = dataTwo+1;
								blockOne.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
					}
				}
			}
			else
			{
				if (blockData >= 4)
				{
					blockData -= 4;
					if (blockData == 3 || blockData == 1)
					{
						Block blockOne = door.getRelative(BlockFace.WEST);
						Block blockTwo = door.getRelative(BlockFace.EAST);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 9 || typeTwo == 8)
							{
								int dataOne = blockOne.getData();
								int dataTwo = blockTwo.getData();
								if (dataOne<dataTwo)
								{
									blockTwo.setTypeId(0);
								}
								else
								{
									blockOne.setTypeId(0);
								}
							}
						}
					}
					else
					{
						Block blockOne = door.getRelative(BlockFace.NORTH);
						Block blockTwo = door.getRelative(BlockFace.SOUTH);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 9 || typeTwo == 8)
							{
								int dataOne = blockOne.getData();
								int dataTwo = blockTwo.getData();
								if (dataOne<dataTwo)
								{
									blockTwo.setTypeId(0);
								}
								else
								{
									blockOne.setTypeId(0);
								}
							}
						}
					}
				}
				// *********************************OPENING*********************************
				else
				{
					if (blockData == 3 || blockData == 1)
					{
						Block blockOne = door.getRelative(BlockFace.WEST);
						Block blockTwo = door.getRelative(BlockFace.EAST);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 0)
							{
								int dataOne = blockOne.getData();
								int newVal = dataOne+1;		
								blockTwo.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
						else if (typeTwo == 9 || typeTwo == 8)
						{
							if (typeOne == 0)
							{
								int dataTwo = blockTwo.getData();
								int newVal = dataTwo+1;
								blockOne.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
					}
					else
					{
						Block blockOne = door.getRelative(BlockFace.NORTH);
						Block blockTwo = door.getRelative(BlockFace.SOUTH);
						int typeOne = blockOne.getTypeId();
						int typeTwo = blockTwo.getTypeId();
						if (typeOne == 9 || typeOne == 8)
						{
							if (typeTwo == 0)
							{
								int dataOne = blockOne.getData();
								int newVal = dataOne+1;
								blockTwo.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
						else if (typeTwo == 9 || typeTwo == 8)
						{
							if (typeOne == 0)
							{
								int dataTwo = blockTwo.getData();
								int newVal = dataTwo+1;
								blockOne.setTypeIdAndData(9, Byte.parseByte(Integer.toString(newVal)),true);
							}
						}
					}
				}
			}	
		}
	public final Integer doorCode[] = new Integer[150];
	public final boolean blockFlow[] = new boolean[150]; // true = blockOne, false = blockTwo;
	public static Guga_SERVER_MOD plugin;
	
}

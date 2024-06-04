package me.Jared;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlaceableWall
{
	FileConfiguration config = WallsMain.getInstance().getConfig();

	private Material block1 = Material.RED_WOOL;
	private Material block2 = Material.GLASS;

	private int wallHealth = 10;

	private void setWallCount(int count)
	{	
		if(getTotalWallIndex().isEmpty())
		{
			config.set("wallCount", 0);
			WallsMain.getInstance().saveConfig();
		}

		ArrayList<Integer> numbers = new ArrayList<Integer>();

		if(config.getConfigurationSection("walls") != null)
		{
			for(String num : config.getConfigurationSection("walls").getKeys(false))
			{
				int number = Integer.parseInt(num);
				numbers.add(number);
			}

			if(numbers.contains(count))
			{
				config.set("wallCount", Collections.max(numbers) + 1);
				WallsMain.getInstance().saveConfig();
			}
			else
			{
				config.set("wallCount", count);
				WallsMain.getInstance().saveConfig();
			}
		}
	}

	public int getWallCount()
	{
		return config.getInt("wallCount");
	}

	public void setWallHealth(int wall, int health)
	{
		config.set("walls." + wall + ".health", health);
		WallsMain.getInstance().saveConfig();
	}

	public int getWallHealth(int wall)
	{
		return config.getInt("walls." + wall + ".health");
	}

	private void setWallLocation(Location location, int wall)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		config.set("walls." + getWallCount() + ".world", location.getWorld().getName());
		config.set("walls." + getWallCount() + "." + wall + ".x", x);
		config.set("walls." + getWallCount() + "." + wall + ".y",y);
		config.set("walls." + getWallCount() + "." + wall + ".z",z);
		
		WallsMain.getInstance().saveConfig();
	}

	public ArrayList<Location> getWallLocation(int wall)
	{
		ArrayList<Location> locList = new ArrayList<Location>();

		for(int i = 0; i < 9; i++)
		{
			String world = config.getString("walls." + wall + ".world");
			double x = config.getDouble("walls." + wall + "." + i + ".x");
			double y = config.getDouble("walls." + wall + "." + i + ".y");
			double z = config.getDouble("walls." + wall + "." + i + ".z");

			Location loc = new Location(Bukkit.getWorld(world),x,y,z);
			locList.add(loc);
		}
		return locList;
	}

	public ArrayList<Integer> getTotalWallIndex()
	{
		ArrayList<Integer> totalWalls = new ArrayList<Integer>();

		if(config.getConfigurationSection("walls") != null)
		{
			for(String num : config.getConfigurationSection("walls").getKeys(false))
			{
				totalWalls.add(Integer.parseInt(num));
			}
		}
		return totalWalls;
	}

	public ArrayList<Location> getWallIndex(int wall)
	{
		ArrayList<Location> wallLocations = new ArrayList<Location>();

		for(String num : config.getConfigurationSection("walls." + wall).getKeys(false))
		{
			String world = config.getString("walls." + wall + ".world");
			double x = config.getDouble("walls." + wall + "." + num + ".x");
			double y = config.getDouble("walls." + wall + "." + num + ".y");
			double z = config.getDouble("walls." + wall + "." + num + ".z");

			Location location = new Location(Bukkit.getWorld(world),x,y,z);
			wallLocations.add(location);
		}
		return wallLocations;
	}

	public void hurtWall(Location location, int hurtAmount)
	{
		for(int walls : getTotalWallIndex())
		{
			if(getWallIndex(walls).contains(location))
			{				
				setWallHealth(walls, getWallHealth(walls) - 1);
				WallsMain.getInstance().saveConfig();

				if(getWallHealth(walls) <= 1)
				{
					if(getTotalWallIndex().size() == 1)
					{
						removeAllWalls();
						break;
					}
					else
					{
						removeWall(location);
					}
				}
				break;
			}
		}
	}

	public void removeWallLocation(int wall)
	{
		for(Location location : getWallLocation(wall))
		{
			location.getBlock().setType(Material.AIR);
		}

		config.set("walls." + wall, null);
		setWallCount(getWallCount() - 1);
		WallsMain.getInstance().saveConfig();
	}

	public void removeWall(Location location)
	{
		for(int walls : getTotalWallIndex())
		{
			if(getWallIndex(walls).contains(location))
			{
				if(getTotalWallIndex().size() == 1)
				{
					removeAllWalls();
					break;
				}

				removeWallLocation(walls);
				break;
			}
		}
	}

	public void removeAllWalls()
	{
		for(int wall : getTotalWallIndex())
		{
			removeWallLocation(wall);
		}
		setWallCount(0);
	}

	public ArrayList<Location> getAllWalls()
	{
		ArrayList<Location> locList = new ArrayList<Location>();
		for(int wallCount = 0; wallCount <= getWallCount(); wallCount++)
		{
			for(Location location : getWallLocation(wallCount))
			{
				locList.add(location);
			}
		}

		return locList;
	}

	public ItemStack getWall(int amount)
	{
		ItemStack wall = new ItemStack(Material.BRICKS, amount);
		ItemMeta im = wall.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Placeable Wall");
		wall.setItemMeta(im);
		return wall;
	}

	// Placing wall if it is true the player is facing north or south and if it is
	// false it is east or west
	public void placeWall(Player player, Location location)
	{
		ArrayList<Location> locList = new ArrayList<Location>();

		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		Location blockPlaced = new Location(location.getWorld(),x,y,z);

		//NORTH or SOUTH
		Location northSouthLoc1 = new Location(location.getWorld(),x,y+1,z);
		Location northSouthLoc2 = new Location(location.getWorld(),x,y+2,z);
		Location northSouthLoc3 = new Location(location.getWorld(),x-1,y,z);
		Location northSouthLoc4 = new Location(location.getWorld(),x+1,y,z);
		Location northSouthLoc5 = new Location(location.getWorld(),x+1,y+1,z);
		Location northSouthLoc6 = new Location(location.getWorld(),x+1,y+2,z);
		Location northSouthLoc7 = new Location(location.getWorld(),x-1,y+1,z);
		Location northSouthLoc8 = new Location(location.getWorld(),x-1,y+2,z);

		locList.add(northSouthLoc1);
		locList.add(northSouthLoc2);
		locList.add(northSouthLoc3);
		locList.add(northSouthLoc4);
		locList.add(northSouthLoc5);
		locList.add(northSouthLoc6);
		locList.add(northSouthLoc7);
		locList.add(northSouthLoc8);

		// EAST OR WEST
		Location eastWestLoc1 = new Location(location.getWorld(),x,y+1,z);
		Location eastWestLoc2 = new Location(location.getWorld(),x,y+2,z);
		Location eastWestLoc3 = new Location(location.getWorld(),x,y,z-1);
		Location eastWestLoc4 = new Location(location.getWorld(),x,y,z+1);
		Location eastWestLoc5 = new Location(location.getWorld(),x,y+1,z+1);
		Location eastWestLoc6 = new Location(location.getWorld(),x,y+2,z+1);
		Location eastWestLoc7 = new Location(location.getWorld(),x,y+1,z-1);
		Location eastWestLoc8 = new Location(location.getWorld(),x,y+2,z-1);

		locList.add(eastWestLoc1);
		locList.add(eastWestLoc2);
		locList.add(eastWestLoc3);
		locList.add(eastWestLoc4);
		locList.add(eastWestLoc5);
		locList.add(eastWestLoc6);
		locList.add(eastWestLoc7);
		locList.add(eastWestLoc8);

		// NORTHWEST or SOUTHEAST
		Location NWSEloc1 = new Location(location.getWorld(),x-1,y,z+1);
		Location NWSEloc2 = new Location(location.getWorld(),x+1,y,z-1);
		Location NWSEloc3 = new Location(location.getWorld(),x,y+1,z);
		Location NWSEloc4 = new Location(location.getWorld(),x-1,y+1,z+1);
		Location NWSEloc5 = new Location(location.getWorld(),x+1,y+1,z-1);
		Location NWSEloc6 = new Location(location.getWorld(),x,y+2,z);
		Location NWSEloc7 = new Location(location.getWorld(),x-1,y+2,z+1);
		Location NWSEloc8 = new Location(location.getWorld(),x+1,y+2,z-1);

		locList.add(NWSEloc1);
		locList.add(NWSEloc2);
		locList.add(NWSEloc3);
		locList.add(NWSEloc4);
		locList.add(NWSEloc5);
		locList.add(NWSEloc6);
		locList.add(NWSEloc7);
		locList.add(NWSEloc8);

		// NORTHEAST or SOUTHWEST
		Location NESWloc1 = new Location(location.getWorld(),x-1,y,z-1);
		Location NESWloc2 = new Location(location.getWorld(),x+1,y,z+1);
		Location NESWloc3 = new Location(location.getWorld(),x,y+1,z);
		Location NESWloc4 = new Location(location.getWorld(),x-1,y+1,z-1);
		Location NESWloc5 = new Location(location.getWorld(),x+1,y+1,z+1);
		Location NESWloc6 = new Location(location.getWorld(),x,y+2,z);
		Location NESWloc7 = new Location(location.getWorld(),x-1,y+2,z-1);
		Location NESWloc8 = new Location(location.getWorld(),x+1,y+2,z+1);

		locList.add(NESWloc1);
		locList.add(NESWloc2);
		locList.add(NESWloc3);
		locList.add(NESWloc4);
		locList.add(NESWloc5);
		locList.add(NESWloc6);
		locList.add(NESWloc7);
		locList.add(NESWloc8);

		for(Location loc : locList)
		{
			switch(loc.getBlock().getType())
			{
			case AIR:
				break;
			case LIGHT:
				break;
			default:
				player.sendMessage(ChatColor.RED + "Something in the way...");
				location.getBlock().setType(Material.AIR);
				player.getInventory().setItem(player.getInventory().getHeldItemSlot(), getWall(player.getInventory().getItemInMainHand().getAmount()));
				return;
			}
		}

		location.getWorld().getBlockAt(blockPlaced).setType(block1);

		if (getPlayerDirection(player) == Direction.NORTH 
				|| getPlayerDirection(player) == Direction.SOUTH)
		{
			// North south axis
			location.getWorld().getBlockAt(blockPlaced).setType(block1);
			location.getWorld().getBlockAt(northSouthLoc1).setType(block2);
			location.getWorld().getBlockAt(northSouthLoc2).setType(block1);
			location.getWorld().getBlockAt(northSouthLoc3).setType(block1);
			location.getWorld().getBlockAt(northSouthLoc4).setType(block1);
			location.getWorld().getBlockAt(northSouthLoc5).setType(block2);
			location.getWorld().getBlockAt(northSouthLoc6).setType(block1);
			location.getWorld().getBlockAt(northSouthLoc7).setType(block2);
			location.getWorld().getBlockAt(northSouthLoc8).setType(block1);

			setWallCount(getWallCount() + 1);
			setWallHealth(getWallCount(), wallHealth);
			setWallLocation(blockPlaced, 0);
			setWallLocation(northSouthLoc1, 1);
			setWallLocation(northSouthLoc2, 2);
			setWallLocation(northSouthLoc3, 3);
			setWallLocation(northSouthLoc4, 4);
			setWallLocation(northSouthLoc5, 5);
			setWallLocation(northSouthLoc6, 6);
			setWallLocation(northSouthLoc7, 7);
			setWallLocation(northSouthLoc8, 8);
		}

		else if(getPlayerDirection(player) == Direction.EAST 
				|| getPlayerDirection(player) == Direction.WEST)
		{
			//East west axis
			location.getWorld().getBlockAt(blockPlaced).setType(block1);
			location.getWorld().getBlockAt(eastWestLoc1).setType(block2);
			location.getWorld().getBlockAt(eastWestLoc2).setType(block1);
			location.getWorld().getBlockAt(eastWestLoc3).setType(block1);
			location.getWorld().getBlockAt(eastWestLoc4).setType(block1);
			location.getWorld().getBlockAt(eastWestLoc5).setType(block2);
			location.getWorld().getBlockAt(eastWestLoc6).setType(block1);
			location.getWorld().getBlockAt(eastWestLoc7).setType(block2);
			location.getWorld().getBlockAt(eastWestLoc8).setType(block1);

			setWallCount(getWallCount() + 1);
			setWallHealth(getWallCount(), wallHealth);
			setWallLocation(blockPlaced, 0);
			setWallLocation(eastWestLoc1, 1);
			setWallLocation(eastWestLoc2, 2);
			setWallLocation(eastWestLoc3, 3);
			setWallLocation(eastWestLoc4, 4);
			setWallLocation(eastWestLoc5, 5);
			setWallLocation(eastWestLoc6, 6);
			setWallLocation(eastWestLoc7, 7);
			setWallLocation(eastWestLoc8, 8);
		}

		else if(getPlayerDirection(player) == Direction.NORTHWEST 
				|| getPlayerDirection(player) == Direction.SOUTHEAST)
		{
			location.getWorld().getBlockAt(blockPlaced).setType(block1);
			location.getWorld().getBlockAt(NWSEloc1).setType(block1);
			location.getWorld().getBlockAt(NWSEloc2).setType(block1);
			location.getWorld().getBlockAt(NWSEloc3).setType(block2);
			location.getWorld().getBlockAt(NWSEloc4).setType(block2);
			location.getWorld().getBlockAt(NWSEloc5).setType(block2);
			location.getWorld().getBlockAt(NWSEloc6).setType(block1);
			location.getWorld().getBlockAt(NWSEloc7).setType(block1);
			location.getWorld().getBlockAt(NWSEloc8).setType(block1);

			setWallCount(getWallCount() + 1);
			setWallHealth(getWallCount(), wallHealth);
			setWallLocation(blockPlaced, 0);
			setWallLocation(NWSEloc1, 1);
			setWallLocation(NWSEloc2, 2);
			setWallLocation(NWSEloc3, 3);
			setWallLocation(NWSEloc4, 4);
			setWallLocation(NWSEloc5, 5);
			setWallLocation(NWSEloc6, 6);
			setWallLocation(NWSEloc7, 7);
			setWallLocation(NWSEloc8, 8);
		}
		else if(getPlayerDirection(player) == Direction.NORTHEAST 
				|| getPlayerDirection(player) == Direction.SOUTHWEST)
		{
			location.getWorld().getBlockAt(blockPlaced).setType(block1);
			location.getWorld().getBlockAt(NESWloc1).setType(block1);
			location.getWorld().getBlockAt(NESWloc2).setType(block1);
			location.getWorld().getBlockAt(NESWloc3).setType(block2);
			location.getWorld().getBlockAt(NESWloc4).setType(block2);
			location.getWorld().getBlockAt(NESWloc5).setType(block2);
			location.getWorld().getBlockAt(NESWloc6).setType(block1);
			location.getWorld().getBlockAt(NESWloc7).setType(block1);
			location.getWorld().getBlockAt(NESWloc8).setType(block1);

			setWallCount(getWallCount() + 1);
			setWallHealth(getWallCount(), wallHealth);
			setWallLocation(blockPlaced, 0);
			setWallLocation(NESWloc1, 1);
			setWallLocation(NESWloc2, 2);
			setWallLocation(NESWloc3, 3);
			setWallLocation(NESWloc4, 4);
			setWallLocation(NESWloc5, 5);
			setWallLocation(NESWloc6, 6);
			setWallLocation(NESWloc7, 7);
			setWallLocation(NESWloc8, 8);
		}
	}

	enum Direction
	{
		NORTH,SOUTH,EAST,WEST,NORTHWEST,NORTHEAST,SOUTHWEST,SOUTHEAST;
	}

	public Direction getPlayerDirection(Player player)
	{
		if(player.getLocation().getYaw() > -155 
				&& player.getLocation().getYaw() < -115)
		{
			return Direction.NORTHEAST;
		}
		else if(player.getLocation().getYaw() > -65 
				&& player.getLocation().getYaw() < -25)
		{
			return Direction.SOUTHEAST;
		}
		else if(player.getLocation().getYaw() > 115 
				&& player.getLocation().getYaw() < 155)
		{
			return Direction.NORTHWEST;
		}
		else if(player.getLocation().getYaw() > 25 
				&& player.getLocation().getYaw() < 65)
		{
			return Direction.SOUTHWEST;
		}

		if (player.getLocation().getYaw() > -135 && player.getLocation().getYaw() < -45)
		{
			return Direction.EAST;
		} else if (player.getLocation().getYaw() > -45 && player.getLocation().getYaw() < 45)
		{
			return Direction.SOUTH;
		} else if (player.getLocation().getYaw() > 45 && player.getLocation().getYaw() < 135)
		{
			return Direction.WEST;
		} else
		{
			return Direction.NORTH;
		}
	}
}
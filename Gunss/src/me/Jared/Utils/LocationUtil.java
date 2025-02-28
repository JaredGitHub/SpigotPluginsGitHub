/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.ItemStack
 */
package me.Jared.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.Jared.Interfaces.IUser;

public class LocationUtil
{
	public static final Set<Material> HOLLOW_MATERIALS = new HashSet<Material>();
	private static final Set<Material> TRANSPARENT_MATERIALS = new HashSet<Material>();
	public static final int RADIUS = 3;
	public static final Vector3D[] VOLUME;

	static
	{
		HOLLOW_MATERIALS.add(Material.AIR);
		HOLLOW_MATERIALS.add(Material.OAK_SAPLING);
		HOLLOW_MATERIALS.add(Material.POWERED_RAIL);
		HOLLOW_MATERIALS.add(Material.DETECTOR_RAIL);
		HOLLOW_MATERIALS.add(Material.TALL_GRASS);
		HOLLOW_MATERIALS.add(Material.DEAD_BUSH);
		HOLLOW_MATERIALS.add(Material.DANDELION);
		HOLLOW_MATERIALS.add(Material.POPPY);
		HOLLOW_MATERIALS.add(Material.BROWN_MUSHROOM);
		HOLLOW_MATERIALS.add(Material.RED_MUSHROOM);
		HOLLOW_MATERIALS.add(Material.TORCH);
		HOLLOW_MATERIALS.add(Material.REDSTONE_WIRE);
		HOLLOW_MATERIALS.add(Material.WHEAT_SEEDS);
		HOLLOW_MATERIALS.add(Material.OAK_SIGN);
		HOLLOW_MATERIALS.add(Material.OAK_DOOR);
		HOLLOW_MATERIALS.add(Material.LADDER);
		HOLLOW_MATERIALS.add(Material.RAIL);
		HOLLOW_MATERIALS.add(Material.OAK_WALL_SIGN);
		HOLLOW_MATERIALS.add(Material.LEVER);
		HOLLOW_MATERIALS.add(Material.STONE_PRESSURE_PLATE);
		HOLLOW_MATERIALS.add(Material.IRON_DOOR);
		HOLLOW_MATERIALS.add(Material.OAK_PRESSURE_PLATE);
		HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH);
		HOLLOW_MATERIALS.add(Material.STONE_BUTTON);
		HOLLOW_MATERIALS.add(Material.SNOW);
		HOLLOW_MATERIALS.add(Material.SUGAR_CANE);
		HOLLOW_MATERIALS.add(Material.REPEATER);
		HOLLOW_MATERIALS.add(Material.PUMPKIN_STEM);
		HOLLOW_MATERIALS.add(Material.MELON_STEM);
		HOLLOW_MATERIALS.add(Material.VINE);
		HOLLOW_MATERIALS.add(Material.OAK_FENCE_GATE);
		HOLLOW_MATERIALS.add(Material.LILY_PAD);
		HOLLOW_MATERIALS.add(Material.NETHER_WART);
		HOLLOW_MATERIALS.add(Material.WHITE_CARPET);
		TRANSPARENT_MATERIALS.add(Material.WATER);
		ArrayList<Vector3D> pos = new ArrayList<Vector3D>();
		int x = -3;
		while (x <= 3)
		{
			int y = -3;
			while (y <= 3)
			{
				int z = -3;
				while (z <= 3)
				{
					pos.add(new Vector3D(x, y, z));
					++z;
				}
				++y;
			}
			++x;
		}
		pos.sort(Comparator.comparingInt(a -> a.x * a.x + a.y * a.y + a.z * a.z));
		VOLUME = pos.toArray(new Vector3D[0]);
	}

	public static ItemStack convertBlockToItem(Block block)
	{
		ItemStack is = new ItemStack(block.getType());
		switch (is.getType())
		{
		case OAK_DOOR:
		{
			is.setType(Material.OAK_DOOR);
			break;
		}
		case IRON_DOOR:
		{
			is.setType(Material.IRON_DOOR);
			break;
		}
		case OAK_SIGN:
		case OAK_WALL_SIGN:
		{
			is.setType(Material.OAK_SIGN);
			break;
		}
		case WHEAT_SEEDS:
		{
			is.setType(Material.WHEAT_SEEDS);
			break;
		}
		case CAKE:
		{
			is.setType(Material.CAKE);
			break;
		}
		case RED_BED:
		{
			is.setType(Material.RED_BED);
			break;
		}
		case REDSTONE_WIRE:
		{
			is.setType(Material.REDSTONE);
			break;
		}
		case REDSTONE_TORCH:
		{
			is.setType(Material.REDSTONE_TORCH);
			break;
		}
		case REPEATER:
		{
			is.setType(Material.REPEATER);
			break;
		}
		case TORCH:
		case FURNACE:
		case LADDER:
		case COBBLESTONE_STAIRS:
		case PUMPKIN:
		case JACK_O_LANTERN:
		case DISPENSER:
		case LEVER:
		case STONE_BUTTON:
		case RAIL:
		case STICKY_PISTON:
		case PISTON_HEAD:
		case OAK_STAIRS:
		case STONE_PRESSURE_PLATE:
		case OAK_PRESSURE_PLATE:
		case OAK_FENCE:
		case OAK_TRAPDOOR:
		case IRON_BARS:
		case GLASS_PANE:
		case OAK_FENCE_GATE:
		case NETHER_BRICK_FENCE:
		{
			break;
		}
		case FIRE:
		{
			return null;
		}
		case PUMPKIN_STEM:
		{
			is.setType(Material.PUMPKIN_SEEDS);
			break;
		}
		case MELON_STEM:
		{
			is.setType(Material.MELON_SEEDS);
			break;
		}
		default:
			break;
		}
		return is;
	}

	public static Location getTarget(LivingEntity entity) throws Exception
	{
		Block block = entity.getTargetBlock(TRANSPARENT_MATERIALS, 300);
		if (block == null)
		{
			throw new Exception("Not targeting a block");
		}
		return block.getLocation();
	}

	static boolean isBlockAboveAir(World world, int x, int y, int z)
	{
		if (y > world.getMaxHeight())
		{
			return true;
		}
		return HOLLOW_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType());
	}

	public static boolean isBlockUnsafeForUser(IUser user, World world, int x, int y, int z)
	{
		if (user.getBase().isOnline() && world.equals(user.getBase().getWorld())
				&& user.getBase().getGameMode() == GameMode.CREATIVE && user.getBase().getAllowFlight())
		{
			return false;
		}
		if (LocationUtil.isBlockDamaging(world, x, y, z))
		{
			return true;
		}
		return LocationUtil.isBlockAboveAir(world, x, y, z);
	}

	public static boolean isBlockUnsafe(World world, int x, int y, int z)
	{
		if (LocationUtil.isBlockDamaging(world, x, y, z))
		{
			return true;
		}
		return LocationUtil.isBlockAboveAir(world, x, y, z);
	}

	public static boolean isBlockDamaging(World world, int x, int y, int z)
	{
		Block below = world.getBlockAt(x, y - 1, z);
		if (below.getType() == Material.LAVA || below.getType() == Material.LAVA)
		{
			return true;
		}
		if (below.getType() == Material.FIRE)
		{
			return true;
		}
		if (below.getType() == Material.RED_BED)
		{
			return true;
		}
		return !HOLLOW_MATERIALS.contains(world.getBlockAt(x, y, z).getType())
				|| !HOLLOW_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType());
	}

	public static Location getRoundedDestination(Location loc)
	{
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = (int) Math.round(loc.getY());
		int z = loc.getBlockZ();
		return new Location(world, (double) x + 0.5, (double) y, (double) z + 0.5, loc.getYaw(), loc.getPitch());
	}

	public static Location getSafeDestination(IUser user, Location loc) throws Exception
	{
		if (user.getBase().isOnline() && loc.getWorld().equals(user.getBase().getWorld())
				&& user.getBase().getGameMode() == GameMode.CREATIVE && user.getBase().getAllowFlight())
		{
			if (LocationUtil.shouldFly(loc))
			{
				user.getBase().setFlying(true);
			}
			return LocationUtil.getRoundedDestination(loc);
		}
		return LocationUtil.getSafeDestination(loc);
	}

	public static Location getSafeDestination(Location loc)
	{
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = (int) Math.round(loc.getY());
		int z = loc.getBlockZ();
		int origX = x;
		int origY = y;
		int origZ = z;
		while (LocationUtil.isBlockAboveAir(world, x, y, z))
		{
			if (--y >= 0)
				continue;
			y = origY;
			break;
		}
		if (LocationUtil.isBlockUnsafe(world, x, y, z))
		{
			x = Math.round(loc.getX()) == (long) origX ? x - 1 : x + 1;
			z = Math.round(loc.getZ()) == (long) origZ ? z - 1 : z + 1;
		}
		int i = 0;
		while (LocationUtil.isBlockUnsafe(world, x, y, z))
		{
			if (++i >= VOLUME.length)
			{
				x = origX;
				y = origY + 3;
				z = origZ;
				break;
			}
			x = origX + LocationUtil.VOLUME[i].x;
			y = origY + LocationUtil.VOLUME[i].y;
			z = origZ + LocationUtil.VOLUME[i].z;
		}
		while (LocationUtil.isBlockUnsafe(world, x, y, z))
		{
			if (++y < world.getMaxHeight())
				continue;
			++x;
			break;
		}
		while (LocationUtil.isBlockUnsafe(world, x, y, z))
		{
			if (--y > 1)
				continue;
			y = world.getHighestBlockYAt(++x, z);
			if (x - 48 <= loc.getBlockX())
				continue;
			return loc.getWorld().getSpawnLocation();
		}
		return new Location(world, (double) x + 0.5, (double) y, (double) z + 0.5, loc.getYaw(), loc.getPitch());
	}

	public static boolean shouldFly(Location loc)
	{
		int y;
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		int count = 0;
		for (y = (int) Math.round(loc.getY()); LocationUtil.isBlockUnsafe(world, x, y, z) && y > -1; --y)
		{
			if (++count <= 2)
				continue;
			return true;
		}
		return y < 0;
	}

	public static Location getLocationFromString(String s)
	{
		String[] pieces = s.split(",");
		Location returner = new Location(Bukkit.getWorld((String) pieces[0]), Double.parseDouble(pieces[1]),
				Double.parseDouble(pieces[2]), Double.parseDouble(pieces[3]), Float.parseFloat(pieces[4]),
				Float.parseFloat(pieces[5]));
		return LocationUtil.getSafeDestination(returner);
	}

	public static Location getLocationFromStringClean(String s)
	{
		String[] pieces = s.split(",");
		Location returner = new Location(Bukkit.getWorld((String) pieces[0]), Double.parseDouble(pieces[1]),
				Double.parseDouble(pieces[2]), Double.parseDouble(pieces[3]), Float.parseFloat(pieces[4]),
				Float.parseFloat(pieces[5]));
		return returner;
	}

	public static String getStringFromLocation(Location loc)
	{
		return String.valueOf(loc.getWorld().getName()) + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ","
				+ loc.getYaw() + "," + loc.getPitch();
	}

	public static class Vector3D
	{
		public int x;
		public int y;
		public int z;

		public Vector3D(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}

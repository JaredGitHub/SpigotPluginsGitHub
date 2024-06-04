package me.Jared.WarzRunnable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.Warz;
import me.Jared.Loot.ConfigItem;

public class SetChestsRunnable extends BukkitRunnable
{
	private FileConfiguration config = Warz.getInstance().getConfig();

	private void saveConfig()
	{
		Warz.getInstance().saveConfig();
	}

	private String region;
	private String zoneString;
	private World world;
	private Player player;
	private int chestIterations;
	private int iterations;

	public SetChestsRunnable(String zone, String region, World world, Player player, int chestsPerTick)
	{
		this.region = region;
		this.zoneString = zone;
		this.world = world;
		this.player = player;
		this.chestIterations = chestsPerTick;

		this.iterations = 0;
	}

	@Override
	public void run()
	{
		ArrayList<String> chestList = new ArrayList<String>(config.getStringList("chests"));
		ArrayList<Location> blockLocations = new ArrayList<Location>(getChestLocationsInRegion(region, world));
		var configItem = new ConfigItem();

		for(int i = 0; i < chestIterations; i++)
		{
			if(iterations >= blockLocations.size())
			{
				player.sendMessage(ChatColor.GREEN + "DONE!");
				this.cancel();
				return;
			}
			var block = blockLocations.get(iterations).getBlock();
			
			// If chest is already assigned a zone in the config
			for(int idx = 0; idx < chestList.size(); idx++)
			{
				if(block.getX() == configItem.getChestX(chestList.get(idx))
						&& block.getY() == configItem.getChestY(chestList.get(idx))
						&& block.getZ() == configItem.getChestZ(chestList.get(idx)))
				{
					player.sendMessage(ChatColor.RED + "Removing duplicate!");
					chestList.remove(idx);
				}
			}
			chestList.add(block.getX() 
					+ ":" + block.getY() 
					+ ":" + block.getZ() + ":" 
					+ zoneString.toUpperCase() + ":");

			player.sendMessage(ChatColor.GREEN + "You have set this chest to zone " + zoneString.toUpperCase() + " at "
					+ "X: " + block.getX() + " Y: " + block.getY() + " Z: " + block.getZ());

			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
			config.set("chests", chestList);
			saveConfig();

			iterations++;
		}
	}

	private List<Location> getChestLocationsInRegion(String regionName, World world)
	{
		List<Location> blockLocations = new ArrayList<>();
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if(!(plugin instanceof WorldGuardPlugin))
		{
			// WorldGuard plugin not found
			return blockLocations;
		}

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

		if(regionManager != null)
		{
			ProtectedRegion region = regionManager.getRegion(regionName);
			if(region != null)
			{
				int minX = region.getMinimumPoint().x();
				int minY = region.getMinimumPoint().y();
				int minZ = region.getMinimumPoint().z();
				int maxX = region.getMaximumPoint().x();
				int maxY = region.getMaximumPoint().y();
				int maxZ = region.getMaximumPoint().z();

				for(int x = minX; x <= maxX; x++)
				{
					for(int y = minY; y <= maxY; y++)
					{
						for(int z = minZ; z <= maxZ; z++)
						{
							Location blockLocation = new Location(world, x, y, z);
							if(blockLocation.getBlock().getType() == Material.CHEST)
							{
								blockLocations.add(blockLocation);
							}
						}
					}
				}
			}
		}
		return blockLocations;
	}

}

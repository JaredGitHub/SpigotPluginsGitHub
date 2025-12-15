package me.Jared.WarzRunnable;

import java.util.ArrayList;
import java.util.Iterator;
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
		ArrayList<String> chestList = new ArrayList<>(config.getStringList("chests." + world.getName()));
		ArrayList<Location> blockLocations = new ArrayList<>(getChestLocationsInRegion(region, world));
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
			Iterator<String> iterator = chestList.iterator();
			while (iterator.hasNext()) {
				String chest = iterator.next();

				Location loc = new Location(world,
						configItem.getChestX(chest),
						configItem.getChestY(chest),
						configItem.getChestZ(chest)
				);

				// Ensure chunk is loaded
				loc.getChunk().load();

				// If no chest exists here anymore, remove
				if (!(loc.getBlock().getType() == Material.CHEST
				|| loc.getBlock().getType() == Material.BARREL)) {
					player.sendMessage(ChatColor.RED + "Removing Non Chest Location!");
					player.playSound(player.getLocation(), Sound.ENTITY_CAT_DEATH, 1, 1);
					iterator.remove();
					continue;
				}

				// If the new block matches an existing config entry → remove duplicate
				if (loc.equals(block.getLocation())) {
					player.sendMessage(ChatColor.RED + "Removing duplicate!");
					player.playSound(player.getLocation(), Sound.ENTITY_GHAST_DEATH, 1, 1);
					iterator.remove();
				}
			}


			chestList.add(
					block.getX() + ":" + block.getY() + ":" + block.getZ() + ":" + zoneString.toUpperCase() + ":");

			player.sendMessage(
					ChatColor.GREEN + "You have set this chest to zone " + zoneString.toUpperCase() + " at " + "X: "
							+ block.getX() + " Y: " + block.getY() + " Z: " + block.getZ());

			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
			config.set("chests." + world.getName(), chestList);
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
							if(blockLocation.getBlock().getType() == Material.CHEST
							||blockLocation.getBlock().getType() == Material.BARREL)
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

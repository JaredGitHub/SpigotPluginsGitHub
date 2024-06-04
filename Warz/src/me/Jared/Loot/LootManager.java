package me.Jared.Loot;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.Warz;
import me.Jared.WarzRunnable.LootRunnable;

public class LootManager
{
	private FileConfiguration config;

	public LootManager()
	{
		this.config = Warz.getInstance().getConfig();
	}

	private Block isDoubleChest(Block block)
	{
		if(!(block.getState() instanceof Chest))
		{
			return null; // Not a chest block
		}

		Block neighborBlock = getNeighborChestBlock(block);

		if(neighborBlock != null && neighborBlock.getState() instanceof Chest)
		{
			return neighborBlock;
		}
		else
		{
			return null;
		}
	}

	private Block getNeighborChestBlock(Block block)
	{
		// Check if the block is part of a double chest by checking its neighbors
		BlockFace[] facesToCheck =
			{ BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

		for(BlockFace face : facesToCheck)
		{
			Block relativeBlock = block.getRelative(face);
			if(relativeBlock.getState() instanceof Chest)
			{
				return relativeBlock;
			}
		}
		return null;
	}

	public void setItems(Zone zone, Block block)
	{
		var configItem = new ConfigItem();

		if(Warz.getChestLocations().contains(block.getLocation()))
		{
			ArrayList<String> items = new ArrayList<String>(configItem.zoneListItems(zone));
			Block otherBlock = isDoubleChest(block);

			Location location = block.getLocation();
			Chest chest = (Chest) block.getState();

			Random rand = new Random();
			int numberOfElements = 2;

			chest.getBlockInventory().clear();

			for(int i = 0; i < numberOfElements; i++)
			{
				int randomIndex = rand.nextInt(0, items.size());
				String randomElement = items.get(randomIndex);

				ItemStack item = configItem.stringToItemStack(randomElement);

				chest.getBlockInventory().setItem(i, item);
				Warz.getChestLocations().remove(location);
			}

			if(otherBlock != null)
			{
				location = otherBlock.getLocation();
				chest = (Chest) otherBlock.getState();
				rand = new Random();

				chest.getBlockInventory().clear();

				for(int i = 0; i < numberOfElements; i++)
				{
					int randomIndex = Math.abs(rand.nextInt(items.size()));
					String randomElement = items.get(randomIndex);

					ItemStack item = configItem.stringToItemStack(randomElement);

					chest.getBlockInventory().setItem(i, item);
					Warz.getChestLocations().remove(location);
				}
			}
		}
	}


	private void addChestsToArray(Zone zone)
	{
		ConfigItem configItem = new ConfigItem();
		for(String locString : configItem.zoneListChests(zone))
		{
			Location location = configItem.getChestLocation(locString);

			Warz.getChestLocations().add(location);
		}
	}

	public void setChests()
	{
		Warz.getChestLocations().clear();
		Warz.getOpenChestLocations().clear();
		addChestsToArray(Zone.LOW);
		addChestsToArray(Zone.MEDIUM);
		addChestsToArray(Zone.HIGH);
		addChestsToArray(Zone.SKYHIGH);
		setOpenChests();
	}

	private void setOpenChests()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player.getOpenInventory().getType() == InventoryType.CHEST)
			{
				if(player.getOpenInventory().getTitle().equals("Chest"))
				{
					String region = getRegion(player.getLocation());
					Zone zone = getZoneFromRegion(region);
					Block block =  player.getOpenInventory().getTopInventory().getLocation().getBlock();

					setItems(zone, block);
				}
			}
		}
	}

	public Zone getZoneFromRegion(String region)
	{
		String zone = config.getString("towns." + region).toUpperCase();

		switch(zone)
		{
		case "LOW":
			return Zone.LOW;
		case "MEDIUM":
			return Zone.MEDIUM;
		case "HIGH":
			return Zone.HIGH;
		case "SKYHIGH":
			return Zone.SKYHIGH;
		default:
			return null;
		}
	}

	public String getRegion(Location location)
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		String region = "";

		if(!(plugin instanceof WorldGuardPlugin))
		{
			// WorldGuard plugin not found
			return null;
		}

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

		BlockVector3 playerLoc = BlockVector3.at(location.getX(), location.getY(),
				location.getZ());
		for(String regions : regionManager.getApplicableRegionsIDs(playerLoc))
		{
			region = regions;
		}
		return region;
	}

	public void runLootRunnable(int seconds)
	{
		var lootRunnable = new LootRunnable(seconds);
		lootRunnable.runTaskTimer(Warz.getInstance(), 0, 20);
	}
}

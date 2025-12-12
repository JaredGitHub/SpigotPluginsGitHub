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
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.Boosters;
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
		} else
		{
			return null;
		}
	}

	private Block getNeighborChestBlock(Block block)
	{
		// Check if the block is part of a double chest by checking its neighbors
		BlockFace[] facesToCheck = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

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

	private boolean setChest(Block block, Zone zone)
	{
		ConfigItem configItem = new ConfigItem();
		Random random = new Random();
		// Getting the list of items based on the zone
		ArrayList<String> items = new ArrayList<>(configItem.zoneListItems(zone));

		Location location = block.getLocation();
		Chest chest = (Chest) block.getState();
		// Clear the chest b4 putting items into it
		chest.getBlockInventory().clear();

		// Getting the total weight of all of the items in the list
		int totalWeight = items.stream().mapToInt(configItem::getWeight).sum();

		// Making an array of randomSlots so that we can check if the item tries to go
		// into the same slot and prevent it!
		ArrayList<Integer> usedSlots = new ArrayList<Integer>();

		// Instead of two make it find the number in the config "DoubleLoot"

		int itemsPerChest = Boosters.getInstance().getConfig().get("DoubleLoot").equals(true) ? 4 : 2;

		for(int itemNumber = 0; itemNumber < itemsPerChest; itemNumber++)
		{
			// Get a random number based on the total weight of the items
			int totalWeightRandom = random.nextInt(totalWeight);
			// Initialize a running total of the weight
			int currentWeight = 0;

			//While usedSlots contains the random chest slot find another random number
			int randNumChestSlot;
			do
			{
				randNumChestSlot = random.nextInt(27);
			} while(usedSlots.contains(randNumChestSlot));
			usedSlots.add(randNumChestSlot);

			for(int i = 0; i < items.size(); i++)
			{

				currentWeight += configItem.getWeight(items.get(i));

				if(totalWeightRandom < currentWeight)
				{
					chest.getBlockInventory()
							.setItem(randNumChestSlot, configItem.stringToItemStackWithLore(items.get(i)));

					Warz.getChestLocations().remove(location);
					break;
				}
			}
		}

		Block otherBlock = isDoubleChest(block);
		if(otherBlock != null)
		{
			return true;
		}
		return false;
	}

	public void setItems(Zone zone, Block block)
	{

		if(Warz.getChestLocations().contains(block.getLocation()))
		{
			if(setChest(block, zone))
			{
				Block otherBlock = isDoubleChest(block);
				setChest(otherBlock, zone);
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
					Block block = player.getOpenInventory().getTopInventory().getLocation().getBlock();

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

		BlockVector3 playerLoc = BlockVector3.at(location.getX(), location.getY(), location.getZ());
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

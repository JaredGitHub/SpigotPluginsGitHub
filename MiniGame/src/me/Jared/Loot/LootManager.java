package me.Jared.Loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import me.Jared.MiniGame;

public class LootManager
{

	private static Block isDoubleChest(Block block)
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

	private static Block getNeighborChestBlock(Block block)
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

	private static boolean setChest(Block block, Tier tier)
	{
		Random random = new Random();
		// Getting the list of items based on the tier
		List<String> items = MiniGame.getInstance().getConfig().getStringList("items");

		Location location = block.getLocation();
		Chest chest = (Chest) block.getState();
		// Clear the chest b4 putting items into it
		chest.getBlockInventory().clear();

		// Getting the total weight of all of the items in the list
		int totalWeight = items.stream().mapToInt(ConfigItem::getWeight).sum();

		// Making an array of randomSlots so that we can check if the item tries to go
		// into the same slot and prevent it!
		ArrayList<Integer> usedSlots = new ArrayList<Integer>();

		// Instead of two make it find the number in the config "DoubleLoot"
		int itemsPerChest = 4;

		for(int itemNumber = 0; itemNumber < itemsPerChest; itemNumber++)
		{
			// Get a random number based on the total weight of the items
			int totalWeightRandom = random.nextInt(totalWeight+1);
			// Initialize a running total of the weight
			int currentWeight = 0;

			//While usedSlots contains the random chest slot find another random number
			int randNumChestSlot;
			do
			{
				randNumChestSlot = random.nextInt(27);
			}while(usedSlots.contains(randNumChestSlot));
			usedSlots.add(randNumChestSlot);

			for(int i = 0; i < items.size(); i++)
			{

				currentWeight += ConfigItem.getWeight(items.get(i));

				if(totalWeightRandom < currentWeight)
				{
					chest.getBlockInventory().setItem(randNumChestSlot,
							ConfigItem.stringToItemStackWithLore(items.get(i)));

					MiniGame.getChestLocations().remove(location);
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

	public static void setItems(Tier tier, Block block)
	{
		if(MiniGame.getChestLocations().contains(block.getLocation()))
		{
			if(setChest(block, tier))
			{
				Block otherBlock = isDoubleChest(block);
				setChest(otherBlock, tier);
			}
		}
	}

	private void addChestsToArray(Tier tier)
	{
		for(String locString : ConfigItem.tierListChests(tier))
		{
			Location location = ConfigItem.getChestLocation(locString);

			MiniGame.getChestLocations().add(location);
		}
	}

	public void setChests()
	{
		addChestsToArray(Tier.LOW);
		addChestsToArray(Tier.MEDIUM);
		addChestsToArray(Tier.HIGH);
		addChestsToArray(Tier.SKYHIGH);
	}
}

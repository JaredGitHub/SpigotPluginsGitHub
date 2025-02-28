package me.Jared;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.util.ArrayList;
import java.util.Random;

public class LootManager
{
	public boolean setChest(Block block) {
		Random random = new Random();
		ArrayList<String> items = new ArrayList<>(Main.getInstance().getConfig().getStringList("items"));

		Location location = block.getLocation();
		Chest chest = (Chest) block.getState();
		chest.getBlockInventory().clear();

		ArrayList<Integer> usedSlots = new ArrayList<>();
		int itemsPerChest = Math.min(10, items.size()); // Ensure we don't exceed available unique items

		for (int itemNumber = 0; itemNumber < itemsPerChest; itemNumber++) {
			int totalWeight = items.stream().mapToInt(ConfigItem::getWeight).sum();
			int totalWeightRandom = random.nextInt(totalWeight);
			int currentWeight = 0;

			int randNumChestSlot;
			do {
				randNumChestSlot = random.nextInt(27);
			} while (usedSlots.contains(randNumChestSlot));
			usedSlots.add(randNumChestSlot);

			for (int i = 0; i < items.size(); i++) {
				currentWeight += ConfigItem.getWeight(items.get(i));

				if (totalWeightRandom < currentWeight) {
					chest.getBlockInventory().setItem(randNumChestSlot,
							ConfigItem.stringToItemStackWithLore(items.get(i)));
					items.remove(i); // Remove the item to ensure uniqueness
					break;
				}
			}
		}
		return true;
	}
}

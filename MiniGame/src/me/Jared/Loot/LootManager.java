package me.Jared.Loot;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.Jared.Manager.GameManager;

public class LootManager
{
	private FileConfiguration config;
	private ArrayList<String> chestList;

	public LootManager(GameManager gameManager)
	{
		this.config = gameManager.getConfig();

		chestList = new ArrayList<String>(config.getStringList("chests"));
	}

	private void setItems(Tier tier)
	{
		ConfigItem configItem = new ConfigItem();
		for(String chests : configItem.tierListChests(tier))
		{
			Block block = Bukkit.getWorld("world").getBlockAt(configItem.getChestLocation(chests));
			BlockState state = block.getState();
			if(state instanceof Chest)
			{
				Chest chest = (Chest) state;

				Random rand = new Random();
				int numberOfElements = 4;

				ArrayList<String> items = new ArrayList<String>(configItem.tierListItems(tier));

				for(int i = 0; i < numberOfElements; i++)
				{
					int randomIndex = Math.abs(rand.nextInt(items.size()));
					String randomElement = items.get(randomIndex);

					ItemStack item = configItem.stringToItemStack(randomElement);

					chest.getBlockInventory().setItem(i, item);
				}
			}
		}
	}

	public void setChests()
	{
		clearChests();
		ConfigItem configItem = new ConfigItem();
		for(String chest : chestList)
		{
			Tier tier = configItem.getChestTier(chest);
			switch(tier)
			{
			case LOW:
				setItems(Tier.LOW);
				break;
			case MEDIUM:
				setItems(Tier.MEDIUM);
				break;
			case HIGH:
				setItems(Tier.HIGH);
				break;
			case SKYHIGH:
				setItems(Tier.SKYHIGH);
				break;
			default:
				break;
			}
		}
	}

	public void clearChests()
	{
		ConfigItem configItem = new ConfigItem();
		for(String chests : new ArrayList<String>(config.getStringList("chests")))
		{
			Block block = Bukkit.getWorld("world").getBlockAt(configItem.getChestLocation(chests));
			BlockState state = block.getState();

			if(state instanceof Chest)
			{
				Chest chest = (Chest) state;

				chest.getBlockInventory().clear();
			}
		}
	}
}

package me.Jared;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class VaultUtil
{
	private static FileConfiguration config = PersonalVault.getInstance().getConfig();

	public static void saveInventory(Player player, Inventory inventory, String world)
	{
		ConfigItem configItem = new ConfigItem();

		for(int i = 0; i < inventory.getSize(); i++)
		{
			if(inventory.getItem(i) == null)
			{
				config.set(player.getUniqueId() + ".inventory" + world + "." + i, null);
			}
			else
			{
				config.set(player.getUniqueId() + ".inventory" + world + "." + i, configItem.itemStackToStringWithLore(inventory.getItem(i)));
			}
		}

		PersonalVault.getInstance().saveConfig();
	}

	public static void loadInventory(Player player, Inventory inventory, String world)
	{
		ConfigItem configItem = new ConfigItem();

		String itemString;
		for(int i = 0; i < inventory.getSize(); i++)
		{
			itemString = player.getUniqueId() + ".inventory" + world + "." + i;
			if(config.getString(itemString) != null)
			{
				inventory.setItem(i, configItem.stringToItemStackWithLore(config.getString(itemString)));
			}
		}
	}
}

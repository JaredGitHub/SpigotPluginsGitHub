package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigManager
{
	public static void iconCreate(ItemStack item, String displayName, Inventory inv, int num)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		inv.setItem(num, item);

	}
	
	public static void leaveAllGames(Player player)
	{
		Bukkit.dispatchCommand(player, "tdm leave");
		Bukkit.dispatchCommand(player, "sg leave");
		Bukkit.dispatchCommand(player, "gg leave");
	}
}

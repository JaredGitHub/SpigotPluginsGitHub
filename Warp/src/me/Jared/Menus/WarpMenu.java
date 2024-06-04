package me.Jared.Menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.ConfigManager;
import me.Jared.MenuSystem.Menu;
import me.Jared.MenuSystem.PlayerMenuUtility;

public class WarpMenu extends Menu
{

	public WarpMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.DARK_GREEN + "                 WARPS";
	}

	@Override
	public int getSlots()
	{

		return 54;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		String warpName = e.getCurrentItem().getItemMeta().getDisplayName();
		Player player = (Player) e.getWhoClicked();
		
		Bukkit.dispatchCommand(player, "warp " + warpName);
		player.closeInventory();
	}

	@Override
	public void setMenuItems()
	{
		int i = 0;
		for(String warpName : ConfigManager.getWarps())
		{
			ConfigManager.iconCreate(new ItemStack(Material.ZOMBIE_HEAD), warpName.toUpperCase(), inventory, i);
			i++;
		}
	}

}

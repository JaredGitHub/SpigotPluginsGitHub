package me.Jared.Menus.CustomKit;

import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Duels;
import me.Jared.Manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChooseFoodOrWeapon extends DuelsMenu
{
	public ChooseFoodOrWeapon(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return "Food or Weapons?";
	}

	@Override
	public int getSlots()
	{
		return 9;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();

		if(e.getSlot() == 3)
			new ChooseWeapon(Duels.getPlayerMenuUtility(player)).open();
		if(e.getSlot() == 5)
			new ChooseFood(Duels.getPlayerMenuUtility(player)).open();
	}

	@Override
	public void setMenuItems()
	{
		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND_SWORD, 1), ChatColor.GREEN + "Weapons", inventory, 3);
		ConfigManager.iconCreate(new ItemStack(Material.COOKED_BEEF, 1), ChatColor.GREEN + "Food", inventory, 5);

	}
}

package me.Jared.Menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.ConfigManager;
import me.Jared.GameGUI;
import me.Jared.MenuSystem.Menu;
import me.Jared.MenuSystem.PlayerMenuUtility;

public class GameGUIMenu extends Menu
{
	protected PlayerMenuUtility playerMenuUtility;

	public GameGUIMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.DARK_AQUA + "           Choose a Game!";
	}

	@Override
	public int getSlots()
	{

		return 9;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		e.setCancelled(true);
		
		Player player = (Player) e.getWhoClicked();
		if(e.getSlot() == 4)
		{
			GameListMenu menu = new GameListMenu(GameGUI.getPlayerMenuUtility(player));
			menu.open();
		}
	}

	@Override
	public void setMenuItems()
	{
		for(int i = 0; i < 9; i++)
		{
			ConfigManager.iconCreate(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), " ", inventory, i);
		}
		
		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND), ChatColor.DARK_AQUA + "Choose a Game!", inventory, 4);
	}

}

package me.Jared.Menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.ConfigManager;
import me.Jared.GameGUI;
import me.Jared.MenuSystem.Menu;
import me.Jared.MenuSystem.PlayerMenuUtility;

public class TDMJoinGUI extends Menu
{
	protected PlayerMenuUtility playerMenuUtility;

	public TDMJoinGUI(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.GRAY + "Choose a team!";
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
		
		switch(e.getSlot())
		{
		case 3:
			ConfigManager.leaveAllGames(player);
			Bukkit.dispatchCommand(player, "tdm join 1");
			player.closeInventory();
			break;
		case 5:
			ConfigManager.leaveAllGames(player);
			Bukkit.dispatchCommand(player, "tdm join 2");
			player.closeInventory();
			break;
		case 0:
			ConfigManager.leaveAllGames(player);
			GameListMenu menu = new GameListMenu(GameGUI.getPlayerMenuUtility(player));
			menu.open();
		default:
			break;
		}
	}

	@Override
	public void setMenuItems()
	{
		ConfigManager.iconCreate(new ItemStack(Material.RED_WOOL), ChatColor.RED + "Join Team 1", inventory, 3);
		ConfigManager.iconCreate(new ItemStack(Material.BLUE_WOOL), ChatColor.BLUE + "Join Team 2", inventory, 5);
		ConfigManager.iconCreate(new ItemStack(Material.ARROW), ChatColor.GRAY + "Back", inventory, 0);
	}

}

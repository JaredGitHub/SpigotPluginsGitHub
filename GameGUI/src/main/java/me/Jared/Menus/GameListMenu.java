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

public class GameListMenu extends Menu
{

	public GameListMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.GREEN + "List of Games";
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

		switch(e.getSlot())
		{
		case 0:
			player.closeInventory();
			break;
		case 1:
			Bukkit.dispatchCommand(player, "warz");
			break;
		case 2:
			Bukkit.dispatchCommand(player, "warz2");
			break;
		case 3:

		case 4:
			Bukkit.dispatchCommand(player, "sg join");
			player.closeInventory();
			break;
		case 5:

			Bukkit.dispatchCommand(player, "gg join");
			break;

		case 6:
			player.closeInventory();
			TDMJoinGUI menu = new TDMJoinGUI(GameGUI.getPlayerMenuUtility(player));
			menu.open();
			break;
		case 7:
			player.closeInventory();
			PlayerListGUI menu1 = new PlayerListGUI(GameGUI.getPlayerMenuUtility(player), player);
			menu1.open();
			break;
		default:
			break;
		}
	}

	@Override
	public void setMenuItems()
	{
		ConfigManager.iconCreate(new ItemStack(Material.RED_WOOL), ChatColor.RED + "Exit", inventory, 0);

		ConfigManager.iconCreate(new ItemStack(Material.ZOMBIE_HEAD), ChatColor.DARK_GREEN + "WarZ (New Map)", inventory, 1);
		ConfigManager.iconCreate(new ItemStack(Material.ZOMBIE_HEAD), ChatColor.DARK_GREEN + "WarZ (Old Map)", inventory, 2);

		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND), ChatColor.GOLD + "Survival Games", inventory, 4);
		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND), ChatColor.GREEN + "Gun Game", inventory, 5);

		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND), ChatColor.RED + "Team Deathmatch", inventory, 6);
		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND), ChatColor.GOLD + "Duel", inventory, 7);

	}

}

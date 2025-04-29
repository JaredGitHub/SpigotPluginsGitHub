package me.Jared;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import me.Jared.MenuSystem.Menu;

public class GameGUIListener implements Listener
{

	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() == null) return;

		InventoryHolder holder = e.getInventory().getHolder();

		if(holder instanceof Menu)
		{
			if(e.getCurrentItem() == null) return;

			Menu menu = (Menu) holder;
			menu.handleMenu(e);

			if(e.getWhoClicked().getOpenInventory().getBottomInventory().getType().equals(InventoryType.PLAYER))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
}

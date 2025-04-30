package me.Jared.Kits;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import me.Jared.MenuSystem.KitsMenu;

public class EventListener implements Listener
{
	//Giving the players the kits
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() == null) return;

		InventoryHolder holder = e.getInventory().getHolder();
		
		if(holder instanceof KitsMenu)
		{
			if(e.getCurrentItem() == null) return;
			
			KitsMenu menu = (KitsMenu) holder;
			menu.handleMenu(e);
			
			if(e.getWhoClicked().getOpenInventory().getBottomInventory().getType().equals(InventoryType.PLAYER))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
}

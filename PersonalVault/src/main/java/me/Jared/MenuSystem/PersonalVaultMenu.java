package me.Jared.MenuSystem;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class PersonalVaultMenu implements InventoryHolder
{

	protected Inventory inventory;
	
	protected PlayerMenuUtility playerMenuUtility;
	
	public PersonalVaultMenu(PlayerMenuUtility playerMenuUtility)
	{
		this.playerMenuUtility = playerMenuUtility;
	}
	
	public abstract String getMenuName();
	
	public abstract int getSlots();
	
	public abstract void handleMenu(InventoryClickEvent e);
	
	public abstract void setMenuItems();
	
	public void open()
	{
		inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
		
		this.setMenuItems();
		
		playerMenuUtility.getOwner().openInventory(inventory);
	}
	
	@Override
	public Inventory getInventory()
	{
		return inventory;
	}
}

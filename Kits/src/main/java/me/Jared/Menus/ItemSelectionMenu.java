package me.Jared.Menus;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.Kits.Main;
import me.Jared.Managers.KitManager;
import me.Jared.MenuSystem.KitsMenu;
import me.Jared.MenuSystem.PlayerMenuUtility;
import net.md_5.bungee.api.ChatColor;

public class ItemSelectionMenu extends KitsMenu
{
	public static int slot;

	public static ItemStack item;

	public ItemSelectionMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	public String getMenuName()
	{
		return String.valueOf(ChatColor.BLUE) + "           Items Selection";
	}

	public int getSlots()
	{
		return 18;
	}

	public int getSlot()
	{
		return slot;
	}

	public ItemStack getSlotItem()
	{
		return item;
	}

	public void handleMenu(InventoryClickEvent e)
	{
		if (e.getClickedInventory().equals(getInventory()))
		{
			slot = e.getSlot();
			item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (e.getInventory() != p.getInventory())
			{
				ItemSelectionMenu2 itemSelectionMenu2 = new ItemSelectionMenu2(Main.getPlayerMenuUtility(p));
				itemSelectionMenu2.open();
			}
			switch (e.getSlot())
			{
			case 9:
				Main.getInstance().saveConfig();
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
				p.sendMessage(String.valueOf(ChatColor.GREEN) + "Saved hotbar setup!");
				SelectKitMenu menu = new SelectKitMenu(Main.getPlayerMenuUtility(p));
				menu.open();
				break;
			case 17:
				p.playSound(p.getLocation(), Sound.ENTITY_GHAST_DEATH, 1.0F, 0.1F);
				p.sendMessage(String.valueOf(ChatColor.RED) + "Discarded hotbar setup!");
				p.closeInventory();
				break;
			}
		}
	}

	public void setMenuItems()
	{
		SelectKitMenu menu = new SelectKitMenu(this.playerMenuUtility);
		if (Main.getInstance().getConfig()
				.get("PlayerUniqueID." + String.valueOf(menu.getPlayer().getUniqueId())) == null)
		{
			KitManager.defaultHotBar(inventory);
		} else
		{
			KitManager.inventoryCustomHotbar(this.inventory, menu.getPlayer().getUniqueId());
		}
		
		for (int slot = 0; slot < 9; slot++)
		{
			if (this.inventory.getItem(slot)==null)
				KitManager.iconCreate(new ItemStack(Material.GRAY_STAINED_GLASS_PANE),
						String.valueOf(ChatColor.GRAY) + "Click me!", this.inventory, slot);
		}
		KitManager.iconCreate(new ItemStack(Material.GREEN_STAINED_GLASS_PANE),
				String.valueOf(ChatColor.GREEN) + "Save", this.inventory, 9);
		KitManager.iconCreate(new ItemStack(Material.RED_STAINED_GLASS_PANE),
				String.valueOf(ChatColor.DARK_RED) + "Discard", this.inventory, 17);
	}
}

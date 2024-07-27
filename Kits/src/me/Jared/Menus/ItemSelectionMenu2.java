package me.Jared.Menus;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.Kits.ConfigItem;
import me.Jared.Kits.Main;
import me.Jared.Managers.KitManager;
import me.Jared.MenuSystem.KitsMenu;
import me.Jared.MenuSystem.PlayerMenuUtility;
import net.md_5.bungee.api.ChatColor;

public class ItemSelectionMenu2 extends KitsMenu
{
	public ItemSelectionMenu2(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	public String getMenuName()
	{
		return String.valueOf(ChatColor.BLUE) + "Select your item!";
	}

	public int getSlots()
	{
		return 36;
	}

	public void handleMenu(InventoryClickEvent e)
	{
		if (e.getClickedInventory().equals(getInventory()))
		{
			Player p = (Player) e.getWhoClicked();
			ItemSelectionMenu itemSelectionMenu = new ItemSelectionMenu(playerMenuUtility);
			FileConfiguration config = Main.getInstance().getConfig();
			ConfigItem configItem = new ConfigItem();
			ArrayList<ItemStack> configItems = new ArrayList<>();


			for (int i = 0; i < 9; i++)
			{
				if(config.getString("PlayerUniqueID." + String.valueOf(p.getUniqueId()) + "." + i) != null)
				{
					configItems.add(configItem.stringToItemStack(config.getString("PlayerUniqueID." + String.valueOf(p.getUniqueId()) + "." + i)));
				}
			}

			if(!configItems.contains(e.getCurrentItem()))
			{
				config.set("PlayerUniqueID." + String.valueOf(p.getUniqueId()) + "." + itemSelectionMenu.getSlot(), configItem.itemStackToString(e.getCurrentItem()));
				Main.getInstance().saveConfig();
				itemSelectionMenu.open();
			}
			else
			{
				for(int i = 0; i < configItems.size(); i++)
				{
					if(configItems.get(i).equals(e.getCurrentItem()))
					{
						if (configItems.contains(e.getCurrentItem()))
						{
							config.set("PlayerUniqueID." + String.valueOf(p.getUniqueId()) + "." + i,
									"");
							config.set("PlayerUniqueID." + String.valueOf(p.getUniqueId()) + "." + itemSelectionMenu.getSlot(),
									configItem.itemStackToString(e.getCurrentItem()));

							p.sendMessage(ChatColor.RED + "You cannot have two of the same items in your hotbar!");
							Main.getInstance().saveConfig();
							itemSelectionMenu.open();
						} 
					}
				}
			}
		}
	}

	public void setMenuItems()
	{
		KitManager.itemSelection(this.inventory);
	}
}

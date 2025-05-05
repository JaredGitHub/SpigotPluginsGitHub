package me.Jared.Menus;

import java.util.ArrayList;

import me.Jared.Manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.Duels;
import me.Jared.Command.DuelCommands;
import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MapMenu extends DuelsMenu
{
	protected PlayerMenuUtility playerMenuUtility;
	private Player player;

	public MapMenu(PlayerMenuUtility playerMenuUtility, Player player)
	{
		super(playerMenuUtility);
		this.player = player;
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.GREEN + "Pick a map!";
	}

	@Override
	public int getSlots()
	{
		return 27;
	}

	private static int mapNumber;
	public static int getMapNumber()
	{
		return mapNumber;
	}

	FileConfiguration config = Duels.getInstance().getConfig();

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		p.closeInventory();

		String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
		//dispatch command /duel <player> <bet> <itemname/mapname>
		Bukkit.dispatchCommand(p, "duel " + player.getName() + " 0 " + itemName);

	}

	@Override
	public void setMenuItems()
	{
		ArrayList<String> mapNames = new ArrayList<String>(config.getStringList("maps"));

		int i = 0;
		for(String mapName : mapNames)
		{
			iconCreate(new ItemStack(Material.SPAWNER), mapName, inventory, i);
			i++;
		}
	}

	//Icon creator
	private void iconCreate(ItemStack item, String displayName, Inventory inv, int num)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		inv.setItem(num, item);

	}

}

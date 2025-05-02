package me.Jared.Menus;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class MapMenu extends DuelsMenu
{
	protected PlayerMenuUtility playerMenuUtility;

	public MapMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
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
	
	public static ArrayList<Player> playersInDuel = new ArrayList<Player>();
	public static ArrayList<Player> playersToDuel = new ArrayList<Player>();
	
	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		playersToDuel.remove(DuelCommands.duelPlayer);
		playersInDuel.remove(p);
		p.closeInventory();

		String itemName = e.getCurrentItem().getItemMeta().getDisplayName();

		if(config.getStringList("maps").isEmpty())
		{
			p.sendMessage(ChatColor.RED + "There are no maps yet, add one with /duel set [mapName] [1 or 2]");
		}
		
		for(String mapName : config.getStringList("maps"))
		{
			if(itemName.equals(mapName))
			{
				playersToDuel.add(DuelCommands.duelPlayer);
				playersInDuel.add(p);

				KitMenu menu = new KitMenu(Duels.getPlayerMenuUtility(p));
				menu.open();
				
				mapNumber = config.getStringList("maps").indexOf(mapName);
			}
		}
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

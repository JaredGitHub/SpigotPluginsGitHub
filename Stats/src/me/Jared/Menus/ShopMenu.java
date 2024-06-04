package me.Jared.Menus;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.StatScoreboard;
import me.Jared.Stats;
import me.Jared.MenuSystem.PlayerMenuUtility;
import me.Jared.MenuSystem.StatsMenu;

public class ShopMenu extends StatsMenu
{

	private Stats stats = Stats.getPlugin(Stats.class);
	FileConfiguration config = stats.getConfig();

	public ShopMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return "Shop";
	}

	@Override
	public int getSlots()
	{
		return 36;
	}

	ConfigItem configItem = new ConfigItem();

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		e.setCancelled(true);

		Player player = (Player) e.getWhoClicked();

		if(e.getCurrentItem() == null) return;
		if(e.getCurrentItem().getItemMeta() == null) return;
		if(e.getCurrentItem().getItemMeta().getLore() == null) return;

		/*List of items from config*/ArrayList<String> itemList = new ArrayList<String>(config.getStringList("shopItems"));
		/* Lore of item clicked in shop */ArrayList<String> lore = new ArrayList<String>(e.getCurrentItem().getItemMeta().getLore());
		/* Price of item clicked in shop*/ int price = Integer.parseInt(lore.get(0).substring(9));		

		for(int i = 0; i < itemList.size(); i++)
		{
			Damageable damage = (Damageable) e.getCurrentItem().getItemMeta();
			
			if(damage.getDamage() == configItem.getDurability(itemList.get(i))
					&& e.getCurrentItem().getType() == configItem.getMaterial(itemList.get(i)))
			{
				int gems = config.getInt(player.getUniqueId() + ".gems");
				if(!(gems < price))
				{
					player.sendMessage(ChatColor.GREEN + "You bought " + e.getCurrentItem().getItemMeta().getDisplayName() + ChatColor.GREEN + " for " + price + " gems!");
					player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);


					ItemStack item = configItem.stringToItemStack(itemList.get(i));
					item.setItemMeta(null);
					
					ItemMeta meta = item.getItemMeta();

					meta.setDisplayName(configItem.getDisplayName(itemList.get(i)));
					item.setItemMeta(meta);

					player.getInventory().addItem(item);

					config.set(player.getUniqueId() + ".gems", gems - price);
					stats.saveConfig();
					
					new StatScoreboard(stats, player);
				}
				else
				{
					if(gems < 0) config.set(player.getUniqueId() + ".gems", 0);
					player.sendMessage(ChatColor.RED + "You do not have enough gems noob!");
					player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 1);

				}
			}
		}
	}

	@Override
	public void setMenuItems()
	{
		for(String str : config.getStringList("shopItems"))
		{
			inventory.addItem(configItem.stringToItemStack(str));
		}
	}
}

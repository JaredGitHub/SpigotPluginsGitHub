package me.Jared.Manager;

import java.util.UUID;

import me.Jared.Duels;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitManager
{

	static ConfigItem configItem = new ConfigItem();

	public static void diamondKit(Player p)
	{
		// Armor
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack helm = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);

		p.getInventory().setChestplate(chest);
		p.getInventory().setHelmet(helm);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);

		// Hotbar (slots 0-8)
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack rod = new ItemStack(Material.FISHING_ROD); // PvP rod tricking
		ItemStack bow = new ItemStack(Material.BOW);
		ItemStack arrows = new ItemStack(Material.ARROW, 32);
		ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE, 4); // substitute for golden heads
		ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET);
		ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET);
		ItemStack blocks = new ItemStack(Material.COBBLESTONE, 64);
		ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);

		p.getInventory().setItem(0, sword);
		p.getInventory().setItem(1, rod);
		p.getInventory().setItem(2, bow);
		p.getInventory().setItem(3, arrows);
		p.getInventory().setItem(4, goldenHead);
		p.getInventory().setItem(5, waterBucket);
		p.getInventory().setItem(6, lavaBucket);
		p.getInventory().setItem(7, blocks);
		p.getInventory().setItem(8, food);
	}


	public static void ironKit(Player p)
	{
		// Armor
		ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack helm = new ItemStack(Material.IRON_HELMET);
		ItemStack legs = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack boots = new ItemStack(Material.IRON_BOOTS);

		p.getInventory().setChestplate(chest);
		p.getInventory().setHelmet(helm);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);

		// Hotbar
		p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
		p.getInventory().setItem(1, new ItemStack(Material.FISHING_ROD));
		p.getInventory().setItem(2, new ItemStack(Material.BOW));
		p.getInventory().setItem(3, new ItemStack(Material.ARROW, 32));
		p.getInventory().setItem(4, new ItemStack(Material.GOLDEN_APPLE, 4));
		p.getInventory().setItem(5, new ItemStack(Material.WATER_BUCKET));
		p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET));
		p.getInventory().setItem(7, new ItemStack(Material.COBBLESTONE, 64));
		p.getInventory().setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
	}


	public static void chainmailKit(Player p)
	{
		// Armor
		ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemStack helm = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemStack legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);

		p.getInventory().setChestplate(chest);
		p.getInventory().setHelmet(helm);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);

		// Hotbar
		p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
		p.getInventory().setItem(1, new ItemStack(Material.FISHING_ROD));
		p.getInventory().setItem(2, new ItemStack(Material.BOW));
		p.getInventory().setItem(3, new ItemStack(Material.ARROW, 32));
		p.getInventory().setItem(4, new ItemStack(Material.GOLDEN_APPLE, 4));
		p.getInventory().setItem(5, new ItemStack(Material.WATER_BUCKET));
		p.getInventory().setItem(6, new ItemStack(Material.LAVA_BUCKET));
		p.getInventory().setItem(7, new ItemStack(Material.COBBLESTONE, 64));
		p.getInventory().setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
	}


	public static void playerCustomHotBar(Player p)
	{
		FileConfiguration config = Duels.getInstance().getConfig();
		UUID uuid = p.getUniqueId();
		Inventory inv = p.getInventory();

		for(int slot = 0; slot < 9; slot++)
		{
			// Set players individual hotbar from config "playerUniqueID.UUID.slotnumber"

			inv.setItem(slot, configItem.stringToItemStack(config.getString("PlayerUniqueID." + uuid + "." + slot)));
		}
	}

	public static void inventoryCustomHotbar(Inventory inv, UUID uuid)
	{
		FileConfiguration config = Duels.getInstance().getConfig();

		for(int slot = 0; slot < 9; slot++)
		{
			if(config.getString("PlayerUniqueID." + uuid + "." + slot) != null)
			{
				String itemString = config.getString("PlayerUniqueID." + uuid + "." + slot);

				ItemStack hotbarItems = configItem.stringToItemStack(itemString);
				inv.setItem(slot, hotbarItems);
			}
		}
	}

	public static void defaultHotBar(UUID uuid)
	{
		FileConfiguration config = Duels.getInstance().getConfig();

		for(int slot = 0; slot < 9; slot++)
		{
			String hotbarItems = config.getString("HotbarItems." + slot);
			config.set("PlayerUniqueID." + uuid + "." + slot, hotbarItems);
			Duels.getInstance().saveConfig();
		}
	}

	public static void defaultHotBar(Inventory inv)
	{

		FileConfiguration config = Duels.getInstance().getConfig();
		for(String str : config.getStringList("HotbarItems"))
		{
			inv.addItem(configItem.stringToItemStack(str));
		}
	}

	public static void itemSelection(Inventory inv)
	{
		FileConfiguration config = Duels.getInstance().getConfig();
		for(String str : config.getStringList("SelectItems"))
		{
			inv.addItem(configItem.stringToItemStack(str));
		}
	}


	// Icon creator
	public static void iconCreate(ItemStack item, String displayName, Inventory inv, int num)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		inv.setItem(num, item);

	}
}

package me.Jared.Managers;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.Kits.ConfigItem;
import me.Jared.Kits.Main;
import net.md_5.bungee.api.ChatColor;

public class KitManager
{

	public static void diamondKit(Player p)
	{
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack helm = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);

		p.getInventory().setChestplate(chest);
		p.getInventory().setHelmet(helm);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);

		if(Main.getInstance().getConfig().get("PlayerUniqueID." + p.getUniqueId()) == null)
		{
			defaultHotBar(p.getUniqueId());
		}
		KitManager.playerCustomHotBar(p);
		KitManager.giveAmmo(p);
	}

	public static void ironKit(Player p)
	{
		ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack helm = new ItemStack(Material.IRON_HELMET);
		ItemStack legs = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack boots = new ItemStack(Material.IRON_BOOTS);

		p.getInventory().setChestplate(chest);
		p.getInventory().setHelmet(helm);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);

		if(Main.getInstance().getConfig().get("PlayerUniqueID." + p.getUniqueId()) == null)
		{
			defaultHotBar(p.getUniqueId());
		}
		KitManager.playerCustomHotBar(p);
		KitManager.giveAmmo(p);

	}

	public static void playerCustomHotBar(Player p)
	{
		FileConfiguration config = Main.getInstance().getConfig();
		UUID uuid = p.getUniqueId();
		Inventory inv = p.getInventory();
		ConfigItem configItem = new ConfigItem();

		for(int slot = 0; slot < 9; slot++)
		{
			// Set players individual hotbar from config "playerUniqueID.UUID.slotnumber"

			inv.setItem(slot, configItem.stringToItemStack(config.getString("PlayerUniqueID." + uuid + "." + slot)));
		}
	}

	public static void inventoryCustomHotbar(Inventory inv, UUID uuid)
	{
		FileConfiguration config = Main.getInstance().getConfig();
		ConfigItem configItem = new ConfigItem();

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
		FileConfiguration config = Main.getInstance().getConfig();

		for(int slot = 0; slot < 9; slot++)
		{
			String hotbarItems = config.getString("HotbarItems." + slot);
			config.set("PlayerUniqueID." + uuid + "." + slot, hotbarItems);
			Main.getInstance().saveConfig();
		}
	}

	public static void defaultHotBar(Inventory inv)
	{

		FileConfiguration config = Main.getInstance().getConfig();
		ConfigItem configItem = new ConfigItem();
		for(String str : config.getStringList("HotbarItems"))
		{
			inv.addItem(configItem.stringToItemStack(str));
		}
	}

	public static void itemSelection(Inventory inv)
	{
		FileConfiguration config = Main.getInstance().getConfig();
		ConfigItem configItem = new ConfigItem();
		for(String str : config.getStringList("SelectItems"))
		{
			inv.addItem(configItem.stringToItemStack(str));
		}
	}

	public static void giveAmmo(Player p)
	{
		ConfigItem configItem = new ConfigItem();
		FileConfiguration config = Main.getInstance().getConfig();
		Inventory inv = p.getInventory();
		ArrayList<ItemStack> guns = new ArrayList<ItemStack>();

		for(int i = 0; i < 9; i++)
		{
			guns.add(configItem.stringToItemStack(config.getString("PlayerUniqueID." + p.getUniqueId() + "." + i)));
			if(guns.get(i) != null)
			{
				Material slot = guns.get(i).getType();

				if(slot == Material.DIAMOND_AXE || slot == Material.IRON_AXE || slot == Material.STONE_AXE
						|| slot == Material.NETHER_STAR || slot == Material.WOODEN_AXE)
					inv.addItem(sniperAmmo(32));
				if(slot == Material.DIAMOND_PICKAXE || slot == Material.IRON_PICKAXE || slot == Material.STONE_PICKAXE
						|| slot == Material.WOODEN_PICKAXE)
					inv.addItem(shotgunAmmo(32));
				if(slot == Material.GOLDEN_SHOVEL || slot == Material.IRON_HOE || slot == Material.GOLDEN_PICKAXE
						|| slot == Material.STONE_HOE || slot == Material.WOODEN_HOE || slot == Material.GOLDEN_HOE
						|| slot == Material.DIAMOND || slot == Material.DIAMOND_HOE)
					inv.addItem(autoAmmo(64));
				if(slot == Material.DIAMOND_SHOVEL || slot == Material.IRON_SHOVEL || slot == Material.STONE_SHOVEL
						|| slot == Material.WOODEN_SHOVEL)
					inv.addItem(pistolAmmo(32));
			}

		}
	}

	public static ItemStack sniperAmmo(int amount)
	{
		ItemStack ammo = new ItemStack(Material.CLAY_BALL, amount);
		ItemMeta meta = ammo.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Sniper Bullets");
		ammo.setItemMeta(meta);
		return ammo;
	}

	public static ItemStack shotgunAmmo(int amount)
	{
		ItemStack ammo = new ItemStack(Material.WHEAT_SEEDS, amount);
		ItemMeta meta = ammo.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Shotgun Shells");
		ammo.setItemMeta(meta);
		return ammo;
	}

	public static ItemStack pistolAmmo(int amount)
	{
		ItemStack ammo = new ItemStack(Material.ENDER_PEARL, amount);
		ItemMeta meta = ammo.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Pistol Ammo");
		ammo.setItemMeta(meta);
		return ammo;
	}

	public static ItemStack autoAmmo(int amount)
	{
		ItemStack ammo = new ItemStack(Material.FLINT, amount);
		ItemMeta meta = ammo.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Automatic Ammo");
		ammo.setItemMeta(meta);
		return ammo;
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

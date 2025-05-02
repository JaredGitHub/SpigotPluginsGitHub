package me.Jared.Manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.Duels;

public class ConfigManager
{
	private static FileConfiguration config;
	private static Duels plugin = Duels.getInstance();

	public static void setupConfig(Duels plugin)
	{
		ConfigManager.config = plugin.getConfig();
		plugin.saveConfig();
	}

	public static void iconCreate(ItemStack item, String displayName, Inventory inv, int num)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		inv.setItem(num, item);
	}

	public static ArrayList<String> getMaps()
	{
		ArrayList<String> list = new ArrayList<String>(config.getStringList("maps"));

		return list;
	}

	public static Location getMapLocation(String mapName, int i)
	{
		double x = config.getDouble("map." + mapName + "." + i + ".x");
		double y = config.getDouble("map." + mapName + "." + i + ".y");
		double z = config.getDouble("map." + mapName + "." + i + ".z");
		float yaw = (float) config.getDouble("map." + mapName + "." + i + ".yaw");
		float pitch = (float) config.getDouble("map." + mapName + "." + i + ".pitch");

		return new Location(Bukkit.getWorld(config.getString("map.world")), x, y, z, yaw, pitch);
	}

	public static void setMapLocation(Location loc, String mapName, int i)
	{
		ArrayList<String> maps = new ArrayList<String>(config.getStringList("maps"));

		if(!maps.contains(mapName))
		{
			maps.add(mapName);
		}

		config.set("maps", maps);
		plugin.saveConfig();

		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("map.world", loc.getWorld().getName());
		config.set("map." + mapName + "." + i + ".x", x);
		config.set("map." + mapName + "." + i + ".y", y);
		config.set("map." + mapName + "." + i + ".z", z);
		config.set("map." + mapName + "." + i + ".yaw", loc.getYaw());
		config.set("map." + mapName + "." + i + ".pitch", loc.getPitch());

		plugin.saveConfig();
	}

	public static void setDuelKit(Player player, String kitName)
	{
		ArrayList<String> kits = new ArrayList<String>(config.getStringList("kits"));

		if(!kits.contains(kitName))
		{
			kits.add(kitName);
		}

		config.set("kits", kits);
		plugin.saveConfig();

		Inventory inventory = player.getInventory();
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();

		//Adding the armor
		assert helmet != null;
		assert chestplate != null;
		assert leggings != null;
		assert boots != null;
		config.set("kit." + kitName + ".armor.helmet", ConfigItem.itemStackToString(helmet));
		config.set("kit." + kitName + ".armor.chestplate", ConfigItem.itemStackToString(chestplate));
		config.set("kit." + kitName + ".armor.leggings", ConfigItem.itemStackToString(leggings));
		config.set("kit." + kitName + ".armor.boots", ConfigItem.itemStackToString(boots));

		//Adding the hotbar
		for(int i = 0; i < 9; i++)
		{
			ItemStack item = inventory.getItem(i);
			assert item != null;
			config.set("kit." + kitName + ".hotbar." + i, ConfigItem.itemStackToString(item));
		}

		for(int i = 9; i < 36; i++)
		{
			ItemStack item = inventory.getItem(i);
			assert item != null;
			config.set("kit." + kitName + ".inventory." + i, ConfigItem.itemStackToString(item));
		}

		plugin.saveConfig();
	}

	public static void getDuelKit(Player player, String kitName)
	{
		if(!config.getStringList("kits").contains(kitName))
		{
			player.sendMessage(ChatColor.RED + "Kit \"" + kitName + "\" does not exist.");
			return;
		}

		// Clear current inventory
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		// Load and apply armor
		String basePath = "kit." + kitName + ".armor.";
		ItemStack helmet = ConfigItem.stringToItemStack(config.getString(basePath + "helmet"));
		ItemStack chest = ConfigItem.stringToItemStack(config.getString(basePath + "chestplate"));
		ItemStack legs = ConfigItem.stringToItemStack(config.getString(basePath + "leggings"));
		ItemStack boots = ConfigItem.stringToItemStack(config.getString(basePath + "boots"));

		player.getInventory().setHelmet(helmet != null ? helmet : new ItemStack(Material.AIR));
		player.getInventory().setChestplate(chest != null ? chest : new ItemStack(Material.AIR));
		player.getInventory().setLeggings(legs != null ? legs : new ItemStack(Material.AIR));
		player.getInventory().setBoots(boots != null ? boots : new ItemStack(Material.AIR));

		// Load hotbar (slots 0-8)
		for(int i = 0; i < 9; i++)
		{
			String itemString = config.getString("kit." + kitName + ".hotbar." + i);
			ItemStack item =
					itemString != null ? ConfigItem.stringToItemStack(itemString) : new ItemStack(Material.AIR);
			player.getInventory().setItem(i, item);
		}

		// Load remaining inventory (slots 9-35)
		for(int i = 9; i < 36; i++)
		{
			String itemString = config.getString("kit." + kitName + ".inventory." + i);
			ItemStack item =
					itemString != null ? ConfigItem.stringToItemStack(itemString) : new ItemStack(Material.AIR);
			player.getInventory().setItem(i, item);
		}
	}

	public static void setArmorKit(String kitName, Material[] armorMaterials)
	{
		List<String> kits = new ArrayList<>(config.getStringList("kits"));
		if(!kits.contains(kitName))
		{
			kits.add(kitName);
		}
		config.set("kits", kits);

		config.set("kit." + kitName + ".armor.helmet", ConfigItem.itemStackToString(new ItemStack(armorMaterials[0])));
		config.set("kit." + kitName + ".armor.chestplate",
				ConfigItem.itemStackToString(new ItemStack(armorMaterials[1])));
		config.set("kit." + kitName + ".armor.leggings",
				ConfigItem.itemStackToString(new ItemStack(armorMaterials[2])));
		config.set("kit." + kitName + ".armor.boots", ConfigItem.itemStackToString(new ItemStack(armorMaterials[3])));

		plugin.saveConfig();
	}

	public static void setKitItem(String kitName, ItemStack item, int startSlot, int endSlot, String kitType)
	{
		// Ensure the kit exists in the config
		List<String> kits = new ArrayList<>(config.getStringList("kits"));
		if(!kits.contains(kitName))
		{
			kits.add(kitName);
			config.set("kits", kits);
		}

		// Find the first empty slot within the specified range
		int slot = -1;
		for(int i = startSlot; i <= endSlot; i++)
		{
			String itemPath = "kit." + kitName + "." + kitType + "." + i;
			if(!config.contains(itemPath))
			{
				slot = i;
				break;
			}
		}

		// If all slots are taken, default to the last slot in the range
		if(slot == -1)
		{
			slot = endSlot;
		}

		// Save the item to the available slot
		config.set("kit." + kitName + "." + kitType + "." + slot, ConfigItem.itemStackToString(item));

		plugin.saveConfig();
	}

	public static void setHotbarKit(String kitName, ItemStack item)
	{
		setKitItem(kitName, item, 0, 8, "hotbar");
	}

	public static void setInventoryKit(String kitName, ItemStack item)
	{
		setKitItem(kitName, item, 9, 35, "inventory");
	}
	
	public static void clearKit(Player player)
	{
		String uuid = player.getUniqueId().toString();

		// Remove kit from the kits list
		List<String> kits = config.getStringList("kits");
		kits.remove(uuid);
		config.set("kits", kits);

		// Remove all kit data
		config.set("kit." + uuid, null);

		plugin.saveConfig();

		player.sendMessage(ChatColor.GREEN + "Your kit has been cleared!");
	}

	public static List<String> getKitList()
	{
		return config.getStringList("kits");
	}

	public static void removeKit(String kitName)
	{
		List<String> kits = config.getStringList("kits");
		kits.remove(kitName);
		config.set("kits", kits);

		config.set("kit." + kitName, null);

		plugin.saveConfig();
	}

	public static int getCountdown()
	{
		plugin.reloadConfig();
		return config.getInt("countdown-seconds");
	}

	public static void setCountdown(int number)
	{
		config.set("countdown-seconds", number);
		plugin.saveConfig();
	}
}

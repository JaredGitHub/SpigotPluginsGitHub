package me.Jared.Manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
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
		float pitch = (float) config.getDouble("map." + mapName + "." + i +".pitch");

		return new Location(Bukkit.getWorld(config.getString("map.world")),x,y,z,yaw,pitch);
	}

	public static void setMapLocation(Location loc, String mapName, int i)
	{
		ArrayList<String> maps = new ArrayList<String>(config.getStringList("maps"));

		if(!maps.contains(mapName))
		{
			maps.add(mapName);
		}

		config.set("maps",maps);
		plugin.saveConfig();

		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("map.world",loc.getWorld().getName());
		config.set("map." + mapName + "." + i + ".x", x);
		config.set("map." + mapName + "." + i + ".y", y);
		config.set("map." + mapName + "." + i + ".z", z);
		config.set("map." + mapName + "." + i + ".yaw", loc.getYaw());
		config.set("map." + mapName + "." + i + ".pitch", loc.getPitch());

		plugin.saveConfig();
	}

	public static int getCountdown()
	{
		plugin.reloadConfig();
		return config.getInt("countdown-seconds");
	}
	public static void setCountdown(int number)
	{
		config.set("countdown-seconds",number);
		plugin.saveConfig();
	}
}

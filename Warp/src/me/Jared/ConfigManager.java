package me.Jared;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigManager
{
	private static WarpMain plugin = WarpMain.getInstance();
	private static FileConfiguration config = plugin.getConfig();

	public static void iconCreate(ItemStack item, String displayName, Inventory inv, int num)
	{
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		inv.setItem(num, item);
	}

	public static ArrayList<String> getWarps()
	{
		ArrayList<String> list = new ArrayList<String>();

		if(config.isConfigurationSection("warp"))
		{
			for(String string : config.getConfigurationSection("warp").getKeys(false))
			{
				list.add(string);
			}
		}
		return list;
	}

	public static Location getWarp(String warpName)
	{
		double x = config.getDouble("warp." + warpName  + ".x");
		double y = config.getDouble("warp." + warpName  + ".y");
		double z = config.getDouble("warp." + warpName  + ".z");
		float yaw = (float) config.getDouble("warp." + warpName  + ".yaw");
		float pitch = (float) config.getDouble("warp." + warpName  + ".pitch");
		World world = Bukkit.getWorld(config.getString("warp." + warpName  +".world"));

		return new Location(world,x,y,z,yaw,pitch);
	}

	public static void addWarp(Location loc, String warpName)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("warp." + warpName + ".world",loc.getWorld().getName());
		config.set("warp." + warpName + ".x", x);
		config.set("warp." + warpName + ".y", y);
		config.set("warp." + warpName + ".z", z);
		config.set("warp." + warpName + ".yaw", loc.getYaw());
		config.set("warp." + warpName + ".pitch", loc.getPitch());

		plugin.saveConfig();
	}

	public static void teleportPlayerToWarp(Player player, String warpName)
	{
		Location warpLoc = ConfigManager.getWarp(warpName);
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers " 
		+ warpLoc.getX() + " " 
		+ warpLoc.getZ() 
		+ " 50 50 false " + player.getName());
		
		player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		player.sendMessage(ChatColor.GREEN + "Warped to " + ChatColor.WHITE + warpName);
	}

	public static void removeWarp(String warpName)
	{
		config.set("warp." + warpName, null);
		plugin.saveConfig();
	}

}



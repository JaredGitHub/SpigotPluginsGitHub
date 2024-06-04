package me.Jared.Essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager
{
	private static FileConfiguration config = Main.getInstance().getConfig();
	public static void setSpawn(Location location)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		
		config.set("spawn.world",location.getWorld().getName());
		config.set("spawn.x", x);
		config.set("spawn.y",y);
		config.set("spawn.z",z);
		config.set("spawn.yaw", location.getYaw());
		config.set("spawn.pitch", location.getPitch());
		Main.getInstance().saveConfig();
	}
	
	public static Location getSpawn()
	{
		return new Location(Bukkit.getWorld(config.getString("spawn.world")),
				config.getDouble("spawn.x"),
				config.getDouble("spawn.y"),
				config.getDouble("spawn.z"),
				(float) config.getDouble("spawn.yaw"),
				(float) config.getDouble("spawn.pitch")
				);
	}
}

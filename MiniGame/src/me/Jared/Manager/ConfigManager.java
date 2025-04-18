package me.Jared.Manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import me.Jared.MiniGame;
import me.Jared.Loot.ConfigItem;

public class ConfigManager
{
	private static FileConfiguration config;
	private static MiniGame plugin = MiniGame.getInstance();

	public static void setupConfig(MiniGame plugin)
	{
		ConfigManager.config = plugin.getConfig();
		plugin.saveConfig();
	}

	public static Location getLobbySpawn()
	{
		return new Location(Bukkit.getWorld(config.getString("lobby-spawn.world")),
				config.getDouble("lobby-spawn.x"),
				config.getDouble("lobby-spawn.y"),
				config.getDouble("lobby-spawn.z"),
				(float) config.getDouble("lobby-spawn.yaw"),
				(float) config.getDouble("lobby-spawn.pitch")
				);
	}

	public static int getStormShrinkTime()
	{
		return config.getInt("stormShrinkTime");
	}

	public static void setLobbySpawn(Location location)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		config.set("lobby-spawn.world",location.getWorld().getName());
		config.set("lobby-spawn.x", x);
		config.set("lobby-spawn.y",y);
		config.set("lobby-spawn.z",z);
		config.set("lobby-spawn.yaw", location.getYaw());
		config.set("lobby-spawn.pitch", location.getPitch());
		plugin.saveConfig();

	}

	public static Location getGameSlotLocation(int i)
	{

		double x = config.getDouble("sg." + i + ".x");
		double y = config.getDouble("sg." + i + ".y");
		double z = config.getDouble("sg." + i + ".z");
		float yaw = (float) config.getDouble("sg." + i + ".yaw");
		float pitch = (float) config.getDouble("sg." + i + ".pitch");

		return new Location(Bukkit.getWorld("world"),x,y,z,yaw,pitch);
	}

	public static void setGameSlot(Location loc, int i)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("sg.world",loc.getWorld().getName());
		config.set("sg." + i + ".x", x);
		config.set("sg." + i + ".y", y);
		config.set("sg." + i + ".z", z);
		config.set("sg." + i + ".yaw", loc.getYaw());
		config.set("sg." + i + ".pitch", loc.getPitch());

		plugin.saveConfig();
	}

	public static ArrayList<Location> getChestLocations()
	{
		ArrayList<Location> locations = new ArrayList<Location>();

		for(String chest : new ArrayList<String>(config.getStringList("chests")))
		{
			locations.add(ConfigItem.getChestLocation(chest));

		}
		return locations;
	}

	public static int getPlayersNeeded()
	{
		return config.getInt("required-players");
	}

	public static void setPlayersNeeded(int number)
	{
		config.set("required-players", number);
		plugin.saveConfig();
	}

	public static int getCountdown()
	{
		return config.getInt("countdown-seconds");

	}
	
	public static void setCountdown(int number)
	{
		config.set("countdown-seconds", number);
		plugin.saveConfig();
	}
}

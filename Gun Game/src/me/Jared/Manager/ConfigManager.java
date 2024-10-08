package me.Jared.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import me.Jared.GunGame;


public class ConfigManager
{
	private static FileConfiguration config;
	private static GunGame plugin = GunGame.getInstance();
	
	public static void setupConfig(GunGame plugin)
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
		double x = config.getDouble("gg." + i + ".x");
		double y = config.getDouble("gg." + i + ".y");
		double z = config.getDouble("gg." + i + ".z");
		float yaw = (float) config.getDouble("gg." + i + ".yaw");
		float pitch = (float) config.getDouble("gg." + i + ".pitch");
		
		return new Location(Bukkit.getWorld(config.getString("gg." + i + ".world")),x,y,z,yaw,pitch);
	}
	
	public static void setGameSlot(Location loc, int i)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		
		config.set("gg." + i + ".world",loc.getWorld().getName());
		config.set("gg." + i + ".x", x);
		config.set("gg." + i + ".y", y);
		config.set("gg." + i + ".z", z);
		config.set("gg." + i + ".yaw", loc.getYaw());
		config.set("gg." + i + ".pitch", loc.getPitch());
		
		plugin.saveConfig();
	}
	
	public static int getPlayersNeeded()
	{
		return config.getInt("required-players");
	}
	public static void setPlayersNeeded(int number)
	{
		config.set("required-players",number);
		plugin.saveConfig();
	}
	
	public static int getCountdown()
	{
		return config.getInt("countdown-seconds");
	}
	public static void setCountdown(int number)
	{
		config.set("countdown-seconds",number);
		plugin.saveConfig();
	}
}

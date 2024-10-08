package me.Jared.Util;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig
{

	private static File file;
	private static FileConfiguration customFile;
	
	public static void setup()
	{
		file = new File(Bukkit.getServer().getPluginManager().getPlugin("Kits").getDataFolder(), "customconfig");
		
		
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		customFile = YamlConfiguration.loadConfiguration(file);
	}
	
	public static FileConfiguration get()
	{
		return customFile;
	}
	
	public static void save()
	{
		try
		{
			customFile.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void reload()
	{
		customFile = YamlConfiguration.loadConfiguration(file);
	}
	
}

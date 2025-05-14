package me.Jared;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin
{
	AirDrop airDrop = new AirDrop(this);

	private static Main instance;

	@Override
	public void onEnable()
	{
		instance = this;
		// Plugin startup logic
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Airdrop Enabled");

		Bukkit.getPluginCommand("airdrop").setExecutor(new AirDropCommand());
		
		AirDropManager airDropManager = new AirDropManager();
		// 10 second timer before airdrop
		airDropManager.runAirDropRunnable(airDrop, 10);

		//If config doesn't contain interval give it interval of 5 minutes 300 seconds
		if(!(this.getConfig().contains("interval")))
		{
			getConfig().set("interval", 900);
		}

		//Looping through each of the chests that may or may not be placed and removing them
		//This makes sure that on restart of the server there aren't a bunch of chests with goodies in them
		if(this.getConfig().contains("airdrops"))
		{
			ConfigurationSection airdropsSection = this.getConfig().getConfigurationSection("airdrops");
			for(String key : airdropsSection.getKeys(false))
			{
				double x = airdropsSection.getDouble(key + ".x");
				double y = airdropsSection.getDouble(key + ".y");
				double z = airdropsSection.getDouble(key + ".z");

				World world = Bukkit.getWorld("warz"); // Replace with your world name or reference
				if(world != null)
				{
					Location airdropLocation = new Location(world, x, y, z);
					airdropLocation.getBlock().setType(Material.AIR);
				}
			}
		}
	}

	public static Main getInstance()
	{
		return instance;
	}

	@Override
	public void onDisable()
	{
		// Plugin shutdown logic
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "AirDrop Disabled");
	}

}


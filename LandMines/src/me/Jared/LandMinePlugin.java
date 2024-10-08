package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.LandMine.LandMineCommand;
import me.Jared.LandMine.LandMineRunnable;

public class LandMinePlugin extends JavaPlugin
{
	private static LandMinePlugin instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Land Mine plugin is here!");
		getServer().getPluginCommand("landmine").setExecutor(new LandMineCommand());
		
		this.getConfig().addDefault("landmineItem", "FLOWER_POT");
		this.getConfig().addDefault("landmineItemName", "&6Land Mine");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		if(!this.getConfig().contains("world"))
		{
			this.getConfig().set("world", "world");
			this.saveConfig();
		}
		
		LandMineRunnable worldLandMine = new LandMineRunnable();
		worldLandMine.runTaskTimer(this, 0, 3);
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Land Mine plugin is gone!");
	}
	
	public static LandMinePlugin getInstance()
	{
		return instance;
	}
}

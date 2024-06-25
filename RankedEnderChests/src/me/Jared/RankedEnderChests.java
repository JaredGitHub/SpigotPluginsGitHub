package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class RankedEnderChests extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Ranked Ender Chests plugin is here!!");
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Ranked Ender Chests plugin is gone OH NOOOOO!!");
	}
}

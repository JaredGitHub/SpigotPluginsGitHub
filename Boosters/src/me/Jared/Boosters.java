package me.Jared;


import me.Jared.Command.BoosterCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Boosters extends JavaPlugin
{
	private static Boosters instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Boosters are here!");
		
		Bukkit.getPluginCommand("boost").setExecutor(new BoosterCommand());
		
		this.getConfig().set("doublegems", false);
		this.getConfig().set("DoubleLoot", false);
		this.saveConfig();
	}
	
	public static Boosters getInstance()
	{
		return instance;
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Boosters are gone now!");
		this.getConfig().set("doublegems", false);
		this.getConfig().set("DoubleLoot", false);
		this.saveConfig();
	}
}

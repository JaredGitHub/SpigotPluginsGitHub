package me.Jared.Kits;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.MenuSystem.PlayerMenuUtility;
import me.Jared.Util.CustomConfig;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener
{
	public static Main plugin;

	public static Plugin getInstance()
	{
		return plugin;
	}
	
	
	@Override
	public void onEnable()
	{
		plugin = this;
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Kits plugin has been enabled!");
		this.getCommand("kit").setExecutor(new Commands());
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		loadConfig();
		
		CustomConfig.setup();
		CustomConfig.get().options().copyDefaults(true);
		CustomConfig.save();
	} 
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Kits plugin has been disabled!");
		
		this.reloadConfig();
		this.saveConfig();
	}
	
	public void loadConfig()
	{
		this.getConfig().options().copyDefaults();
		this.saveConfig();
	}
	
	private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
	
	public static PlayerMenuUtility getPlayerMenuUtility(Player p)
	{
		PlayerMenuUtility playerMenuUtility;
		
		if(playerMenuUtilityMap.containsKey(p))
		{
			return playerMenuUtilityMap.get(p);
		}
		else
		{
			playerMenuUtility = new PlayerMenuUtility(p);
			playerMenuUtilityMap.put(p, playerMenuUtility);
			
			return playerMenuUtility;
		}
	}
}

package me.Jared;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Command.DuelCommands;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Listeners.DuelListener;
import me.Jared.Manager.ConfigManager;

public class Duels extends JavaPlugin
{
	private static Duels instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Duels plugin is here now!");
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		ConfigManager.setupConfig(this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new DuelListener(), instance);
		this.getCommand("duel").setExecutor(new DuelCommands());

	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Duels plugin is gone now!");
	}

	public static Duels getInstance()
	{
		return instance;
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

package me.Jared;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.MenuSystem.PlayerMenuUtility;


public class GameGUI extends JavaPlugin
{

	private static GameGUI instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Game GUI plugin is now enabled!");
		
		getServer().getPluginCommand("games").setExecutor(new GameGUICommand());
		getServer().getPluginCommand("g").setExecutor(new GameGUICommand());
		getServer().getPluginManager().registerEvents(new GameGUIListener(), this);
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Game GUI plugin is now disabled!");
	}
	
	public static GameGUI getInstance()
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

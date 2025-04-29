package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Commands.EventCommands;
import me.Jared.Commands.TeamCommands;
import me.Jared.Listeners.GameListener;
import me.Jared.Manager.EventExpansion;
import me.Jared.Manager.GameManager;

public class Event extends JavaPlugin
{
	private GameManager gameManager;
	private static Event instance;
	@Override
	public void onEnable()
	{
		instance = this;
		this.gameManager = new GameManager(this);
		gameManager.setGameState(GameState.INACTIVE);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Event plugin is here!");
		
		Bukkit.getPluginManager().registerEvents(new GameListener(gameManager), this);
		
		Bukkit.getPluginCommand("event").setExecutor(new EventCommands(gameManager));
		Bukkit.getPluginCommand("team").setExecutor(new TeamCommands(gameManager));
		
		new EventExpansion().register();
	}
	
	@Override
	public void onDisable()
	{
		this.gameManager = new GameManager(this);
		gameManager.setGameState(GameState.INACTIVE);
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Event plugin is gone!");
	}
	
	public static Event getInstance()
	{
		return instance;
	}
	
	public GameManager getGameManager()
	{
		return gameManager;
	}
}

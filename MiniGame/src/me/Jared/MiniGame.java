package me.Jared;


import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Command.ArenaCommand;
import me.Jared.Command.LootCommand;
import me.Jared.Listeners.GameListener;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import me.Jared.Manager.MiniGameExpansion;

public class MiniGame extends JavaPlugin
{
	private GameManager gameManager;
	private static MiniGame instance;
	private static ArrayList<Location> chestLocations;
	@Override
	public void onEnable()
	{
		instance = this;
		ConfigManager.setupConfig(this);
		chestLocations = new ArrayList<>();
		this.gameManager = new GameManager(this);
		gameManager.setGameState(GameState.WAITING);

		Bukkit.getPluginManager().registerEvents(new GameListener(gameManager), this);
		getCommand("sg").setExecutor(new ArenaCommand(gameManager));
		getCommand("survivalgames").setExecutor(new ArenaCommand(gameManager));
		getCommand("spectate").setExecutor(new ArenaCommand(gameManager));
		
		getCommand("minigame").setExecutor(new LootCommand(gameManager));
		getCommand("setchest").setExecutor(new LootCommand(gameManager));

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MiniGame plugin is here!");

		//Placeholder hooker
		new MiniGameExpansion().register();
	}
	
	public static ArrayList<Location> getChestLocations()
	{
		return chestLocations;
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MiniGame plugin is gone!");
	}

	public static MiniGame getInstance()
	{
		return instance;
	}

	public GameManager getGameManager()
	{
		return gameManager;
	}
}

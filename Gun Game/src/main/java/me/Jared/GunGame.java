package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Command.GunGameCommand;
import me.Jared.Listeners.GameListener;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import me.Jared.Manager.GunGameExpansion;
import net.md_5.bungee.api.ChatColor;

public class GunGame extends JavaPlugin
{
	private GameManager gameManager;
	private static GunGame instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		ConfigManager.setupConfig(this);
		
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Gun Game Plugin Is Here!");
		
		this.gameManager = new GameManager(this);
		gameManager.setGameState(GameState.WAITING);
		
		//Event Handlers
		Bukkit.getServer().getPluginManager().registerEvents(new GameListener(gameManager), this);
		//Command Handlers
		getCommand("gungame").setExecutor(new GunGameCommand(gameManager));
		getCommand("gg").setExecutor(new GunGameCommand(gameManager));
		
		new GunGameExpansion().register();
		
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Gun Game Plugin Is Gone!");
		gameManager.setGameState(GameState.WAITING);
	}

	public static GunGame getInstance()
	{
		return instance;
	}
	
	public GameManager getGameManager()
	{
		return gameManager;
	}
}

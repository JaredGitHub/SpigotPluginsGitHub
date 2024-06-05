package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Command.DeathmatchCommand;
import me.Jared.Listeners.DeathmatchListener;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import me.Jared.Manager.TDMExpansion;
import net.md_5.bungee.api.ChatColor;

public class Deathmatch extends JavaPlugin
{
	
	private GameManager gameManager;
	private static Deathmatch instance;
	@Override
	public void onEnable()
	{
		instance = this;
		
		this.getConfig().addDefault("required-players", 2);
		this.getConfig().addDefault("countdown-seconds", 15);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		ConfigManager.setupConfig(this);
		
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Team Deathmatch Plugin Is Here!");
		
		this.gameManager = new GameManager(this);
		gameManager.setGameState(GameState.WAITING);
		
		//Event Handlers
		Bukkit.getServer().getPluginManager().registerEvents(new DeathmatchListener(gameManager), this);
		//Command Handlers
		getCommand("tdm").setExecutor(new DeathmatchCommand(gameManager));
		
		new TDMExpansion().register();
		
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Team Deathmatch Plugin Is Gone!");
	}
	
	public static Deathmatch getInstance()
	{
		return instance;
	}
	
	public GameManager getGameManager()
	{
		return gameManager;
	}
}

package me.jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.jared.teams.TeamCommands;
import me.jared.teams.TeamListener;

public class TeamMane extends JavaPlugin
{
	
	public void loadConfig()
	{
		this.getConfig().options().copyDefaults();
		this.saveConfig();
	}

	public static TeamMane plugin;
	
	
	
	@Override
	public void onEnable()
	{	
		plugin = this;
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Teams has been enabled");

		//teams
		this.getCommand("team").setExecutor(new TeamCommands(this));
		this.getServer().getPluginManager().registerEvents(new TeamListener(this), this);

		loadConfig();
	}


	@Override
	public void onDisable()
	{
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Teams has been disabled!");
		this.reloadConfig();
		this.saveConfig();
		
	}
}





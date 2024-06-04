package me.jared.barb;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class BarbWire extends JavaPlugin
{
	private static BarbWire instance;
	
	public static BarbWire getInstance()
	{
		return instance;
	}
	@Override
	public void onEnable()
	{

		if(!this.getConfig().contains("world"))
		{
			this.getConfig().set("world", "world");
			this.saveConfig();
		}
		
		instance = this;
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BarbWire is here!");

		this.getServer().getPluginManager().registerEvents(new BarbWireListener(this), this);

		for(Player p : Bukkit.getOnlinePlayers())
		{
			Bukkit.getServer().getScheduler().runTaskTimer(this, new HurtPlayerRunnable(p), 0, 0);
		}
	}

	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BarbWire is gone!");
	}

}

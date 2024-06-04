package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Parachute extends JavaPlugin
{
	
	@Override
	public void onEnable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Parachutes are here!");
		
		Bukkit.getServer().getPluginManager().registerEvents(new ParachuteListener(this), this);
		
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Parachutes are gone!");
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getPassengers().isEmpty()) return;
			if(p.getPassengers().get(0) != null)
			{
				p.getPassengers().get(0).remove();
			}
		}
	}
}

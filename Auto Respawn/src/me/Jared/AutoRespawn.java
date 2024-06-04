package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoRespawn extends JavaPlugin
{
	public void onEnable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "AutoRespawn has successfully been initialized!!");
		getServer().getPluginManager().registerEvents(new RespawnListener(this), (Plugin)this);
	}

	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "AutoRespawn is going bye bye!");
	}
}

package me.jared.Compass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Compass extends JavaPlugin implements Listener
{
	@Override
	public void onEnable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Compass plugin has started up!");

		this.getServer().getPluginManager().registerEvents(this, this);
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			Bukkit.getServer().getScheduler().runTaskTimer(this, new Tracker(p), 0, 1);
		}

	}

	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Compass plugin is going down!");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		Bukkit.getServer().getScheduler().runTaskTimer((Plugin)this, new Tracker(p), 0L, 1L);
		
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		
		for(ItemStack item : p.getInventory().getContents())
		{
			ItemMeta meta = getCompass().getItemMeta();
			if(item == null)
			{
				continue;
			}

			if(item.getType().equals(Material.COMPASS))
			{
				item.setItemMeta(meta);
				break;
			}
		}
	}
	public ItemStack getCompass()
	{

		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta im = compass.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Compass");
		compass.setItemMeta(im);
		return compass;
	}
}

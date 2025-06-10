package me.jared.Compass;

import me.Jared.AirDrop;
import me.Jared.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Compass extends JavaPlugin implements Listener
{
	private int trackPlayerID = -1;
	private int trackLocationID = -1;

	@Override
	public void onEnable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Compass plugin has started up!");

		this.getServer().getPluginManager().registerEvents(this, this);

		for(Player p : Bukkit.getOnlinePlayers())
		{
			trackPlayerID = Bukkit.getServer().getScheduler().runTaskTimer(this, new TrackPlayer(p), 0L, 1L).getTaskId();
		}
	}

	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Compass plugin is going down!");
	}


	private Location getAirDropLocation()
	{
		//Get the config from aidrop plugin
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("AirDrop").getConfig();
		int airdropIndex = config.getInt("airdrop");

		double x = config.getDouble("airdrops." + airdropIndex + ".x");
		double y = config.getDouble("airdrops." + airdropIndex + ".y");
		double z = config.getDouble("airdrops." + airdropIndex + ".z");

		return new Location(Bukkit.getWorld("warz"), x, y, z);
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e)
	{
		//Return if the item is null
		if(e.getItem() == null)
		{
			return;
		}

		if(e.getItem().getType().equals(Material.COMPASS) && (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK))
		{
			Player p = e.getPlayer();

			//If the player is not in the warz world tell them it only works in warz world
			if(!p.getWorld().getName().equals("warz"))
			{
				p.sendMessage(ChatColor.RED + "Airdrop finder only works in warz");
				return;
			}

			if(trackLocationID != -1)
			{
				p.sendMessage(ChatColor.GREEN + "Nearest Player Tracking...");
				Bukkit.getServer().getScheduler().cancelTask(trackLocationID);
				trackLocationID = -1;
				trackPlayerID = Bukkit.getServer().getScheduler().runTaskTimer(this, new TrackPlayer(p), 0L, 1L)
						.getTaskId();
			} else
			{
				//Airdrop location
				Location airDropLocation = getAirDropLocation();
				p.sendMessage(ChatColor.GREEN + "Tracking airdrop location...");
				//Cancel the task and turn it to -1
				Bukkit.getServer().getScheduler().cancelTask(trackPlayerID);
				trackPlayerID = -1;
				trackLocationID = Bukkit.getServer().getScheduler()
						.runTaskTimer(this, new TrackLocation(p, airDropLocation), 0L, 1L)
						.getTaskId();

			}
		}
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

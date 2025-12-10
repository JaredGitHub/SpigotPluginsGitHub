package me.jared.Compass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Compass extends JavaPlugin implements Listener
{
	// Per-player task IDs
	private final Map<UUID, Integer> playerTrackTasks = new HashMap<>();
	private final Map<UUID, Integer> locationTrackTasks = new HashMap<>();

	@Override
	public void onEnable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Compass plugin has started!");

		Bukkit.getPluginManager().registerEvents(this, this);

		// If server reloads, start tracking again for all online players
		for (Player p : Bukkit.getOnlinePlayers())
		{
			startPlayerTracking(p);
		}
	}

	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Compass plugin shutting down...");

		// Cancel all running tasks
		for (int id : playerTrackTasks.values()) Bukkit.getScheduler().cancelTask(id);
		for (int id : locationTrackTasks.values()) Bukkit.getScheduler().cancelTask(id);
	}

	// -----------------------------
	//  AIRDROP LOCATION FETCH
	// -----------------------------
	private Location getAirDropLocation()
	{
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("AirDrop").getConfig();
		int index = config.getInt("airdrop");

		double x = config.getDouble("airdrops." + index + ".x");
		double y = config.getDouble("airdrops." + index + ".y");
		double z = config.getDouble("airdrops." + index + ".z");

		return new Location(Bukkit.getWorld("warz"), x, y, z);
	}

	// -----------------------------
	//  TASK MANAGEMENT
	// -----------------------------
	private void startPlayerTracking(Player p)
	{
		UUID id = p.getUniqueId();

		// Cancel old task if exists
		stopPlayerTracking(p);

		int task = Bukkit.getScheduler().runTaskTimer(this, new TrackPlayer(p), 0L, 1L).getTaskId();
		playerTrackTasks.put(id, task);
	}

	private void startLocationTracking(Player p, Location loc)
	{
		UUID id = p.getUniqueId();

		// Cancel old task if exists
		stopLocationTracking(p);

		int task = Bukkit.getScheduler()
				.runTaskTimer(this, new TrackLocation(p, loc), 0L, 1L)
				.getTaskId();

		locationTrackTasks.put(id, task);
	}

	private void stopPlayerTracking(Player p)
	{
		UUID id = p.getUniqueId();

		if (playerTrackTasks.containsKey(id))
		{
			Bukkit.getScheduler().cancelTask(playerTrackTasks.get(id));
			playerTrackTasks.remove(id);
		}
	}

	private void stopLocationTracking(Player p)
	{
		UUID id = p.getUniqueId();

		if (locationTrackTasks.containsKey(id))
		{
			Bukkit.getScheduler().cancelTask(locationTrackTasks.get(id));
			locationTrackTasks.remove(id);
		}
	}

	// -----------------------------
	//  EVENTS
	// -----------------------------
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		startPlayerTracking(e.getPlayer());
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e)
	{
		if (e.getItem() == null) return;
		if (!e.getItem().getType().equals(Material.COMPASS)) return;

		if (e.getAction() != Action.RIGHT_CLICK_AIR &&
				e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player p = e.getPlayer();

		if (!p.getWorld().getName().equals("warz"))
		{
			p.sendMessage(ChatColor.RED + "Airdrop finder only works in warz world.");
			return;
		}

		UUID id = p.getUniqueId();
		boolean playerTracking = playerTrackTasks.containsKey(id);
		boolean locationTracking = locationTrackTasks.containsKey(id);

		// Toggle between player tracking and location tracking
		if (playerTracking)
		{
			// Switch to airdrop
			stopPlayerTracking(p);

			Location airDropLocation = getAirDropLocation();
			p.sendMessage(ChatColor.GREEN + "Tracking airdrop location...");
			startLocationTracking(p, airDropLocation);
		}
		else
		{
			// Switch to player tracking
			stopLocationTracking(p);

			p.sendMessage(ChatColor.GREEN + "Nearest player tracking...");
			startPlayerTracking(p);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();

		if(p.getWorld().getName().equals("world"))
		{
			if(playerTrackTasks.get(e.getPlayer().getUniqueId()) == null)
			{
				stopLocationTracking(p);
				startPlayerTracking(p);
			}

		}

		for (ItemStack item : p.getInventory().getContents())
		{
			if (item == null) continue;

			if (item.getType() == Material.COMPASS)
			{
				item.setItemMeta(getCompass().getItemMeta());
				break;
			}
		}
	}

	// -----------------------------
	//  COMPASS ITEM
	// -----------------------------
	public ItemStack getCompass()
	{
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta im = compass.getItemMeta();

		im.setDisplayName(ChatColor.BLUE + "Compass");

		compass.setItemMeta(im);
		return compass;
	}
}

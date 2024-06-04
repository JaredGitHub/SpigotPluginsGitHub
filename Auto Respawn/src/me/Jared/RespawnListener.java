package me.Jared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class RespawnListener implements Listener
{
	AutoRespawn plugin;

	private Map<UUID, Location> deathLocation;

	ArrayList<UUID> deadPlayers;

	public RespawnListener(AutoRespawn passedPlugin)
	{
		this.deathLocation = new HashMap<>();
		this.deadPlayers = new ArrayList<>();
		this.plugin = passedPlugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		if (this.deadPlayers.contains(p.getUniqueId()))
		{
			p.setGameMode(GameMode.SURVIVAL);
			p.teleport(Bukkit.getWorld(p.getWorld().toString()).getSpawnLocation());
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity().getPlayer();
		this.deadPlayers.add(p.getUniqueId());
		this.deathLocation.put(p.getUniqueId(), p.getLocation());
		Bukkit.getScheduler().runTaskLater((Plugin) this.plugin, () -> p.spigot().respawn(), 1L);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		if (this.deathLocation.get(p.getUniqueId()) != null)
		{
			Bukkit.getScheduler().runTaskLater((Plugin) this.plugin, () -> p.setGameMode(GameMode.SPECTATOR), 2L);
			e.setRespawnLocation(this.deathLocation.get(p.getUniqueId()));
			Bukkit.getScheduler().runTaskLater((Plugin) this.plugin, () -> {
				p.setGameMode(GameMode.SURVIVAL);
				
				World world = p.getWorld();
				Location location = world.getSpawnLocation();
				
				p.teleport(location);
				this.deathLocation.remove(p.getUniqueId());
			}, 100L);
		}
	}
}

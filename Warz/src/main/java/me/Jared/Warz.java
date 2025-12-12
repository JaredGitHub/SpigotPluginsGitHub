package me.Jared;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Commands.WarzCommands;
import me.Jared.Listener.WarzListener;
import me.Jared.Loot.LootManager;
import me.Jared.Zombies.ZombieRunnable;

public class Warz extends JavaPlugin
{
	private static Warz instance;
	private static Map<String, Set<Location>> chestLocationsPerWorld;
	private static Map<String, Set<Location>> openChestLocationsPerWorld;

	private LootManager lootManagerWarz;
	private LootManager lootManagerWarz2;

	@Override
	public void onEnable()
	{
		instance = this;
		chestLocationsPerWorld = new HashMap<>();
		openChestLocationsPerWorld = new HashMap<>();

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Warz plugin is here!!!!");
		Bukkit.getPluginCommand("setzone").setExecutor(new WarzCommands());
		Bukkit.getPluginCommand("setloot").setExecutor(new WarzCommands());
		Bukkit.getPluginCommand("addspawnpoint").setExecutor(new WarzCommands());
		Bukkit.getPluginCommand("warz").setExecutor(new WarzCommands());
		Bukkit.getPluginCommand("warz2").setExecutor(new WarzCommands());

		lootManagerWarz = new LootManager("warz");
		lootManagerWarz.runLootRunnable(2, "warz");

		lootManagerWarz2 = new LootManager("warz2");
		lootManagerWarz2.runLootRunnable(2, "warz2");

		ZombieRunnable zombieRunnable = new ZombieRunnable(50, 120, "warz");
		zombieRunnable.runTaskTimer(this, 0L, 20L);

		ZombieRunnable zombieRunnable2 = new ZombieRunnable(50, 90, "warz2");
		zombieRunnable2.runTaskTimer(this, 0L, 20L);

		Bukkit.getPluginManager().registerEvents(new WarzListener(), this);
	}

	public static Warz getInstance()
	{
		return instance;
	}

	public static Set<Location> getChestLocations(String warzWorld)
	{
		return chestLocationsPerWorld.computeIfAbsent(warzWorld, k -> new HashSet<>());
	}

	public static Set<Location> getOpenChestLocations(String warzWorld)
	{
		return openChestLocationsPerWorld.computeIfAbsent(warzWorld, k -> new HashSet<>());

	}

	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Warz plugin is gone!");

		for(Entity entity : Bukkit.getWorld("warz").getEntities())
		{
			if(entity instanceof ArmorStand)
			{
				entity.remove();
			}
		}
		for(Entity entity : Bukkit.getWorld("warz2").getEntities())
		{
			if(entity instanceof ArmorStand)
			{
				entity.remove();
			}
		}
	}
}


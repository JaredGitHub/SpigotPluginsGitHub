package me.Jared;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Commands.WarzCommands;
import me.Jared.Listener.WarzListener;
import me.Jared.Loot.LootManager;
import me.Jared.Zombies.ZombieRunnable;

public class Warz extends JavaPlugin
{
	private static Warz instance;
	private static ArrayList<Location> chestLocations;
	private static ArrayList<Location> openChestLocations;
	private static ArrayList<Inventory> inventories;

	@Override
	public void onEnable()
	{
		instance = this;
		chestLocations = new ArrayList<>();
		openChestLocations = new ArrayList<>();
		inventories = new ArrayList<>();

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Warz plugin is here!!!!");
		Bukkit.getPluginCommand("setzone").setExecutor(new WarzCommands());
		Bukkit.getPluginCommand("setloot").setExecutor(new WarzCommands());
		Bukkit.getPluginCommand("addspawnpoint").setExecutor(new WarzCommands());

		Bukkit.getPluginCommand("warz").setExecutor(new WarzCommands());

		LootManager lootManager = new LootManager();
		lootManager.runLootRunnable(2);

	    ZombieRunnable zombieRunnable = new ZombieRunnable(50, 10, 1);
	    zombieRunnable.runTaskTimer(this, 0L, 20L);

		Bukkit.getPluginManager().registerEvents(new WarzListener(), this);
	}

	public static Warz getInstance()
	{
		return instance;
	}

	public static ArrayList<Location> getChestLocations()
	{
		return chestLocations;
	}

	public static ArrayList<Location> getOpenChestLocations()
	{
		return openChestLocations;
	}

	public static ArrayList<Inventory> getInventories()
	{
		return inventories;
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
	}
}


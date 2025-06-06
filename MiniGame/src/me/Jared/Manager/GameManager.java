package me.Jared.Manager;

import java.util.ArrayList;
import java.util.HashMap;

import me.Jared.runnable.StormRunnable;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Jared.GameState;
import me.Jared.MiniGame;
import me.Jared.Loot.LootManager;
import me.Jared.runnable.ChestRunnable;
import me.Jared.runnable.Countdown;
import me.Jared.runnable.Winning;

public class GameManager
{
	private final MiniGame plugin;
	private final PlayerManager playerManager;
	private final LootManager lootManager;
	private Countdown countdown;
	private ChestRunnable chestRunnable;
	private ArrayList<Location> locations;
	private HashMap<Location, Boolean> chestOpen;

	private GameState gameState = GameState.WAITING;

	public GameManager(MiniGame plugin)
	{
		this.plugin = plugin;

		this.playerManager = new PlayerManager(this);
		this.lootManager = new LootManager();
		this.locations = new ArrayList<Location>();
		this.chestOpen = new HashMap<Location, Boolean>();
	}

	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}

	public MiniGame getPlugin()
	{
		return plugin;
	}

	public ArrayList<Location> getLocations()
	{
		return this.locations;
	}

	public HashMap<Location, Boolean> getChestOpen()
	{
		return this.chestOpen;
	}

	public void setGameState(GameState gameState)
	{
		if(gameState == GameState.LIVE && gameState == GameState.COUNTDOWN)
			return;
		if(this.gameState == gameState)
			return;

		this.gameState = gameState;

		switch(gameState)
		{
		case LIVE:

			if(this.countdown != null)
				this.countdown.cancel();
			locations.addAll(ConfigManager.getChestLocations());

			for(Location location : locations)
			{
				this.chestOpen.put(location, false);
			}

			chestRunnable = new ChestRunnable(locations, this);
			chestRunnable.runTaskTimer(plugin, 0, 25);

			// Run storm to get smaller 4 times total
			var stormRunnable = new StormRunnable(this, plugin);
			stormRunnable.runTaskTimer(plugin, 0, 20);

			playerManager.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Be the last one standing!");

			break;
		case COUNTDOWN:
			Bukkit.broadcastMessage(ChatColor.GREEN + "Survival Games has started!");

			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak f");

			lootManager.setChests();
			for(Player player : playerManager.getPlayers())
			{
				Bukkit.getLogger().info("Current Spawn Location Countdown: " + player.getWorld().getSpawnLocation());

				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.setHealth(20);

				player.getInventory().addItem(new ItemStack(Material.LEATHER));
				player.getInventory().addItem(new ItemStack(Material.COMPASS));
			}

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]");
			this.countdown = new Countdown(this);
			this.countdown.runTaskTimerAsynchronously(plugin, 0, 20);

			break;
		case RECRUITING:

			for(Player player : playerManager.getPlayers())
			{
				Bukkit.getLogger().info("Current Spawn Location Recruiting: " + player.getWorld().getSpawnLocation());

				player.teleport(player.getWorld().getSpawnLocation());
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.setHealth(20);
			}

			if(this.chestRunnable != null)
				this.chestRunnable.cancel();
			break;
		case WAITING:

			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "boostpad clear");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ladder clear");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "web clear");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak t");

			if(this.countdown != null)
				this.countdown.cancel();
			for(Player player : playerManager.getPlayers())
			{
				player.setSneaking(true);

				player.teleport(player.getWorld().getSpawnLocation());
				player.getInventory().clear();
			}
			playerManager.getPlayers().clear();
			if(this.chestRunnable != null)
				this.chestRunnable.cancel();

			break;
		case WINNING:
			if(this.chestRunnable != null)
				this.chestRunnable.cancel();

			for(Player player : playerManager.getPlayers())
			{
				Bukkit.getLogger().info("Current Spawn Location Winning: " + player.getWorld().getSpawnLocation());

				plugin.getConfig().set(player.getUniqueId() + ".wins",
						plugin.getConfig().getInt(player.getUniqueId() + ".wins") + 1);
				plugin.saveConfig();

				Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won the game!");
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
						"givegems " + player.getName() + " 250");
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

				player.sendTitle(ChatColor.GREEN + "You won!", null, 20, 20, 20);
				player.setLevel(0);
				Winning winning = new Winning(this, player);
				winning.runTaskLater(plugin, 100);
				return;
			}

			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kill @e[type=item]");
			break;
		default:
			break;
		}
	}

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	public LootManager getLootManager()
	{
		return lootManager;
	}

	public GameState getGameState()
	{
		return gameState;
	}

}
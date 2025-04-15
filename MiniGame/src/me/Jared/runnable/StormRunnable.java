package me.Jared.runnable;

import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StormRunnable extends BukkitRunnable
{
	GameManager gameManager;
	private int seconds = ConfigManager.getStormShrinkTime();
	private final int stormInterval = seconds / 6;
	private int secondsTillShrink = stormInterval;
	private final World world = Bukkit.getWorld("world");
	private final FileConfiguration config;
	private final JavaPlugin plugin;
	private Location stormCenter;
	private int stormRadius;

	public StormRunnable(GameManager gameManager, JavaPlugin plugin)
	{
		this.gameManager = gameManager;
		this.config = plugin.getConfig();
		this.plugin = plugin;
	}

	private BukkitRunnable currentWallTask;

	public void startSmokeWallBorders(Location center, int radius)
	{
		// Cancel the previous task if it exists
		if(currentWallTask != null)
		{
			currentWallTask.cancel();
		}

		this.stormCenter = center;
		this.stormRadius = radius;

		currentWallTask = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					if(isOutsideStorm(player.getLocation()))
					{
						player.damage(2.0); // Half a heart
						player.sendMessage(ChatColor.RED + "You're in the storm!");
						player.sendTitle(ChatColor.RED + "You're in the storm!", "");
						player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation(), 5);
						player.playSound(player.getLocation(), Sound.ENTITY_WITCH_HURT, 1f, 0.8f);

					}
				}

				if(gameManager.getGameState() != GameState.LIVE)
				{
					currentWallTask.cancel();
				}

				World world = center.getWorld();
				if(world == null)
					return;

				// Loop square border
				for(int x = -radius; x <= radius; x++)
				{
					// Z borders
					drawVerticalColumn(world, center.getBlockX() + x, center.getBlockZ() - radius);
					drawVerticalColumn(world, center.getBlockX() + x, center.getBlockZ() + radius);
				}
				for(int z = -radius + 1; z <= radius - 1; z++)
				{
					// X borders
					drawVerticalColumn(world, center.getBlockX() - radius, center.getBlockZ() + z);
					drawVerticalColumn(world, center.getBlockX() + radius, center.getBlockZ() + z);
				}

			}
		};

		// Run every 2 ticks for a persistent effect
		currentWallTask.runTaskTimer(plugin, 0L, 20L);
	}

	private boolean isOutsideStorm(Location loc)
	{
		if(stormCenter == null)
			return false; // Failsafe

		if(!loc.getWorld().equals(stormCenter.getWorld()))
			return false;

		double dx = loc.getX() - stormCenter.getX();
		double dz = loc.getZ() - stormCenter.getZ();
		double dy = loc.getY();

		return Math.abs(dx) > stormRadius || Math.abs(dz) > stormRadius || dy > 70;
	}

	// Helper to draw from ground up to highest block at x/z
	private void drawVerticalColumn(World world, int x, int z)
	{
		int minY = 0; // Raise to make it look like the wall comes from the sky
		int maxY = 70; // Or use world.getMaxHeight()

		for(int y = minY; y <= maxY; y += 2)
		{
			Location loc = new Location(world, x + 0.5, y, z + 0.5);

			// Smoke particle
			world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 0, 0, 0, 0, 0);

			// Add some variation: ash or soul fire for that chaotic storm feel
			if(Math.random() < 0.3)
			{
				world.spawnParticle(Particle.ASH, loc, 0, 0, 0, 0, 0);
			}

			if(Math.random() < 0.1)
			{
				world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 0, 0, 0, 0, 0);
			}
		}
	}

	@Override
	public void run()
	{
		if(seconds > 0)
		{
			if(gameManager.getGameState() == GameState.LIVE)
			{
				//Decrement seconds till the shrink and the total seconds
				secondsTillShrink--;
				seconds--;
				Location center = new Location(Bukkit.getWorld("world"), config.getInt("lobby-spawn.x"),
						config.getInt("lobby-spawn.y"), config.getInt("lobby-spawn.z"));

				//Loop through all players currently in the game
				for(Player player : gameManager.getPlayerManager().getPlayers())
				{
					//Show the player the time left till the next shrink
					player.setLevel(secondsTillShrink);

					// IF seconds and stormInterval divide evenly into each other then shrink the world border
					if(seconds % stormInterval == 0 && seconds > 0)
					{
						//Set the worldsize to the amount of seconds that are left
						startSmokeWallBorders(center, seconds);

						// Resetting the seconds till the next storm shrink
						secondsTillShrink = stormInterval;
					}
				}
			}
		}
		// If it is in any other gamestate besides live cancel this runnable
		else
		{
			for(Player player : gameManager.getPlayerManager().getPlayers())
			{
				player.setLevel(0);
			}

			this.cancel();
		}
	}
}
package me.Jared.runnable;

import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StormRunnable extends BukkitRunnable
{
	GameManager gameManager;
	private int seconds = ConfigManager.getStormShrinkTime();
	private final int stormInterval = seconds / 4;
	private int secondsTillShrink = stormInterval;
	private final World world = Bukkit.getWorld("world");
	private final FileConfiguration config;

	public StormRunnable(GameManager gameManager, JavaPlugin plugin)
	{
		this.gameManager = gameManager;
		this.config = plugin.getConfig();
	}

	@Override
	public void run()
	{
		if(gameManager.getGameState() == GameState.LIVE)
		{
			if(seconds > 0)
			{
				//Decrement seconds till the shrink and the total seconds
				secondsTillShrink--;
				seconds--;
				world.getWorldBorder().setCenter(config.getInt("lobby-spawn.x"), config.getInt("lobby-spawn.z"));

				//Loop through all players currently in the game
				for(Player player : gameManager.getPlayerManager().getPlayers())
				{
					//Show the player the time left till the next shrink
					player.setLevel(secondsTillShrink);

					// IF seconds and stormInterval divide evenly into each other then shrink the world border
					if(seconds % stormInterval == 0)
					{
						//Set the worldsize to the amount of seconds that are left
						world.getWorldBorder().setSize(seconds);

						// Resetting the seconds till the next storm shrink
						secondsTillShrink = stormInterval;
					}
				}
			}
		}
		// If it is in any other gamestate besides live cancel this runnable and reset the world border and center
		else
		{
			for(Player player : gameManager.getPlayerManager().getPlayers())
			{
				player.setLevel(0);
			}

			this.cancel();
			//Reset world border to normal size
			World world = Bukkit.getWorld("world");
			world.getWorldBorder().setSize(30000000);
			world.getWorldBorder().setCenter(0,0);
			Bukkit.getConsoleSender().sendMessage("reset world border and world center");
		}
	}
}
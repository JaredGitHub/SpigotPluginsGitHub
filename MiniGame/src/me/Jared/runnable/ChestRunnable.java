package me.Jared.runnable;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.GameState;
import me.Jared.Loot.ConfigItem;
import me.Jared.Loot.Tier;
import me.Jared.Manager.GameManager;

public class ChestRunnable extends BukkitRunnable
{
	private ArrayList<Location> locations;
	private GameManager gameManager;

	public ChestRunnable(ArrayList<Location> locations, GameManager gameManager)
	{
		this.locations = locations;
		this.gameManager = gameManager;
	}

	@Override
	public void run()
	{
		// spawn particles

		if(gameManager.getGameState() == GameState.LIVE)
		{
			for(Location location : locations)
			{
				if(!gameManager.getChestOpen().get(location))
				{
					// Get the tier based on the location
					Tier tier = ConfigItem.getChestTier(location);

					for(int i = 0; i < 360; i++)
					{
						double x = .5 * Math.cos(i) + location.getX();
						double z = .5 * Math.sin(i) + location.getZ();

						Location loc = new Location(location.getWorld(), x + 0.5, location.getY() + 1.3, z + 0.5);

						if(i % 18 == 0)
						{
							switch(tier)
							{
							case LOW:
								location.getWorld().spawnParticle(Particle.CRIT, loc, 0, 0, 0, 0, 0);
								break;
							case MEDIUM:
								location.getWorld().spawnParticle(Particle.END_ROD, loc, 0, 0, 0, 0, 0);
								break;
							case HIGH:
								location.getWorld().spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0, 0);
								break;
							case SKYHIGH:
								location.getWorld().spawnParticle(Particle.PORTAL, loc, 0, 0, 0, 0, 0);
								break;
							default:
								break;
							}

						}
					}
				}
			}
		} else
		{
			this.cancel();
		}
	}
}

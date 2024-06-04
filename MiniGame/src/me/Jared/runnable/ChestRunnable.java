package me.Jared.runnable;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.GameState;
import me.Jared.Manager.GameManager;

public class ChestRunnable extends BukkitRunnable
{
	private Location location;
	private GameManager gameManager;

	public ChestRunnable(Location location, GameManager gameManager)
	{
		this.location = location;
		this.gameManager = gameManager;
	}

	@Override
	public void run()
	{
		//spawn particles

		if(gameManager.getGameState() == GameState.LIVE)
		{
			if(gameManager.getChestOpen().get(location) == false)
			{
				for(int i = 0; i < 360; i++)
				{
					double x = .5 * Math.cos(i) + location.getX();
					double z = .5 * Math.sin(i) + location.getZ();

					Location loc = new Location(location.getWorld(),x+0.5,location.getY()+1.3,z+0.5);

					if(i%36==0)
					{
						location.getWorld().spawnParticle(Particle.FLAME, loc, 0,0,0,0,0);
					}

				}
			}
		}
		else
		{
			this.cancel();
		}
	}
}

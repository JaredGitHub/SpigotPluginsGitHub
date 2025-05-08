package me.Jared.WarzRunnable;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Warz;
import me.Jared.Loot.LootManager;

public class ParticleRunnable extends BukkitRunnable
{
	private int seconds;

	public ParticleRunnable(int seconds)
	{
		this.seconds = seconds;
	}

	@Override
	public void run()
	{
		if(seconds <= 0)
		{
			this.cancel();
		}

		for(Location location : Warz.getChestLocations())
		{
			for(int i = 0; i < 12; i++)
			{
				double x = .5 * Math.cos(i) + location.getX();
				double z = .5 * Math.sin(i) + location.getZ();

				Location loc = new Location(location.getWorld(), x + 0.5, location.getY() + 1.3, z + 0.5);

				var lootManager = new LootManager();
				var region = lootManager.getRegion(location);
				var zone = lootManager.getZoneFromRegion(region);
				switch(zone)
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
				default:
					break;

				}
			}

			location = new Location(location.getWorld(), location.getX() + 0.5, location.getY() + 1,
					location.getZ() + 0.5);
			removeEntityAtLocation(location);
		}
		seconds--;
	}

	private void removeEntityAtLocation(Location location)
	{
		World world = location.getWorld();
		if(world == null)
		{
			return;
		}

		for(Entity entity : world.getEntities())
		{
			if(entity.getLocation().equals(location))
			{
				entity.remove();
			}
		}
	}

}
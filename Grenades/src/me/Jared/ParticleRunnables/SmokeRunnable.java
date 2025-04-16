package me.Jared.ParticleRunnables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class SmokeRunnable extends BukkitRunnable
{
	private int seconds;
	private int radius;
	private Entity entity;

	public SmokeRunnable(Entity entity, int radius, int seconds)
	{
		this.entity = entity;
		this.radius = radius;
		this.seconds = seconds;
	}

	@Override
	public void run()
	{
		seconds--;
		for(int i = 0; i < 360; i++)
		{
			Location entityLoc = entity.getLocation();
			double x = radius * Math.cos(i) + entityLoc.getX();
			double z = radius * Math.sin(i) + entityLoc.getZ();

			Location loc = new Location(entityLoc.getWorld(),x,entityLoc.getY()+1,z);

			if(i%4==0)
			{
				entityLoc.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 4,2.3,1.3,2.3,0,null,true);
			}
			entity.remove();
		}

		if(seconds <= 0)
		{
			this.cancel();
		}
	}
}
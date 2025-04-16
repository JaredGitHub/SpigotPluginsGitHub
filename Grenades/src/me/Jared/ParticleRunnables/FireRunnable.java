package me.Jared.ParticleRunnables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Grenade;

public class FireRunnable extends BukkitRunnable
{
	private int seconds;
	private int radius;
	private Entity entity;
	private Grenade grenade;

	public FireRunnable(Grenade grenade, Entity entity, int radius, int seconds)
	{
		this.entity = entity;
		this.radius = radius;
		this.seconds = seconds;
		this.grenade = grenade;
	}
	@Override
	public void run()
	{
		
		Location entityLoc = entity.getLocation();
		if(seconds%25==0)
		{
			entityLoc.getWorld().playSound(entityLoc, Sound.BLOCK_FIRE_AMBIENT, 1, 0.5f);
		}
		seconds--;
		for(int i = 0; i < 360; i++)
		{
			grenade.setLocation(entityLoc);
			double x = radius * Math.cos(i) + entityLoc.getX();
			double z = radius * Math.sin(i) + entityLoc.getZ();

			Location loc = new Location(entityLoc.getWorld(),x,entityLoc.getY()+1,z);

			entityLoc.getWorld().spawnParticle(Particle.FLAME, loc, 1,1,1,1,0,null,true);
			entity.remove();
		}

		if(seconds <= 0)
		{
			this.cancel();
			grenade.setLocation(null);
		}
	}
}
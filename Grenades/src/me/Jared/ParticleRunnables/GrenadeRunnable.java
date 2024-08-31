package me.Jared.ParticleRunnables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Grenade;

public class GrenadeRunnable extends BukkitRunnable
{
	private int seconds;
	private float power;
	private Entity entity;
	private Grenade grenade;

	public GrenadeRunnable(Grenade grenade, Entity entity, int seconds, float power)
	{
		this.entity = entity;
		this.seconds = seconds;
		this.grenade = grenade;
		this.power = power;
	}

	@Override
	public void run()
	{
		seconds--;
		Location entityLoc = entity.getLocation();
		grenade.setLocation(entityLoc);

		entityLoc.getWorld().spawnParticle(Particle.EXPLOSION, entityLoc, 2, 2, 2, 1, 1);
		entityLoc.getWorld().spawnParticle(Particle.FLAME, entityLoc, 1, 1, 1, 0, 0);
		entity.remove();

		if(seconds <= 0)
		{
			entityLoc.getWorld().playSound(entityLoc, Sound.ENTITY_GENERIC_EXPLODE, 2, 0.25f);
			this.cancel();
			grenade.setLocation(null);
		}
		
		Location location = entity.getLocation();
		location.getWorld().createExplosion(location, power, false, false);
		entity.remove();
	}
}

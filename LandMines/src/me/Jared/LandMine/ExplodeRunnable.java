package me.Jared.LandMine;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ExplodeRunnable extends BukkitRunnable
{
	private int seconds;
	private int power;
	private Entity entity;

	public ExplodeRunnable(Entity entity, int seconds, int power)
	{
		this.entity = entity;
		this.seconds = seconds;
		this.power = power;
	}

	@Override
	public void run()
	{
		seconds--;
		Location entityLoc = entity.getLocation();

		if(seconds <= 0)
		{
			if(entity instanceof LivingEntity)
			{
				((LivingEntity) entity).damage(power);
			}
			
			entityLoc.getWorld().playSound(entityLoc, Sound.ENTITY_GENERIC_EXPLODE, 2, 0.25f);
			entityLoc.getWorld().spawnParticle(Particle.EXPLOSION, entityLoc, 10, 1, 1, 1, 1);
			entityLoc.getWorld().spawnParticle(Particle.FLAME, entityLoc, 1, 1, 1, 0, 0);
			
			this.cancel();
		}
	}
}

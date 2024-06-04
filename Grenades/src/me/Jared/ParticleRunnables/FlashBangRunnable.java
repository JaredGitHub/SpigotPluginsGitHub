package me.Jared.ParticleRunnables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Grenade;

public class FlashBangRunnable extends BukkitRunnable
{
	private int seconds;
	private Entity entity;
	private Grenade grenade;

	public FlashBangRunnable(Grenade grenade, Entity entity, int seconds)
	{
		this.entity = entity;
		this.seconds = seconds;
		this.grenade = grenade;
	}

	@Override
	public void run()
	{
		seconds--;
		Location entityLoc = entity.getLocation();
		grenade.setLocation(entityLoc);

		entityLoc.getWorld().spawnParticle(Particle.FLASH, entityLoc, 1, 1, 1, 0, 0);
		entity.remove();

		if(seconds <= 0)
		{
			entityLoc.getWorld().playSound(entityLoc, Sound.AMBIENT_UNDERWATER_ENTER, 2, 0.25f);
			this.cancel();
			grenade.setLocation(null);
		}
	}
}
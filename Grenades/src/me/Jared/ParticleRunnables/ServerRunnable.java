package me.Jared.ParticleRunnables;

import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.Jared.Grenade;
import me.Jared.GrenadesMain;

public class ServerRunnable implements Runnable
{
	Grenade[] grenades = GrenadesMain.grenades;

	@Override
	public void run()
	{

		for(int i = 0; i < grenades.length; i++)
		{
			if(grenades[i].getLocation() != null)
			{
				World world = grenades[i].getLocation().getWorld();
				for(Entity entity : world.getEntities())
				{
					if(entity instanceof LivingEntity && entity instanceof Damageable)
					{

						switch(grenades[i].getType())
						{
						case FLASHBANG:

							if(entity.getLocation().distanceSquared(grenades[i].getLocation()) < 13 * 13)
							{
								((LivingEntity) entity)
								.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1));
							}
							break;
						case MOLOTOV:
							if(entity.getLocation().distanceSquared(grenades[i].getLocation()) < 25)
							{
								entity.setFireTicks(20 * 5);
								((Damageable) entity).damage(1.5);
							}
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}
}

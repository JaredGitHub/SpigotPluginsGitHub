package me.Jared.ParticleRunnables;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Grenade;
import me.Jared.GrenadesMain;

public class StickyRunnable extends BukkitRunnable
{
	Entity entity;
	Grenade grenade;

	public StickyRunnable(Grenade grenade, Entity entity)
	{
		this.entity = entity;
		this.grenade = grenade;
	}

	Grenade[] grenades = GrenadesMain.grenades;

	@Override
	public void run()
	{
		grenade.setLocation(entity.getLocation());

		for(Entity victim : entity.getWorld().getEntities())
		{
			if(victim instanceof LivingEntity)
			{
				for(int i = 0; i < grenades.length; i++)
				{
					if(grenades[i].getLocation() != null)
					{
						if(victim.getLocation().distanceSquared(grenades[i].getLocation()) < 8)
						{
							if(!(victim.equals(grenades[i].getThrower())))
							{
								((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 1));
								grenades[i].setStickyVictim(victim);
								grenades[i].remove();
							}
						}
					}
				}
			}
		}
	}
}

package me.Jared.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveBullets extends BukkitRunnable
{
	private String world;
	public RemoveBullets(String worldName)
	{
		this.world = worldName;
	}

	@Override
	public void run()
	{
		for(Entity entity : Bukkit.getWorld(world).getEntities())
		{
			if(entity instanceof Arrow)
			{
				Arrow arrow = (Arrow) entity;
				if(arrow.isOnGround())
				{
					entity.remove();
				}
			}
		}

	}

}

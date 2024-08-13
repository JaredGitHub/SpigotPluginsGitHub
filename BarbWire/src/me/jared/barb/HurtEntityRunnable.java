package me.jared.barb;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;


public class HurtEntityRunnable implements Runnable
{


	Material web = Material.COBWEB;

	@Override
	public void run()
	{
		for(World world : Bukkit.getWorlds())
		{
			for(LivingEntity entity : world.getLivingEntities())
			{
				if(entity.getLocation().getBlock().getType().equals(web) || entity.getEyeLocation().getBlock().getType().equals(web))
				{

					entity.damage(2);
				}
			}
		}
	}
}

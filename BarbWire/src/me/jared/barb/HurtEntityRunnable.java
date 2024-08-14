package me.jared.barb;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
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
				if(inWebs(entity))
				{

					entity.damage(2);
				}
			}
		}
	}

	private boolean inWebs(LivingEntity entity)
	{
		if((entity.getLocation().getBlock() != null && entity.getLocation().getBlock().getType() == Material.COBWEB)
				|| (entity.getLocation().getBlock().getRelative(BlockFace.UP) != null
						&& entity.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.COBWEB))
			return true;
		return false;
	}
}

package me.Jared.LandMine;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jared.BoostPad;

public class LandMineRunnable extends BukkitRunnable
{
	private String getIndex(String string, int num)
	{
		StringBuilder stringBuilder = new StringBuilder();

		int count = 0;
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == ',')
			{
				count++;
				continue;
			}

			if(count == num)
			{
				stringBuilder.append(string.charAt(i));
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public void run()
	{
		for(World world : Bukkit.getWorlds())
		{
			LandMine landMine = new LandMine(world);

			ArrayList<String> landmines = new ArrayList<String>(
					BoostPad.getInstance().getConfig().getStringList("landmines"));

			for(Entity entity : landMine.getWorld().getEntities())
			{
				if(entity instanceof Player)
				{
					Player player = (Player) entity;
					landMine.changeItemToLandMine(player.getInventory());
				}
				Block entityFootY = entity.getLocation().getBlock();

				for(String landminesLoc : landmines)
				{
					double x = Double.parseDouble(getIndex(landminesLoc, 0));
					double y = Double.parseDouble(getIndex(landminesLoc, 1));
					double z = Double.parseDouble(getIndex(landminesLoc, 2));
					String worldStr = getIndex(landminesLoc, 3);
					Location loc = new Location(Bukkit.getWorld(worldStr), x, y, z);

					if(loc.equals(entityFootY.getLocation()))
					{
						if(entityFootY.getType() == Material.STONE_PRESSURE_PLATE)
						{
							if(entityFootY.getType() == landMine.getItem())
							{
								landMine.blowUp(entity.getLocation(), entity);
							}
							if(entity instanceof LivingEntity)
							{
								if(entityFootY.getType() == landMine.getItem())
								{
									landMine.blowUp(entity.getLocation(), entity);
								}
							}
						}
					}
				}
			}
		}
	}
}

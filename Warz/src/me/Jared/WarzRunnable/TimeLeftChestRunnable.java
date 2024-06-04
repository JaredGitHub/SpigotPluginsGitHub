package me.Jared.WarzRunnable;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Warz;

public class TimeLeftChestRunnable extends BukkitRunnable
{
	private int seconds;
	private ArrayList<ArmorStand> armorStands;

	public TimeLeftChestRunnable(int seconds)
	{
		this.armorStands = new ArrayList<>();
		this.seconds = seconds;
	}

	@Override
	public void run()
	{
		for(ArmorStand armorStand : armorStands)
		{
			armorStand.remove();
			removeEntityAtLocation(armorStand.getLocation());
		}
		armorStands.clear();

		if(seconds <= 0)
		{
			this.cancel();
		}

		for(Location location : Warz.getOpenChestLocations())
		{
			Location loc = new Location(location.getWorld(), location.getX() + 0.5, location.getY() + 1,
					location.getZ() + 0.5);
			ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			armorStand.setMarker(true);
			
			int minutes = seconds / 60;
			int secondsLeft = seconds % 60;

			if(seconds % 60 == 0)
			{
				armorStand.setCustomName(ChatColor.GREEN + "" + minutes + "m ");
			} else if(minutes <= 0)
			{
				armorStand.setCustomName(ChatColor.GREEN + "" + secondsLeft + "s");
			} else
			{
				armorStand.setCustomName(ChatColor.GREEN + "" + minutes + "m " + secondsLeft + "s");
			}

			armorStand.setInvisible(true);
			armorStand.setCustomNameVisible(true);
			armorStand.setPersistent(true);
			armorStand.setGravity(false);

			armorStands.add(armorStand);
		}

		seconds--;
	}

	private void removeEntityAtLocation(Location location)
	{
		World world = location.getWorld();
		if(world == null)
		{
			return;
		}

		for(Entity entity : world.getEntities())
		{
			if(entity.getLocation().equals(location))
			{
				entity.remove();
			}
		}
	}

}

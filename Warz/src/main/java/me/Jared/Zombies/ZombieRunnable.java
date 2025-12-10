package me.Jared.Zombies;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieRunnable extends BukkitRunnable
{
	private int radius;
	private int zombieAmount;
	private int interval;
	private int seconds;

	public ZombieRunnable(int radius, int interval)
	{
		this.radius = radius;
		this.interval = interval;
		this.seconds = 0;
	}

	@Override
	public void run()
	{
		seconds++;

		if(seconds % interval == 0)
		{
			Random random = new Random();
			zombieAmount = random.nextInt(6) + 5;
			//Loop through all players and spawn zombie at a radius of 10 on em and make the zombie buffer or weaker depending on the zone
			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(player.getWorld().equals(Bukkit.getWorld("warz")))
				{
					ZombieUtil zombieUtil = new ZombieUtil(player, radius, zombieAmount);
					zombieUtil.spawnZombie();
				}
			}
		}
	}
}

package me.jared.food.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jared.food.Sugar;

public class BleedRunnable extends BukkitRunnable
{

	@Override
	public void run()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(Sugar.bleeders.contains(player.getUniqueId()))
			{
				player.damage(2);
			}
		}	
	}
}

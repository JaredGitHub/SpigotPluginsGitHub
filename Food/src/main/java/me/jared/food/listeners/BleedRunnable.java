package main.java.me.jared.food.listeners;

import main.java.me.jared.food.Sugar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


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

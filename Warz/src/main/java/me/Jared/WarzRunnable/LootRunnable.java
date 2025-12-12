package me.Jared.WarzRunnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Warz;
import me.Jared.Loot.LootManager;


public class LootRunnable extends BukkitRunnable
{
	private int seconds;
	private final String warzWorld;

	public LootRunnable(int seconds, String warzWorld)
	{
		this.seconds = seconds;
		this.warzWorld = warzWorld;
	}

	@Override
	public void run()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player.getWorld().equals(Bukkit.getWorld(warzWorld)))
			{
				player.setLevel(seconds);
			}
		}

		if(seconds == 60)
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(player.getWorld().equals(Bukkit.getWorld(warzWorld)))
				{
					player.sendMessage(ChatColor.GOLD + "There is one more minute before the chests reset!");
				}
			}
		}

		if(seconds <= 0)
		{
			this.cancel();

			int interval = 300;
			var lootManager = new LootManager(warzWorld);
			lootManager.setChests();
			lootManager.runLootRunnable(interval, warzWorld);

			var particleRunnable = new ParticleRunnable(interval,warzWorld, lootManager);
			particleRunnable.runTaskTimer(Warz.getInstance(), 0, 20);

			var timeLeftChestRunnable = new TimeLeftChestRunnable(interval,warzWorld);
			timeLeftChestRunnable.runTaskTimer(Warz.getInstance(), 0, 20);

			for(Player online : Bukkit.getOnlinePlayers())
			{
				if(online.getWorld().equals(Bukkit.getWorld(warzWorld)))
				{
					online.playSound(online, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					online.sendMessage(ChatColor.GREEN + "Chests have been refilled!");
				}
			}
		}
		seconds--;
	}
}



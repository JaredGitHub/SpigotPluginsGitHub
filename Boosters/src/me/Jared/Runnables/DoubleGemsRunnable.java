package me.Jared.Runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.Boosters;
import me.Jared.StatScoreboard;
import me.Jared.Stats;

public class DoubleGemsRunnable extends BukkitRunnable
{
	private int minutes;
	private FileConfiguration config;
	private Stats stats = Stats.getPlugin(Stats.class);

	public DoubleGemsRunnable(FileConfiguration config, int minutes)
	{
		this.minutes = minutes;
		this.config = config;
	}
	@Override
	public void run()
	{
		if(config.getBoolean("doublegems"))
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				new StatScoreboard(stats, p);
			}
			Bukkit.getConsoleSender().sendMessage(minutes + " Minutes Left Of Double Gems Booster");
			if(minutes == 60)
			{
				Bukkit.broadcastMessage(ChatColor.GOLD + "There is one more hour left of the double gems booster");
			}

			if(minutes == 10)
			{
				Bukkit.broadcastMessage(ChatColor.GOLD + "There is 10 more minutes left of the double gems booster");
			}

			if(minutes <= 0)
			{

				Bukkit.broadcastMessage(ChatColor.RED + "Double gems booster is now inactive!");
				config.set("doublegems", false);
				Boosters.getInstance().saveConfig();
				
				for(Player p : Bukkit.getOnlinePlayers())
				{
					new StatScoreboard(stats, p);
				}
				this.cancel();
			}
			minutes--;
		}
		else
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				new StatScoreboard(stats, p);
			}
			Bukkit.broadcastMessage(ChatColor.RED + "Double gems booster is now inactive!");
			config.set("doublegems", false);
			Boosters.getInstance().saveConfig();
			this.cancel();
		}
	}
}

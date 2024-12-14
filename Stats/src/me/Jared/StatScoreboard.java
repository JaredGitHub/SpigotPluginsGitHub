package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class StatScoreboard
{
	Stats stats;
	FileConfiguration config;

	public StatScoreboard(Stats stats, Player player)
	{
		this.stats = stats;
		this.config = stats.getConfig();

		player.setScoreboard(this.getScoreboard(player));
		stats.getNametagManager().setNametags(player);
		stats.getNametagManager().newTag(player);
	}

	public String formatNumber(long number)
	{
		if(number >= 1_000_000_000)
		{
			return String.format("%.1fB", number / 1_000_000_000.0);
		} else if(number >= 1_000_000)
		{
			return String.format("%.1fM", number / 1_000_000.0);
		} else if(number >= 1_000)
		{
			return String.format("%.1fK", number / 1_000.0);
		} else
		{
			return String.valueOf(number);
		}
	}

	public Scoreboard getScoreboard(Player p)
	{
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("Statistics", Criteria.DUMMY,
				ChatColor.GOLD + "" + ChatColor.BOLD + "JaredServer");
		Objective healthObjective = scoreboard.registerNewObjective("Health", Criteria.HEALTH, ChatColor.RED + "❤");
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

		for(String entry : scoreboard.getEntries())
		{
			scoreboard.resetScores(entry);
		}
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "JaredServer");

		double kills = this.config.getInt(p.getUniqueId() + ".kills");
		double deaths = this.config.getInt(p.getUniqueId() + ".deaths");

		double kdr;
		if(deaths < 1.0D)
		{
			kdr = kills;
		} else
		{
			kdr = kills / deaths;
		}
		objective.getScore(" ").setScore(21);

		objective.getScore(
				ChatColor.GOLD + "Kills: " + ChatColor.GREEN + formatNumber(config.getInt(p.getUniqueId() + ".kills")))
				.setScore(20);
		objective.getScore(
				ChatColor.GOLD + "Deaths: " + ChatColor.RED + formatNumber(config.getInt(p.getUniqueId() + ".deaths")))
				.setScore(9);
		objective.getScore(ChatColor.GOLD + "KDR: " + ChatColor.RESET + (Math.round(kdr * 100.0D) / 100.0D))
				.setScore(8);

		int initialKillsToRankup = 10;
		int accumulatedKillsToRankup = 0;
		int rank = 0;

		for(int i = 1; i < kills; i++)
		{
			accumulatedKillsToRankup += initialKillsToRankup;
			accumulatedKillsToRankup *= 1.15;

			if(accumulatedKillsToRankup % i == 0)
			{
				if(accumulatedKillsToRankup >= kills)
				{
					break;
				}
				rank++;
			}
		}

		objective.getScore(ChatColor.GOLD + "Rank: " + ChatColor.RESET + rank).setScore(7);
		config.set(p.getUniqueId() + ".rank", rank);
		stats.saveConfig();
		
		objective.getScore("").setScore(6);
		objective.getScore(ChatColor.GOLD + "KillStreak: " + ChatColor.RESET
				+ config.getInt(String.valueOf(p.getUniqueId()) + ".killStreak")).setScore(5);
		objective.getScore(ChatColor.GOLD + "Highest KS: " + ChatColor.DARK_PURPLE
				+ config.getInt(String.valueOf(p.getUniqueId()) + ".highks")).setScore(4);
		objective.getScore(ChatColor.GOLD + "Zombie Kills: " + ChatColor.DARK_GREEN
				+ formatNumber(config.getInt(p.getUniqueId() + ".zombiekills"))).setScore(3);
		objective.getScore(
				ChatColor.GOLD + "Gems: " + ChatColor.GREEN + formatNumber(config.getInt(p.getUniqueId() + ".gems")))
				.setScore(2);

		if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
		{
			objective.getScore(ChatColor.BOLD + "" + ChatColor.GREEN + " Double Gems Active!").setScore(1);
		}
		if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("DoubleLoot"))
		{
			objective.getScore(ChatColor.BOLD + "" + ChatColor.GREEN + " Double Loot Active!").setScore(1);
		}

		return scoreboard;

	}
}

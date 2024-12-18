package me.Jared;

import java.util.UUID;

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

	private String formatNumber(long number)
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

		// Sidebar objective
		Objective objective = scoreboard.registerNewObjective("Statistics", Criteria.DUMMY,
				ChatColor.GOLD + "" + ChatColor.BOLD + "JaredServer");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Health objective
		Objective healthObjective = scoreboard.getObjective("Health");
		if(healthObjective == null)
		{
			healthObjective = scoreboard.registerNewObjective("Health", Criteria.HEALTH, ChatColor.RED + "❤");
		}
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

		// Fetch stats
		int kills = config.getInt(p.getUniqueId() + ".kills");
		int deaths = config.getInt(p.getUniqueId() + ".deaths");
		int killStreak = config.getInt(p.getUniqueId() + ".killStreak");
		int highKS = config.getInt(p.getUniqueId() + ".highks");
		int zombieKills = config.getInt(p.getUniqueId() + ".zombiekills");
		int gems = config.getInt(p.getUniqueId() + ".gems");

		double kdr = deaths == 0 ? kills : (double) kills / deaths;

		// Rank calculation
		// ELO-based ranking system
		int elo = calculateELO(kills, deaths, config, p.getUniqueId()); // Calculate player's ELO
		String rank = getRankByELO(elo); // Map ELO to a rank

		config.set(p.getUniqueId() + ".rank",rank);

		// Scoreboard entries
		objective.getScore(" ").setScore(21);
		objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.GREEN + formatNumber(kills)).setScore(20);
		objective.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.RED + formatNumber(deaths)).setScore(19);
		objective.getScore(ChatColor.GOLD + "KDR: " + ChatColor.RESET + String.format("%.2f", kdr)).setScore(18);
		objective.getScore(ChatColor.GOLD + "Rank: " +  ChatColor.translateAlternateColorCodes('&', rank)).setScore(17);

		objective.getScore("").setScore(16);
		objective.getScore(ChatColor.GOLD + "KillStreak: " + ChatColor.RESET + killStreak).setScore(15);
		objective.getScore(ChatColor.GOLD + "Highest KS: " + ChatColor.DARK_PURPLE + highKS).setScore(14);
		objective.getScore(ChatColor.GOLD + "Zombie Kills: " + ChatColor.DARK_GREEN + formatNumber(zombieKills))
				.setScore(13);
		objective.getScore(ChatColor.GOLD + "Gems: " + ChatColor.GREEN + formatNumber(gems)).setScore(12);

		// Boosters plugin
		if(Bukkit.getPluginManager().getPlugin("Boosters") != null)
		{
			FileConfiguration boosterConfig = Bukkit.getPluginManager().getPlugin("Boosters").getConfig();
			if(boosterConfig.getBoolean("doublegems"))
			{
				objective.getScore(ChatColor.BOLD + "" + ChatColor.GREEN + " Double Gems Active!").setScore(1);
			}
			if(boosterConfig.getBoolean("DoubleLoot"))
			{
				objective.getScore(ChatColor.BOLD + "" + ChatColor.AQUA + " Double Loot Active!").setScore(0);
			}
		}

		stats.saveConfig();

		return scoreboard;

	}

	private int calculateELO(int kills, int deaths, FileConfiguration config, UUID uuid)
	{
		final int BASE_ELO = 1000; // Starting ELO for all players
		final int K_FACTOR = 32; // Determines how much ELO changes per "match"

		// Fetch current ELO or set default if not present
		int currentELO = config.getInt(uuid + ".elo", BASE_ELO);

		// Calculate player's score (simple win/loss based on kills and deaths)
		double score = deaths == 0 ? 1.0 : (double) kills / (kills + deaths);

		// Expected performance (Assume opponent's ELO is 1000 for simplicity)
		double opponentELO = 1000;
		double expected = 1 / (1 + Math.pow(10, (opponentELO - currentELO) / 400.0));

		// Update ELO
		int newELO = (int) (currentELO + K_FACTOR * (score - expected));
		config.set(uuid + ".elo", newELO); // Save updated ELO
		stats.saveConfig();
		return newELO;
	}

	private String getRankByELO(int elo)
	{
		if(elo < 1200)
		{
			return "&7Bambi";
		} else if(elo < 1400)
		{
			return "&aScavenger";
		} else if(elo < 1600)
		{
			return "&bCitizen";
		} else if(elo < 1800)
		{
			return "&cHunter";
		} else if(elo < 2000)
		{
			return "&9Survivor";
		} else if(elo < 2200)
		{
			return "&6Officer";
		} else if(elo < 2400)
		{
			return "&0Deputy";
		} else if(elo < 2600)
		{
			return "&1Sheriff";
		} else if(elo < 2800)
		{
			return "&bSoldier";
		} else if(elo < 3000)
		{
			return "&dWarrior";
		} else if(elo < 3200)
		{
			return "&bHero";
		} else if(elo < 3500)
		{
			return "&cLegend";
		} else
		{
			return "&eImmortal";
		}
	}

}
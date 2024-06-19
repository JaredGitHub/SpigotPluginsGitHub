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

	public Scoreboard getScoreboard(Player p)
	{
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("Statistics", Criteria.DUMMY, ChatColor.GOLD + "" + ChatColor.BOLD + "JaredServer");
		Objective healthObjective = scoreboard.registerNewObjective("Health", Criteria.HEALTH, ChatColor.RED + "❤");
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
	
		for(String entry : scoreboard.getEntries())
		{
			scoreboard.resetScores(entry);
		}
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "JaredServer");


		objective.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=-=-=-=-").setScore(9);
		objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.RESET + config.getInt(p.getUniqueId() +".kills")).setScore(8);
		objective.getScore(ChatColor.GOLD + "Zombie Kills: " + ChatColor.RESET + config.getInt(p.getUniqueId() +".zombiekills")).setScore(7);
		objective.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.RESET + config.getInt(p.getUniqueId() +".deaths")).setScore(6);
		objective.getScore(ChatColor.GOLD + "Highest KS: " + ChatColor.RESET + config.getInt(String.valueOf(p.getUniqueId()) + ".highks")).setScore(5);
		objective.getScore(ChatColor.GOLD + "KillStreak: " + ChatColor.RESET + config.getInt(String.valueOf(p.getUniqueId()) + ".killStreak")).setScore(4);
		objective.getScore(ChatColor.GOLD + "Gems: " + ChatColor.RESET + config.getInt(p.getUniqueId() + ".gems")).setScore(2);

		double kills = this.config.getInt(p.getUniqueId() + ".kills");
		double deaths = this.config.getInt(p.getUniqueId() + ".deaths");

		double kdr;
		if (deaths < 1.0D) {
			kdr = kills;
		} else {
			kdr = kills / deaths;
		}

		objective.getScore(ChatColor.GOLD + "KDR: " + ChatColor.RESET +(Math.round(kdr * 100.0D) / 100.0D)).setScore(3);

		if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
		{
			objective.getScore(ChatColor.BOLD +"" + ChatColor.GREEN + " Double Gems Active!").setScore(1);
		}
		else
		{
			objective.getScore(ChatColor.ITALIC +"" + ChatColor.UNDERLINE + "      store.jaredcoen.com      ").setScore(1);
		}
		objective.getScore(ChatColor.BLUE + ""+ "=-=-=-=-=-=-=-=-=-=-=-=").setScore(0);

		return scoreboard;

	}
}

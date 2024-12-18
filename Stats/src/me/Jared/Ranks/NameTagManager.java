package me.Jared.Ranks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import me.Jared.Stats;
import net.md_5.bungee.api.ChatColor;


public class NameTagManager
{
	Stats stats;
	public NameTagManager(Stats stats)
	{
		this.stats = stats;
	}

	public void setNametags(Player player)
	{

		for(Rank rank : Rank.values())
		{
			if(player.getScoreboard().getTeam(rank.name()) == null)
			{
				Team team = player.getScoreboard().registerNewTeam(rank.name());
				team.setPrefix(rank.getDisplay());
				
				String numberRank = ChatColor.translateAlternateColorCodes('&', stats.getConfig().getString(player.getUniqueId() + ".rank"));
				team.setSuffix(" [" + numberRank + ChatColor.RESET + "]");
			}
			else
			{
				Team team = player.getScoreboard().getTeam(rank.name());
				team.setPrefix(rank.getDisplay());
				
				String numberRank = ChatColor.translateAlternateColorCodes('&',stats.getConfig().getString(player.getUniqueId() + ".rank"));
				team.setSuffix(" [" + numberRank + ChatColor.RESET + "]");

			}
		}

		for(Player target : Bukkit.getOnlinePlayers())
		{	
			if(player.getUniqueId() != target.getUniqueId())
			{
				player.getScoreboard().getTeam(stats.getRankManager().getRank(target.getUniqueId()).name()).addEntry(target.getName());
				
				String numberRank = ChatColor.translateAlternateColorCodes('&',stats.getConfig().getString(target.getUniqueId() + ".rank"));
				player.getScoreboard().getTeam(stats.getRankManager().getRank(target.getUniqueId()).name()).setSuffix(" [" + numberRank + ChatColor.RESET + "]");
				
				String numberRank1 = ChatColor.translateAlternateColorCodes('&',stats.getConfig().getString(player.getUniqueId() + ".rank"));
				target.getScoreboard().getTeam(stats.getRankManager().getRank(player.getUniqueId()).name()).setSuffix(" [" + numberRank1 + ChatColor.RESET + "]");
			}
		}
	}

	public void newTag(Player player)
	{
		Rank rank = stats.getRankManager().getRank(player.getUniqueId());

		for(Player target : Bukkit.getOnlinePlayers())
		{
			if(target.getScoreboard().getTeam(rank.name()) != null)
			{
				target.getScoreboard().getTeam(rank.name()).addEntry(player.getName());
				
				String numberRank = ChatColor.translateAlternateColorCodes('&',stats.getConfig().getString(target.getUniqueId() + ".rank"));
				player.getScoreboard().getTeam(stats.getRankManager().getRank(target.getUniqueId()).name()).setSuffix(" [" + numberRank + ChatColor.RESET + "]");
				
				String numberRank1 = ChatColor.translateAlternateColorCodes('&',stats.getConfig().getString(player.getUniqueId() + ".rank"));
				target.getScoreboard().getTeam(stats.getRankManager().getRank(player.getUniqueId()).name()).setSuffix(" [" + numberRank1 + ChatColor.RESET + "]");
			}
		}
	}

	public void removeTag(Player player)
	{
		for(Player target : Bukkit.getOnlinePlayers())
		{
			if(target.getScoreboard().getEntryTeam(player.getName()) != null)
			{
				target.getScoreboard().getEntryTeam(player.getName()).removeEntry(player.getName());
			}
		}
	}
}
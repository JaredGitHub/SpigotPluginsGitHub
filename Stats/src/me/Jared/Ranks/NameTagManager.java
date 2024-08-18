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
				
				int numberRank = stats.getConfig().getInt(player.getUniqueId() + ".rank");
				team.setSuffix(ChatColor.BOLD + " [" + numberRank + "]");
			}
			else
			{
				Team team = player.getScoreboard().getTeam(rank.name());
				team.setPrefix(rank.getDisplay());
				
				int numberRank = stats.getConfig().getInt(player.getUniqueId() + ".rank");
				team.setSuffix(ChatColor.BOLD + " [" + numberRank + "]");

			}

		}

		for(Player target : Bukkit.getOnlinePlayers())
		{	
			if(player.getUniqueId() != target.getUniqueId())
			{
				player.getScoreboard().getTeam(stats.getRankManager().getRank(target.getUniqueId()).name()).addEntry(target.getName());
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

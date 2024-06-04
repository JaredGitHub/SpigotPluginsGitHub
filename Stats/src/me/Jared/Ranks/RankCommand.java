package me.Jared.Ranks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Jared.Stats;

public class RankCommand implements CommandExecutor
{

	Stats stats;
	public RankCommand(Stats stats)
	{
		this.stats = stats;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(sender.hasPermission("stats"))
		{
			if(args.length == 2)
			{
				if(Bukkit.getOfflinePlayer(args[0]) != null)
				{
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
					
					for(Rank rank : Rank.values())
					{
						if(rank.name().equalsIgnoreCase(args[1]))
						{
							stats.getRankManager().setRank(target.getUniqueId(), rank, false);
							
							sender.sendMessage(ChatColor.GREEN + target.getName() + " is now " + rank.getDisplay() + ChatColor.GREEN + " rank!");
							
							if(target.isOnline())
							{
								target.getPlayer().sendMessage(ChatColor.GREEN + "You now have " + rank.getDisplay() + ChatColor.GREEN + " rank!");
								target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
								
								stats.getNametagManager().setNametags(target.getPlayer());
								stats.getNametagManager().newTag(target.getPlayer());
							}
							
							return false;
						}
					}
					
					sender.sendMessage(ChatColor.RED + "Valid rank options include, VIP, VIPPLUS, MVP, and MVPPLUS!");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Player doesn't exist!");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Usage: /rank <player> <rank>");
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "You must be op!");
		}
		
		return false;
	}

}

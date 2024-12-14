package me.Jared.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Boosters;
import me.Jared.StatScoreboard;
import me.Jared.Stats;
import me.Jared.Runnables.DoubleGemsRunnable;
import me.Jared.Runnables.DoubleLootRunnable;

public class BoosterCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(!sender.hasPermission("booster"))
			return true;
		if(cmd.getName().equalsIgnoreCase("boost"))
		{
			if(args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Do /boost doublegems <playerName> <on/off> <minutes>!!");
			}

			if(args.length >= 2)
			{
				if(args[0].equalsIgnoreCase("doublegems"))
				{
					if(args.length == 4)
					{
						if(args[2].equalsIgnoreCase("on"))
						{
							FileConfiguration config = Boosters.getInstance().getConfig();
							if(config.getBoolean("doublegems"))
							{
								sender.sendMessage(ChatColor.RED + "It's already on noob!");
								return true;
							}

							sender.sendMessage(ChatColor.GREEN + args[1] + " has set boosters to on");
							Bukkit.broadcastMessage(
									ChatColor.GREEN + args[1] + " has set double gems booster for " + args[3] + " minutes!");

							int time = Integer.parseInt(args[3]);
							var runnable = new DoubleGemsRunnable(config, time);
							runnable.runTaskTimer(Boosters.getInstance(), 0, 20 * 60);

							config.set("doublegems", true);
							Boosters.getInstance().saveConfig();
						}
					}

					if(args[1].equalsIgnoreCase("off"))
					{
						FileConfiguration config = Boosters.getInstance().getConfig();

						sender.sendMessage(ChatColor.RED + "Set boosters to off");

						config.set("doublegems", false);
						Boosters.getInstance().saveConfig();

						for(Player p : Bukkit.getOnlinePlayers())
						{
							new StatScoreboard(Stats.getPlugin(Stats.class), p);
						}
					}
				}
				else if(args[0].equalsIgnoreCase("doubleloot"))
				{
					if(args.length == 4)
					{
						if(args[2].equalsIgnoreCase("on"))
						{
							FileConfiguration config = Boosters.getInstance().getConfig();
							if(config.getBoolean("DoubleLoot"))
							{
								sender.sendMessage(ChatColor.RED + "It's already on noob!");
								return true;
							}

							sender.sendMessage(ChatColor.GREEN + args[1] + " has set boosters to on");
							Bukkit.broadcastMessage(
									ChatColor.GREEN + args[1] + " has set Double Loot booster for " + args[3] + " minutes!");

							int time = Integer.parseInt(args[3]);
							var runnable = new DoubleLootRunnable(config, time);
							runnable.runTaskTimer(Boosters.getInstance(), 0, 20 * 60);

							config.set("DoubleLoot", true);
							Boosters.getInstance().saveConfig();
						}
					}

					if(args[1].equalsIgnoreCase("off"))
					{
						FileConfiguration config = Boosters.getInstance().getConfig();

						sender.sendMessage(ChatColor.RED + "Set boosters to off");

						config.set("DoubleLoot", false);
						Boosters.getInstance().saveConfig();

						for(Player p : Bukkit.getOnlinePlayers())
						{
							new StatScoreboard(Stats.getPlugin(Stats.class), p);
						}
					}
				}
			}
		}

		return true;
	}

}

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

public class BoosterCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(!sender.hasPermission("booster")) return true;
		if(cmd.getName().equalsIgnoreCase("boost"))
		{
			if(args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Do /boost doublegems!!");
			}

			if(args.length >= 2)
			{
				if(args[0].equalsIgnoreCase("doublegems"))
				{
					if(args.length == 3)
					{
						if(args[1].equalsIgnoreCase("on"))
						{
							FileConfiguration config = Boosters.getInstance().getConfig();
							if(config.getBoolean("doublegems"))
							{
								sender.sendMessage(ChatColor.RED + "It's already on noob!");
								return true;
							}

							sender.sendMessage(ChatColor.GREEN + "Set boosters to on");
							Bukkit.broadcastMessage(ChatColor.GREEN + "Set double gems booster for " + args[2] + " minutes!");
							
							int time = Integer.parseInt(args[2]);
							var runnable = new DoubleGemsRunnable(config, time);
							runnable.runTaskTimer(Boosters.getInstance(), 0, 20*60);

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
			}
		}

		return true;
	}

}

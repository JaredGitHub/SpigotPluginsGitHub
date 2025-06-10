package me.jared.food;

import main.java.me.jared.food.Sugar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParticleCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{


		if(sender instanceof Player)
		{
			if(sender.hasPermission("food"))
			{
				if(args.length < 1)
				{
					sender.sendMessage(ChatColor.RED + "Usage: /foodparticles <amount>");
					return true;
				}
				try
				{
					int amount = Integer.parseInt(args[0]);

					if(amount >= 10000)
					{
						sender.sendMessage(ChatColor.RED + "Woah there bucko, that is way too many particles, I tell yah!");
						return true;
					}

					sender.sendMessage(ChatColor.GREEN + "You have made the food have " + amount + " particles per eat!");
					Sugar.plugin.getConfig().set("particleAmount", amount);
					Sugar.plugin.saveConfig();

				}catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "Please use number for second argument!");
				}
			}
		}
		return true;
	}
}

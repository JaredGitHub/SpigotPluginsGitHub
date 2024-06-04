package me.jared.cleanup;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.jared.BoostPad;

public class Cleanup implements CommandExecutor
{
	FileConfiguration config = BoostPad.getPlugin(BoostPad.class).getConfig();

	private String getIndex(String string, int num)
	{
		StringBuilder stringBuilder = new StringBuilder();

		int count = 0;
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == ',')
			{
				count++;
				continue;
			}

			if(count == num)
			{
				stringBuilder.append(string.charAt(i));
			}
		}

		return stringBuilder.toString();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(!sender.hasPermission("cleanup.cleanup"))
		{
			sender.sendMessage(ChatColor.RED + "Nice try noob, but not today!!");
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("boostpad"))
		{
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.GRAY + "Usage: /boostpad clear");
				return true;
			}

			if (args.length > 0 && args[0].equalsIgnoreCase("clear"))
			{
				ArrayList<String> boostPads = new ArrayList<String>(config.getStringList("boostPads"));

				int count = 0;
				for (String boostPadLoc : boostPads)
				{
					double x = Double.parseDouble(getIndex(boostPadLoc, 0));
					double y = Double.parseDouble(getIndex(boostPadLoc, 1));
					double z = Double.parseDouble(getIndex(boostPadLoc, 2));
					String world = getIndex(boostPadLoc, 3);
					Location loc = new Location(Bukkit.getWorld(world), x, y, z);

					loc.getBlock().setType(Material.AIR);
					config.set("boostPads", null);
					BoostPad.getPlugin(BoostPad.class).saveConfig();
					count++;
				}
				sender.sendMessage(ChatColor.GREEN + "Removed all " + count + " placed boost pads");
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("ladder"))
		{
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.GRAY + "Usage: /ladder clear");
				return true;
			}

			if (args.length > 0 && args[0].equalsIgnoreCase("clear"))
			{
				ArrayList<String> ladders = new ArrayList<String>(config.getStringList("ladders"));

				int count = 0;
				for (String laddersLoc : ladders)
				{
					double x = Double.parseDouble(getIndex(laddersLoc, 0));
					double y = Double.parseDouble(getIndex(laddersLoc, 1));
					double z = Double.parseDouble(getIndex(laddersLoc, 2));
					String world = getIndex(laddersLoc, 3);
					Location loc = new Location(Bukkit.getWorld(world), x, y, z);

					loc.getBlock().setType(Material.AIR);
					config.set("ladders", null);
					BoostPad.getPlugin(BoostPad.class).saveConfig();
					count++;
				}

				sender.sendMessage(ChatColor.GREEN + "Removed all " + count + " placed ladders");
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("web"))
		{
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.GRAY + "Usage: /web clear");
				return true;
			}

			if (args.length > 0 && args[0].equalsIgnoreCase("clear"))
			{
				ArrayList<String> webs = new ArrayList<String>(config.getStringList("webs"));

				int count = 0;
				for (String websLoc : webs)
				{
					double x = Double.parseDouble(getIndex(websLoc, 0));
					double y = Double.parseDouble(getIndex(websLoc, 1));
					double z = Double.parseDouble(getIndex(websLoc, 2));

					String world = getIndex(websLoc, 3);
					Location loc = new Location(Bukkit.getWorld(world), x, y, z);

					loc.getBlock().setType(Material.AIR);
					config.set("webs", null);
					BoostPad.getPlugin(BoostPad.class).saveConfig();

					count++;
				}
				sender.sendMessage(ChatColor.GREEN + "Removed all " + count + " placed webs");
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("landmines"))
		{
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.GRAY + "Usage: /landmine clear");
				return true;
			}

			if (args.length > 0 && args[0].equalsIgnoreCase("clear"))
			{
				ArrayList<String> landmines = new ArrayList<String>(config.getStringList("landmines"));

				int count = 0;
				for (String landmineLoc : landmines)
				{
					double x = Double.parseDouble(getIndex(landmineLoc, 0));
					double y = Double.parseDouble(getIndex(landmineLoc, 1));
					double z = Double.parseDouble(getIndex(landmineLoc, 2));
					String world = getIndex(landmineLoc, 3);
					Location loc = new Location(Bukkit.getWorld(world), x, y, z);

					loc.getBlock().setType(Material.AIR);
					config.set("landmines", null);
					BoostPad.getPlugin(BoostPad.class).saveConfig();

					count++;
				}
				sender.sendMessage(ChatColor.GREEN + "Removed all " + count + " placed landmines");
			}
			return true;
		}
		return false;
	}
}

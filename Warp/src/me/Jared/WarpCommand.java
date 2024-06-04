package me.Jared;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Menus.WarpMenu;

public class WarpCommand implements CommandExecutor, TabCompleter
{

	FileConfiguration config = WarpMain.getInstance().getConfig();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("warp"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				if(args.length == 0)
				{
					WarpMenu menu = new WarpMenu(WarpMain.getPlayerMenuUtility(player));
					menu.open();
				}

				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("help"))
					{
						if(player.hasPermission("warpedit"))
						{
							player.sendMessage(ChatColor.GOLD + "----------Warp Help----------");
							player.sendMessage(ChatColor.GOLD + "-" + ChatColor.GRAY
									+ "Type /warp add <warp name> to set a warp to your location");
							player.sendMessage(ChatColor.GOLD + "-" + ChatColor.GRAY
									+ "Type /warp remove <warp name> to remove a warp.");
							player.sendMessage(
									ChatColor.GOLD + "-" + ChatColor.GRAY + "Type /warp list to get a list of warps");
						}
						return true;
					}

					if(args[0].equals("list"))
					{
						player.sendMessage(ChatColor.GOLD + "----------Warp List----------");
						for(String warpName : ConfigManager.getWarps())
						{
							player.sendMessage(ChatColor.GOLD + "- " + ChatColor.WHITE + warpName);
						}
						return true;
					}

					for(String warpName : ConfigManager.getWarps())
					{
						if(ConfigManager.getWarp(warpName) != null
								&& args[0].equalsIgnoreCase(warpName))
						{
							ConfigManager.teleportPlayerToWarp(player, warpName);
							return true;
						}
					}
					player.sendMessage(ChatColor.RED + "Type /warp list to get a list of warps!");
					return true;
				}

				if(args.length == 2)
				{
					if(player.hasPermission("warps"))
					{
						if(args[0].equals("add"))
						{
							String warpName = args[1];
							ConfigManager.addWarp(player.getLocation(), warpName);
							player.sendMessage(ChatColor.GREEN + "Successfully added warp " + ChatColor.WHITE + warpName);
						}

						if(args[0].equals("remove"))
						{
							try
							{
								String warp = args[1];

								if(ConfigManager.getWarps().isEmpty())
								{
									player.sendMessage(
											ChatColor.RED + "There are currently no warps. Type /warp add <warp name> ");
								} else
								{
									for(String warpName : ConfigManager.getWarps())
									{
										if(ConfigManager.getWarp(warpName) != null
												&& warp.equals(warpName))
										{
											player.sendMessage(ChatColor.GREEN + "Successfully removed warp " 
													+ ChatColor.WHITE + warpName);

											ConfigManager.removeWarp(warpName);
											return true;
										}
									}
									player.sendMessage(ChatColor.RED + "Type /warp list to get a list of warps!");
									return true;
								}
							} catch(NumberFormatException e)
							{
								player.sendMessage(ChatColor.RED + "Make sure you use a number. Type /warp list to view all warps");
								return true;
							}
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "No permission NOOOB!!!");
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();

		if(cmd.getName().equalsIgnoreCase("warp"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				for(String warp : ConfigManager.getWarps())
				{
					list.add(warp);
				}

				if(player.hasPermission("warpedit"))
				{
					list.add("add");
					list.add("remove");
					list.add("help");
					list.add("list");
				} else
				{
					list.add("list");
				}
			}
		}

		return list;
	}

}

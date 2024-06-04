package me.Jared.LandMine;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandMineCommand implements CommandExecutor, TabCompleter
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;

			if(!player.isOp())
			{
				player.sendMessage(ChatColor.RED + "NO NOOOOB");
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("landmine"))
			{
				if(args.length >= 2)
				{
					LandMine mine = new LandMine();
					String arg1 = args[1];
					if(args[0].equalsIgnoreCase("displayname"))
					{
						StringBuilder strBuilder = new StringBuilder();
						for(int i = 1; i < args.length; i++)
						{
							strBuilder.append(args[i]);
							strBuilder.append(" ");
						}
						arg1 = strBuilder.toString();
						arg1.stripTrailing();

						mine.setItemName(arg1);
						player.sendMessage(ChatColor.GREEN + "Successfully set land mine display name to " + arg1);
					}

					if(args[0].equalsIgnoreCase("setitem"))
					{
						for(Material material : Material.values())
						{
							if(args[1].equalsIgnoreCase(material.name()))
							{
								mine.setItem(args[1]);
								player.sendMessage(ChatColor.GREEN + "Successfully set land mine material to " + args[1]);
								return true;
							}						
						}
						player.sendMessage(ChatColor.RED + "That is not a material!");	
					}
				}
			}
			else if(args.length == 0)
			{
				LandMine mine = new LandMine();
				player.getInventory().addItem(new ItemStack(mine.getItem()));
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Usage: /landmine <displayname|blowupsize|item>");
			}
		}
		return true;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();

		if(cmd.getName().equalsIgnoreCase("landmine"))
		{
			if(args.length == 1)
			{
				list.add("displayname");
				list.add("setitem");
			}
			else if(args.length == 2 && args[0].equalsIgnoreCase("setitem"))
			{
				for(Material material : Material.values())
				{
					if(material.isBlock())
					{
						list.add(material.name());

					}
				}
			}
		}
		return list;
	}
}

package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Jared.Menus.GameGUIMenu;

public class GameGUICommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("games") || cmd.getName().equalsIgnoreCase("g"))
			{
				GameGUIMenu menu = new GameGUIMenu(GameGUI.getPlayerMenuUtility(player));
				menu.open();
			}
		}
		else if(args.length == 1)
		{
			if(cmd.getName().equalsIgnoreCase("games") || cmd.getName().equalsIgnoreCase("g"))
			{
				Player player = Bukkit.getPlayer(args[0]);
				
				GameGUIMenu menu = new GameGUIMenu(GameGUI.getPlayerMenuUtility(player));
				menu.open();
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Second argument should be a player!");
		}
		
		return true;
	}
}

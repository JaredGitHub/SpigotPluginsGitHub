package me.Jared;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GrenadeCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("grenades"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				if(player.hasPermission("grenades"))
				{
					for(int i = 0; i < GrenadesMain.grenades.length; i++)
					{
						ItemStack grenade = new ItemStack(GrenadesMain.grenades[i].getItem(),64);
						player.getInventory().addItem(grenade);
					}
				}
			}
		}

		return true;
	}

}

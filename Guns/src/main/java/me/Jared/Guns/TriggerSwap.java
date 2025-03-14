package me.Jared.Guns;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.GunsPlugin;

public class TriggerSwap implements CommandExecutor
{
	
	private GunsPlugin plugin;
	public TriggerSwap(GunsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			FileConfiguration config = plugin.getConfig();
			
			String triggerSwap = player.getUniqueId() + ".triggerswap";
			player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
			if((config.getBoolean(triggerSwap) == false))
			{
				player.sendMessage(ChatColor.GRAY 
						+ "You have set your trigger mode to " 
						+ ChatColor.UNDERLINE + " LEFT CLICK SHOOT "
						+ ChatColor.RESET + "" 
						+ ChatColor.GRAY + " and " 
						+ ChatColor.UNDERLINE + " RIGHT CLICK AIM");
				config.set(triggerSwap, true);
				plugin.saveConfig();
				return true;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY 
						+ "You have set your trigger mode to " 
						+ ChatColor.UNDERLINE + " LEFT CLICK AIM "
						+ ChatColor.RESET + "" 
						+ ChatColor.GRAY + " and " 
						+ ChatColor.UNDERLINE + " RIGHT CLICK SHOOT");
				config.set(triggerSwap, false);
				plugin.saveConfig();
				return true;
			}
		}
		
		return true;
	}

}

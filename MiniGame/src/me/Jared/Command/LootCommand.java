package me.Jared.Command;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Jared.MiniGame;
import me.Jared.Loot.ConfigItem;
import me.Jared.Loot.Tier;
import me.Jared.Manager.GameManager;

public class LootCommand implements CommandExecutor
{

	FileConfiguration config;
	MiniGame plugin;
	private GameManager gameManager;

	public LootCommand(GameManager gameManager)
	{
		this.gameManager = gameManager;

		this.config = gameManager.getConfig();
		this.plugin = gameManager.getPlugin();
	}

	private void setConfigItem(Player player, Tier tier, int weight)
	{

		ArrayList<String> itemList = new ArrayList<String>(gameManager.getConfig().getStringList("items"));
		ConfigItem configItem = new ConfigItem();

		ItemStack playerItem = new ItemStack(player.getInventory().getItemInMainHand());
		String item = configItem.itemStackToStringWithLore(playerItem, tier, weight);
		itemList.add(item);
		config.set("items", itemList);
		plugin.saveConfig();

		player.sendMessage(ChatColor.GREEN + "You set " + playerItem.getType().name() + ChatColor.GREEN
				+ " in your loot table as " + tier + " tier.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;

			if(player.hasPermission("hg"))
			{
				if(cmd.getName().equalsIgnoreCase("minigame"))
				{
					if(args.length == 0)
					{
						player.sendMessage(ChatColor.GRAY + "Type /setloot <LOW, MEDIUM, HIGH, SKYHIGH> <weight>");
						return true;
					} else if(args[0].equalsIgnoreCase("setloot"))
					{

						if(args.length == 3)
						{
							int weight = Integer.parseInt(args[2]);
							if(args[1].equalsIgnoreCase("low"))
							{
								setConfigItem(player, Tier.LOW, weight);
							} else if(args[1].equalsIgnoreCase("medium"))
							{
								setConfigItem(player, Tier.MEDIUM, weight);
							} else if(args[1].equalsIgnoreCase("high"))
							{
								setConfigItem(player, Tier.HIGH, weight);
							} else if(args[1].equalsIgnoreCase("skyhigh"))
							{
								setConfigItem(player, Tier.SKYHIGH, weight);
							} else
							{
								player.sendMessage(
										ChatColor.GRAY + "Type /setloot <LOW, MEDIUM, HIGH, SKYHIGH> <weight>");
								return true;
							}
						}
						else
						{
							player.sendMessage(ChatColor.GRAY + "Type /setloot <LOW, MEDIUM, HIGH, SKYHIGH> <weight>");
							return true;
						}
					}
				}

				if(cmd.getName().equalsIgnoreCase("setchest"))
				{
					ArrayList<String> chestList = new ArrayList<String>(config.getStringList("chests"));
					if(args.length == 0)
					{
						player.sendMessage(ChatColor.GRAY + "Type /setchest <LOW, MEDIUM, HIGH, SKYHIGH>");
						return true;
					}

					if(!(player.getTargetBlock((Set<Material>) null, 10).getType().equals(Material.CHEST)))
					{
						player.sendMessage(ChatColor.RED + "Make sure you are looking at a chest!");
						return true;
					} else
					{
						if(args.length == 1)
						{
							if((args[0].equalsIgnoreCase("LOW")) || (args[0].equalsIgnoreCase("MEDIUM")
									|| (args[0].equalsIgnoreCase("HIGH") || (args[0].equalsIgnoreCase("SKYHIGH")))))
							{
								Block block = player.getTargetBlock((Set<Material>) null, 10);
								Location location = block.getLocation();

								chestList.add(location.getX() + ":" + location.getY() + ":" + location.getZ() + ":"
										+ args[0].toUpperCase() + ":");

								player.sendMessage(
										ChatColor.GREEN + "You have set this chest to tier " + args[0].toUpperCase());
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
								config.set("chests", chestList);
								plugin.saveConfig();
							} else
							{
								player.sendMessage(
										ChatColor.RED + "Make sure you type either LOW, MEDIUM, HIGH, or SKYHIGH");
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}

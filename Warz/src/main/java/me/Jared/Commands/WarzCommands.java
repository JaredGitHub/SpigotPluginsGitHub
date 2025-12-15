package me.Jared.Commands;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Jared.Warz;
import me.Jared.Loot.ConfigItem;
import me.Jared.Loot.ConfigManager;
import me.Jared.Loot.Zone;
import me.Jared.SQL.WarzDataAccessObject;
import me.Jared.WarzRunnable.SetChestsRunnable;

public class WarzCommands implements CommandExecutor, TabCompleter
{

	FileConfiguration config = Warz.getInstance().getConfig();

	private void saveConfig()
	{
		Warz.getInstance().saveConfig();
	}

	private void setConfigItem(Player player, Zone zone, int weight)
	{
		ArrayList<String> itemList = new ArrayList<>(config.getStringList("items"));
		ConfigItem configItem = new ConfigItem();

		ItemStack playerItem = new ItemStack(player.getInventory().getItemInMainHand());
		String item = configItem.itemStackToStringWithLore(playerItem, zone, weight);

		itemList.add(item);
		config.set("items", itemList);
		saveConfig();

		player.sendMessage(ChatColor.GREEN + "You set " + playerItem.getType().name() + ChatColor.GREEN
				+ " in your loot table as zone " + zone + " with a weight of " + weight + ".");
	}

	private boolean isItemRepeat(Zone zone, Player player)
	{
		ConfigItem configItem = new ConfigItem();
		ArrayList<String> items = new ArrayList<>(configItem.zoneListItems(zone));

		for(String item : items)
		{
			if(configItem.stringToItemStack(item).getType() == player.getInventory().getItemInMainHand().getType())
			{
				player.sendMessage(ChatColor.RED + "You already have an item of that type!!!!!!!!!!!!!!!!");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{

		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player) sender;
		}
		else
		{
			sender.sendMessage("Only players can use this command.");
			return true;
		}

		// Warz commands
		if(cmd.getName().equalsIgnoreCase("warz"))
		{
			sendPlayerToWarz(player, "warz");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("warz2"))
		{
			sendPlayerToWarz(player, "warz2");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("warz3"))
		{
			sendPlayerToWarz(player, "warz3");
			return true;
		}

		if(!sender.hasPermission("warz"))
		{
			sender.sendMessage(ChatColor.RED + "Not for you noob!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("setzone"))
		{
			if(args.length == 2)
			{
				setzone(args, player, player.getWorld().getName());

			} else
			{
				player.sendMessage(ChatColor.RED + "Usage: /setzone low <region>");
				return true;
			}
		}

		if(cmd.getName().equalsIgnoreCase("setloot"))
		{
			if(args.length == 0)
			{
				player.sendMessage(ChatColor.GRAY + "Type /setloot <LOW, MEDIUM, HIGH, SKYHIGH> <weight(1-100)>");
				return true;
			}
			if(player.getInventory().getItemInMainHand().getType() == Material.AIR)
			{
				player.sendMessage(ChatColor.RED + "Make sure you are holding an item!");
				return true;
			}

			if(args.length == 2)
			{
				int weight = Integer.parseInt(args[1]);
				if(args[0].equalsIgnoreCase("low"))
				{
					// check if there is already this item type in there!!
					if(!isItemRepeat(Zone.LOW, player))
					{
						setConfigItem(player, Zone.LOW, weight);
					} else
					{
						return true;
					}

				} else if(args[0].equalsIgnoreCase("medium"))
				{
					if(!isItemRepeat(Zone.MEDIUM, player))
					{
						setConfigItem(player, Zone.MEDIUM, weight);
					} else
					{
						return true;
					}
				} else if(args[0].equalsIgnoreCase("high"))
				{
					if(!isItemRepeat(Zone.HIGH, player))
					{
						setConfigItem(player, Zone.HIGH, weight);
					} else
					{
						return true;
					}

				} else if(args[0].equalsIgnoreCase("skyhigh"))
				{
					if(!isItemRepeat(Zone.SKYHIGH, player))
					{
						setConfigItem(player, Zone.SKYHIGH, weight);
					} else
					{
						return true;
					}

				} else
				{
					player.sendMessage(ChatColor.GRAY + "Type /setloot <LOW, MEDIUM, HIGH, SKYHIGH");
					return true;
				}
			} else
			{
				player.sendMessage(ChatColor.GRAY + "Type /setloot <LOW, MEDIUM, HIGH, SKYHIGH> <weight(1-100)>");
				return true;
			}
		}

		if(cmd.getName().equalsIgnoreCase("addspawnpoint"))
		{
			if(!player.getWorld().equals(Bukkit.getWorld("world")))
			{
				String warzWorld = player.getWorld().getName();
				ConfigManager.setGameSlot(player.getLocation(), warzWorld, ConfigManager.getGameSlotsSize(warzWorld));
				player.sendMessage(ChatColor.GREEN + "Added a spawn point of your location!");
			}
			else
			{
				player.sendMessage(ChatColor.RED + "You must be in the warz world noob!");
			}

		}

		return true;
	}

	private void setzone(String[] args, Player player, String warzWorld)
	{
		String zone = args[0].toUpperCase();
		String region = args[1];
		World world = Bukkit.getWorld(warzWorld);

		config.set("towns." + region, zone);
		saveConfig();

		var setChestsRunnable = new SetChestsRunnable(zone, region, world, player, 50);
		setChestsRunnable.runTaskTimer(Warz.getInstance(), 1, 5);

		player.sendMessage(
				ChatColor.GREEN + "Setting all chests in zone " + ChatColor.WHITE + region + ChatColor.GREEN
						+ " to zone " + ChatColor.WHITE + zone + "!");
	}

	private void sendPlayerToWarz(Player player, String warzWorld)
	{
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if(block.getType() == Material.AIR)
		{
			player.sendMessage(ChatColor.RED + "Stay still");
		}
		if(player.getWorld().equals(Bukkit.getWorld("world")))
		{
			WarzDataAccessObject.savePlayerWorldData(player);
			ConfigManager.setPlayerInWarz(player, warzWorld);

		} else
		{
			player.sendMessage(ChatColor.RED + "You are already in warz noob!");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args)
	{
		List<String> list = new ArrayList<>();

		if(cmd.getName().equalsIgnoreCase("setzone"))
		{
			list.add("low");
			list.add("medium");
			list.add("high");
			list.add("skyhigh");
		}
		if(args.length > 1)
		{
			list.clear();
		}

		return list;
	}
}

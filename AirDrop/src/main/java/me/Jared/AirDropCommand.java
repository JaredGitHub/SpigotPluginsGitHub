package me.Jared;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AirDropCommand implements CommandExecutor
{
	FileConfiguration config = Main.getInstance().getConfig();
	AirDrop airDrop = Main.getInstance().airDrop;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{

		if(!(sender instanceof Player player))
		{
			sender.sendMessage("Only players can use this command!");
			return true;
		}
		if(!player.getWorld().getName().equals("warz"))
		{
			sender.sendMessage(ChatColor.RED + "Only in the warz world!");
			return true;
		}
		if(command.getName().equalsIgnoreCase("airdrop"))
		{
			if(config.getInt("airdrop") <= 0)
			{
				player.sendMessage(ChatColor.RED + "There is currently no AirDrop on the map!");
				return true;
			}
			// Anyone can do this
			int locationNumber = airDrop.getRandomLocationTemporary();
			Location randomLocation = airDrop.getGameSlotLocation(locationNumber);
			player.sendMessage(
					ChatColor.GREEN + "There is an air drop at X: " + ChatColor.GRAY + (int) randomLocation.getX()
							+ ChatColor.GREEN + " Y: " + ChatColor.GRAY + (int) randomLocation.getY() + ChatColor.GREEN
							+ " Z: " + ChatColor.GRAY + (int) randomLocation.getZ());
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

			if(player.hasPermission("airdrop"))
			{
				if(args.length == 1 && args[0].equalsIgnoreCase("add"))
				{
					if(player.hasPermission("airdrop") || player.isOp())
					{
						player.sendMessage(ChatColor.GREEN + "You have placed a new drop on your location!");
						airDrop.setGameSlot(player.getLocation());
					} else
					{
						player.sendMessage(
								ChatColor.RED + "You must be in the Warz world and have permission to do this.");
					}
				} else if(args.length == 2 && args[0].equalsIgnoreCase("additem"))
				{
					if(player.hasPermission("airdrop") || player.isOp())
					{
						int arg2Int = Integer.parseInt(args[1]);
						setConfigItem(player, arg2Int);
					} else
					{
						player.sendMessage(ChatColor.RED + "You don't have permission to add items.");
					}
				}
			}
		}

		return true;
	}

	private void setConfigItem(Player player, int weight)
	{
		ArrayList<String> itemList = new ArrayList<>(config.getStringList("items"));
		ConfigItem configItem = new ConfigItem();

		ItemStack playerItem = new ItemStack(player.getInventory().getItemInMainHand());
		String item = configItem.itemStackToStringWithLore(playerItem, weight);

		itemList.add(item);
		config.set("items", itemList);
		Main.getInstance().saveConfig();

		player.sendMessage(ChatColor.GREEN + "You set " + playerItem.getType().name() + ChatColor.GREEN
				+ " in your loot table with a weight of " + weight + ".");
	}
}

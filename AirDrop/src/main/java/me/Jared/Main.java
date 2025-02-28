package me.Jared;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Main extends JavaPlugin
{
	AirDrop airDrop = new AirDrop(this);

	private static Main instance;

	@Override
	public void onEnable()
	{
		instance = this;
		// Plugin startup logic
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Airdrop Enabled");

		AirDropManager airDropManager = new AirDropManager();
		// 10 second timer before airdrop
		airDropManager.runAirDropRunnable(airDrop, 10);

		//If config doesn't contain interval give it interval of 5 minutes 300 seconds
		if(!(this.getConfig().contains("interval")))
		{
			getConfig().set("interval", 300);
		}

		//Looping through each of the chests that may or may not be placed and removing them
		//This makes sure that on restart of the server there aren't a bunch of chests with goodies in them
		if(this.getConfig().contains("airdrops"))
		{
			ConfigurationSection airdropsSection = this.getConfig().getConfigurationSection("airdrops");
			for (String key : airdropsSection.getKeys(false)) {
				double x = airdropsSection.getDouble(key + ".x");
				double y = airdropsSection.getDouble(key + ".y");
				double z = airdropsSection.getDouble(key + ".z");

				World world = Bukkit.getWorld("warz"); // Replace with your world name or reference
				if (world != null) {
					Location airdropLocation = new Location(world, x, y, z);
					airdropLocation.getBlock().setType(Material.AIR);
				}
			}
		}
	}

	public static Main getInstance()
	{
		return instance;
	}

	@Override
	public void onDisable()
	{
		// Plugin shutdown logic
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "AirDrop Disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		// Check if the sender is a player
		if(sender instanceof Player)
		{
			Player player = (Player) sender;

			if(args.length == 2)
			{
				String arg1 = args[0];
				String arg2 = args[1];

				if(arg1.equalsIgnoreCase("additem"))
				{
					int arg2Int = Integer.parseInt(arg2);
					setConfigItem(player, arg2Int);
				}
			} else
			{
				if(player.getWorld().equals(Bukkit.getWorld("warz")))
				{
					player.sendMessage(ChatColor.GREEN + "You have placed a new drop on your location!");

					airDrop.setGameSlot(player.getLocation());
				} else
				{
					player.sendMessage(ChatColor.RED + "You must be in the Warz world for this to work!");
					return true;
				}
			}

		} else
		{
			sender.sendMessage("Only players can use this command!");
		}
		return true; // Return true if the command was handled successfully
	}

	private void setConfigItem(Player player, int weight)
	{
		ArrayList<String> itemList = new ArrayList<>(this.getConfig().getStringList("items"));
		ConfigItem configItem = new ConfigItem();

		ItemStack playerItem = new ItemStack(player.getInventory().getItemInMainHand());
		String item = configItem.itemStackToStringWithLore(playerItem, weight);

		itemList.add(item);
		this.getConfig().set("items", itemList);
		saveConfig();

		player.sendMessage(ChatColor.GREEN + "You set " + playerItem.getType().name() + ChatColor.GREEN
				+ " in your loot table with a weight of " + weight + ".");
	}
}


package me.Jared.Shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import me.Jared.Stats;
import me.Jared.Menus.ConfigItem;
import me.Jared.Menus.ShopMenu;

public class Commands implements CommandExecutor, TabCompleter
{
	private Stats stats = Stats.getPlugin(Stats.class);
	FileConfiguration config = stats.getConfig();
	ConfigItem configItem = new ConfigItem();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You are not a player!");
			return true;
		}

		// Creating a command "shop"
		if(cmd.getName().equalsIgnoreCase("shop"))
		{
			Player p = (Player) sender;

			if(args.length == 0)
			{
				if(p.getWorld().getName().equals("world"))
				{
					//Open gui inventory
					ShopMenu menu = new ShopMenu(stats.getPlayerMenuUtility(p), "shopItems");
					menu.open();
					return true;
				}
				else if(p.getWorld().getName().contains("warz"))
				{
					ShopMenu menu = new ShopMenu(stats.getPlayerMenuUtility(p), "warzItems");
					menu.open();
					return true;
				}
			}
			if(!p.hasPermission("stats"))
			{
				p.sendMessage(ChatColor.RED + "No permission noob!!");
				return true;
			}
			if(args[0].equalsIgnoreCase("remove"))
			{
				ItemStack item = new ItemStack(p.getInventory().getItemInMainHand());

				ArrayList<String> itemList = new ArrayList<String>(config.getStringList("shopItems"));

				boolean remove = false;
				for(int i = 0; i < itemList.size(); i++)
				{
					Material material = item.getType();
					Material configMaterial = configItem.getMaterial(itemList.get(i));

					if(material.equals(configMaterial))
					{
						if(item.hasItemMeta())
						{
							p.sendMessage(ChatColor.RED + "You have removed " + item.getItemMeta().getDisplayName()
									+ ChatColor.RED + " from the shop!");
						} else
						{
							p.sendMessage(ChatColor.RED + "You have removed " + ChatColor.RESET + item.getType().name()
									+ ChatColor.RED + " from the shop!");

						}
						p.playSound(p.getLocation(), Sound.ENTITY_GHAST_DEATH, 1, 1);

						remove = true;
						itemList.remove(itemList.get(i));
					}
				}

				if(remove == false)
				{
					p.sendMessage(ChatColor.RED + "That item isn't in the shop!");
					return true;
				}

				config.set("shopItems", itemList);
				stats.saveConfig();
				return true;

			} else if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("add"))
				{

					if(p.getInventory().getItemInMainHand().getType() == Material.AIR)
					{
						p.sendMessage(ChatColor.RED + "Hold an item to add to the shop!");
						return true;
					}
					try
					{

						try
						{

							int price = Integer.parseInt(args[1]);
							ItemStack item = new ItemStack(p.getInventory().getItemInMainHand());

							ArrayList<String> itemList = new ArrayList<String>(config.getStringList("shopItems"));

							for(int i = 0; i < itemList.size(); i++)
							{
								Damageable damage = (Damageable) item.getItemMeta();
								if(configItem.getMaterial(itemList.get(i)).equals(item.getType())
										&& configItem.getDurability(itemList.get(i)) == damage.getDamage())
								{
									p.sendMessage(ChatColor.RED + "You cannot add duplicate items!");
									return true;
								}
							}

							if(item.hasItemMeta())
							{
								p.sendMessage(ChatColor.GREEN + "You have added " + item.getItemMeta().getDisplayName()
										+ ChatColor.GREEN + " to the shop for " + price + " gems!");
							} else
							{
								p.sendMessage(
										ChatColor.GREEN + "You have added " + ChatColor.RESET + item.getType().name()
												+ ChatColor.GREEN + " to the shop for " + price + " gems!");
							}
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

							itemList.add(configItem.itemStackToString(item, price));
							config.set("shopItems", itemList);
							stats.saveConfig();
							return true;

						} catch(NumberFormatException e)
						{
							p.sendMessage(ChatColor.GRAY + "Usage: /shop add (price)");
							return true;
						}

					} catch(NullPointerException e)
					{
						p.sendMessage(ChatColor.RED + "Hold an item to add to the shop!");
						return true;
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().contains(""))
		{
			return null;
		}
		return null;
	}
}

package me.Jared.Kits;

import java.util.ArrayList;
import java.util.List;

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

import me.Jared.Managers.KitManager;
import me.Jared.Menus.SelectKitMenu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Commands implements CommandExecutor, TabCompleter
{

	private Main main = Main.getPlugin(Main.class);
	FileConfiguration config = main.getConfig();
	ConfigItem configItem = new ConfigItem();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "You are not a player!");
			return true;
		}
		// Creating a command "kit"

		if(cmd.getName().equalsIgnoreCase("kit"))
		{
			Player p = (Player) sender;
			if(args.length == 0)
			{
				// Open gui inventory
				KitManager.defaultHotBar(p.getUniqueId());
				SelectKitMenu menu = new SelectKitMenu(Main.getPlayerMenuUtility(p));
				menu.open();
			}

			else if(args.length == 1)
			{
				if(p.hasPermission("jared") && args[0].equalsIgnoreCase("edit"))
				{
					TextComponent hotbar = clickableText(
							net.md_5.bungee.api.ChatColor.GREEN + "'hotbar'" + net.md_5.bungee.api.ChatColor.RESET,
							"Click me to set your default hotbar!", "kit edit hotbar");

					TextComponent message = new TextComponent("Type or click ");
					message.addExtra(hotbar);
					message.addExtra(" to set your new default.");

					TextComponent info = clickableText(net.md_5.bungee.api.ChatColor.GRAY + " [?]",
							"Clicking 'menu' sets the default menu to\nthe contents of your inventory not"
									+ "\nincluding hotbar.\n" + "\nClicking 'hotbar' sets your hotbar default "
									+ "\nas the contents of your current hotbar");
					message.addExtra(info);
					p.spigot().sendMessage(message);
				}
			}

			else if(args.length == 2)
			{
				if(p.hasPermission("jared"))
				{
					if(args[1].equalsIgnoreCase("hotbar") && args[0].equalsIgnoreCase("edit"))
					{

						p.sendMessage(
								ChatColor.GREEN + "Your default hotbar is set to the contents of your inventory!");
						p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.05f);
						FileConfiguration config = Main.getInstance().getConfig();
						ArrayList<ItemStack> configItems = new ArrayList<ItemStack>();
						for(int i = 0; i <= 9; i++)
						{
							configItems.add(p.getInventory().getItem(i));
						}
						for(int slot = 0; slot <= 9; slot++)
						{
							if(configItems.get(slot) == null)
								continue;
							config.set("HotbarItems." + slot, configItems.get(slot));
							Main.getInstance().saveConfig();
						}
					}

					if(args[0].equalsIgnoreCase("menu"))
					{
						if(args[1].equalsIgnoreCase("add"))
						{
							if(p.getInventory().getItemInMainHand().getType() == Material.AIR)
							{
								p.sendMessage(ChatColor.RED + "Hold an item to add to the item selection menu!");
								return true;
							}
							try
							{
								try
								{
									ItemStack item = new ItemStack(p.getInventory().getItemInMainHand());

									ArrayList<String> itemList = new ArrayList<String>(config.getStringList("SelectItems"));

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
										p.sendMessage(
												ChatColor.GREEN + "You have added " + item.getItemMeta().getDisplayName()
												+ ChatColor.GREEN + " to the kit menu!");
									} else
									{
										p.sendMessage(ChatColor.GREEN + "You have added " + item.getType().name()
												+ ChatColor.GREEN + " to the kit menu!");
									}
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

									itemList.add(configItem.itemStackToString(item));
									config.set("SelectItems", itemList);
									main.saveConfig();
									return true;

								} catch(NumberFormatException e)
								{
									p.sendMessage(ChatColor.GRAY + "Usage: /kit menu add");
									return true;
								}

							} catch(NullPointerException e)
							{
								p.sendMessage(ChatColor.RED + "Hold an item to add to the kit menu!");
								return true;
							}
						}
						else if(args[1].equalsIgnoreCase("clear"))
						{
							config.set("SelectItems", null);
							main.saveConfig();
							p.sendMessage(ChatColor.GREEN + "Successfully cleared the kit menu!");
							p.playSound(p, Sound.ENTITY_GHAST_DEATH, 1, 1);
							return true;
						}
					}
				}
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public TextComponent clickableText(String chatText, String hoverText)
	{
		TextComponent message = new TextComponent(chatText);
		ComponentBuilder hoverMessage = new ComponentBuilder(hoverText);

		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage.create()));

		return message;

	}

	@SuppressWarnings("deprecation")
	public TextComponent clickableText(String chatText, String hoverText, String cmd)
	{
		TextComponent message = new TextComponent(chatText);
		ComponentBuilder hoverMessage = new ComponentBuilder(hoverText);

		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage.create()));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd));

		return message;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();

		if(sender.hasPermission("kits"))
		{
			if(cmd.getName().equalsIgnoreCase("kit"))
			{
				list.add("edit");
				list.add("menu");
				if(args[0].equalsIgnoreCase("edit"))
				{
					list.add("hotbar");
					list.remove("edit");
				} else if(args[0].equalsIgnoreCase("menu"))
				{
					list.add("add");
					list.add("clear");
				}
			}
		}
		return list;
	}

}

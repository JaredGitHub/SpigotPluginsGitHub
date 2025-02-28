package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ConfigItem
{
	private Main plugin = Main.getInstance();
	FileConfiguration config = plugin.getConfig();

	private static String getItemDataIndex(String string, int num)
	{
		StringBuilder stringBuilder = new StringBuilder();

		int count = 0;
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == ':')
			{
				count++;
				continue;
			}

			if(count == num)
			{
				stringBuilder.append(string.charAt(i));
			}
		}

		return stringBuilder.toString();
	}

	public static String itemStackToStringWithLore(ItemStack item, int weight)
	{
		String material = item.getType().name();
		int amount = item.getAmount();

		String displayName = "";
		int durability = 0;
		if(item.hasItemMeta())
		{
			displayName = item.getItemMeta().getDisplayName().replace(ChatColor.COLOR_CHAR, '&');
			if(item.getItemMeta() instanceof Damageable)
			{
				durability = ((Damageable) item.getItemMeta()).getDamage();
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(material + ":");
		stringBuilder.append(displayName + ":");
		stringBuilder.append(amount + ":");
		stringBuilder.append(durability + ":");
		if(item.hasItemMeta() && item.getItemMeta().hasLore())
		{
			String lore = item.getItemMeta().getLore().get(0).replace(ChatColor.COLOR_CHAR, '&');
			stringBuilder.append(lore + ":");

		} else
		{
			stringBuilder.append(":");
		}

		for(int i = 0; i < material.length(); i++)
		{
			char charAt = material.charAt(i);
			if(charAt == ':')
			{
				break;
			}
		}
		stringBuilder.append(weight + ":");

		return stringBuilder.toString();
	}

	public static ItemStack stringToItemStackWithLore(String string)
	{
		Material material = getMaterial(string);
		String displayName = getDisplayName(string);
		String itemLore = getLore(string);
		int amount = getAmount(string);
		int damage = getDamage(string);

		if(material == null)
		{
			Bukkit.getConsoleSender().sendMessage("There is something messed up in the config!");
		}

		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		if(itemLore.length() > 1)
		{
			if(itemLore.length() >= 1)
			{
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.translateAlternateColorCodes('&', itemLore));
				meta.setLore(lore);
			}

			((Damageable) meta).setDamage(damage);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static Material getMaterial(String string)
	{
		return Material.getMaterial(getItemDataIndex(string, 0));
	}

	public static String getDisplayName(String string)
	{
		return getItemDataIndex(ChatColor.translateAlternateColorCodes('&', string), 1);
	}
	public static int getAmount(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 2));
	}

	public static int getDamage(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 3));
	}

	public static String getLore(String string)
	{
		return getItemDataIndex(string, 4);
	}

	public static int getWeight(String string)
	{
		try
		{
			return Integer.parseInt(getItemDataIndex(string, 5));
		}
		catch(NumberFormatException e)
		{
			return 1000;
		}
	}
}
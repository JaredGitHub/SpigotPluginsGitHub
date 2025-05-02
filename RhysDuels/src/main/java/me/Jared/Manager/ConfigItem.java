package me.Jared.Manager;

import me.Jared.Duels;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigItem
{

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

	public static ItemStack stringToItemStack(String string)
	{
		// Getting each of the pieces of item data from the string
		Material material = getMaterial(string);
		if(material == null)
			return new ItemStack(Material.AIR);

		String displayName = getDisplayName(string);
		int amount = getAmount(string);
		int durability = getDurability(string);

		// Setting the material and the amount
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		// Apply color codes to display name
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		// Setting durability
		ItemMeta updatedMeta = item.getItemMeta();
		if(updatedMeta instanceof Damageable)
		{
			((Damageable) updatedMeta).setDamage(durability);
			item.setItemMeta(updatedMeta);
		}

		return item;
	}

	private static String toTitleCase(String phrase)
	{
		StringBuilder sb = new StringBuilder(phrase);
		for(int i = 0; i < phrase.length(); i++)
		{
			if(i == 0 || phrase.charAt(i - 1) == ' ')
			{
				sb.replace(i, i + 1, phrase.substring(i, i + 1).toUpperCase());
			}
		}
		return sb.toString();
	}

	public static String itemStackToString(ItemStack item)
	{
		if(item == null || item.getType() == Material.AIR)
			return null;

		String material = item.getType().name();
		int amount = item.getAmount();

		int durability = 0;
		ItemMeta meta = item.getItemMeta();
		if(meta instanceof Damageable)
		{
			durability = ((Damageable) meta).getDamage();
		}

		String displayName;
		if(meta != null && meta.hasDisplayName())
		{
			displayName = meta.getDisplayName();
		} else
		{
			displayName = material.replaceAll("_", " ").toLowerCase();
			displayName = toTitleCase(displayName);
		}

		// Replace § with & for serialization
		displayName = displayName.replace("§", "&");

		return material + ":" + displayName + ":" + amount + ":" + durability + ":";
	}

	public static Material getMaterial(String string)
	{
		return Material.getMaterial(getItemDataIndex(string, 0));
	}

	public static String getDisplayName(String string)
	{
		return getItemDataIndex(string, 1);
	}

	public static int getAmount(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 2));
	}

	public static short getDurability(String string)
	{
		return Short.parseShort(getItemDataIndex(string, 3));
	}
}

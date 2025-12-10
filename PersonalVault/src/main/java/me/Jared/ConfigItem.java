package me.Jared;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigItem
{

	private PersonalVault pv = PersonalVault.getInstance();
	FileConfiguration config = pv.getConfig();
	private String getItemDataIndex(String string, int num)
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
	public ItemStack stringToItemStackWithLore(String string)
	{
		Material material = getMaterial(string);
		String displayName = getDisplayName(string);
		String itemLore = getLore(string);
		int durability = getDurability(string);
		int amount = getAmount(string);

		if(material == null)
			Bukkit.getConsoleSender().sendMessage("There is something messed up in the config!");

		ItemStack item = new ItemStack(material, amount);

		ItemMeta meta = item.getItemMeta();
		if(itemLore.length() >= 1)
		{
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.translateAlternateColorCodes('&', itemLore));
			meta.setLore(lore);
		}
		meta.setDisplayName(displayName);
		
		((Damageable) meta).setDamage(durability);

		item.setItemMeta(meta);
		return item;
	}

	public String itemStackToStringWithLore(ItemStack item)
	{
		if(item.getType() == null)
		{
			return "";
		}
		String material = item.getType().name();
		int amount = item.getAmount();

		String displayName = "";
		int durability = 0;
		if(item.hasItemMeta())
		{
			displayName = item.getItemMeta().getDisplayName().replaceAll("§", "&");
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
		if(item.hasItemMeta())
		{
			if(item.getItemMeta().hasLore())
			{
				String lore = item.getItemMeta().getLore().get(0).replaceAll("§", "&");
				stringBuilder.append(lore + ":");
			}
		}

		for(int i = 0; i < material.length(); i++)
		{
			char charAt = material.charAt(i);
			if(charAt == ':')
			{
				break;
			}
		}

		return stringBuilder.toString();
	}

	public Material getMaterial(String string)
	{
		return Material.getMaterial(getItemDataIndex(string, 0));
	}

	public String getDisplayName(String string)
	{
		return getItemDataIndex(ChatColor.translateAlternateColorCodes('&', string), 1);
	}

	public int getAmount(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 2));
	}

	public int getDurability(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 3));
	}

	public String getLore(String string)
	{
		return getItemDataIndex(string, 4);
	}
}

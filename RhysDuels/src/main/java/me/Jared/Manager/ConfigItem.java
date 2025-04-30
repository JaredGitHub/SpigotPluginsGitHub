package me.Jared.Manager;

import me.Jared.Duels;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigItem
{
	private Duels main = Duels.getPlugin(Duels.class);
	FileConfiguration config = main.getConfig();

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

	public ItemStack stringToItemStack(String string)
	{
		//Getting each of the pieces of item data from the string
		Material material = getMaterial(string);
		if(material == null) return new ItemStack(Material.AIR);
		String displayName = getDisplayName(string);
		int amount = getAmount(string);
		int durability = getDurability(string);


		//Setting the material and the amount
		ItemStack item = new ItemStack(material,amount);

		ItemMeta meta = item.getItemMeta();

		//Setting displayname
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		//Setting durability
		Damageable damage = (Damageable) item.getItemMeta();
		damage.setDamage(durability);

		return item;

	}

	String toTitleCase(String phrase)
	{
		StringBuilder sb= new StringBuilder(phrase);
		for (int i=0; i<phrase.length(); i++) {
			if(i==0 || phrase.charAt(i-1)==' ') {
				sb.replace(i,i+1,phrase.substring(i,i+1).toUpperCase());
			}
		}
		return sb.toString();
	}

	public String itemStackToString(ItemStack item)
	{
		String material = item.getType().name();
		int amount = item.getAmount();
		Damageable damage = (Damageable) item.getItemMeta();
		int durability = damage.getDamage();

		String displayName = "";
		if(item.hasItemMeta())
		{
			displayName = item.getItemMeta().getDisplayName();
		}
		else
		{
			displayName = material.replaceAll("_", " ");
			displayName = displayName.toLowerCase();
			displayName = toTitleCase(displayName);
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(material + ":");
		stringBuilder.append(displayName + ":");
		stringBuilder.append(amount + ":");
		stringBuilder.append(durability + ":");

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
		return getItemDataIndex(string, 1);
	}

	public int getAmount(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 2));
	}

	public short getDurability(String string)
	{
		return Short.parseShort(getItemDataIndex(string, 3));
	}
}

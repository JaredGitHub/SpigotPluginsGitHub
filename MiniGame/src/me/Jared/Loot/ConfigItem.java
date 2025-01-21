package me.Jared.Loot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.MiniGame;

public class ConfigItem
{
	private MiniGame plugin = MiniGame.getInstance();
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

	public static Location getChestLocation(String string)
	{
		Location location = new Location(Bukkit.getWorld("world"), getChestX(string), getChestY(string),
				getChestZ(string));

		return location;
	}
	
	public static Tier getChestTier(Location location)
	{
		Tier tier = null;

		List<String> chestStringLocations = MiniGame.getInstance().getConfig().getStringList("chests");
		
		for(String chestLocationString : chestStringLocations)
		{
			Location chestLocation = getChestLocation(chestLocationString);
			if(chestLocation.getX() == location.getX()
					&& chestLocation.getY() == location.getY()
					&& chestLocation.getZ() == location.getZ())
			{
				tier = getChestTier(chestLocationString);
			}	
		}
		return tier;
	}

	public static Tier getChestTier(String string)
	{
		Tier tier = null;

		String str = getItemDataIndex(string, 3);

		switch(str)
		{
		case "LOW":
			tier = Tier.LOW;
			break;
		case "MEDIUM":
			tier = Tier.MEDIUM;
			break;
		case "HIGH":
			tier = Tier.HIGH;
			break;
		case "SKYHIGH":
			tier = Tier.SKYHIGH;
			break;
		default:
			tier = Tier.LOW;
			break;
		}

		return tier;
	}

	public static ArrayList<String> tierListChests(Tier tier)
	{
		ArrayList<String> chestList = new ArrayList<>(MiniGame.getInstance().getConfig().getStringList("chests"));
		ArrayList<String> tierList = new ArrayList<>();
		for(String chest : chestList)
		{
			if(getChestTier(chest).equals(tier))
			{
				tierList.add(chest);
			}
		}
		return tierList;
	}

	public static ArrayList<String> tierListItems(Tier tier)
	{
		List<String> itemList = MiniGame.getInstance().getConfig().getStringList("items");
		ArrayList<String> tierList = new ArrayList<>();
		for(String item : itemList)
		{
			if(getTier(item).equals(tier))
			{
				tierList.add(item);
			}
		}

		return tierList;
	}

	public static ItemStack stringToItemStack(String string)
	{
		Material material = getMaterial(string);
		String displayName = getDisplayName(string);
		int amount = getAmount(string);

		if(material == null)
		{
			Bukkit.getConsoleSender().sendMessage("There is something messed up in the config!");
		}

		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		return item;
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

	public String itemStackToStringWithLore(ItemStack item, Tier tier, int weight)
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
		stringBuilder.append(tier + ":");
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

	public static double getChestX(String string)
	{
		return Double.parseDouble(getItemDataIndex(string, 0));
	}

	public static double getChestY(String string)
	{
		return Double.parseDouble(getItemDataIndex(string, 1));
	}

	public static double getChestZ(String string)
	{
		return Double.parseDouble(getItemDataIndex(string, 2));
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

	public static Tier getTier(String string)
	{
		Tier tier = null;

		String str = getItemDataIndex(string, 4);

		switch(str)
		{
		case "LOW":
			tier = Tier.LOW;
			break;
		case "MEDIUM":
			tier = Tier.MEDIUM;
			break;
		case "HIGH":
			tier = Tier.HIGH;
			break;
		case "SKYHIGH":
			tier = Tier.SKYHIGH;
			break;
		default:
			break;
		}

		return tier;
	}

	public static String getLore(String string)
	{
		return getItemDataIndex(string, 5);
	}

	public static int getWeight(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 6));
	}
}

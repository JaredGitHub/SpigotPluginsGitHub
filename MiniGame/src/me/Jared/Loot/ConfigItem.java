package me.Jared.Loot;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import me.Jared.MiniGame;

public class ConfigItem
{
	private MiniGame plugin = MiniGame.getPlugin(MiniGame.class);
	FileConfiguration config = plugin.getConfig();

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

	public Location getChestLocation(String string)
	{
		Location location = new Location(Bukkit.getWorld("world"), 
				getChestX(string), 
				getChestY(string), 
				getChestZ(string));

		return location;
	}

	public Tier getChestTier(String string)
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
			break;
		}

		return tier;
	}


	public ArrayList<String> tierListChests(Tier tier)
	{
		ArrayList<String> itemList = new ArrayList<String>(config.getStringList("chests"));
		ArrayList<String> tierList = new ArrayList<String>();
		for(String item : itemList)
		{
			if(getChestTier(item).equals(tier))
			{
				tierList.add(item);
			}

		}

		return tierList;
	}

	public ArrayList<String> tierListItems(Tier tier)
	{
		ArrayList<String> itemList = new ArrayList<String>(config.getStringList("items"));
		ArrayList<String> tierList = new ArrayList<String>();
		for(String item : itemList)
		{
			if(getTier(item).equals(tier))
			{
				tierList.add(item);
			}

		}

		return tierList;
	}

	public ItemStack stringToItemStack(String string)
	{
		Material material = getMaterial(string);
		String displayName = getDisplayName(string);
		int amount = getAmount(string);

		if(material == null) Bukkit.getConsoleSender().sendMessage("There is something messed up in the config!");
		
		ItemStack item = new ItemStack(material,amount);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		return item;
	}

	public String itemStackToString(ItemStack item, Tier tier)
	{
		String material = item.getType().name();
		int amount = item.getAmount();

		String displayName = "";
		int durability = 0;
		if(item.hasItemMeta())
		{
			displayName = item.getItemMeta().getDisplayName();
			if(item.getItemMeta() instanceof Damageable)
			{
				durability = ((Damageable) item.getItemMeta()).getDamage();  
			}
		}
		else
		{
			displayName = material.replaceAll("_", " ");
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(material + ":");
		stringBuilder.append(displayName + ":");
		stringBuilder.append(amount + ":");
		stringBuilder.append(durability + ":");
		stringBuilder.append(tier + ":");
		if(item.getItemMeta().hasLore())
		{
			String lore = item.getItemMeta().getLore().get(0);
			stringBuilder.append(lore + ":");
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

	public double getChestX(String string)
	{
		return Double.parseDouble(getItemDataIndex(string, 0));
	}
	public double getChestY(String string)
	{
		return Double.parseDouble(getItemDataIndex(string, 1));
	}
	public double getChestZ(String string)
	{
		return Double.parseDouble(getItemDataIndex(string, 2));
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

	public int getDurability(String string)
	{
		return Integer.parseInt(getItemDataIndex(string, 3));
	}

	public Tier getTier(String string)
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

	public String getLore(String string)
	{
		return getItemDataIndex(string, 5);
	}
}

package me.Jared.LandMine;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.Jared.LandMinePlugin;
import net.md_5.bungee.api.ChatColor;

public class LandMine
{
	FileConfiguration config = LandMinePlugin.getInstance().getConfig();

	private void saveConfig()
	{
		LandMinePlugin.getInstance().saveConfig();
	}

	private String landMineItem = config.getString("landmineItem");
	private String displayName = config.getString("landmineItemName");

	public void setItem(String material)
	{
		config.set("landmineItem", material.toString().toUpperCase());
		saveConfig();
	}

	public Material getItem()
	{
		return Material.getMaterial(landMineItem.toUpperCase());
	}

	public void setItemName(String name)
	{
		config.set("landmineItemName", name.toUpperCase());
		saveConfig();
	}

	public String getItemName()
	{
		return ChatColor.translateAlternateColorCodes('&', this.displayName);
	}

	public void changeItemToLandMine(Inventory inventory)
	{
		for(ItemStack item : inventory.getContents())
		{
			if(item == null) continue;
			if(item.getType() == this.getItem())
			{
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(this.getItemName());
				item.setItemMeta(meta);
			}
		}
	}
	
	public void blowUp(Location location, Entity entity)
	{
		location.getWorld().createExplosion(location, 0f, false, false);
		entity.setVelocity(new Vector(0,0.5,0));
		
		if(entity instanceof LivingEntity)
		{
			((LivingEntity) entity).damage(10);
		}
		
		location.getBlock().setType(Material.AIR);
	}
}

package main.java.me.jared.food.listeners;

import main.java.me.jared.food.Sugar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CornedBeef implements Listener
{
	FileConfiguration config = Sugar.plugin.getConfig();

	int cooldowntime = this.config.getInt("cbDelay");

	public ItemStack getBeef()
	{
		ItemStack is = new ItemStack(Material.PINK_DYE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.config.getString("cbString").replaceAll("&", "§"));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getBeef().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for(i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if(item != null)
				if(item.getType().equals(getBeef().getType()))
					if(!item.hasItemMeta())
					{
						item.setItemMeta(meta);
					} else if(item.hasItemMeta() && !item.getItemMeta().equals(getBeef().getItemMeta()))
					{
						item.setItemMeta(meta);
					}
			b++;
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() == getBeef().getType())
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemMeta meta = getBeef().getItemMeta();
				byte b;
				int i;
				ItemStack[] arrayOfItemStack;
				for(i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i;)
				{
					ItemStack item = arrayOfItemStack[b];
					if(item != null)
						if(item.getType().equals(Material.PINK_DYE))
						{
							item.setItemMeta(meta);
							break;
						}
					b++;
				}
				if(MountainDew.cooldown.containsKey(player.getUniqueId()))
				{
					long ticksleft = ((Long) MountainDew.cooldown.get(player.getUniqueId())).longValue() / 50L
							+ this.cooldowntime - System.currentTimeMillis() / 50L;
					if(ticksleft > 0L)
						return;
				}
				for(Player p : Bukkit.getOnlinePlayers())
				{
					Location loc = player.getEyeLocation();
					Sugar.putParticle(loc, getBeef());
					p.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
				}
				double health = player.getHealth();
				if(health == 20.0D)
				{
					player.setHealth(20.0D);
				} else if(health < 12.0D)
				{
					player.setHealth(health + 8.0D);
				} else if(health >= 12.0D)
				{
					player.setHealth(20.0D);
				}
				int food = player.getFoodLevel();
				if(food == 20)
				{
					player.setFoodLevel(20);
				} else if(food < 12)
				{
					player.setFoodLevel(food + 6);
				} else if(food >= 12)
				{
					player.setFoodLevel(20);
				}
				ItemStack is = player.getInventory().getItemInMainHand();
				if(is != null)
					if(is.getAmount() > 1)
					{
						is.setAmount(is.getAmount() - 1);
					} else
					{
						player.getInventory().setItemInMainHand(null);
					}
				MountainDew.cooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
			}
	}
}

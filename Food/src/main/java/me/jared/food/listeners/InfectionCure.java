package main.java.me.jared.food.listeners;

import main.java.me.jared.food.Sugar;
import main.java.me.jared.food.listeners.MountainDew;
import org.bukkit.Bukkit;
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
import org.bukkit.potion.PotionEffect;


public class InfectionCure implements Listener
{
	FileConfiguration config = Sugar.plugin.getConfig();

	int cooldowntime = this.config.getInt("icDelay");

	public ItemStack getCure()
	{
		ItemStack is = new ItemStack(Material.LIME_DYE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.config.getString("icString").replaceAll("&", "§"));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getCure().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for(i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if(item != null)
				if(item.getType().equals(getCure().getType()))
					if(!item.hasItemMeta())
					{
						item.setItemMeta(meta);
					} else if(item.hasItemMeta() && !item.getItemMeta().equals(getCure().getItemMeta()))
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
		if(player.getInventory().getItemInMainHand().getType() == getCure().getType())
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemMeta meta = getCure().getItemMeta();
				byte b;
				int i;
				ItemStack[] arrayOfItemStack;
				for(i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i;)
				{
					ItemStack item = arrayOfItemStack[b];
					if(item != null)
						if(item.getType().equals(Material.LIME_DYE))
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
				for(Player online : Bukkit.getOnlinePlayers())
				{
					online.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
				}

				for(PotionEffect effect : player.getActivePotionEffects())
				{
					player.removePotionEffect(effect.getType());
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

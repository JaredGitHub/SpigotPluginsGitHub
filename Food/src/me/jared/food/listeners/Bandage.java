package me.jared.food.listeners;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jared.food.Sugar;
import net.md_5.bungee.api.ChatColor;

public class Bandage implements Listener
{
	FileConfiguration config = Sugar.plugin.getConfig();

	public ItemStack getBandage()
	{
		ItemStack is = new ItemStack(Material.PAPER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.config.getString("bandageString").replaceAll("&", "§"));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getBandage().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for(i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if(item != null)
				if(item.getType().equals(getBandage().getType()))
					if(!item.hasItemMeta())
					{
						item.setItemMeta(meta);
					} else if(item.hasItemMeta() && !item.getItemMeta().equals(getBandage().getItemMeta()))
					{
						item.setItemMeta(meta);
					}
			b++;
		}
	}

	ArrayList<UUID> bleeders = Sugar.bleeders;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() == getBandage().getType())
		{
			if(bleeders.contains(player.getUniqueId()))
			{
				player.sendMessage(ChatColor.GREEN + "You have bandaged yourself up!");
				player.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 2, 1);

				bleeders.remove(player.getUniqueId());

				ItemStack is = player.getInventory().getItemInMainHand();
				if(is != null)
					if(is.getAmount() > 1)
					{
						is.setAmount(is.getAmount() - 1);
					} else
					{
						player.getInventory().setItemInMainHand(null);
					}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();

			if(player.getWorld().getName().equals("warz"))
			{
				if(e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
				{
					Random random = new Random();
					int randomNumber = random.nextInt(20);

					if(randomNumber == 1)
					{
						bleeders.add(player.getUniqueId());

						player.sendMessage(ChatColor.DARK_RED + "You are bleeding!");
						player.playSound(player, Sound.BLOCK_HONEY_BLOCK_BREAK, 1, 1);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		bleeders.remove(e.getEntity().getUniqueId());
	}
}

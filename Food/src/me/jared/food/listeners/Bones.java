package me.jared.food.listeners;

import java.util.HashMap;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jared.food.Sugar;
import net.md_5.bungee.api.ChatColor;

public class Bones implements Listener
{
	FileConfiguration config = Sugar.plugin.getConfig();

	public ItemStack getBones()
	{
		ItemStack is = new ItemStack(Material.BONE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.config.getString("bonesString").replaceAll("&", "§"));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getBones().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for(i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if(item != null)
				if(item.getType().equals(getBones().getType()))
					if(!item.hasItemMeta())
					{
						item.setItemMeta(meta);
					} else if(item.hasItemMeta() && !item.getItemMeta().equals(getBones().getItemMeta()))
					{
						item.setItemMeta(meta);
					}
			b++;
		}
	}

	private HashMap<UUID, Long> cooldown = new HashMap<>();
	private int cooldowntime = 40;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand().getType() == getBones().getType())
		{
			if(player.getWalkSpeed() == 0.1f)
			{
				if(this.cooldown.containsKey(player.getUniqueId()))
				{
					long ticksleft = ((Long) this.cooldown.get(player.getUniqueId())).longValue() / 50L
							+ this.cooldowntime - System.currentTimeMillis() / 50L;
					if(ticksleft > 0L)
						return;
				}

				player.sendMessage(ChatColor.GREEN + "You have fixed your broken legs!");
				player.playSound(player, Sound.ENTITY_TURTLE_EGG_CRACK, 2, 1);

				player.setWalkSpeed(0.2f);

				ItemStack is = player.getInventory().getItemInMainHand();
				if(is != null)
					if(is.getAmount() > 1)
					{
						is.setAmount(is.getAmount() - 1);
					} else
					{
						player.getInventory().setItemInMainHand(null);
					}
				this.cooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
			}
		}
	}

	@EventHandler
	public void onFall(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();

			if(player.getWorld().getName().equals("warz"))
			{
				if(e.getCause() == EntityDamageEvent.DamageCause.FALL
						&& !(e.isCancelled()))
				{
					Random random = new Random();
					int randomNumber = random.nextInt(3);

					if(randomNumber == 1)
					{
						player.setWalkSpeed(0.1f);
						player.swingMainHand();
						player.setSprinting(false);

						player.sendMessage(ChatColor.AQUA + "You have broken your legs!");
						player.playSound(player, Sound.ITEM_WOLF_ARMOR_CRACK, 1, 1);
					} 
					else
					{
						player.setWalkSpeed(0.2f);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();

		if(player.getWalkSpeed() == 0.1f)
		{
			if(!player.getWorld().getName().equals("warz"))
			{
				player.setWalkSpeed(0.2f);
				
			}
			if(event.getTo().getY() > event.getFrom().getY())
			{
				event.setCancelled(true);
			}
		}


	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		player.setWalkSpeed(0.2f);
	}

}

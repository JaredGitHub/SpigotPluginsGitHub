package main.java.me.jared.food.listeners;

import java.util.HashMap;
import java.util.UUID;

import main.java.me.jared.food.Sugar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class BloodBag implements Listener
{
	FileConfiguration config = Sugar.plugin.getConfig();

	public ItemStack getBloodBag()
	{
		ItemStack is = new ItemStack(Material.RED_DYE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.config.getString("bloodbagString").replaceAll("&", "§"));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getBloodBag().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for(i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if(item != null)
				if(item.getType().equals(getBloodBag().getType()))
					if(!item.hasItemMeta())
					{
						item.setItemMeta(meta);
					} else if(item.hasItemMeta() && !item.getItemMeta().equals(getBloodBag().getItemMeta()))
					{
						item.setItemMeta(meta);
					}
			b++;
		}
	}

	private HashMap<UUID, Long> cooldown = new HashMap<>();
	private int cooldowntime = 1;

	@EventHandler
	public void onPlayerInteract(PlayerInteractAtEntityEvent event)
	{
		if(event.getRightClicked() instanceof Player)
		{
			Player clicker = event.getPlayer();
			Player clickedPlayer = (Player) event.getRightClicked();

			if(clicker.getInventory().getItemInMainHand().getType() == getBloodBag().getType())
			{

				if(this.cooldown.containsKey(clicker.getUniqueId()))
				{
					long ticksleft = ((Long) this.cooldown.get(clicker.getUniqueId())).longValue() / 50L
							+ this.cooldowntime - System.currentTimeMillis() / 50L;
					if(ticksleft > 0L)
						return;
				}

				if(clickedPlayer.getHealth() < 18)
				{
					clickedPlayer.setHealth(clickedPlayer.getHealth() + 2);
					clicker.sendMessage(ChatColor.RED + "You healed " + clickedPlayer.getName());

					ItemStack is = clicker.getInventory().getItemInMainHand();
					if(is != null)
						if(is.getAmount() > 1)
						{
							is.setAmount(is.getAmount() - 1);
						} else
						{
							clicker.getInventory().setItemInMainHand(null);
						}
					for(Player player : Bukkit.getOnlinePlayers())
					{
						player.playSound(clickedPlayer, Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
						player.playSound(clicker, Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
					}

					this.cooldown.put(clicker.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
				} else
				{
					clicker.sendMessage(ChatColor.GREEN + "Player is full health!");
					clicker.playSound(clickedPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

					clickedPlayer.setHealth(20);
				}

			}
		}
	}
}

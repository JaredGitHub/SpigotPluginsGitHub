package me.jared.food.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import me.jared.food.Sugar;
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

public class MountainDew implements Listener
{
	FileConfiguration config = Sugar.plugin.getConfig();

	ArrayList<String> lore = new ArrayList<>();

	public static HashMap<UUID, Long> cooldown = new HashMap<>();

	private int cooldowntime = this.config.getInt("dewDelay");

	public ItemStack getDew()
	{
		ItemStack is = new ItemStack(Material.BONE_MEAL);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.config.getString("dewString").replaceAll("&", "§"));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		ItemMeta meta = getDew().getItemMeta();
		Player p = (Player) e.getPlayer();
		byte b;
		int i;
		ItemStack[] arrayOfItemStack;
		for(i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;)
		{
			ItemStack item = arrayOfItemStack[b];
			if(item != null)
				if(item.getType().equals(getDew().getType()))
					if(!item.hasItemMeta())
					{
						item.setItemMeta(meta);
					} else if(item.hasItemMeta() && !item.getItemMeta().equals(getDew().getItemMeta()))
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
		if(player.getInventory().getItemInMainHand().getType() == getDew().getType())
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemMeta meta = getDew().getItemMeta();
				byte b;
				int i;
				ItemStack[] arrayOfItemStack;
				for(i = (arrayOfItemStack = player.getInventory().getContents()).length, b = 0; b < i;)
				{
					ItemStack item = arrayOfItemStack[b];
					if(item != null)
						if(item.getType().equals(Material.BONE_MEAL))
						{
							item.setItemMeta(meta);
							break;
						}
					b++;
				}
				if(cooldown.containsKey(player.getUniqueId()))
				{
					long ticksleft = ((Long) cooldown.get(player.getUniqueId())).longValue() / 50L + this.cooldowntime
							- System.currentTimeMillis() / 50L;
					if(ticksleft > 0L)
						return;
				}
				for(Player p : Bukkit.getOnlinePlayers())
					p.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0F, 1.0F);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				ItemStack is = player.getInventory().getItemInMainHand();
				if(is != null)
					if(is.getAmount() > 1)
					{
						is.setAmount(is.getAmount() - 1);
					} else
					{
						player.getInventory().setItemInMainHand(null);
					}
				cooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
			}
	}
}

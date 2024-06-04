package me.jared.behavior;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jared.BoostPad;
import net.md_5.bungee.api.ChatColor;

public class Boost implements Listener
{

	private static HashMap<Integer, Integer> noFallEntities = new HashMap<>();

	public static HashMap<Integer, Integer> getNoFallEntities()
	{
		return noFallEntities;
	}

	// Make name for the sponge "Boost Pad"
	public void boost(Player p)
	{
		for(ItemStack item : p.getInventory().getContents())
		{
			String name = ChatColor.BLUE + "Boost Pad";

			if(item == null)
				continue;

			if(item.getType() == Material.SPONGE)
			{
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(name);
				item.setItemMeta(meta);
			}
		}
		// Makes the player bounce in the direction they are looking when they jump on
		// the sponge
		if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE)
		{
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2, 2);
			p.setVelocity(p.getLocation().getDirection().multiply(2.5).setY(1.25));
			addNoFall(p, 20 * 5);
		}
	}

	public void addNoFall(final Entity e, int ticks)
	{
		if(noFallEntities.containsKey(Integer.valueOf(e.getEntityId())))
			Bukkit.getServer().getScheduler()
					.cancelTask(((Integer) noFallEntities.get(Integer.valueOf(e.getEntityId()))).intValue());
		int taskId = BoostPad.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BoostPad.getInstance(),
				new Runnable()
				{
					public void run()
					{
						if(noFallEntities.containsKey(Integer.valueOf(e.getEntityId())))
							noFallEntities.remove(Integer.valueOf(e.getEntityId()));
					}
				}, ticks);
		noFallEntities.put(Integer.valueOf(e.getEntityId()), Integer.valueOf(taskId));
	}
}

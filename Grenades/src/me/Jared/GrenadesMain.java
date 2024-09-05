package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Grenade.GrenadeType;
import me.Jared.ParticleRunnables.ServerRunnable;

public class GrenadesMain extends JavaPlugin implements Listener
{
	public static GrenadesMain instance;

	@Override
	public void onEnable()
	{
		instance = this;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Grenades are enabled!");

		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginCommand("grenades").setExecutor(new GrenadeCommand());

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ServerRunnable(), 0, 5l);
	}

	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Grenades are disabled!");
	}

	public static GrenadesMain getInstance()
	{
		return instance;
	}

	public static final Grenade[] grenades = new Grenade[]
	{ new Grenade(Material.SLIME_BALL, "&6Grenade", GrenadeType.GRENADE, 5f, 1),
			new Grenade(Material.MAGMA_CREAM, "&4Molotov", GrenadeType.MOLOTOV, 2, 2),
			new Grenade(Material.GUNPOWDER, "&8Flashbang", GrenadeType.FLASHBANG, 2, 3),
			new Grenade(Material.GHAST_TEAR, "&7Smoke Grenade", GrenadeType.SMOKE, 2, 4),
			new Grenade(Material.HONEYCOMB, "&7Sticky Grenade", GrenadeType.STICKY, 0.5f, 5) };

	@EventHandler
	public void onRightClick(PlayerInteractEvent e)
	{
		if((e.getAction() == (Action.RIGHT_CLICK_BLOCK)) || (e.getAction() == (Action.RIGHT_CLICK_AIR)))
		{
			Player player = e.getPlayer();
			Material itemInHand = player.getInventory().getItemInMainHand().getType();

			for(int i = 0; i < grenades.length; i++)
			{
				if(itemInHand == grenades[i].getItem())
				{
					grenades[i].throwGrenade(player);
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();

		for(int i = 0; i < grenades.length; i++)
		{
			for(ItemStack item : player.getInventory())
			{
				if(item != null && item.getType() == grenades[i].getItem())
				{
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(grenades[i].getDisplayName());
					item.setItemMeta(meta);
				}
			}
		}
	}
}

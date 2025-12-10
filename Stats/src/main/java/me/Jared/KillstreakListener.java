package me.Jared;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.Jared.Events.KillstreakFifteenEvent;
import me.Jared.Events.KillstreakFiveEvent;
import me.Jared.Events.KillstreakTenEvent;
import me.Jared.Events.KillstreakTwentyEvent;

public class KillstreakListener implements Listener
{
	private Stats stats = Stats.getPlugin(Stats.class);
	FileConfiguration config = stats.getConfig();


	private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
	private int cooldowntime = 3*1000;

	@EventHandler
	public void onDeath(PlayerDeathEvent  e)
	{
		Player p = e.getEntity();

		if(config.getBoolean("killstreakRewards") == false
				|| p.getWorld().getName().equals("warz"))
		{
			return;
		}
		
		if(p.getKiller() != null)
		{
			//Setting killstreak stuff
			int killStreakConfig = config.getInt(p.getKiller().getUniqueId() + ".killStreak");
			//If killstreak 5
			switch (killStreakConfig)
			{
			case 5:
				KillstreakFiveEvent killstreakFiveEvent = new KillstreakFiveEvent(p.getKiller());
				stats.getServer().getPluginManager().callEvent(killstreakFiveEvent);
				break;

			case 10:
				KillstreakTenEvent killstreakTenEvent = new KillstreakTenEvent(p.getKiller());
				stats.getServer().getPluginManager().callEvent(killstreakTenEvent);
				break;
			case 15:
				KillstreakFifteenEvent killstreakFifteenEvent = new KillstreakFifteenEvent(p.getKiller());
				stats.getServer().getPluginManager().callEvent(killstreakFifteenEvent);
				break;
			case 20:
				KillstreakTwentyEvent killstreakTwentyEvent = new KillstreakTwentyEvent(p.getKiller());
				stats.getServer().getPluginManager().callEvent(killstreakTwentyEvent);
				break;

			default:
				break;
			}
		}
	}

	//Kill streak reward stuff
	@EventHandler
	public void onKillStreak5(KillstreakFiveEvent e)
	{
		Player p = e.getPlayer();

		//If player is in cooldown STOP!!!
		if(cooldown.containsKey(p.getUniqueId()))
		{
			long timeleft = ((cooldown.get(p.getUniqueId())) + cooldowntime) - (System.currentTimeMillis());

			timeleft /= 1000.0;
			if(timeleft > 0)
			{
				return;
			}
		}

		//Give player speeed and resistance for 15 seconds
		//Set bounty on player for 50 gems
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 15 * 20, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 3));

		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
		p.sendMessage(ChatColor.GREEN + "You got five killstreak!");

		cooldown.put(p.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onKillStreak10(KillstreakTenEvent e)
	{
		Player p = e.getPlayer();

		//If player is in cooldown STOP!!!
		if(cooldown.containsKey(p.getUniqueId()))
		{
			long timeleft = ((cooldown.get(p.getUniqueId())) + cooldowntime) - (System.currentTimeMillis());

			timeleft /= 1000.0;
			if(timeleft > 0)
			{
				return;
			}
		}

		//Give player speed and resistance for a minute

		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60 * 20, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 3));

		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
		p.sendMessage(ChatColor.GREEN + "You got ten killstreak!");

		cooldown.put(p.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onKillStreak15(KillstreakFifteenEvent e)
	{
		Player p = e.getPlayer();

		//If player is in cooldown STOP!!!
		if(cooldown.containsKey(p.getUniqueId()))
		{
			long timeleft = ((cooldown.get(p.getUniqueId())) + cooldowntime) - (System.currentTimeMillis());

			timeleft /= 1000.0;
			if(timeleft > 0)
			{
				return;
			}
		}

		//Give player minigun and resistance for 30 seconds

		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60 * 20, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 3));

		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
		p.sendMessage(ChatColor.GREEN + "You got fifteen killstreak!");
		giveMinigun(p,10);

		cooldown.put(p.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onKillStreak20(KillstreakTwentyEvent e)
	{
		Player p = e.getPlayer();

		//If player is in cooldown STOP!!!
		if(cooldown.containsKey(p.getUniqueId()))
		{
			long timeleft = ((cooldown.get(p.getUniqueId())) + cooldowntime) - (System.currentTimeMillis());

			timeleft /= 1000.0;
			if(timeleft > 0)
			{
				return;
			}
		}

		//Give player a minigun and speed and resistance for 2 minutes
		p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 120 * 20, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120 * 20, 3));

		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
		p.sendMessage(ChatColor.GREEN + "You got twenty killstreak!");
		giveMinigun(p,20);
		
		cooldown.put(p.getUniqueId(), System.currentTimeMillis());
	}

	private final HashMap<UUID, ItemStack[]> oldInventory = new HashMap<UUID, ItemStack[]>();
	public void giveMinigun(Player p, int duration)
	{
		oldInventory.put(p.getUniqueId(), p.getInventory().getContents());

		Inventory inventory = p.getInventory();
		inventory.clear();
		inventory.setItem(0, new ItemStack(Material.STICK));
		for(int i  = 10; i < 27; i++)
		{
			inventory.setItem(i, new ItemStack(Material.FLINT,64));
		}
		if(p.isOnline())
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(stats, () ->
			{
				p.getInventory().setContents(oldInventory.get(p.getUniqueId()));

				oldInventory.remove(p.getUniqueId());

			}, 20L * duration);
		}
		else
		{
			Bukkit.broadcastMessage("TESTING");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		if(oldInventory.containsKey(p.getUniqueId()))
		{
			p.getInventory().setContents(oldInventory.get(p.getUniqueId()));
			oldInventory.remove(p.getUniqueId());

		}
	}
}

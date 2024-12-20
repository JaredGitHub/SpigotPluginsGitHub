package me.jared.grapple;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Grapple extends JavaPlugin implements Listener
{
	public HashMap<Integer, Integer> noFallEntities = new HashMap<>();
	FileConfiguration config = this.getConfig();

	public void loadConfig()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void onEnable()
	{
		loadConfig();
		getConfig().addDefault("grappleCooldown", 3);
		getConfig().addDefault("grappleUses", 20);
		saveConfig();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Grapple plugin is here  BOIIIII!!!");
		getServer().getPluginManager().registerEvents(this, (Plugin) this);

	}

	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Grapple is gone!");
	}

	private ArrayList<Player> cooldown = new ArrayList<Player>();

	@EventHandler
	public void onGrapple(PlayerFishEvent e)
	{
		Player p = e.getPlayer();

		if(!(cooldown.contains(p)) && (e.getState() == State.IN_GROUND)
				|| !(cooldown.contains(p)) && (e.getState() == State.CAUGHT_ENTITY))
		{
			new GrappleCooldownTask(p, getConfig().getInt("grappleCooldown") * 20).runTaskTimer(this, 0, 1);

			for(Player online : Bukkit.getOnlinePlayers())
			{
				online.playSound(p.getLocation(), Sound.ENTITY_MAGMA_CUBE_JUMP, 1, 1);
			}
			
			if(p.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD)
			{
				p.sendMessage(ChatColor.RED + "No offhand noob!");
				return;
			}

			ItemStack item = new ItemStack(p.getInventory().getItemInMainHand());
			Damageable meta = (Damageable) item.getItemMeta();

			String strLore = meta.getLore().get(0);
			String loreUses = strLore.substring(16);
			Integer number = Integer.valueOf(loreUses);
			meta.setDamage(30);

			if(number <= 1)
			{
				p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
			}

			number--;
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Uses Left - " + ChatColor.GREEN + number);
			meta.setLore(lore);
			p.getInventory().getItemInMainHand().setItemMeta(meta);

			Location target = e.getHook().getLocation();
			Vector v = getVectorForPoints(p.getLocation(), target);
			p.setVelocity(v);
			addNoFall((Entity) p, 60);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
			{
				@Override
				public void run()
				{
					cooldown.remove(p);
				}
			}, getConfig().getInt("grappleCooldown") * 20);
			cooldown.add(p);
		}
	}

	int grappleUses;

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		ItemStack grapple = getGrapple(p);

		for(ItemStack item : p.getInventory().getContents())
		{
			if(item == null)
			{
				continue;
			}
			if(item.getType().equals(Material.FISHING_ROD))
			{
				if(!item.hasItemMeta())
				{
					item.setItemMeta(grapple.getItemMeta());
					break;
				} else if(item.getItemMeta().getLore() == null)
				{
					item.setItemMeta(grapple.getItemMeta());
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		if(event.getCause() == EntityDamageEvent.DamageCause.FALL
				&& this.noFallEntities.containsKey(Integer.valueOf(event.getEntity().getEntityId())))
			event.setCancelled(true);
	}

	public void sendActionBar(Player player, String message)
	{
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}

	public void addNoFall(final Entity e, int ticks)
	{
		if(this.noFallEntities.containsKey(Integer.valueOf(e.getEntityId())))
			Bukkit.getServer().getScheduler()
					.cancelTask(((Integer) this.noFallEntities.get(Integer.valueOf(e.getEntityId()))).intValue());
		int taskId = getServer().getScheduler().scheduleSyncDelayedTask((Plugin) this, new Runnable()
		{
			public void run()
			{
				if(Grapple.this.noFallEntities.containsKey(Integer.valueOf(e.getEntityId())))
					Grapple.this.noFallEntities.remove(Integer.valueOf(e.getEntityId()));
			}
		}, ticks);
		this.noFallEntities.put(Integer.valueOf(e.getEntityId()), Integer.valueOf(taskId));
	}

	private Vector getVectorForPoints(Location l1, Location l2)
	{
		double g = -0.08;
		double d = l2.distance(l1);
		double t = d;
		double vX = (1.0 + 0.14 * t) * (l2.getX() - l1.getX()) / t;
		double vY = (1.0 + 0.04 * t) * (l2.getY() - l1.getY()) / t - 0.5 * g * t;
		double vZ = (1.0 + 0.14 * t) * (l2.getZ() - l1.getZ()) / t;
		return new Vector(vX, vY, vZ);
	}

	public ItemStack getGrapple(Player p)
	{
		if(p.hasPermission("ranks.default"))
		{
			grappleUses = 5;
		}
		if(p.hasPermission("ranks.vip"))
		{
			grappleUses = 10;
		}
		if(p.hasPermission("ranks.vipplus"))
		{
			grappleUses = 15;
		}
		if(p.hasPermission("ranks.mvp"))
		{
			grappleUses = 20;
		}
		if(p.hasPermission("ranks.mvpplus"))
		{
			grappleUses = 25;
		}
		if(p.hasPermission("ranks.owner"))
		{
			grappleUses = 500;
		}

		ItemStack grapple = new ItemStack(Material.FISHING_ROD);
		ItemMeta im = grapple.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Grappling Hook");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Uses Left - " + ChatColor.GREEN + grappleUses);
		im.setLore(lore);
		grapple.setItemMeta(im);

		return grapple;
	}
}

package me.jared;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

import me.jared.behavior.Boost;
import me.jared.cleanup.Cleanup;
import net.md_5.bungee.api.ChatColor;

public class BoostPad extends JavaPlugin implements Listener
{
	private static BoostPad instance;
	@Override
	public void onEnable()
	{
		instance = this;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Boost pads are here baby!!");

		getServer().getPluginManager().registerEvents(this, (Plugin)this);
		getServer().getPluginManager().registerEvents(new Boost(), (Plugin)this);

		this.getCommand("boostpad").setExecutor(new Cleanup());
		this.getCommand("ladder").setExecutor(new Cleanup());
		this.getCommand("web").setExecutor(new Cleanup());
		this.getCommand("landmines").setExecutor(new Cleanup());
	}

	public static BoostPad getInstance()
	{
		return instance;
	}

	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Boost pads are gone!! OH NOO!!");
	}


	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL && 
				Boost.getNoFallEntities().containsKey(Integer.valueOf(event.getEntity().getEntityId())))
			event.setCancelled(true); 
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();

		//Boosts the player into the air based upon the direction they are facing
		Boost boost = new Boost();
		boost.boost(p);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		ArrayList<String> boostPads = new ArrayList<String>(this.getConfig().getStringList("boostPads"));

		if(e.getBlock().getType().equals(Material.SPONGE))
		{
			double x = e.getBlock().getLocation().getX();
			double y = e.getBlock().getLocation().getY();
			double z =e.getBlock().getLocation().getZ();
			String world = e.getBlock().getLocation().getWorld().getName();

			boostPads.add(x +", " + y +", " + z + "," + world);
			this.getConfig().set("boostPads",boostPads);
			this.saveConfig();
		}

		ArrayList<String> ladders = new ArrayList<String>(this.getConfig().getStringList("ladders"));
		if(e.getBlock().getType().equals(Material.LADDER))
		{
			double x = e.getBlock().getLocation().getX();
			double y = e.getBlock().getLocation().getY();
			double z =e.getBlock().getLocation().getZ();
			String world = e.getBlock().getLocation().getWorld().getName();

			ladders.add(x +", " + y +", " + z + "," + world);

			this.getConfig().set("ladders", ladders);
			this.saveConfig();
		}

		ArrayList<String> webs = new ArrayList<String>(this.getConfig().getStringList("webs"));
		if(e.getBlock().getType().equals(Material.COBWEB))
		{
			double x = e.getBlock().getLocation().getX();
			double y = e.getBlock().getLocation().getY();
			double z =e.getBlock().getLocation().getZ();
			String world = e.getBlock().getLocation().getWorld().getName();

			webs.add(x +", " + y +", " + z + "," + world);

			this.getConfig().set("webs", webs);
			this.saveConfig();
		}

		ArrayList<String> landmines = new ArrayList<String>(this.getConfig().getStringList("landmines"));
		if(e.getBlock().getType().equals(Material.STONE_PRESSURE_PLATE))
		{
			double x = e.getBlock().getLocation().getX();
			double y = e.getBlock().getLocation().getY();
			double z =e.getBlock().getLocation().getZ();
			String world = e.getBlock().getLocation().getWorld().getName();

			landmines.add(x +", " + y +", " + z + "," + world);

			this.getConfig().set("landmines", landmines);
			this.saveConfig();
		}
	}

	private String getIndex(String string, int num)
	{
		StringBuilder stringBuilder = new StringBuilder();

		int count = 0;
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == ',')
			{
				count++;
				continue;
			}

			if(count == num)
			{
				stringBuilder.append(string.charAt(i));
			}
		}

		return stringBuilder.toString();
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		BlockIterator iterator = new BlockIterator(e.getEntity().getWorld(), e.getEntity().getLocation().toVector(), e.getEntity().getVelocity().normalize(), 0.0D, 4);
		Block hitBlock = null;
		while(iterator.hasNext())
		{
			hitBlock = iterator.next();
			if(hitBlock.getType() != Material.AIR) break;
		}

		if(hitBlock.getType() == Material.SPONGE)
		{
			if(e.getEntity().getShooter() instanceof Player)
			{
				Player p = (Player) e.getEntity().getShooter();

				ArrayList<String> boostPads = new ArrayList<String>(this.getConfig().getStringList("boostPads"));

				for (String boostPadLoc : boostPads)
				{
					double x = Double.parseDouble(getIndex(boostPadLoc, 0));
					double y = Double.parseDouble(getIndex(boostPadLoc, 1));
					double z = Double.parseDouble(getIndex(boostPadLoc, 2));
					String world = getIndex(boostPadLoc, 3);
					
					Location loc = new Location(Bukkit.getWorld(world), x, y, z);

					p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);

					if(loc.equals(hitBlock.getLocation()))
					{
						hitBlock.getLocation().getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}
}

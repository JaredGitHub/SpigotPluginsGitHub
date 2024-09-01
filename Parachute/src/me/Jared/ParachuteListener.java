package me.Jared;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class ParachuteListener implements Listener
{
	private Parachute plugin;
	public ParachuteListener(Parachute plugin)
	{
		this.plugin = plugin;
	}
	public HashMap<Integer, Integer> noFallEntities = new HashMap<>();

	public ItemStack getParachute()
	{

		ItemStack parachute = new ItemStack(Material.LEATHER);
		ItemMeta im = parachute.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Parachute");
		parachute.setItemMeta(im);
		return parachute;
	}

	EntityType head = EntityType.CHICKEN;

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();

		if(p.getPassengers().isEmpty())
		{
			Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);

			//If the player falls while holding leather a chicken will spawn on their head
			if(p.getInventory().getItemInMainHand().getType().equals(getParachute().getType()))
			{
				if(p.getFallDistance() >= 2.5)
				{
					if(block.getType() == Material.AIR)
					{
						//Put a chicken on their head
						LivingEntity chicken = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), head);
						chicken.setSilent(true);
						chicken.setAI(false);
						chicken.setVisualFire(false);
						p.addPassenger(chicken);

						ItemStack is = p.getInventory().getItemInMainHand();

						if(is.getAmount() <= 1) p.getInventory().setItemInMainHand((new ItemStack(Material.AIR)));

						is.setAmount(is.getAmount() - 1);
					}
				}
			}

			for(ItemStack item : p.getInventory().getContents())
			{
				ItemMeta meta = getParachute().getItemMeta();
				if(item == null)
				{
					continue;
				}

				if(item.getType().equals(Material.LEATHER))
				{
					item.setItemMeta(meta);
					break;
				}
			}
		}
		else
		{

			//Activating parachute event when player has a chicken on their head

			if(p.getPassengers().get(0).getType() == head && p.getFallDistance() >= 2.5)
			{
				ParachuteEvent parachuteEvent = new ParachuteEvent(p);
				plugin.getServer().getPluginManager().callEvent(parachuteEvent);
			}

			//If the player is not in the air, a chicken will be removed from their head
			Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if(block.getType() != Material.AIR)
			{
				if(p.getPassengers().get(0) != null && p.getPassengers().get(0).getType() == head)
				{
					addNoFall(p, 20 * 5);
					p.getPassengers().get(0).remove();
				}
			}

			if(p.isSneaking())
			{
				addNoFall(p, 20 * 4);
				if(p.getPassengers().isEmpty()) return;
				if(p.getPassengers().get(0) != null)
				{
					p.getPassengers().get(0).remove();
				}
			}
		}
	}

	Cooldown cooldown = new Cooldown(0.4);
	@EventHandler
	public void onParachute(ParachuteEvent e)
	{
		Player p = e.getPlayer();

		if(!cooldown.isOnCooldown(p))
		{
			for(Player online : Bukkit.getOnlinePlayers())
			{
				online.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
			}
			cooldown.putInCooldown(p);
		}

		double flySpeed = 0.50;
		double downSpeed = 0.4;
		Vector vec = new Vector (p.getLocation().getDirection().multiply(flySpeed).getX(), p.getVelocity().getY() * downSpeed, p.getLocation().getDirection().multiply(flySpeed).getZ());

		p.setVelocity(vec);
	}


	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL && 
				this.noFallEntities.containsKey(Integer.valueOf(event.getEntity().getEntityId())))
			event.setCancelled(true); 
	}

	public void addNoFall(final Entity e, int ticks)
	{
		if (this.noFallEntities.containsKey(Integer.valueOf(e.getEntityId())))
			Bukkit.getServer().getScheduler().cancelTask(((Integer)noFallEntities.get(Integer.valueOf(e.getEntityId()))).intValue()); 
		int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)plugin, new Runnable()
		{
			public void run()
			{
				if (noFallEntities.containsKey(Integer.valueOf(e.getEntityId())))
					noFallEntities.remove(Integer.valueOf(e.getEntityId())); 
			}
		},  ticks);
		this.noFallEntities.put(Integer.valueOf(e.getEntityId()), Integer.valueOf(taskId));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		for(Entity entity : player.getWorld().getEntities())
		{
			if(entity.getType() == EntityType.CHICKEN)
			{
				if(entity.getVehicle() == null)
				{
					entity.remove();
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		if(p.getPassengers().isEmpty()) return;
		if(p.getPassengers().get(0) != null)
		{
			p.getPassengers().get(0).remove();
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			
			addNoFall(p, 20 * 4);
			if(p.getPassengers().isEmpty()) return;
			if(p.getPassengers().get(0) != null)
			{
				p.getPassengers().get(0).remove();
			}
		}
	}
}

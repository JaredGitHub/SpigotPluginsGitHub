package me.Jared.Listeners;

import java.util.HashMap;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.util.BlockIterator;

import me.Jared.GunsPlugin;
import me.Jared.Events.GunsBulletCollideEvent;
import me.Jared.Events.GunsDamageEntityEvent;
import me.Jared.Events.GunsKillEntityEvent;
import me.Jared.Guns.Bullet;
import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;

public class EntityListener implements Listener
{
	public static HashMap<Entity, HashMap<Entity, Long>> s = new HashMap<Entity, HashMap<Entity, Long>>();
	public static HashMap<Entity, HashMap<EntityDamageEvent.DamageCause, Long>> ss = new HashMap<Entity, HashMap<DamageCause, Long>>();
	public static HashMap<Integer, Long> lastBlood = new HashMap<Integer, Long>();

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{

		Projectile check = event.getEntity();
		Bullet bullet = GunsPlugin.getPlugin.getBullet(check.getEntityId());
		if(bullet != null)
		{
			bullet.onHit();
			bullet.setNextTickDestroy();
			BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(),
					event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0, 4);
			Block hitBlock = null;
			while(iterator.hasNext())
			{
				hitBlock = iterator.next();
				if(hitBlock.getType() != Material.AIR)
					break;
			}
			hitBlock.getWorld().playEffect(hitBlock.getLocation(), Effect.STEP_SOUND, (Object) hitBlock.getType());
			GunsBulletCollideEvent evv = new GunsBulletCollideEvent(bullet.getShooter(), bullet.getGun(), hitBlock);
			Bukkit.getPluginManager().callEvent((Event) evv);
			bullet.setNextTickDestroy();
			switch(hitBlock.getType())
			{
			case GLASS:
				breakGlass(hitBlock);
			case GLASS_PANE:
				breakGlass(hitBlock);
			default:
				break;
			}
		}
	}

	private void breakGlass(Block hitBlock)
	{
		if(hitBlock.getWorld().getName().equals("warz"))
		{
			Location blockLocation = hitBlock.getLocation();
			Location aboveLocation = blockLocation.clone().add(0, 1, 0);
			Block aboveBlock = aboveLocation.getBlock();
			Material aboveBlockType = aboveBlock.getType();

			if(aboveBlockType == Material.RED_WOOL)
				return;
			hitBlock.setType(Material.AIR);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Bullet bullet;
		Entity damager;
		EntityDamageEvent e;
		LivingEntity livingEntity = event.getEntity();

		if(livingEntity.getLastDamageCause() != null
				&& (e = livingEntity.getLastDamageCause()) instanceof EntityDamageByEntityEvent
				&& (damager = ((EntityDamageByEntityEvent) e).getDamager()) instanceof Projectile
				&& (bullet = GunsPlugin.getPlugin.getBullet((Entity) ((Projectile) damager))) != null)
		{
			GunPlayer shooter = bullet.getShooter();
			Gun used = bullet.getGun();
			GunsKillEntityEvent pvpgunkill = new GunsKillEntityEvent(shooter, used, livingEntity);
			GunsPlugin.getPlugin.getServer().getPluginManager().callEvent((Event) pvpgunkill);
		}
	}

	public boolean damage(Entity damager, Entity damaged)
	{
		if(!s.containsKey(damager))
		{
			HashMap<Entity, Long> hashMap = new HashMap<Entity, Long>();
			hashMap.put(damaged, System.currentTimeMillis());
			s.put(damager, hashMap);
			return true;
		}
		HashMap<Entity, Long> lastOfEntity = s.get(damager);
		if(lastOfEntity.containsKey(damaged) && System.currentTimeMillis() <= lastOfEntity.get(damaged) + 600L)
		{
			return false;
		}
		lastOfEntity.put(damaged, System.currentTimeMillis());
		s.put(damager, lastOfEntity);
		return true;
	}

	public boolean damage(EntityDamageEvent.DamageCause damager, Entity damaged)
	{
		if(damager != EntityDamageEvent.DamageCause.PROJECTILE)
		{
			if(damager == EntityDamageEvent.DamageCause.FIRE_TICK)
			{
				damager = EntityDamageEvent.DamageCause.LAVA;
			}
			if(!ss.containsKey(damaged))
			{
				HashMap<EntityDamageEvent.DamageCause, Long> hashMap = new HashMap<EntityDamageEvent.DamageCause, Long>();
				hashMap.put(damager, System.currentTimeMillis());
				ss.put(damaged, hashMap);
				return true;
			}
			HashMap<EntityDamageEvent.DamageCause, Long> lastOfEntity = ss.get(damaged);
			if(lastOfEntity.containsKey(damager) && System.currentTimeMillis() <= lastOfEntity.get(damager) + 600L)
			{
				return false;
			}
			lastOfEntity.put(damager, System.currentTimeMillis());
			ss.put(damaged, lastOfEntity);
			return true;
		}
		return true;
	}

	@EventHandler
	public void getDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof LivingEntity)
		{
			LivingEntity hurt = (LivingEntity) e.getEntity();
			EntityDamageEvent.DamageCause dm = e.getCause();
			if(dm == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || dm == EntityDamageEvent.DamageCause.PROJECTILE)
			{
				hurt.setMaximumNoDamageTicks(0);
				e.setCancelled(false);
				return;
			}
			if(!this.damage(e.getCause(), (Entity) hurt))
			{
				e.setCancelled(true);
			}
			hurt.setMaximumNoDamageTicks(0);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		Entity damager = event.getDamager();
		if(event.getEntity() instanceof Damageable)
		{
			Bullet bullet;
			Damageable hurt = (Damageable) event.getEntity();
			if(!(damager instanceof Projectile) && !this.damage(damager, (Entity) hurt))
			{
				event.setCancelled(true);
				return;
			}
			if(damager instanceof Projectile
					&& (bullet = GunsPlugin.getPlugin.getBullet(((Projectile) damager).getEntityId())) != null)
			{
				if(bullet.getShooter().getPlayer().getEntityId() == hurt.getEntityId())
				{
					event.setCancelled(true);
				}
				boolean headshot = false;
				if(this.isNear(bullet.getProjectile().getLocation(), ((LivingEntity) hurt).getEyeLocation(), 0.26))
				{
					headshot = true;
				}
				GunsDamageEntityEvent pvpgundmg = new GunsDamageEntityEvent(event, bullet.getShooter(), bullet.getGun(),
						event.getEntity(), headshot);
				GunsPlugin.getPlugin.getServer().getPluginManager().callEvent((Event) pvpgundmg);
				if(!pvpgundmg.isCancelled() && !event.isCancelled())
				{
					double damage = pvpgundmg.getDamage();
					double mult = 1.0;
					if(pvpgundmg.isHeadshot())
					{
						mult = 2.0;
						hurt.getWorld().playSound(hurt.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
					}
					event.setDamage(damage * mult);
					bullet.getGun().doKnockback((Entity) hurt, bullet.getVelocity());
					int armorPenetration = bullet.getGun().getArmorPenetration();
					if(armorPenetration > 0)
					{
						double health = hurt.getHealth();
						double newHealth = health - (double) armorPenetration;
						if(newHealth < 0.0)
						{
							newHealth = 0.0;
						}
						if(newHealth > 20.0)
						{
							newHealth = 20.0;
						}
						hurt.setHealth(newHealth);
					}
				}
			}
		}
	}

	private boolean isNear(Location location, Location eyeLocation, double d)
	{
		return Math.abs(location.getY() - eyeLocation.getY()) <= d;
	}
}

package me.Jared.Guns;

import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

import me.Jared.GunsPlugin;
import me.Jared.Utils.Explosion;

public class Bullet
{
	private int ticks;
	private int releaseTime;
	private boolean dead = false;
	private boolean active = true;
	private boolean destroyNextTick = false;
	private boolean released = false;
	private Entity projectile;
	private Vector velocity;
	private Location lastLocation;
	private Location startLocation;
	private GunPlayer shooter;
	private Gun shotFrom;

	public Bullet(GunPlayer owner, Vector vec, Gun gun)
	{
		this.shotFrom = gun;
		this.shooter = owner;
		this.velocity = vec.clone().normalize().multiply(gun.getBulletSpeed());

		if(gun.isThrowable())
		{
			ItemStack thrown = new ItemStack(gun.getGunType(), 1);
			this.projectile = owner.getPlayer().getWorld().dropItem(owner.getPlayer().getEyeLocation(), thrown);
			((Item) this.projectile).setPickupDelay(12000);
		} else
		{
			Location loc = owner.getPlayer().getEyeLocation();
			adjustLocationForHand(loc, owner);

			this.projectile = owner.getPlayer().launchProjectile(Snowball.class, this.velocity);
			this.projectile.teleport(loc);

			if(gun.getAmmoType() == Material.CLAY_BALL)
			{
				this.projectile.setGravity(false);
			} else
			{
				this.projectile.setGravity(true);
			}
		}
		this.startLocation = this.projectile.getLocation();
	}

	private void adjustLocationForHand(Location loc, GunPlayer owner)
	{
		double offset = (owner.getPlayer().getMainHand() == MainHand.LEFT) ? -0.3 : 0.3;

		String direction = getDirection(loc);

		// Get player's direction vector
		Vector directionVector = owner.getPlayer().getLocation().getDirection().normalize();

		// Apply backward offset first (this moves the location backward along the player's look direction)
		loc.add(directionVector.multiply(-0.3));
		switch(direction)
		{
		case "NORTH":
			loc.add(offset, 0, 0);
			break;
		case "SOUTH":
			loc.add(-offset, 0, 0);
			break;
		case "EAST":
			loc.add(0, 0, offset);
			loc.setYaw(90);
			break;
		case "WEST":
			loc.add(0, 0, -offset);
			loc.setYaw(-90);
			break;
		case "NORTHEAST":
			loc.add(offset, 0, offset);
			loc.setYaw(135);
			break;
		case "NORTHWEST":
			loc.add(offset, 0, -offset);
			loc.setYaw(-135);
			break;
		case "SOUTHEAST":
			loc.add(-offset, 0, offset);
			loc.setYaw(45);
			break;
		case "SOUTHWEST":
			loc.add(-offset, 0, -offset);
			loc.setYaw(-45);
			break;
		}

	}

	private String getDirection(Location location)
	{
		float yaw = location.getYaw();
		if(yaw > -155 && yaw < -115)
		{
			return "NORTHEAST";
		} else if(yaw > -65 && yaw < -25)
		{
			return "SOUTHEAST";
		} else if(yaw > 115 && yaw < 155)
		{
			return "NORTHWEST";
		} else if(yaw > 25 && yaw < 65)
		{
			return "SOUTHWEST";
		} else if(yaw > -135 && yaw < -45)
		{
			return "EAST";
		} else if(yaw > -45 && yaw < 45)
		{
			return "SOUTH";
		} else if(yaw > 45 && yaw < 135)
		{
			return "WEST";
		} else
		{
			return "NORTH";
		}
	}

	public void tick()
	{
		if(!this.dead)
		{
			++this.ticks;
			if(this.projectile != null)
			{
				this.lastLocation = this.projectile.getLocation();
//				if(this.ticks > this.releaseTime)
//				{
//					this.dead = true;
//					return;
//				}
				if(this.shotFrom.hasSmokeTrail())
				{
					this.lastLocation.getWorld().playEffect(this.lastLocation, Effect.SMOKE, 1);
				}
				if(this.shotFrom.isThrowable() && this.ticks == 90)
				{
					this.remove();
					return;
				}
				if(this.active)
				{
					double dis = this.lastLocation.distance(this.startLocation);

					if(this.lastLocation.getWorld().equals(this.startLocation.getWorld())
							&& dis > this.shotFrom.getMaxDistance())
					{
						this.active = false;
						if(!this.shotFrom.isThrowable())
						{
							this.velocity.multiply(0.15);
						}
					}
				}
			} else
			{
				this.dead = true;
			}
			if(this.ticks > 200)
			{
				this.dead = true;
			}
		} else
		{
			this.remove();
		}
		if(this.destroyNextTick)
		{
			this.dead = true;
		}
	}

	public Gun getGun()
	{
		return this.shotFrom;
	}

	public GunPlayer getShooter()
	{
		return this.shooter;
	}

	public Vector getVelocity()
	{
		return this.velocity;
	}

	public void remove()
	{
		this.dead = true;
		GunsPlugin.getPlugin.removeBullet(this);
		this.onHit();
		this.destroy();
	}

	public void onHit()
	{
		if(this.released)
		{
			return;
		}
		this.released = true;
		if(this.projectile != null)
		{
			this.lastLocation = this.projectile.getLocation();
			if(this.shotFrom != null)
			{
				int rad;
				int rad2 = rad = (int) this.shotFrom.getExplodeRadius();
				if(this.shotFrom.getFireRadius() <= (double) rad && rad > 0)
				{
					int i = -rad;
					while(i <= rad)
					{
						int ii = -rad2 / 2;
						while(ii <= rad2 / 2)
						{
							int iii = -rad;
							while(iii <= rad)
							{
								Location nloc = this.lastLocation.clone().add((double) i, (double) ii, (double) iii);
								if(nloc.distance(this.lastLocation) <= (double) rad
										&& GunsPlugin.random.nextInt(10) == 1)
								{
									new Explosion(nloc).explode();
								}
								++iii;
							}
							++ii;
						}
						++i;
					}
					new Explosion(this.lastLocation).explode();
				}
				this.explode();
				this.fireSpread();
				this.flash();
			}
		}
	}

	private final void explode()
	{
		if(this.shotFrom.getExplodeRadius() > 0.0)
		{
			double damage;
			this.lastLocation.getWorld().createExplosion(this.lastLocation, 0.0f);
			if(this.shotFrom.isThrowable())
			{
				this.projectile.teleport(this.projectile.getLocation().add(0.0, 1.0, 0.0));
			}
			if((damage = (double) this.shotFrom.getExplosionDamage()) <= 0.0)
			{
				damage = this.shotFrom.getGunDamage();
			}
			if(damage > 0.0)
			{
				int rad = (int) this.shotFrom.getExplodeRadius();
				List<Entity> entities = this.projectile.getNearbyEntities((double) rad, (double) rad, (double) rad);
				for(Entity entity : entities)
				{
					if(entity.isValid() && entity instanceof LivingEntity && !((((LivingEntity) entity)).getHealth()
							> 0.0))
						continue;
				}
			}
		}
	}

	private final void fireSpread()
	{
		if(this.shotFrom.getFireRadius() > 0.0)
		{
			this.lastLocation.getWorld().playSound(this.lastLocation, Sound.ENTITY_GENERIC_SPLASH, 20.0f, 20.0f);
			int rad = (int) this.shotFrom.getFireRadius();
			List<Entity> entities = this.projectile.getNearbyEntities((double) rad, (double) rad, (double) rad);
			for(Entity entity : entities)
			{
				LivingEntity lentity;
				if(entity.isValid() && entity instanceof LivingEntity && !(
						((lentity = (LivingEntity) entity)).getHealth() <= 0.0) && !(lentity instanceof Player))
					continue;
			}
		}
	}

	private final void flash()
	{
		if(this.shotFrom.getFlashRadius() > 0.0)
		{
			this.lastLocation.getWorld().playSound(this.lastLocation, Sound.ENTITY_GENERIC_SPLASH, 20.0f, 20.0f);
			int c = (int) this.shotFrom.getFlashRadius();
			List<Entity> entities = this.projectile.getNearbyEntities((double) c, (double) c, (double) c);
			for(Entity entity : entities)
			{
				LivingEntity lentity;
				if(entity instanceof LivingEntity && !(((lentity = (LivingEntity) entity)).getHealth() <= 0.0)
						&& !(lentity instanceof Player))
					continue;
			}
		}
	}

	public void destroy()
	{
		this.projectile = null;
		this.velocity = null;
		this.shotFrom = null;
		this.shooter = null;
	}

	public Entity getProjectile()
	{
		return this.projectile;
	}

	public void setNextTickDestroy()
	{
		this.destroyNextTick = true;
	}
}
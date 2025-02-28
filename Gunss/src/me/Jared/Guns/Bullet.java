package me.Jared.Guns;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
		this.velocity = vec;
		if(gun.isThrowable())
		{
			ItemStack thrown = new ItemStack(gun.getGunType(), 1);
			this.projectile = owner.getPlayer().getWorld().dropItem(owner.getPlayer().getEyeLocation(), thrown);
			((Item) this.projectile).setPickupDelay(12000);
			this.startLocation = this.projectile.getLocation();
		} else
		{
			Location loc = owner.getPlayer().getEyeLocation();

			double offset = 0.3;
			double yOffset = -0.05;
			if(owner.getPlayer().getMainHand() == MainHand.LEFT)
			{
				offset = -0.2;
				yOffset = -0.2;
			}
			
//			float pitch = owner.getPlayer().getEyeLocation().getPitch();
//			
//			if(pitch <= -45)
//			{
//				loc.setPitch(90);
//			}
//			else if(pitch  >= 20)
//			{
//				loc.setPitch(-90);
//			} 
			
			loc.setPitch(0);
			switch(getDirection(loc))
			{
			case NORTH:
				loc.add(offset, yOffset, 0);
				break;
			case SOUTH:
				loc.add(-offset, yOffset, 0);
				break;
			case EAST:
				loc.add(0, yOffset, offset);
				loc.setYaw(90);
				break;
			case WEST:
				loc.add(0, yOffset, -offset);
				loc.setYaw(-90);
				break;
			case NORTHEAST:
				loc.add(offset, yOffset, offset);
				loc.setYaw(135);
				break;
			case NORTHWEST:
				loc.add(offset, yOffset, -offset);
				loc.setYaw(-135);
				break;
			case SOUTHEAST:
				loc.add(-offset, yOffset, offset);
				loc.setYaw(45);
				break;
			case SOUTHWEST:
				loc.add(-offset, yOffset, -offset);
				loc.setYaw(-45);
				break;
			}

			Class<Arrow> clazz = Arrow.class;
			
			this.projectile = owner.getPlayer().launchProjectile(clazz, this.velocity);
			this.projectile.teleport(loc);

			this.startLocation = loc;
		}
		this.releaseTime = this.shotFrom.getReleaseTime() == -1 ? 80 + (gun.isThrowable() ? 0 : 1) * 400
				: this.shotFrom.getReleaseTime();
	}

	enum Direction
	{
		NORTH, SOUTH, EAST, WEST, NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST;
	}

	public Direction getDirection(Location location)
	{
		if(location.getYaw() > -155 && location.getYaw() < -115)
		{
			return Direction.NORTHEAST;
		} else if(location.getYaw() > -65 && location.getYaw() < -25)
		{
			return Direction.SOUTHEAST;
		} else if(location.getYaw() > 115 && location.getYaw() < 155)
		{
			return Direction.NORTHWEST;
		} else if(location.getYaw() > 25 && location.getYaw() < 65)
		{
			return Direction.SOUTHWEST;
		} else if(location.getYaw() > -135 && location.getYaw() < -45)
		{
			return Direction.EAST;
		} else if(location.getYaw() > -45 && location.getYaw() < 45)
		{
			return Direction.SOUTH;
		} else if(location.getYaw() > 45 && location.getYaw() < 135)
		{
			return Direction.WEST;
		} else
		{
			return Direction.NORTH;
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
				if(this.ticks > this.releaseTime)
				{
					this.dead = true;
					return;
				}
				if(this.shotFrom.hasSmokeTrail())
				{
					this.lastLocation.getWorld().playEffect(this.lastLocation, Effect.SMOKE, 1);
				}
				if(this.shotFrom.getRay())
				{
					Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 3);
					this.lastLocation.getWorld().spawnParticle(Particle.ASH, this.lastLocation.getX(),
							this.lastLocation.getY(), this.lastLocation.getZ(), 1, 0.5, 0, 0.5, 0,
							dustOptions);
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
					this.projectile.setVelocity(this.velocity);
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
					if(entity.isValid() && entity instanceof LivingEntity
							&& !((((LivingEntity) entity)).getHealth() > 0.0))
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
				if(entity.isValid() && entity instanceof LivingEntity
						&& !(((lentity = (LivingEntity) entity)).getHealth() <= 0.0) && !(lentity instanceof Player))
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
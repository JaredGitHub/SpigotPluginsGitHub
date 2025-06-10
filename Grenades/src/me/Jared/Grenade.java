package me.Jared;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.ParticleRunnables.FireRunnable;
import me.Jared.ParticleRunnables.FlashBangRunnable;
import me.Jared.ParticleRunnables.GrenadeRunnable;
import me.Jared.ParticleRunnables.SmokeRunnable;
import me.Jared.ParticleRunnables.StickyRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Grenade
{
	private Material grenadeItem;
	private String displayName;
	private GrenadeType grenadeType;
	private float power;
	private long cooldownSeconds;
	private Location grenadeLocation;
	private Item explosive;
	private Player thrower;
	private Entity stickyVictim;

	public enum GrenadeType
	{
		MOLOTOV, GRENADE, FLASHBANG, SMOKE, STICKY
	}

	;

	public Grenade(Material item, String name, GrenadeType type, float power, long cooldownSeconds)
	{
		this.grenadeItem = item;
		this.displayName = ChatColor.translateAlternateColorCodes('&', name);
		this.grenadeType = type;
		this.power = power;
		this.cooldownSeconds = cooldownSeconds;
	}

	public Player getThrower()
	{
		return thrower;
	}

	public void remove()
	{
		explosive.remove();
	}

	public Location getLocation()
	{
		return this.grenadeLocation;
	}

	public void setLocation(Location location)
	{
		this.grenadeLocation = location;
	}

	public Material getItem()
	{
		return grenadeItem;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public Entity getStickyVictim()
	{
		return this.stickyVictim;
	}

	public void setStickyVictim(Entity entity)
	{
		this.stickyVictim = entity;
	}

	public GrenadeType getType()
	{
		return this.grenadeType;
	}

	private void blowUpGrenade(Entity entity)
	{
		GrenadeRunnable grenadeRunnable = new GrenadeRunnable(this, entity, 1, 3.5f);
		grenadeRunnable.runTaskTimer(GrenadesMain.getInstance(), 20, 1);
	}

	private void blowUpMolotov(Entity entity)
	{
		//if it hits the ground do this

		FireRunnable fireRunnable = new FireRunnable(this, entity, 2, 7 * 20);
		fireRunnable.runTaskTimer(GrenadesMain.getInstance(), 0, 1);

	}

	private void blowUpSmoke(Entity entity)
	{
		SmokeRunnable smokeRunnable = new SmokeRunnable(entity, 2, 7 * 20);
		smokeRunnable.runTaskTimer(GrenadesMain.getInstance(), 50l, 1);
	}

	private void blowUpFlashbang(Entity entity)
	{
		FlashBangRunnable flashBangRunnable = new FlashBangRunnable(this, entity, 1 * 20);
		flashBangRunnable.runTaskTimer(GrenadesMain.getInstance(), 50l, 1);
	}

	public void blowUpSticky(Entity entity)
	{
		var stickyRunnable = new StickyRunnable(this, entity);
		stickyRunnable.runTaskTimer(GrenadesMain.getInstance(), 0, 1);

		Bukkit.getScheduler().runTaskLater(GrenadesMain.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if(getStickyVictim() != null)
				{
					Location location = getStickyVictim().getLocation();
					location.getWorld().createExplosion(location, power, false, false);
					((Damageable) getStickyVictim()).damage(10);
				} else
				{
					Location location = getLocation();
					location.getWorld().createExplosion(location, power, false, false);
				}

				entity.remove();
				setLocation(null);
				setStickyVictim(null);
				stickyRunnable.cancel();
			}
		}, 50L);
	}

	boolean isInRegion(Player player, String stringRegion)
	{
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		World world = WorldGuardPlugin.inst().wrapPlayer(player).getWorld();
		RegionManager regions = container.get(world);

		for(ProtectedRegion r : regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation())))
		{
			if(r.getId().contains(stringRegion))
			{
				return true;
			}
		}
		return false;
	}

	public HashMap<UUID, Long> cooldown = new HashMap<>();

	public void throwGrenade(Player player)
	{
		this.thrower = player;

		if(isInRegion(player, "spawn") == false)
		{
			if(cooldown.containsKey(player.getUniqueId()))
			{
				long ticksleft =
						((Long) cooldown.get(player.getUniqueId())).longValue() / 50L + (this.cooldownSeconds * 20)
								- System.currentTimeMillis() / 50L;
				long milliseconds = ticksleft * 50;
				if(ticksleft > 0L)

				{

					double seconds = milliseconds / 1000.0;

					thrower.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + seconds + "s"));
					return;
				}
			}

			player.playSound(player, Sound.ENTITY_FISHING_BOBBER_THROW, 2, 1);

			Location playerLocation = thrower.getLocation().add(0, 2, 0);
			Vector vector = thrower.getEyeLocation().getDirection();

			var playerHand = thrower.getInventory().getItemInMainHand();
			playerHand.setAmount(playerHand.getAmount() - 1);

			ItemStack item = new ItemStack(grenadeItem);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(displayName);
			item.setItemMeta(meta);

			this.explosive = thrower.getLocation().getWorld().dropItem(playerLocation, item);
			explosive.setPickupDelay(999);
			explosive.setVelocity(vector.multiply(1.1));

			switch(grenadeType)
			{
			case GRENADE:
				blowUpGrenade(explosive);
				break;
			case MOLOTOV:
				blowUpMolotov(explosive);
				break;
			case FLASHBANG:
				blowUpFlashbang(explosive);
				break;
			case SMOKE:
				blowUpSmoke(explosive);
				break;
			case STICKY:
				blowUpSticky(explosive);
				break;
			default:
				break;
			}

			cooldown.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
		} else
		{
			thrower.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					new TextComponent(ChatColor.RED + "" + ChatColor.UNDERLINE + "" + "Not here noob!"));
		}
	}
}

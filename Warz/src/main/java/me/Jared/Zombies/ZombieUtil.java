package me.Jared.Zombies;

import java.util.Objects;
import java.util.Random;

import me.Jared.Loot.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.Warz;
import me.Jared.Loot.Zone;

public class ZombieUtil
{
	private FileConfiguration config = Warz.getInstance().getConfig();
	private Player player;
	private int radius;
	private int zombieAmount;
	private Zone zone;

	public ZombieUtil(Player player, int radius, int zombieAmount)
	{
		this.player = player;
		this.radius = radius;
		this.zombieAmount = zombieAmount;

		LootManager lootManager = new LootManager(player.getWorld().getName());
		this.zone = lootManager.getZoneFromRegion(lootManager.getRegion(player.getLocation()));
	}
	public void spawnZombie()
	{
		if(zone != null)
		{
			switch(zone)
			{
			case LOW:
				lowZombie();
				createCreeper(false, ChatColor.GRAY + "Tier 1 Creeper");
				break;
			case MEDIUM:
				mediumZombie();
				createCreeper(false, ChatColor.WHITE + "Tier 2 Creeper");
				break;
			case HIGH:
				highZombie();
				createCreeper(true, ChatColor.BLUE + "Tier 3 Creeper");
				break;
			case SKYHIGH:
				skyhighZombie();
				createCreeper(true, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tier 4 Creeper");
				break;
			default:
				break;
			}
		}
	}

	private Location spawnRadius(Location playerLocation, int radius)
	{
		Random rand = new Random();
		Location newloc;
		do
		{

			double angle = rand.nextDouble() * 360; // Generate a random angle
			double x = playerLocation.getX() + (rand.nextDouble() * radius * Math.cos(Math.toRadians(angle)));
			double z = playerLocation.getZ() + (rand.nextDouble() * radius * Math.sin(Math.toRadians(angle)));

			newloc = new Location(playerLocation.getWorld(), x, 0, z);
			double y = playerLocation.getWorld().getHighestBlockYAt(newloc);
			newloc = new Location(playerLocation.getWorld(), x, y + 1, z);
			newloc.setYaw(playerLocation.getYaw());
			newloc.setPitch(playerLocation.getPitch());
		} while(newloc.distance(playerLocation) < 25);

		return newloc;
	}

	private void createZombie(double health, int speed, Material helmet, int damage, String customName)
	{
		for(int i = 0; i < zombieAmount; i++)
		{
			Entity zombie = player.getWorld().spawnEntity(spawnRadius(player.getLocation(), radius), EntityType.ZOMBIE);

			// Give zombie helmet so that they don't burn
			Zombie zomb = (Zombie) zombie;
			zomb.setAdult();

			zomb.getEquipment().setHelmet(new ItemStack(helmet));

			zombie.setCustomName(customName);
			zombie.setCustomNameVisible(true);

			AttributeInstance attackAttribute = ((Attributable) zombie).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
			AttributeInstance healthAttribute = ((Attributable) zombie).getAttribute(Attribute.GENERIC_MAX_HEALTH);

			// Set speed of zombie
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 30000, speed);
			potionEffect.apply(zomb);

			if(attackAttribute != null)
			{
				attackAttribute.setBaseValue(damage);
			}
			if(healthAttribute != null)
			{
				healthAttribute.setBaseValue(health);
			}
		}
	}

	private void createCreeper(boolean isCharged, String customName)
	{
		Creeper creeper = (Creeper) player.getWorld()
				.spawnEntity(spawnRadius(player.getLocation(), radius), EntityType.CREEPER);
		creeper.setPowered(isCharged);

		creeper.setCustomName(customName);
		creeper.setCustomNameVisible(true);
	}

	private void lowZombie()
	{
		createZombie(6, 1, Material.LEATHER_HELMET, 3, ChatColor.GRAY + "Tier 1 Zombie");
	}

	private void mediumZombie()
	{
		createZombie(8, 2, Material.IRON_HELMET, 6, ChatColor.WHITE + "Tier 2 Zombie");
	}

	private void highZombie()
	{
		createZombie(8, 2, Material.DIAMOND_HELMET, 8, ChatColor.BLUE + "Tier 3 Zombie");
	}

	private void skyhighZombie()
	{
		createZombie(10, 2, Material.NETHERITE_HELMET, 10,
				ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Tier 4 Zombie");
	}
}

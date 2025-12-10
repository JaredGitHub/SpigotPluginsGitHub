package me.Jared;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AirDrop
{
	private Plugin plugin;

	private FileConfiguration config;
	private int airdropTask;

	public AirDrop(Plugin plugin)
	{
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}

	public void drop()
	{

		removeAirdrop(airdropTask);
		Location randomLocation = getRandomLocation();
		airdropTask = createFakeBeaconBeam(randomLocation, 100);
		for(Player player : Bukkit.getOnlinePlayers())
		{

			if(player.getWorld().equals(Bukkit.getWorld("warz")))
			{
				player.sendMessage(
						ChatColor.GREEN + "There is an air drop at X: " + ChatColor.GRAY + (int) randomLocation.getX()
								+ ChatColor.GREEN + " Y: " + ChatColor.GRAY + (int) randomLocation.getY()
								+ ChatColor.GREEN + " Z: " + ChatColor.GRAY + (int) randomLocation.getZ()
								+ ChatColor.GREEN + " you only have 10 minutes!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			}
		}

		Block block = randomLocation.getBlock();
		block.setType(Material.CHEST);
		LootManager lootManager = new LootManager();
		lootManager.setChest(block);

	}

	private void removeAirdrop(int id)
	{
		Bukkit.getScheduler().cancelTask(id);
		if(config.contains("airdrops"))
		{
			ConfigurationSection airdropsSection = config.getConfigurationSection("airdrops");
			for(String key : airdropsSection.getKeys(false))
			{
				double x = airdropsSection.getDouble(key + ".x");
				double y = airdropsSection.getDouble(key + ".y");
				double z = airdropsSection.getDouble(key + ".z");

				World world = Bukkit.getWorld("warz"); // Replace with your world name or reference
				if(world != null)
				{
					Location airdropLocation = new Location(world, x, y, z);
					airdropLocation.getBlock().setType(Material.AIR);
				}
			}
		}
	}

	private int createFakeBeaconBeam(Location loc, int height)
	{
		// Align to the center of the block
		Location centerLoc = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getY(), loc.getBlockZ() + 0.5);


		return new BukkitRunnable()
		{
			int ticksElapsed = 0; // Tracks elapsed time

			@Override
			public void run()
			{

				// Spawn particles to form a constant vertical pillar
				for(int i = 0; i <= height; i++)
				{
					Location particleLoc = centerLoc.clone().add(0, i + 2.5, 0); // Only adjust vertically

					loc.getWorld().spawnParticle(Particle.SONIC_BOOM, particleLoc, 50, 0, 0, 0,0,null,true);
					if(i % 2 == 0)
					{
						loc.getWorld().spawnParticle(Particle.SONIC_BOOM, particleLoc, 1, 0, 0, 0,0,null,true);
					}
				}

				if(ticksElapsed % 4 == 0)
				{
					loc.getWorld().playSound(centerLoc, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
				}

				ticksElapsed += 2;
			}
		}.runTaskTimer(plugin, 0L, 2L).getTaskId();
	}

	private Location getRandomLocation()
	{
		int randomGameSlot = getRandomGameSlot();
		setRandomLocationTemporary(randomGameSlot);
		return getGameSlotLocation(randomGameSlot);
	}

	private void setRandomLocationTemporary(int airdrop)
	{
		config.set("airdrop", airdrop);
		plugin.saveConfig();
	}

	public int getRandomLocationTemporary()
	{
		return config.getInt("airdrop");
	}

	public Location getGameSlotLocation(int i)
	{
		double x = config.getDouble("airdrops." + i + ".x");
		double y = config.getDouble("airdrops." + i + ".y");
		double z = config.getDouble("airdrops." + i + ".z");

		return new Location(Bukkit.getWorld("warz"), x, y, z);
	}

	public void setGameSlot(Location loc)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		int i = getGameSlotsSize();

		config.set("airdrops." + i + ".x", x);
		config.set("airdrops." + i + ".y", y);
		config.set("airdrops." + i + ".z", z);

		plugin.saveConfig();
	}

	private int getGameSlotsSize()
	{
		int amount = 1;
		if(config.contains("airdrops"))
		{
			for(String string : config.getConfigurationSection("airdrops").getKeys(false))
				amount++;
		}
		return amount;
	}

	private int getRandomGameSlot()
	{
		Random random = new Random();
		return random.nextInt(1, getGameSlotsSize());
	}
}

package me.Jared.Loot;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.Warz;

public class ConfigManager
{
	private static FileConfiguration config = Warz.getInstance().getConfig();
	private static Warz plugin = Warz.getInstance();

	public static Location getLobbySpawn()
	{
		return new Location(Bukkit.getWorld(config.getString("lobby-spawn.world")), config.getDouble("lobby-spawn.x"),
				config.getDouble("lobby-spawn.y"), config.getDouble("lobby-spawn.z"),
				(float) config.getDouble("lobby-spawn.yaw"), (float) config.getDouble("lobby-spawn.pitch"));
	}

	public static void setLobbySpawn(Location location)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		config.set("lobby-spawn.world", location.getWorld().getName());
		config.set("lobby-spawn.x", x);
		config.set("lobby-spawn.y", y);
		config.set("lobby-spawn.z", z);
		config.set("lobby-spawn.yaw", location.getYaw());
		config.set("lobby-spawn.pitch", location.getPitch());
		plugin.saveConfig();

	}

	public static int getGameSlotsSize()
	{
		ArrayList<String> totalSpawnPoints = new ArrayList<String>();

		if(config.contains("warz"))
		{
			for(String string : config.getConfigurationSection("warz").getKeys(false))
			{
				totalSpawnPoints.add(string);
			}
			return totalSpawnPoints.size();
		} else
		{
			return 1;
		}
	}

	public static Location getGameSlotLocation(int i)
	{

		String world = config.getString("warz.world");
		double x = config.getDouble("warz." + i + ".x");
		double y = config.getDouble("warz." + i + ".y");
		double z = config.getDouble("warz." + i + ".z");
		float yaw = (float) config.getDouble("warz." + i + ".yaw");
		float pitch = (float) config.getDouble("warz." + i + ".pitch");

		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public static void setGameSlot(Location loc, int i)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("warz.world", loc.getWorld().getName());
		config.set("warz." + i + ".x", x);
		config.set("warz." + i + ".y", y);
		config.set("warz." + i + ".z", z);
		config.set("warz." + i + ".yaw", loc.getYaw());
		config.set("warz." + i + ".pitch", loc.getPitch());

		plugin.saveConfig();
	}

	public static void savePlayerWarzData(Player player, String world)
	{
		Location loc = player.getLocation();

		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		String uuid = player.getUniqueId().toString();

		config.set(uuid + ".world", world);
		config.set(uuid + ".x", x);
		config.set(uuid + ".y", y);
		config.set(uuid + ".z", z);
		config.set(uuid + ".yaw", loc.getYaw());
		config.set(uuid + ".pitch", loc.getPitch());
		config.set(uuid + ".health", player.getHealth());
		saveInventory(player, world);

		plugin.saveConfig();
	}

	private static boolean isInRegion(Player player, String stringRegion)
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

	public static void setPlayerInWarz(Player player)
	{
		if(isInRegion(player, "spawn"))
		{
			if(config.get(player.getUniqueId().toString() + ".world") == null)
			{
				Random rand = new Random();
				Location randomLocation = ConfigManager
						.getGameSlotLocation(rand.nextInt(1, ConfigManager.getGameSlotsSize()));
				player.teleport(randomLocation);
				player.getInventory().clear();
			}
			else
			{
				String uuid = player.getUniqueId().toString();

				String world = config.getString(uuid + ".world");
				double x = config.getDouble(uuid + ".x");
				double y = config.getDouble(uuid + ".y");
				double z = config.getDouble(uuid + ".z");
				float yaw = (float) config.getDouble(uuid + ".yaw");
				float pitch = (float) config.getDouble(uuid + ".pitch");
				double health = config.getDouble(uuid + ".health");

				Location newLoc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
				player.teleport(newLoc);
				player.setHealth(health);

				loadInventory(player, "warz");
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Please do this at spawn!");
		}

	}

	public static void saveInventory(Player player, String world)
	{
		config.set(player.getUniqueId() + ".inventory" + world,null);

		ConfigItem configItem = new ConfigItem();

		int i = 0;
		for(ItemStack item : player.getInventory().getContents())
		{
			if(item != null)
			{
				config.set(player.getUniqueId() + ".inventory" + world + "." + i, configItem.itemStackToString(item));
			}
			i++;
		}

		plugin.saveConfig();
	}

	public static void loadInventory(Player player, String world)
	{
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}

		player.getInventory().clear();
		Inventory inventory = player.getInventory();

		ConfigItem configItem = new ConfigItem();

		String itemString;
		for(int i = 0; i < player.getInventory().getSize(); i++)
		{
			itemString = player.getUniqueId() + ".inventory" + world + "." + i;
			if(config.getString(itemString) != null)
			{
				inventory.setItem(i, configItem.stringToItemStackWithLore(config.getString(itemString)));
			}
		}
	}

}

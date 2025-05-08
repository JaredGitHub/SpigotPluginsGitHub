package me.Jared.Loot;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.Jared.Commands.CustomMapRenderer;
import me.Jared.SQL.PlayerData;
import me.Jared.SQL.WarzDataAccessObject;
import me.Jared.Warz;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
		config.set("lobby-spawn.x", Double.valueOf(x));
		config.set("lobby-spawn.y", Double.valueOf(y));
		config.set("lobby-spawn.z", Double.valueOf(z));
		config.set("lobby-spawn.yaw", Float.valueOf(location.getYaw()));
		config.set("lobby-spawn.pitch", Float.valueOf(location.getPitch()));
		plugin.saveConfig();
	}

	public static int getGameSlotsSize()
	{
		ArrayList<String> totalSpawnPoints = new ArrayList<>();
		if(config.contains("warz"))
		{
			for(String string : config.getConfigurationSection("warz").getKeys(false))
				totalSpawnPoints.add(string);
			return totalSpawnPoints.size();
		}
		return 1;
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
		config.set("warz." + i + ".x", Double.valueOf(x));
		config.set("warz." + i + ".y", Double.valueOf(y));
		config.set("warz." + i + ".z", Double.valueOf(z));
		config.set("warz." + i + ".yaw", Float.valueOf(loc.getYaw()));
		config.set("warz." + i + ".pitch", Float.valueOf(loc.getPitch()));
		plugin.saveConfig();
	}

	public static boolean isInRegion(Player player, String stringRegion)
	{
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		World world = WorldGuardPlugin.inst().wrapPlayer(player).getWorld();
		RegionManager regions = container.get(world);
		for(ProtectedRegion r : regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation())))
		{
			if(r.getId().contains(stringRegion))
				return true;
		}
		return false;
	}

	public static void setPlayerInWarz(Player player)
	{
		if(isInRegion(player, "spawn"))
		{
			player.teleport(new Location(Bukkit.getWorld("world"), 1961, 108, 1403));
			player.sendTitle(ChatColor.GREEN + "Teleporting to warz", "", 5, 5, 5);
			String uuid = player.getUniqueId().toString();
			Bukkit.getScheduler().runTaskAsynchronously(Warz.getInstance(), () ->
			{
				// Fetch player data asynchronously
				WarzDataAccessObject.getPlayerByUUID(uuid, playerData ->
				{
					player.resetTitle();
					// If there is player data
					if(playerData != null)
					{
						// Get all the player's location data and health for DB
						double x = playerData.getX();
						double y = playerData.getY();
						double z = playerData.getZ();
						float yaw = playerData.getYaw();
						float pitch = playerData.getPitch();
						double health = playerData.getHealth();
						Location newLoc = new Location(Bukkit.getWorld("warz"), x, y, z, yaw, pitch);

						//Teleport the player to the location and set their health to that value from the DB
						player.teleport(newLoc);
						player.setHealth(health);
						applyInventory(player, playerData.getInventory());

					} else
					{
						Random rand = new Random();
						Location randomLocation = getGameSlotLocation(rand.nextInt(1, getGameSlotsSize()));
						player.teleport(randomLocation);
						giveFreshStartItems(player);
					}
				});
			});
		} else
		{
			player.sendMessage(ChatColor.RED + "Please do this at spawn!");
		}
	}

	public static void loadInventory(Player player, String world)
	{
		if(world.equals("warz"))
		{
			// Fetch the inventory asynchronously
			WarzDataAccessObject.getPlayerByUUID(player.getUniqueId().toString(), playerData ->
			{
				if(playerData != null)
				{
					String inventoryString = playerData.getInventory();
					Bukkit.getScheduler().runTask(Warz.getInstance(), () -> applyInventory(player, inventoryString));
				} else
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player data not found.");
				}
			});
		} else
		{

			// This method is synchronous, so we can call it directly
			String inventoryString = WarzDataAccessObject.getPlayerWorldInventoryBase64(
					player.getUniqueId().toString());
			applyInventory(player, inventoryString);
		}
	}

	public static void clearPlayerData(String uuid)
	{
		WarzDataAccessObject.clearPlayerData(uuid);
	}

	public static void giveFreshStartItems(Player p)
	{
		Color color = null;
		ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
		ItemStack food = new ItemStack(Material.LAPIS_LAZULI, 4);

		if(p.hasPermission("ranks.default"))
		{
			color = null;
		}

		if(p.hasPermission("ranks.vip"))
		{
			color = Color.LIME;
			sword = new ItemStack(Material.STONE_SWORD);
			food = new ItemStack(Material.LAPIS_LAZULI, 8);
		}
		if(p.hasPermission("ranks.vipplus"))
		{
			color = Color.GREEN;
			sword = new ItemStack(Material.STONE_SWORD);
			food = new ItemStack(Material.YELLOW_DYE, 4);
		}
		if(p.hasPermission("ranks.mvp"))
		{
			color = Color.TEAL;
			sword = new ItemStack(Material.IRON_SWORD);
			food = new ItemStack(Material.YELLOW_DYE, 8);
		}
		if(p.hasPermission("ranks.mvpplus"))
		{
			color = Color.BLUE;
			sword = new ItemStack(Material.IRON_SWORD);
			food = new ItemStack(Material.PINK_DYE, 4);
		}

		ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta) chestPlate.getItemMeta();
		meta.setColor(color);
		chestPlate.setItemMeta(meta);
		p.getInventory().setChestplate(chestPlate);

		p.getInventory().setItem(0, sword);
		p.getInventory().setItem(1, food);
		p.getInventory().setItem(2, new ItemStack(Material.BONE, 1));
		p.getInventory().setItem(3, new ItemStack(Material.PAPER, 1));
		p.getInventory().setItem(4, new ItemStack(Material.COMPASS));
		p.getInventory().setItem(5, getMap(p));
	}

	public static ItemStack getMap(Player player)
	{
		ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
		ItemMeta itemMeta = mapItem.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + player.getDisplayName() + "'s Map");
		mapItem.setItemMeta(itemMeta);

		MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

		MapView mapView = Bukkit.createMap(player.getWorld());
		mapView.setScale(MapView.Scale.FARTHEST);
		mapView.setTrackingPosition(true);
		mapView.setCenterX(0);
		mapView.setCenterZ(0);

		CustomMapRenderer mapRenderer = new CustomMapRenderer();

		mapView.getRenderers().clear();
		mapView.addRenderer(mapRenderer);

		mapMeta.setMapView(mapView);
		mapItem.setItemMeta(mapMeta);

		return mapItem;
	}

	// Helper method to apply the inventory to the player
	private static void applyInventory(Player player, String inventoryString)
	{
		try
		{
			PlayerInventory inventory = player.getInventory();
			ItemStack[] items = InventorySaver.fromBase64(inventoryString);

			// Clear the inventory
			inventory.clear();

			for(int i = 0; i < 36; i++)
			{
				inventory.setItem(i, items[i]);
			}

			// Set armor
			ItemStack[] armor = new ItemStack[] { items[36], items[37], items[38], items[39] };
			inventory.setArmorContents(armor);

			// Set off-hand
			inventory.setItemInOffHand(items[40]);
		} catch(IOException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't get the items from the array!");
		}
	}

	public static void savePlayerWarzData(Player player, Location location, Inventory inventory)
	{
		if(WarzDataAccessObject.savePlayerWarzData(player, location, inventory) == 0)
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.RED + "IT DIDN'T SEND ANYTHING TO THE SQL SERVER DANGIT!!!!");
	}
}

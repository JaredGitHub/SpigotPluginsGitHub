package me.Jared.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.Jared.Warz;
import me.Jared.Commands.CustomMapRenderer;
import me.Jared.Loot.ConfigItem;
import me.Jared.Loot.ConfigManager;
import me.Jared.Loot.LootManager;
import me.Jared.Loot.Zone;
import me.Jared.SQL.WarzDataAccessObject;

public class WarzListener implements Listener
{
	private Map<UUID, Location> deathLocation;
	ArrayList<UUID> deadPlayers;
	private Location randomLocation;

	public WarzListener()
	{
		deadPlayers = new ArrayList<>();
		deathLocation = new HashMap<>();
	}

	@EventHandler
	public void onChestOpen(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();

		if(player.getWorld().getName().equals("warz"))
		{
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.CHEST)
			{
				LootManager lootManager = new LootManager();
				String region = lootManager.getRegion(player.getLocation());
				Zone zone = lootManager.getZoneFromRegion(region);
				Block block = e.getClickedBlock();

				lootManager.setItems(zone, block);
				if(!Warz.getOpenChestLocations().contains(block.getLocation()))
				{
					Warz.getOpenChestLocations().add(block.getLocation());
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		if(player.getWorld().getName().equals("warz"))
		{
			if(player.getGameMode() == GameMode.SURVIVAL)
			{
				WarzDataAccessObject dao = new WarzDataAccessObject();
				if(dao.savePlayerWarzData(player) == 0)
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "IT DIDN'T SEND ANYTHING TO THE SQL SERVER DANGIT!!!!");
				}
			}
		} else if(player.getWorld().getName().equals("world"))
		{
			ConfigManager.saveInventory(player, "world");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();

		if(!deadPlayers.contains(player.getUniqueId()))
		{
			ConfigManager.loadInventory(player, "world");
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		e.setDroppedExp(0);

		Player p = e.getEntity();
		deadPlayers.add(p.getUniqueId());

		if(p.getWorld().getName().equals("world"))
		{
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".inventoryworld", null);
			Warz.getInstance().saveConfig();
		} else if(p.getWorld().getName().equals("warz"))
		{
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".inventorywarz", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".world", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".x", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".y", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".z", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".yaw", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".pitch", null);
			Warz.getInstance().getConfig().set(p.getUniqueId() + ".health", null);

			Warz.getInstance().saveConfig();
		}
	}

	private void givePlayerItems(Player p)
	{
		Color color = null;
		ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
		ItemStack food = new ItemStack(Material.LAPIS_LAZULI, 4);

		if(p.hasPermission("ranks.default"))
		{
			color = null;
		} else if(p.hasPermission("ranks.vip"))
		{
			color = Color.LIME;
			sword = new ItemStack(Material.STONE_SWORD);
			food = new ItemStack(Material.LAPIS_LAZULI, 8);
		} else if(p.hasPermission("ranks.vipplus"))
		{
			color = Color.GREEN;
			sword = new ItemStack(Material.STONE_SWORD);
			food = new ItemStack(Material.YELLOW_DYE, 4);
		} else if(p.hasPermission("ranks.mvp"))
		{
			color = Color.TEAL;
			sword = new ItemStack(Material.IRON_SWORD);
			food = new ItemStack(Material.YELLOW_DYE, 8);
		} else if(p.hasPermission("ranks.mvpplus"))
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

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();

		if(p.getWorld().equals(Bukkit.getWorld("warz")))
		{
			Random rand = new Random();
			randomLocation = ConfigManager.getGameSlotLocation(rand.nextInt(1, ConfigManager.getGameSlotsSize()));
			deathLocation.put(p.getUniqueId(), p.getEyeLocation());

			if(deathLocation.get(p.getUniqueId()) != null)
			{
				p.setGameMode(GameMode.SPECTATOR);
				e.setRespawnLocation(deathLocation.get(p.getUniqueId()));
				Bukkit.getScheduler().runTaskLater(Warz.getInstance(), () ->
				{
					if(p.isOnline())
					{
						p.setGameMode(GameMode.SURVIVAL);

						givePlayerItems(p);

						p.teleport(randomLocation);
						deathLocation.remove(p.getUniqueId());
						deadPlayers.remove(p.getUniqueId());
					}
				}, 100L);
			}
		}
	}

	@EventHandler
	public void onCommandExecute(PlayerCommandPreprocessEvent e)
	{
		Player player = e.getPlayer();

		if(player.getWorld().equals(Bukkit.getWorld("warz")))
		{
			if(!player.hasPermission("jared"))
			{
				switch(e.getMessage())
				{
				case "/msg":
				case "/tpa":
				case "/spawn":
				case "/r":
					break;
				default:
					e.setCancelled(true);
					break;
				}
			}
		}
	}

	private ItemStack getItem(Zone zone)
	{
		var configItem = new ConfigItem();
		ArrayList<String> items = new ArrayList<>(configItem.zoneListItems(zone));

		Random rand = new Random();
		int randomIndex = rand.nextInt(0, items.size());
		String randomElement = items.get(randomIndex);

		ItemStack item = configItem.stringToItemStack(randomElement);
		return item;
	}

	@EventHandler
	public void onZombieKill(EntityDeathEvent e)
	{
		if(e.getEntity() instanceof Zombie || e.getEntity() instanceof Drowned)
		{
			Zombie zombie = (Zombie) e.getEntity();
			e.getDrops().clear();

			switch(zombie.getEquipment().getHelmet().getType())
			{
			case LEATHER_HELMET:
				e.getDrops().add(getItem(Zone.LOW));
				break;
			case IRON_HELMET:
				e.getDrops().add(getItem(Zone.MEDIUM));
				break;
			case DIAMOND_HELMET:
				e.getDrops().add(getItem(Zone.HIGH));
				break;
			case NETHERITE_HELMET:
				e.getDrops().add(getItem(Zone.SKYHIGH));
				break;
			default:
				break;
			}
		}
	}

	@EventHandler
	public void onZombieStrike(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, 20 * 60, 1);
			PotionEffect potionEffect1 = new PotionEffect(PotionEffectType.HUNGER, 20 * 60, 3);

			if(player.getWorld().getName().equals("warz"))
			{
				if(e.getDamager() instanceof Zombie)
				{
					Random random = new Random();
					int randomNumber = random.nextInt(5);
					if(randomNumber == 4)
					{
						potionEffect.apply(player);
					} else if(randomNumber == 3)
					{
						potionEffect1.apply(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();

		ItemStack mainHand = player.getInventory().getItemInMainHand();

		if(mainHand.getType() == Material.MAP || mainHand.getType() == Material.FILLED_MAP)
		{
			MapMeta mapMeta = (MapMeta) mainHand.getItemMeta();
			if(!mapMeta.hasMapView())
			{
				player.getInventory().setItemInMainHand(getMap(player));
			}
		}
	}

	public ItemStack getMap(Player player)
	{
		ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);
		ItemMeta itemMeta = mapItem.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GOLD + player.getDisplayName() + "'s Map");
		mapItem.setItemMeta(itemMeta);

		MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

		MapView mapView = Bukkit.createMap(player.getWorld());
		mapView.setScale(Scale.FARTHEST);
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

}

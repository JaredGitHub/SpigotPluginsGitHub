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
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
	public static ArrayList<UUID> deadPlayers;

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
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.CHEST
					&& Warz.getInstance().getChestLocations().contains(e.getClickedBlock().getLocation()))
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

	private final ArrayList<String> playerSpectating = new ArrayList<>();

	private void addPlayerSpectating(Player player)
	{
		if(player.getGameMode() == GameMode.SPECTATOR)
		{
			player.sendMessage(ChatColor.RED + "You are in spectator now i see");
			playerSpectating.add(player.getName());
		} else
		{
			playerSpectating.remove(player.getName());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		if(player.getWorld().getName().equals("warz"))
		{
			//If player is in spectator
			if(playerSpectating.contains(player.getName()))
			{
				//Clear the dat aof the player so that they spawn with new items based on their rank
				ConfigManager.clearPlayerData(player.getUniqueId().toString());
			} else
			{
				//Remove them from list of players spectating if they are not in spectator mode anymore
				playerSpectating.remove(player.getName());
				//If the player is in any other gamemode besides spectator
				// save them to the database for their location and inventory
				ConfigManager.savePlayerWarzData(player, player.getLocation(), player.getInventory());
			}
		} else if(player.getWorld().getName().equals("world"))
		{
			WarzDataAccessObject.savePlayerWorldData(player);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();

		//Clearing inventory and setting level to 0 then loading the "world" inventory and telepoting them to spawn
		player.getInventory().clear();
		player.setLevel(0);
		ConfigManager.loadInventory(player, "world");
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		e.setDroppedExp(0);

		Player p = e.getEntity();
		deadPlayers.add(p.getUniqueId());
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();

		if(p.getWorld().equals(Bukkit.getWorld("warz")))
		{
			Random rand = new Random();
			Location randomLocation = ConfigManager.getGameSlotLocation(
					rand.nextInt(1, ConfigManager.getGameSlotsSize()));
			deathLocation.put(p.getUniqueId(), p.getEyeLocation());

			if(deathLocation.get(p.getUniqueId()) != null)
			{
				p.setGameMode(GameMode.SPECTATOR);
				addPlayerSpectating(p);
				e.setRespawnLocation(deathLocation.get(p.getUniqueId()));
				Bukkit.getScheduler().runTaskLater(Warz.getInstance(), () ->
				{
					//Set them to survival and give them items for starting
					p.setGameMode(GameMode.SURVIVAL);
					ConfigManager.giveFreshStartItems(p);
					p.teleport(randomLocation);
					deathLocation.remove(p.getUniqueId());
					deadPlayers.remove(p.getUniqueId());
					playerSpectating.remove(p.getName());

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
			if(e.getMessage().equalsIgnoreCase("/spawn"))
			{
				if(player.getWorld().getName().equals("warz"))
				{
					player.sendTitle(ChatColor.YELLOW + "Teleporting to spawn...", "", 5, 5, 5);
					ConfigManager.savePlayerWarzData(player,player.getLocation(), player.getInventory());
					player.getInventory().clear();
					ConfigManager.loadInventory(player, "world");
				}
			}

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
		//If the entity is a creeper
		if(e.getEntity() instanceof Creeper)
		{
			e.getDrops().clear();
			e.getDrops().add(getItem(Zone.SKYHIGH));
		}

		//If the entity is a zombie or a drowned
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
			PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, 20 * 30, 1);
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
				player.getInventory().setItemInMainHand(ConfigManager.getMap(player));
			}
		}
	}
}

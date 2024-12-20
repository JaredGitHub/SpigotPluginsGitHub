package me.Jared.Listeners;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.GameState;
import me.Jared.Kits.Main;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import me.Jared.Managers.KitManager;

public class DeathmatchListener implements Listener
{

	private GameManager gameManager;

	public DeathmatchListener(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		if(gameManager.getGameState() != GameState.WAITING)
		{
			if(gameManager.getPlayerManager().getPlayers().contains(player))
			{
				ConfigManager.setKills(ConfigManager.getTeam(player), 0);
				gameManager.setGameState(GameState.WAITING);
			}
		}
		gameManager.getPlayerManager().removePlayer(player);
	}

	private HashMap<UUID, Long> cooldown = new HashMap<>();
	private int cooldowntime = 3*1000;

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		//If player is in cooldown STOP!!!
		if(cooldown.containsKey(player.getUniqueId()))
		{
			long timeleft = ((cooldown.get(player.getUniqueId())) + cooldowntime) - (System.currentTimeMillis());

			timeleft /= 1000.0;
			if(timeleft > 0)
			{
				if(gameManager.getGameState() == GameState.LIVE)
				{
					e.getDrops().clear();
					e.setDroppedExp(0);
				}
				return;
			}
		}

		if(gameManager.getGameState() == GameState.LIVE)
		{
			e.getDrops().clear();
			e.setDroppedExp(0);

			if(player.getKiller() != null)
			{

				//Do team deathmatch tings
				//Set config things
				int killerTeam = ConfigManager.getTeam(player.getKiller());
				int teamKills = ConfigManager.getKills(killerTeam);
				ConfigManager.setKills(killerTeam, teamKills + 1);
			}
		}
		cooldown.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		if(gameManager.getPlayerManager().getPlayers().contains(player) && gameManager.getGameState() == GameState.COUNTDOWN)
		{
			Location newToLocation = e.getFrom().setDirection(e.getTo().getDirection());
			e.setTo(newToLocation);
		}

		if(!(gameManager.getPlayerManager().getPlayers().contains(player)) && gameManager.getGameState() == GameState.LIVE)
		{
			if(!player.hasPermission("tdm") && player.getGameMode() == GameMode.SPECTATOR)
			{
				Location pLoc = player.getLocation();
				Location spawnLoc = ConfigManager.getLobbySpawn();

				if(spawnLoc.distance(pLoc) >= 100)
				{
					player.teleport(spawnLoc);
				}
			}
		}
	}

	@EventHandler
	public void onCommandExecute(PlayerCommandPreprocessEvent e)
	{
		Player player = e.getPlayer();

		if(gameManager.getPlayerManager().getPlayers().contains(player))
		{
			if((e.getMessage().equalsIgnoreCase("/tdm leave")))
			{
				return;
			}
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot do commands right now! Type /tdm leave to leave");

		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		if(gameManager.getGameState() == GameState.LIVE)
		{
			Player player = e.getPlayer();

			if (Main.getInstance().getConfig().get("PlayerUniqueID." + player.getUniqueId()) != null)
			{
				KitManager.playerCustomHotBar(player);
				KitManager.diamondKit(player);
				KitManager.giveAmmo(player);
			} else
			{
				KitManager.defaultHotBar(player.getUniqueId());
				KitManager.diamondKit(player);
				for (int i = 0; i < 9; i++)
				{
					player.getInventory().addItem(new ItemStack[]
							{ KitManager.sniperAmmo(128) });
				}
				player.getInventory().addItem(new ItemStack[]
						{ KitManager.shotgunAmmo(128) });
				player.getInventory().addItem(new ItemStack[]
						{ KitManager.autoAmmo(128) });
			}


			//Respawn player to their respective side.
			Location location = ConfigManager.getGameSlotLocation(ConfigManager.getTeam(player));
			e.setRespawnLocation(spawnRadius(location,2));
		}
	}

	private Location spawnRadius(Location loc, int radius)
	{
		Location center = loc;
		Random rand = new Random();
		double angle = rand.nextDouble()*360; //Generate a random angle
		double x = center.getX() + (rand.nextDouble()*radius*Math.cos(Math.toRadians(angle))); // x
		double z = center.getZ() + (rand.nextDouble()*radius*Math.sin(Math.toRadians(angle))); // z
		Location newloc = new Location(Bukkit.getWorld("world"), x, center.getY(), z);
		newloc.setYaw(center.getYaw());
		newloc.setPitch(center.getPitch());
		return newloc;
	}



	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e)
	{
		if(gameManager.getGameState() == GameState.LIVE)
		{
			if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
			{
				Player p = (Player) e.getEntity();
				Player pdamager = (Player) e.getDamager();


				int damagedTeam = ConfigManager.getTeam(p);
				int damagerTeam = ConfigManager.getTeam(pdamager);


				if(damagedTeam == damagerTeam)
				{

					pdamager.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");
					e.setCancelled(true);
				}
			}

			if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player)
			{
				Projectile projectile = (Projectile) e.getDamager();
				Player p = (Player) e.getEntity();
				Player pdamager = (Player) projectile.getShooter();

				int damagedTeam = ConfigManager.getTeam(p);
				int damagerTeam = ConfigManager.getTeam(pdamager);

				if(damagedTeam == damagerTeam)
				{
					pdamager.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");
					e.setDamage(0);
					e.setCancelled(true);
				}
			}
		}
	}
}
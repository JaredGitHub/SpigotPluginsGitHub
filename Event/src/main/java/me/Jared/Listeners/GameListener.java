package me.Jared.Listeners;

import me.Jared.Commands.EventCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import me.Jared.Event;
import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;

public class GameListener implements Listener
{
	GameManager gameManager;

	public GameListener(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if(gameManager.getGameState() != GameState.INACTIVE)
		{
			Bukkit.getScheduler().runTaskLater(gameManager.getPlugin(), new Runnable()
			{

				@Override
				public void run()
				{
					e.getPlayer().teleport(ConfigManager.getLobbySpawn());
					gameManager.getPlayerManager().setPlayerInGame(e.getPlayer());
					e.getPlayer().sendTitle(ChatColor.DARK_GREEN + "EVENT TIME", "", 20, 20, 20);

				}
			}, 20);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		if(gameManager.getGameState() == GameState.LIVE)
		{
			if(gameManager.getPlayerManager().getPlayers().contains(player))
			{
				gameManager.getPlayerManager().removePlayer(player);
				String teamName = ConfigManager.getTeam(player);

				player.teleport(ConfigManager.getLobbySpawn());

				if(ConfigManager.playerInTeam(player))
				{
					ConfigManager.removeTeam(player);
				}

				if(ConfigManager.getTeams().size() <= 1)
				{
					gameManager.setGameState(GameState.WINNING);
				} else if(!ConfigManager.teamExists(teamName))
				{
					gameManager.setGameState(GameState.COUNTDOWN);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		if(gameManager.getGameState() == GameState.LIVE)
		{
			if(gameManager.getPlayerManager().getPlayers().contains(player))
			{
				gameManager.getPlayerManager().removePlayer(player);
				String teamName = ConfigManager.getTeam(player);

				if(ConfigManager.playerInTeam(player))
				{
					ConfigManager.removeTeam(player);
				}

				if(ConfigManager.getTeams().size() <= 1)
				{
					gameManager.setGameState(GameState.WINNING);
				} else if(!ConfigManager.teamExists(teamName))
				{
					gameManager.setGameState(GameState.COUNTDOWN);
				}
			}
			e.getDrops().clear();
		}
	}

	//Making sure that the player teleports back up to the event area when they die and the event is still going on.
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();
		if(gameManager.getGameState() != GameState.INACTIVE)
		{
			player.teleport(ConfigManager.getLobbySpawn());
			e.setRespawnLocation(ConfigManager.getLobbySpawn());
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player damaged = (Player) e.getEntity();
			FileConfiguration config = Event.getInstance().getConfig();

			if(gameManager.getGameState() != GameState.INACTIVE)
			{
				String damagedTeam = config.getString("players." + damaged.getName() + ".team");

				// Try casting to EntityDamageByEntityEvent
				if(e instanceof EntityDamageByEntityEvent)
				{
					Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
					Player attacker = null;

					if(damager instanceof Player)
					{
						attacker = (Player) damager;
					} else if(damager instanceof Projectile)
					{
						Projectile projectile = (Projectile) damager;
						if(projectile.getShooter() instanceof Player)
						{
							attacker = (Player) projectile.getShooter();
						}
					}

					if(attacker != null)
					{
						String damagerTeam = config.getString("players." + attacker.getName() + ".team");

						if(damagedTeam != null && damagerTeam != null && damagedTeam.equals(damagerTeam))
						{
							boolean friendlyFire = config.getBoolean(damagedTeam + ".FriendlyFire");
							if(!friendlyFire)
							{
								attacker.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		if(EventCommands.noMovePlayers.contains(player) && gameManager.getGameState() == GameState.COUNTDOWN)
		{
			Location newToLocation = e.getFrom().setDirection(e.getTo().getDirection());
			e.setTo(newToLocation);
		}

		if(!(gameManager.getPlayerManager().getPlayers().contains(player))
				&& gameManager.getGameState() == GameState.LIVE)
		{
			if(!player.hasPermission("event") && player.getGameMode() == GameMode.SPECTATOR)
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
		String command = e.getMessage().split(" ")[0]; // Extracts the command part of the message

		// Check if the player does not have permission or tries to execute a command other than "/team"
		if(!player.hasPermission("event") && !command.equalsIgnoreCase("/team")
				&& gameManager.getGameState() != GameState.INACTIVE)
		{
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You can only use the /team command right now!");
		}
	}

}

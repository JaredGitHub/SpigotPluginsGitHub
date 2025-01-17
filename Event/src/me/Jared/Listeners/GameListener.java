package me.Jared.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	public void onCommand(PlayerCommandPreprocessEvent e)
	{
		if(gameManager.getGameState() == GameState.LIVE)
		{
			e.getPlayer().sendMessage(ChatColor.RED + "No commands at this time!");
			e.setCancelled(true);
		}
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
					e.getPlayer().sendTitle("EVENT TIME", "EVENT TIME", 0, 0, 0);
					
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
				}
				
				else if(!ConfigManager.teamExists(teamName))
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
							
				player.teleport(ConfigManager.getLobbySpawn());
				
				if(ConfigManager.playerInTeam(player))
				{
					ConfigManager.removeTeam(player);
				}
				
				if(ConfigManager.getTeams().size() <= 1)
				{
					gameManager.setGameState(GameState.WINNING);
				}
				
				else if(!ConfigManager.teamExists(teamName))
				{
					gameManager.setGameState(GameState.COUNTDOWN);
				}
			}
			e.getDrops().clear();
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e)
	{
		FileConfiguration config = Event.getInstance().getConfig();

		if(gameManager.getGameState() != GameState.INACTIVE)
		{
			if(e.getEntity() instanceof Player && e.getDamager() instanceof Player)
			{
				Player p = (Player) e.getEntity();
				Player pdamager = (Player) e.getDamager();

				String damagedTeam = config.getString("players." + p.getName() + ".team");
				String damagerTeam = config.getString("players." + pdamager.getName() + ".team");

				if(damagedTeam.equals(damagerTeam))
				{
					boolean friendlyFire = config.getBoolean(damagedTeam + ".FriendlyFire");
					if(friendlyFire == false)
					{
						pdamager.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");
						e.setCancelled(true);
					}
				}
			}

			if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player)
			{
				Projectile projectile = (Projectile) e.getDamager();
				Player p = (Player) e.getEntity();

				String damagedTeam = config.getString("players." + p.getName() + ".team");
				Player pdamager = (Player) projectile.getShooter();
				String damagerTeam = config.getString("players." + pdamager.getName() + ".team");

				if(damagedTeam == null || damagerTeam == null) return;
				if(damagedTeam.equals(damagerTeam))
				{
					boolean friendlyFire = config.getBoolean(damagedTeam + ".FriendlyFire");
					if(friendlyFire == false)
					{
						pdamager.sendMessage(ChatColor.RED + "You cannot hurt your teammates!");

						e.setCancelled(true);
					}
				}
			}
		}
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
			if(!player.hasPermission("hg") && player.getGameMode() == GameMode.SPECTATOR)
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

		if(!player.hasPermission("event"))
		{
			if(gameManager.getPlayerManager().getPlayers().contains(player))
			{
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot do commands right now!");

			}
		}
	}
}

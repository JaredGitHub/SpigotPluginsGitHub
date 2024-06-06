package me.Jared.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.Jared.GameState;
import me.Jared.GunGame;
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
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		if(gameManager.getPlayerManager().getPlayers().contains(player))
		{
			gameManager.getPlayerManager().removePlayer(player);
			player.teleport(player.getWorld().getSpawnLocation());
		}
		if(gameManager.getPlayerManager().getPlayers().contains(player) 
				&& gameManager.getGameState() == GameState.LIVE)
		{
			gameManager.getPlayerManager().removePlayer(player);
			player.teleport(player.getWorld().getSpawnLocation());

			if(gameManager.getPlayerManager().getPlayerCount() == 1)
			{
				this.gameManager.setGameState(GameState.WINNING);
			}
			player.getInventory().clear();
		}

		if(player.getGameMode() == GameMode.SPECTATOR)
		{
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(player.getWorld().getSpawnLocation());
		}

		if(gameManager.getGameState() == GameState.WINNING)
		{
			if(gameManager.getPlayerManager().getPlayerCount() == 0)
			{
				gameManager.setGameState(GameState.WAITING);
			}
		}

	}

	private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
	private int cooldowntime = 2*1000;

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		if(gameManager.getGameState() == GameState.LIVE)
		{
			//Make it so that the player doesn't drop their items because that would be annoying AF
			e.getDrops().clear();

			//If player is in cooldown STOP!!!
			if(cooldown.containsKey(player.getUniqueId()))
			{
				long timeleft = ((cooldown.get(player.getUniqueId())) + cooldowntime) - (System.currentTimeMillis());

				timeleft /= 1000.0;
				if(timeleft > 0)
				{
					if(gameManager.getGameState() == GameState.LIVE) e.getDrops().clear();
					return;
				}
			}

			if(player.getKiller() != null && gameManager.getPlayerManager().getPlayers().contains(player.getKiller()))
			{
				Player killer = player.getKiller();

				HashMap<UUID, Integer> kills = gameManager.getPlayerManager().getKills();

				if(kills.get(killer.getUniqueId()) == (gameManager.getPlayerManager().getGuns().size()-1))
				{
					for(Player p : Bukkit.getOnlinePlayers())
					{
						if(!p.equals(killer) && gameManager.getPlayerManager().getPlayers().contains(player))
						{
							gameManager.getPlayerManager().removePlayer(p);
						}
					}

					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "toggleks true");

					gameManager.setGameState(GameState.WINNING);
					return;
				}

				kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);

				//Give killer the next gun
				gameManager.getPlayerManager().giveGuns(killer, kills.get(killer.getUniqueId()));
				killer.setHealth(20);
			}

			cooldown.put(player.getUniqueId(), System.currentTimeMillis());
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
			if(!player.hasPermission("gungame") && player.getGameMode() == GameMode.SPECTATOR)
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
			if((e.getMessage().equalsIgnoreCase("/gg leave")) || (e.getMessage().equalsIgnoreCase("/gungame leave")))
			{
				return;
			}
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot do commands right now!");
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();
		if(gameManager.getGameState() == GameState.LIVE)
		{
			HashMap<UUID, Integer> kills = gameManager.getPlayerManager().getKills();

			gameManager.getPlayerManager().giveGuns(player, kills.get(player.getUniqueId()));

			ArrayList<Integer> numbers = new ArrayList<Integer>();
			
			for(String num : GunGame.getInstance().getConfig().getConfigurationSection("gg").getKeys(false))
			{
				int number = Integer.parseInt(num);
				numbers.add(number);
			}

			Random random = new Random();
			int randomNumber = random.nextInt(1, numbers.size()+1);
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_PURPLE + ""+randomNumber);
			Location location = ConfigManager.getGameSlotLocation(randomNumber);

			e.setRespawnLocation(location);

		}
		else
		{
			player.setRespawnLocation(player.getWorld().getSpawnLocation(), true);
		}
	}
}

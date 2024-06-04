package me.Jared.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();

		gameManager.getPlayerManager().removePlayer(player);

		if(player.getGameMode() == GameMode.SPECTATOR)
		{
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(player.getWorld().getSpawnLocation());
		}

		if(gameManager.getGameState() != GameState.RECRUITING)
		{
			if(gameManager.getPlayerManager().getPlayerCount() == 1)
			{
				gameManager.setGameState(GameState.WINNING);
				player.getInventory().clear();

			}
		}
		if(gameManager.getGameState() == GameState.WINNING)
		{
			if(gameManager.getPlayerManager().getPlayerCount() == 0)
			{
				gameManager.setGameState(GameState.WAITING);
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();

		if(gameManager.getGameState() == GameState.LIVE)
		{
			player.teleport(player.getWorld().getSpawnLocation());
		}
		gameManager.getPlayerManager().removePlayer(player);
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

		if(!player.hasPermission("hg"))
		{
			if(gameManager.getPlayerManager().getPlayers().contains(player))
			{
				if((e.getMessage().equalsIgnoreCase("/sg leave")) || (e.getMessage().equalsIgnoreCase("/survivalgames leave")))
				{
					return;
				}
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot do commands right now!");

			}
		}
	}

	@EventHandler
	public void onChestOpen(PlayerInteractEvent e)
	{
		if(e.getClickedBlock() != null)
		{
			if(e.getClickedBlock().getType() == Material.CHEST 
					&& gameManager.getGameState() == GameState.LIVE)
			{
				//Set chest to open
				Location location = e.getClickedBlock().getLocation();
				gameManager.getChestOpen().put(location, true);
			}
		}
	}
}


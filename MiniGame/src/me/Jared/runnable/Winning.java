package me.Jared.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.GameState;
import me.Jared.Manager.GameManager;

public class Winning extends BukkitRunnable
{

	GameManager gameManager;
	Player player;

	public Winning(GameManager gameManager, Player player)
	{
		this.gameManager = gameManager;
		this.player = player;
	}

	@Override
	public void run()
	{
		gameManager.setGameState(GameState.WAITING);
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
		player.sendMessage("YOU WON NOW YOU TELEPORTED");
		player.getInventory().clear();

		this.cancel();
	}
}

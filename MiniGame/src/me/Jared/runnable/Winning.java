package me.Jared.runnable;

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
		player.teleport(player.getWorld().getSpawnLocation());
		player.getInventory().clear();
		gameManager.setGameState(GameState.WAITING);

		this.cancel();
	}
}

package me.Jared.runnable;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.GameState;
import me.Jared.Manager.GameManager;
import me.Jared.Manager.PlayerManager;

public class Winning extends BukkitRunnable
{

	GameManager gameManager;
	
	public Winning(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}
	
	@Override
	public void run()
	{
		PlayerManager playerManager = gameManager.getPlayerManager();
		for(Player player : playerManager.getPlayers())
		{
			player.teleport(player.getWorld().getSpawnLocation());
			player.getInventory().clear();
			gameManager.setGameState(GameState.WAITING);
		}
		this.cancel();
		
	}

}

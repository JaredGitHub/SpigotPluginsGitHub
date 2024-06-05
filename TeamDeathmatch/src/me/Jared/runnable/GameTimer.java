package me.Jared.runnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;

public class GameTimer extends BukkitRunnable
{
	GameManager gameManager;

	public GameTimer(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	private int gameTimeSeconds = ConfigManager.getDeathmatchTime();

	@Override
	public void run()
	{
		if(gameManager.getGameState() == GameState.LIVE)
		{	
			for(Player player : gameManager.getPlayerManager().getPlayers())
			{
				player.setLevel(gameTimeSeconds);
				if(gameTimeSeconds % 60 == 0)
				{
					player.sendMessage(ChatColor.GREEN + ""+gameTimeSeconds/60  + " minute" + (gameTimeSeconds/60 == 1 ? "": "s") + " left!");
					player.sendTitle(ChatColor.GREEN.toString() + gameTimeSeconds/60 + " minute" + (gameTimeSeconds/60 == 1 ? "" : "s"), ChatColor.GRAY + "until game ends!",1,20,1);
				}
			}
		}
		else
		{
			Bukkit.getConsoleSender().sendMessage("CANCELED THE GAMETIMER TASK");
			cancel();
		}
		
		//If game timer is done set gamestate to waiting
		if(gameTimeSeconds == 0)
		{
			gameManager.setGameState(GameState.WAITING);
		}

		gameTimeSeconds--;
	}
}

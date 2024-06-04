package me.Jared.Runnable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;

public class Countdown extends BukkitRunnable
{
	GameManager gameManager;

	public Countdown(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	private int countdownSeconds = ConfigManager.getCountdown();
	@Override
	public void run()
	{
		if(countdownSeconds == 0)
		{
			for(Player player : gameManager.getPlayerManager().getPlayers())
			{
				player.sendTitle(ChatColor.GOLD + "Let the games begin!", ChatColor.BLUE + "Be the last one standing!", 1, 20, 1);
			}

			gameManager.setGameState(GameState.LIVE);
			
			cancel();
			return;
		}

		if(countdownSeconds <= 10 || countdownSeconds % 15 == 0)
		{
			gameManager.getPlayerManager().sendMessage(ChatColor.GREEN + "Game will start in " + countdownSeconds + " second" + (countdownSeconds== 1 ? "": "s") + ".");
		}

		for(Player player : gameManager.getPlayerManager().getPlayers())
		{
			player.sendTitle(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds == 1 ? "" : "s"), ChatColor.GRAY + "until game starts!",1,20,1);
		}

		countdownSeconds--;
	}

}

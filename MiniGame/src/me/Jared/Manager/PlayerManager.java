package me.Jared.Manager;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.Jared.GameState;

public class PlayerManager
{
	private ArrayList<Player> players;
	private GameManager gameManager;

	public PlayerManager(GameManager gameManager)
	{
		this.setGameManager(gameManager);
		this.players = new ArrayList<Player>();

	}

	public int getPlayerCount()
	{
		return players.size();
	}

	public void setPlayerInGame(Player player)
	{
		players.add(player);
	}

	public void removePlayer(Player player)
	{
		players.remove(player);

		if (gameManager.getPlayerManager().getPlayerCount() == 1 && gameManager.getGameState().equals(GameState.LIVE))
		{
			gameManager.setGameState(GameState.WINNING);
		}

	}

	public ArrayList<Player> getPlayers()
	{
		return players;
	}

	public void sendMessage(String string)
	{
		for (Player player : getPlayers())
		{
			player.sendMessage(string);
		}
	}

	public void teleportPlayerInGame()
	{
		int i = 0;
		for (Player player : getPlayers())
		{
			i++;
			player.teleport(ConfigManager.getGameSlotLocation(i));
		}
	}

	public GameManager getGameManager()
	{
		return gameManager;
	}

	public void setGameManager(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

}

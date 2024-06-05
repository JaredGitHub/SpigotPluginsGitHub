
package me.Jared.Manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
	}
	public void removePlayers()
	{
		players.clear();
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
	
	public void teleportPlayers(Location location)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			player.teleport(location);
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

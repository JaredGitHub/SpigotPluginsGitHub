
package me.Jared.Manager;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerManager
{
	private ArrayList<Player> players;
	private GameManager gameManager;

	private int maxPlayers;


	public PlayerManager(GameManager gameManager)
	{
		this.setGameManager(gameManager);
		this.players = new ArrayList<Player>();
		this.maxPlayers = ConfigManager.getPlayersNeeded();
	}

	public int getMaxPlayers()
	{
		return maxPlayers;
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
			Location location = ConfigManager.getGameSlotLocation(i);
			player.teleport(spawnRadius(location,4));
		}
	}

	private Location spawnRadius(Location loc, int radius)
	{
		Location center = loc;
		Random rand = new Random();
		double angle = rand.nextDouble()*360; //Generate a random angle
		double x = center.getX() + (rand.nextDouble()*radius*Math.cos(Math.toRadians(angle))); // x
		double z = center.getZ() + (rand.nextDouble()*radius*Math.sin(Math.toRadians(angle))); // z
		Location newloc = new Location(Bukkit.getWorld("world"), x, center.getY(), z);
		newloc.setYaw(center.getYaw());
		newloc.setPitch(center.getPitch());
		return newloc;
	}
	
	public void teleportPlayers(Location location)
	{
		for(Player player : getPlayers())
		{
			player.teleport(location);
		}
	}
	public void removePlayers()
	{
		for(Player player : players)
		{
			removePlayer(player);
			Bukkit.getConsoleSender().sendMessage("Removed player " + player.getName());
		}
		players = new ArrayList<Player>();
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

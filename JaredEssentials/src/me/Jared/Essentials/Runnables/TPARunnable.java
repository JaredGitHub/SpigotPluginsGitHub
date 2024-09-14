package me.Jared.Essentials.Runnables;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TPARunnable extends BukkitRunnable
{
	private int seconds;
	private Player player;

	private HashMap<Player, Player> requesters;
	private ArrayList<Player> playersInCooldown;

	public TPARunnable(Player player, int seconds, HashMap<Player, Player> requesters,
			ArrayList<Player> playersInCooldown)
	{
		this.player = player;
		this.requesters = requesters;
		this.playersInCooldown = playersInCooldown;

		this.seconds = seconds;
	}

	private Location previousLocation;

	@Override
	public void run()
	{
		if(!(player.isOnline()))
		{
			cancel();
		}
		if(seconds <= 0)
		{
			requesters.get(player).teleport(player);

			// Start five minute timer and add player to ArrayList
			playersInCooldown.add(requesters.get(player));
			this.cancel();
			return;
		}

		if(requesters.get(player) != null)
		{
			Location currentLocation = requesters.get(player).getLocation();

			if(previousLocation != null)
			{
				if(currentLocation.getX() != previousLocation.getX()
						|| currentLocation.getZ() != previousLocation.getZ())
				{
					requesters.get(player).sendMessage(ChatColor.RED + "You moved!");
					player.sendMessage(ChatColor.RED + requesters.get(player).getName() + " moved like a noob!");
					requesters.remove(player);
					this.cancel();
					return;
				}
			}

			player.sendMessage(ChatColor.GOLD + "Teleporting " + requesters.get(player).getName() + " to "
					+ player.getName() + " in " + ChatColor.WHITE + seconds + ChatColor.GOLD + " seconds!");

			requesters.get(player)
					.sendMessage(ChatColor.GOLD + "Teleporting " + requesters.get(player).getName() + " to "
							+ player.getName() + " in " + ChatColor.WHITE + seconds + ChatColor.GOLD
							+ " seconds, don't move!");

			previousLocation = currentLocation.clone();
			seconds--;
		}
	}
}

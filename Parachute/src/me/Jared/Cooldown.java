package me.Jared;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Cooldown
{
	public Cooldown(double seconds)
	{
		this.seconds = seconds;
	}

	private double seconds = 0;

	private HashMap<UUID, Long> hashmap = new HashMap<UUID, Long>();

	public void putInCooldown(Player player)
	{
		hashmap.put(player.getUniqueId(), (long) (System.currentTimeMillis() + (seconds * 1000L)));
	}

	public boolean isOnCooldown(Player player)
	{
		UUID uuid = player.getUniqueId();
		return (hashmap.get(uuid) != null && hashmap.get(uuid) > System.currentTimeMillis());
	}

	public int getCooldownSeconds(Player player)
	{
		UUID uuid = player.getUniqueId();
		return (int) ((hashmap.get(uuid) - System.currentTimeMillis()) / 1000L);
	}
}

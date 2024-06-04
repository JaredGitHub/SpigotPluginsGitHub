package me.Jared;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Cooldown
{
	public Cooldown(long seconds)
	{
		this.seconds = seconds;
	}

	private long seconds = 0;

	private HashMap<UUID, Long> hashmap = new HashMap<UUID, Long>();

	public void putInCooldown(Player player)
	{
		hashmap.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
	}

	public boolean isOnCooldown(Player player)
	{
		UUID uuid = player.getUniqueId();
		return (hashmap.get(uuid) != null && hashmap.get(uuid) > System.currentTimeMillis());
	}

	public long getCooldownSeconds(Player player)
	{
		UUID uuid = player.getUniqueId();
		return (int) ((hashmap.get(uuid) - System.currentTimeMillis()) / 1000L);
	}

	private long getCooldownFloat(Player player)
	{
		UUID uuid = player.getUniqueId();
		return (hashmap.get(uuid) - System.currentTimeMillis());
	}

	public String getCooldownLeft(Player player)
	{
		String decimals = ""+getCooldownFloat(player);
		if(decimals.length() > 1)
		{
			return getCooldownSeconds(player) + "." + decimals.substring(1, decimals.length() - 1);
		}
		else
		{
			return decimals;
		}
	}
}

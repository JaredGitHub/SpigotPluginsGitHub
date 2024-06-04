package me.jared.Compass;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Util
{	

	//Method to make the compass spin
	private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
	private int cooldowntime = 5;
	private int baba;
	public void spin(Player p)
	{
		if(cooldown.containsKey(p.getUniqueId()))
		{
			long ticksleft = ((cooldown.get(p.getUniqueId()) / 50) + cooldowntime) - (System.currentTimeMillis() / 50);

			if(ticksleft > 0)
			{
				return;
			}
		}
		
		
		switch(baba)
		{
		case 1: 
			p.setCompassTarget(p.getLocation().add(0,0,-5));
			baba = 2;
			break;
		case 2: 
			p.setCompassTarget(p.getLocation().add(5,0,0));
			baba = 3;
			break;
		case 3: 
			p.setCompassTarget(p.getLocation().add(0,0,5));
			baba = 4;
			break;
		case 4: 
			p.setCompassTarget(p.getLocation().add(-5,0,0));
			baba = 1;
			break;
		default:
			baba = 1;
			break;
		}
		cooldown.put(p.getUniqueId(), System.currentTimeMillis());

	}


	// Method for getting the nearest player
	public Player getNearestPlayer(Player player)
	{
		double distance = Double.POSITIVE_INFINITY; // To make sure the first
		// player checked is closest
		Player target = null;
		for (Entity entity : player.getNearbyEntities(200, 200, 200))
		{
			if (!(entity instanceof Player))
				continue;
			if(entity == player) continue; //Added this check so you don't target yourself.
			double distanceto = player.getLocation().distance(entity.getLocation());
			if (distanceto > distance)
				continue;
			distance = distanceto;
			
			if(entity instanceof Player)
			{
				Player p = (Player) entity;
				if(p.getGameMode().equals(GameMode.SURVIVAL))
				{
					target = p;
				}
			}
		}
		return target;
	}
}

package me.jared.barb;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;


public class HurtPlayerRunnable implements Runnable
{

	private Player p;

	public HurtPlayerRunnable(Player p)
	{
		this.p = p;
	}

	Material web = Material.COBWEB;
	private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();

	@Override
	public void run()
	{
		if(cooldown.containsKey(p.getUniqueId()))
		{
			long ticksleft = ((cooldown.get(p.getUniqueId()) / 50) + 20) - (System.currentTimeMillis() / 50);

			if(ticksleft > 0)
			{
				return;
			}
		}

		if(p.getLocation().getBlock().getType().equals(web) || p.getEyeLocation().getBlock().getType().equals(web))
		{

			p.damage(2);
		}

		cooldown.put(p.getUniqueId(), System.currentTimeMillis());
	}
}

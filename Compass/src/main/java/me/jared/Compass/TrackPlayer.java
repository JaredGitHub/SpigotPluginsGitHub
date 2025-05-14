package me.jared.Compass;

import org.bukkit.entity.Player;

public class TrackPlayer implements Runnable
{
	Util util = new Util();

	private Player p;

	public TrackPlayer(Player p)
	{
		this.p = p;
	}

	@Override
	public void run()
	{
		Player playerNear = util.getNearestPlayer(p);

		//If there are players around point to them with the compass
		
		if(playerNear == null)
		{
			util.spin(p);
		}else
		{
			p.setCompassTarget(playerNear.getLocation());
		}
		
	}
}


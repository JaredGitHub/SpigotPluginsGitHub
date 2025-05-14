package me.jared.Compass;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TrackLocation implements Runnable
{

	Util util = new Util();

	private Location location;
	private Player p;
	public TrackLocation(Player p, Location location)
	{
		this.p = p;
		this.location = location;
	}

	@Override
	public void run()
	{
		//If there are players around point to them with the compass

		if(location == null)
		{
			util.spin(p);
		}else
		{
			p.setCompassTarget(location);
		}
	}
}

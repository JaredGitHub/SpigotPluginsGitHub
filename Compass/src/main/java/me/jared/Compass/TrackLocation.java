package me.jared.Compass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TrackLocation implements Runnable
{

	Util util = new Util();
	FileConfiguration airdropConfig;

	private Location location;
	private Player p;
	public TrackLocation(Player p, Location location)
	{
		this.airdropConfig = Bukkit.getPluginManager().getPlugin("AirDrop").getConfig();
		this.p = p;
		this.location = location;
	}

	@Override
	public void run()
	{
		//If there are players around point to them with the compass

		if(location == null || airdropConfig.getInt("airdrop") == 0)
		{
			util.spin(p);
		}else
		{
			p.setCompassTarget(location);
		}
	}
}

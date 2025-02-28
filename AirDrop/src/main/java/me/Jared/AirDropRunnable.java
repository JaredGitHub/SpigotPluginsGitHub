package me.Jared;

import org.bukkit.scheduler.BukkitRunnable;

public class AirDropRunnable extends BukkitRunnable
{
	private int seconds;
	private AirDrop airDrop;

	public AirDropRunnable(AirDrop airDrop, int seconds)
	{
		this.seconds = seconds;
		this.airDrop = airDrop;
	}

	@Override
	public void run()
	{
		if(seconds <= 0)
		{
			this.cancel();
			airDrop.drop();
			int interval = Main.getInstance().getConfig().getInt("interval");
			var airDropManager = new AirDropManager();
			airDropManager.runAirDropRunnable(airDrop, interval);
		}
		seconds--;
	}
}

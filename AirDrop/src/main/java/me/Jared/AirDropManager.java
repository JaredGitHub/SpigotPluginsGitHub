package me.Jared;

import org.bukkit.plugin.Plugin;

public class AirDropManager
{
	private Plugin plugin;
	public AirDropManager()
	{
		this.plugin = Main.getInstance();
	}

	public void runAirDropRunnable(AirDrop airDrop, int seconds)
	{
		var airDropRunnable = new AirDropRunnable(airDrop, seconds);
		airDropRunnable.runTaskTimer(plugin, 0, 20);
	}
}


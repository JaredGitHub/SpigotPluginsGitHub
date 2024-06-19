package me.Jared.LandMine;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.Jared.LandMinePlugin;
import me.jared.BoostPad;

public class LandMineEvents implements Listener
{
	FileConfiguration config = LandMinePlugin.getInstance().getConfig();

	private String getIndex(String string, int num)
	{
		StringBuilder stringBuilder = new StringBuilder();

		int count = 0;
		for(int i = 0; i < string.length(); i++)
		{
			if(string.charAt(i) == ',')
			{
				count++;
				continue;
			}

			if(count == num)
			{
				stringBuilder.append(string.charAt(i));
			}
		}

		return stringBuilder.toString();
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		LandMine landMine = new LandMine();

		Player player = e.getPlayer();

		landMine.changeItemToLandMine(player.getInventory());

		Block playerFootY = player.getLocation().getBlock();

		ArrayList<String> landmines = new ArrayList<String>(BoostPad.getInstance().getConfig().getStringList("landmines"));

		for (String landminesLoc : landmines)
		{
			double x = Double.parseDouble(getIndex(landminesLoc, 0));
			double y = Double.parseDouble(getIndex(landminesLoc, 1));
			double z = Double.parseDouble(getIndex(landminesLoc, 2));
			String world = getIndex(landminesLoc, 3);
			Location loc = new Location(Bukkit.getWorld(world), x, y, z);

			if(loc.equals(playerFootY.getLocation()))
			{
				if(playerFootY.getType() == Material.STONE_PRESSURE_PLATE)
				{
					if(playerFootY.getType() == landMine.getItem())
					{
						landMine.blowUp(player.getLocation(), player);
					}

					for(Entity entity : Bukkit.getWorld(world).getEntities())
					{
						if(entity instanceof LivingEntity)
						{
							Block entityFootY = entity.getLocation().getBlock();
							if(entityFootY.getType() == landMine.getItem())
							{
								landMine.blowUp(entity.getLocation(), entity);
							}
						}
					}
				}
			}
		}
	}
}

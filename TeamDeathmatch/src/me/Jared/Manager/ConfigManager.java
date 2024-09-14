package me.Jared.Manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Deathmatch;


public class ConfigManager
{
	private static FileConfiguration config;
	private static Deathmatch plugin = Deathmatch.getInstance();

	public static void setupConfig(Deathmatch plugin)
	{
		ConfigManager.config = plugin.getConfig();
		plugin.saveConfig();
	}

	public static void tdmReset()
	{
		config.set("tdm", null);
		plugin.saveConfig();
	}

	public static ArrayList<String> getWinners(int winningTeam)
	{
		ArrayList<String> list = new ArrayList<>(config.getStringList("tdm.team." + winningTeam + ".players"));

		return list;
	}

	public static int getWinningTeam()
	{
		//return winning team based on the team
		int team1 = getKills(1);
		int team2 = getKills(2);

		if(team1 > team2)
		{
			return 1;
		}
		else if(team1 < team2)
		{
			return 2;
		}
		else
		{
			return 0;
		}
	}


	public static void setKills(int team, int kills)
	{
		config.set("tdm.team." + team + ".kills", kills);
		plugin.saveConfig();
	}

	public static int getKills(int team)
	{
		return config.getInt("tdm.team." + team + ".kills");
	}

	public static void setTeam(int number, Player player)
	{
		ArrayList<String> list = new ArrayList<>(config.getStringList("tdm.team." + number + ".players"));

		if(!list.contains(player.getUniqueId().toString()))
		{
			list.add(player.getUniqueId().toString());
		}

		config.set("tdm.team." + number + ".players", list);
		plugin.saveConfig();
	}

	public static int getTeam(Player player)
	{
		return config.getInt("tdm." + player.getUniqueId() + ".team");
	}

	public static void setTeam(Player player, int number)
	{
		config.set("tdm." + player.getUniqueId() + ".team", number);
		plugin.saveConfig();
	}

	public static int getDeathmatchTime()
	{
		return config.getInt("deathmatch-time");
	}

	public static void setDeathmatchTime(int number)
	{
		config.set("deathmatch-time", number);
		plugin.saveConfig();
	}

	public static Location getLobbySpawn()
	{
		return new Location(Bukkit.getWorld(config.getString("lobby-spawn.world")),
				config.getDouble("lobby-spawn.x"),
				config.getDouble("lobby-spawn.y"),
				config.getDouble("lobby-spawn.z"),
				(float) config.getDouble("lobby-spawn.yaw"),
				(float) config.getDouble("lobby-spawn.pitch")
				);
	}

	public static void setLobbySpawn(Location location)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		config.set("lobby-spawn.world",location.getWorld().getName());
		config.set("lobby-spawn.x", x);
		config.set("lobby-spawn.y",y);
		config.set("lobby-spawn.z",z);
		config.set("lobby-spawn.yaw", location.getYaw());
		config.set("lobby-spawn.pitch", location.getPitch());
		plugin.saveConfig();

	}

	public static void setSpawn(Location location)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		config.set("spawn.world",location.getWorld().getName());
		config.set("spawn.x", x);
		config.set("spawn.y",y);
		config.set("spawn.z",z);
		config.set("spawn.yaw", location.getYaw());
		config.set("spawn.pitch", location.getPitch());
		plugin.saveConfig();
	}

	public static Location getSpawn()
	{
		return new Location(Bukkit.getWorld(config.getString("spawn.world")),
				config.getDouble("spawn.x"),
				config.getDouble("spawn.y"),
				config.getDouble("spawn.z"),
				(float) config.getDouble("spawn.yaw"),
				(float) config.getDouble("spawn.pitch")
				);
	}

	public static Location getGameSlotLocation(int i)
	{

		String world = config.getString("tdmspawn.world",Bukkit.getWorlds().get(0).getName());
		double x = config.getDouble("tdmspawn." + i + ".x");
		double y = config.getDouble("tdmspawn." + i + ".y");
		double z = config.getDouble("tdmspawn." + i + ".z");
		float yaw = (float) config.getDouble("tdmspawn." + i + ".yaw");
		float pitch = (float) config.getDouble("tdmspawn." + i + ".pitch");

		return new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
	}

	public static void setGameSlot(Location loc, int i)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("tdmspawn.world",loc.getWorld().getName());
		config.set("tdmspawn." + i + ".x", x);
		config.set("tdmspawn." + i + ".y", y);
		config.set("tdmspawn." + i + ".z", z);
		config.set("tdmspawn." + i + ".yaw", loc.getYaw());
		config.set("tdmspawn." + i + ".pitch", loc.getPitch());

		plugin.saveConfig();
	}

	public static int getPlayersNeeded()
	{
		return config.getInt("required-players");
	}
	public static void setPlayersNeeded(int number)
	{
		config.set("required-players",number);
		plugin.saveConfig();
	}

	public static int getCountdown()
	{
		return config.getInt("countdown-seconds");
	}
	public static void setCountdown(int number)
	{
		config.set("countdown-seconds",number);
		plugin.saveConfig();
	}
}

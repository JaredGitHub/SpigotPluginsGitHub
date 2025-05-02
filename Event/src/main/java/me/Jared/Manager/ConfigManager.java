package me.Jared.Manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Event;

public class ConfigManager
{
	private static FileConfiguration config = Event.getInstance().getConfig();

	private static void saveConfig()
	{
		Event.getInstance().saveConfig();
	}

	public static Location getLobbySpawn()
	{
		return new Location(Bukkit.getWorld(config.getString("lobby-spawn.world")), config.getDouble("lobby-spawn.x"),
				config.getDouble("lobby-spawn.y"), config.getDouble("lobby-spawn.z"),
				(float) config.getDouble("lobby-spawn.yaw"), (float) config.getDouble("lobby-spawn.pitch"));
	}

	public static void setLobbySpawn(Location location)
	{
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		config.set("lobby-spawn.world", location.getWorld().getName());
		config.set("lobby-spawn.x", x);
		config.set("lobby-spawn.y", y);
		config.set("lobby-spawn.z", z);
		config.set("lobby-spawn.yaw", location.getYaw());
		config.set("lobby-spawn.pitch", location.getPitch());
		saveConfig();
	}

	public static Location getEventSpawn(int i)
	{
		Event.getInstance().reloadConfig();
		double x = config.getDouble("event." + i + ".x");
		double y = config.getDouble("event." + i + ".y");
		double z = config.getDouble("event." + i + ".z");
		float yaw = (float) config.getDouble("event." + i + ".yaw");
		float pitch = (float) config.getDouble("event." + i + ".pitch");

		return new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
	}

	public static void setEventSpawn(Location loc, int i)
	{
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();

		config.set("event.world", loc.getWorld().getName());
		config.set("event." + i + ".x", x);
		config.set("event." + i + ".y", y);
		config.set("event." + i + ".z", z);
		config.set("event." + i + ".yaw", loc.getYaw());
		config.set("event." + i + ".pitch", loc.getPitch());

		saveConfig();
	}

	public static String getTeam(Player player)
	{
		String team = config.getString("players." + player.getName() + ".team");
		return team;
	}

	public static String getTeamName(int i)
	{
		var list = config.getStringList("teams");
		return list.get(i);
	}

	public static int getTeamIndex(Player player)
	{
		Event.getInstance().reloadConfig();
		String string = getTeam(player);
		int team = config.getStringList("teams").indexOf(string);
		return team;
	}

	public static List<Integer> getTeams()
	{
		ArrayList<Integer> teams = new ArrayList<Integer>();

		int teamNumber = config.getStringList("teams").size();

		for(int i = 0; i < teamNumber; i++)
		{
			teams.add(i);
		}

		return teams;
	}

	public static List<Player> getPlayers(String team)
	{
		var stringList = config.getStringList("team." + team + ".Members");
		var playerList = new ArrayList<Player>();
		for(String string : stringList)
		{
			playerList.add(Bukkit.getPlayer(string));
		}

		return playerList;
	}

	public static void clearTeams()
	{
		config.set("teams", null);
		config.set("team", null);
		config.set("players", null);
		saveConfig();
	}

	public static int getCountdown()
	{
		return config.getInt("countdown-seconds");
	}

	public static void setCountdown(int number)
	{
		config.set("countdown-seconds", number);
		saveConfig();
	}

	public static boolean playerInTeam(Player player)
	{
		return config.getConfigurationSection("players").contains(player.getName());
	}

	public static boolean teamExists(String team)
	{
		return config.getStringList("teams").contains(team) ? true : false;
	}

	public static void removeTeam(Player player)
	{
		final String teamName = config.getString("players." + player.getName() + ".team");
		if(teamName == null)
			return;

		List<String> configList = new ArrayList<>(config.getStringList("team." + teamName + ".Members"));
		if(configList.isEmpty())
			return;

		configList.remove(player.getName());

		if(configList.isEmpty())
		{
			// Delete team entirely
			config.set("team." + teamName, null);
			List<String> teamList = new ArrayList<>(config.getStringList("teams"));
			teamList.remove(teamName);
			config.set("teams", teamList);
		} else
		{
			// Set new leader and update members
			config.set("team." + teamName + ".Leader", configList.get(0));
			config.set("team." + teamName + ".Members", configList);
			config.set("team." + teamName + ".FriendlyFire", false);
		}

		config.set("players." + player.getName(), null);
		saveConfig();
	}

}

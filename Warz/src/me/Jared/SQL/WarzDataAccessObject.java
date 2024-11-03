package me.Jared.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.Jared.Warz;

public class WarzDataAccessObject
{
	static final String DATABASE_URL = "jdbc:mysql://mysql.apexhosting.gdn:3306/apexMC2060480";
	static final String USERNAME = "apexMC2060480";
	static final String PASSWORD = "vBn7s!yFHN5m8yk$hEY@4UVe";

	Plugin plugin = Warz.getInstance();
	FileConfiguration config = plugin.getConfig();

	public int savePlayerWarzData(Player player)
	{
		Location loc = player.getLocation();

		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();

		String uuid = player.getUniqueId().toString();
		String world = player.getWorld().getName();
		double health = player.getHealth();
		

		try
		{
			Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			conn.createStatement();
			String updateString = "INSERT INTO Warz (uuid,world,x,y,z,yaw,pitch,health) VALUES (?,?,?,?,?,?,?,?)"
					+ "ON DUPLICATE KEY UPDATE world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z), health = VALUES(health), yaw = VALUES(yaw), pitch = VALUES(pitch)";

			PreparedStatement preparedStatement = conn.prepareStatement(updateString);

			preparedStatement.setString(1, uuid);
			preparedStatement.setString(2, world);
			preparedStatement.setDouble(3, x);
			preparedStatement.setDouble(4, y);
			preparedStatement.setDouble(5, z);
			preparedStatement.setFloat(6, yaw);
			preparedStatement.setFloat(7, pitch);
			preparedStatement.setDouble(8, health);

			// ExecuteUpdate returns the number of rows affected by the query
			int updates = preparedStatement.executeUpdate();

			return updates;

		}catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		return 0;
	}
	
	public PlayerData getPlayerByUUID(String uuid)
	{
		// Create an empty set of Pets. Will be appended and returned.
		ArrayList<PlayerData> returnThese = new ArrayList<>();

		// Open the connection
		try
		{
			Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

			String sqlString = "SELECT ID, uuid, world, health, x,y,z,yaw,pitch FROM Warz WHERE uuid = ?";

			PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
			preparedStatement.setString(1, uuid);

			ResultSet resultSet = preparedStatement.executeQuery();

			while(resultSet.next())
			{
				// For each row in the result set, create a new pet
				PlayerData p = new PlayerData();
				p.setId(resultSet.getInt(("ID")));
				p.setUuid(resultSet.getString(("uuid")));
				p.setHealth(resultSet.getDouble("health"));
				p.setWorld(resultSet.getString("world"));
				p.setX(resultSet.getInt("x"));
				p.setY(resultSet.getInt("y"));
				p.setZ(resultSet.getInt("z"));
				p.setYaw(resultSet.getFloat("yaw"));
				p.setPitch(resultSet.getFloat("pitch"));

				returnThese.add(p);
			}

		} catch(SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + e.getMessage());
		}

		// Return the first element in the list
		if(returnThese.size() > 0)
		{
			return returnThese.get(0);
		}

		return null;
	}
	
	
	

}

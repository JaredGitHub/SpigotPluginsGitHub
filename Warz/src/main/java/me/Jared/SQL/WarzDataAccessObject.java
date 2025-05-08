package me.Jared.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import me.Jared.Warz;
import me.Jared.Loot.InventorySaver;

public class WarzDataAccessObject
{

	static final String DATABASE_URL = Warz.getInstance().getConfig().getString("DATABASE_URL");

	static final String USERNAME = Warz.getInstance().getConfig().getString("USERNAME");

	static final String PASSWORD = Warz.getInstance().getConfig().getString("DB_PASSWORD");

	public static boolean clearPlayerData(String uuid)
	{
		try(Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD))
		{
			// Delete from Warz table
			try(PreparedStatement warzStmt = conn.prepareStatement("DELETE FROM Warz WHERE uuid = ?"))
			{
				warzStmt.setString(1, uuid);
				warzStmt.executeUpdate();
			}

			return true;
		} catch(SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to clear player data: " + e.getMessage());
			return false;
		}
	}

	public static int savePlayerWarzData(Player player, Location overrideLocation, Inventory overrideInventory)
	{
		Inventory inventory = overrideInventory != null ? overrideInventory : player.getInventory();
		Location loc = overrideLocation != null ? overrideLocation : player.getLocation();

		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		String uuid = player.getUniqueId().toString();
		double health = player.getHealth();

		try
		{
			Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			String updateString = "INSERT INTO Warz (uuid, x, y, z, yaw, pitch, health, inventory) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
					+ "ON DUPLICATE KEY UPDATE x = VALUES(x), y = VALUES(y), z = VALUES(z), "
					+ "health = VALUES(health), yaw = VALUES(yaw), pitch = VALUES(pitch), inventory = VALUES(inventory)";
			PreparedStatement preparedStatement = conn.prepareStatement(updateString);
			preparedStatement.setString(1, uuid);
			preparedStatement.setDouble(2, x);
			preparedStatement.setDouble(3, y);
			preparedStatement.setDouble(4, z);
			preparedStatement.setFloat(5, yaw);
			preparedStatement.setFloat(6, pitch);
			preparedStatement.setDouble(7, health);
			preparedStatement.setString(8, InventorySaver.toBase64(inventory));
			return preparedStatement.executeUpdate();
		} catch(SQLException e)
		{
			System.out.println(e.getMessage());
			return 0;
		}
	}

	public static void getPlayerByUUID(String uuid, Consumer<PlayerData> callback)
	{
		Bukkit.getScheduler().runTaskAsynchronously(Warz.getInstance(), () ->
		{
			PlayerData playerData = null;
			try(Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
					PreparedStatement preparedStatement = conn.prepareStatement(
							"SELECT uuid, health, x, y, z, yaw, pitch, inventory FROM Warz WHERE uuid = ?"))
			{

				preparedStatement.setString(1, uuid);
				ResultSet resultSet = preparedStatement.executeQuery();

				if(resultSet.next())
				{
					playerData = new PlayerData();
					playerData.setUuid(resultSet.getString("uuid"));
					playerData.setHealth(resultSet.getDouble("health"));
					playerData.setX(resultSet.getInt("x"));
					playerData.setY(resultSet.getInt("y"));
					playerData.setZ(resultSet.getInt("z"));
					playerData.setYaw(resultSet.getFloat("yaw"));
					playerData.setPitch(resultSet.getFloat("pitch"));
					playerData.setInventory(resultSet.getString("inventory"));
				}
			} catch(SQLException e)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error fetching player data: " + e.getMessage());
			}

			PlayerData finalPlayerData = playerData;
			Bukkit.getScheduler().runTask(Warz.getInstance(), () -> callback.accept(finalPlayerData));
		});
	}

	public static String getPlayerWorldInventoryBase64(String uuid)
	{
		ArrayList<String> returnThese = new ArrayList<>();
		try
		{
			Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			String sqlString = "SELECT uuid, `inventory` FROM `World` WHERE uuid = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
			preparedStatement.setString(1, uuid);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next())
			{
				returnThese.add(resultSet.getString("inventory"));
			}
		} catch(SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "IT DIDN'T LOAD THE INVENTORY!");
		}
		if(returnThese.size() > 0)
		{
			return returnThese.get(0);
		}
		return null;
	}

	public static int savePlayerWorldData(Player player)
	{
		Inventory inventory = player.getInventory();

		try
		{
			Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			conn.createStatement();
			String updateString = "INSERT INTO World (inventory,uuid) VALUES (?,?) ON DUPLICATE KEY UPDATE inventory = VALUES(inventory)";
			PreparedStatement preparedStatement = conn.prepareStatement(updateString);

			preparedStatement.setString(1, InventorySaver.toBase64(inventory));
			preparedStatement.setString(2, player.getUniqueId().toString());
			int updates = preparedStatement.executeUpdate();
			return updates;
		} catch(SQLException e)
		{
			System.out.println(e.getMessage());
			return 0;
		}
	}
}

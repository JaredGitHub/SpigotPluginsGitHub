package me.Jared.Ranks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import me.Jared.Stats;

public class RankManager
{
	private Stats stats;

	private File file;
	private YamlConfiguration config;

	private HashMap<UUID, PermissionAttachment> perms = new HashMap<>();

	public RankManager(Stats stats)
	{
		this.stats = stats;

		if(!stats.getDataFolder().exists())
		{
			stats.getDataFolder().mkdir();
		}

		file = new File(stats.getDataFolder(), "ranks.yml");

		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);

	}

	public void setRank(UUID uuid, Rank rank)
	{
		// Always save the rank to config regardless of online status
		try
		{
			config.set(uuid.toString(), rank.name());
			config.save(file);
		} catch(IOException e)
		{
			e.printStackTrace();
			return; // Don't proceed if save failed
		}

		// Only update permissions and nametag if player is online
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline())
		{
			updatePlayerPermissions(player, rank);
		}
		// If player isn't online, the permissions will be applied when they join
	}

	// Separate method to update permissions for online players
	private void updatePlayerPermissions(Player player, Rank rank)
	{
		PermissionAttachment attachment;
		UUID uuid = player.getUniqueId();

		if(perms.containsKey(uuid))
		{
			attachment = perms.get(uuid);
		} else
		{
			attachment = player.addAttachment(stats);
			perms.put(uuid, attachment);
		}

		// Clear all previous permissions from this attachment
		for(String permission : attachment.getPermissions().keySet())
		{
			attachment.unsetPermission(permission);
		}

		// OWNER gets all permissions from all ranks
		if(rank == Rank.OWNER)
		{
			for(Rank r : Rank.values())
			{
				if(r != Rank.OWNER)
				{
					for(String perm : r.getPermissions())
					{
						if(perm != null && !perm.trim().isEmpty())
						{
							attachment.setPermission(perm, true);
						}
					}
				}
			}
		}

		// Set permissions for the current rank
		for(String perm : rank.getPermissions())
		{
			if(perm != null && !perm.trim().isEmpty())
			{
				attachment.setPermission(perm, true);
			}
		}

		// Recalculate permissions
		player.recalculatePermissions();

		// Update nametag if available
		if(stats.getNametagManager() != null)
		{
			stats.getNametagManager().removeTag(player);
			stats.getNametagManager().newTag(player);
		}
	}

	// Call this method when a player joins to apply their saved rank
	public void applySavedRank(Player player, boolean firstJoin)
	{
		UUID uuid = player.getUniqueId();

		if(config.contains(uuid.toString()))
		{
			try
			{
				String rankName = config.getString(uuid.toString());
				Rank rank = Rank.valueOf(rankName);
				updatePlayerPermissions(player, rank);
			} catch(IllegalArgumentException e)
			{
				// Handle invalid rank in config
				e.printStackTrace();
			}
		} else if(firstJoin)
		{ // You might want to set a default rank for first join
			// Set default rank for new players
			setRank(uuid, Rank.DEFAULT); // Replace DEFAULT with your default rank
		}
	}

	public Rank getRank(UUID uuid)
	{
		if(config.getString(uuid.toString()) != null)
		{
			return Rank.valueOf(config.getString(uuid.toString()));
		} else
		{
			return Rank.DEFAULT;
		}
	}

	public HashMap<UUID, PermissionAttachment> getPerms()
	{
		return perms;
	}

}
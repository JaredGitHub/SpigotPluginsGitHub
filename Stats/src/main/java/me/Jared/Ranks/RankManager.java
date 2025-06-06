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

	public void setRank(UUID uuid, Rank rank, boolean firstJoin)
	{
		if(Bukkit.getOfflinePlayer(uuid).isOnline() && !firstJoin)
		{
			Player player = Bukkit.getPlayer(uuid);
			PermissionAttachment attachment;
			if(perms.containsKey(uuid))
			{
				attachment = perms.get(uuid);
			} else
			{
				attachment = player.addAttachment(stats);
				perms.put(uuid, attachment);
			}

			// Unset all possible rank permissions
			for(Rank r : Rank.values())
			{
				for(String perm : r.getPermissions())
				{
					attachment.unsetPermission(perm);
				}
			}

			// Add permissions for the new rank
			if(rank == Rank.OWNER)
			{
				// OWNER gets permissions from all other ranks
				for(Rank r : Rank.values())
				{
					if(r != Rank.OWNER)
					{
						for(String perm : r.getPermissions())
						{
							attachment.setPermission(perm, true);
						}
					}
				}
			}

			// Always apply the base rank's permissions too
			for(String perm : rank.getPermissions())
			{
				attachment.setPermission(perm, true);
			}
		}

		// Save rank in config
		config.set(uuid.toString(), rank.name());
		try
		{
			config.save(file);
		} catch(IOException e)
		{
			e.printStackTrace();
		}

		// Update nametag if player is online
		if(Bukkit.getOfflinePlayer(uuid).isOnline())
		{
			Player player = Bukkit.getPlayer(uuid);
			stats.getNametagManager().removeTag(player);
			stats.getNametagManager().newTag(player);
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
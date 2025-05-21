package me.Jared.eventRSVP;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventRSVPExpansion extends PlaceholderExpansion
{
	@Override
	public String getAuthor()
	{
		return "Jared";
	}

	@Override
	public String getIdentifier()
	{
		return "EventRSVP";
	}

	@Override
	public String getVersion()
	{
		return "1.0";
	}

	@Override
	public boolean canRegister()
	{
		return true;
	}

	@Override
	public boolean persist()
	{
		return true;
	}

	@Override
	public String onPlaceholderRequest(Player player, String params)
	{
		if (player == null) return "";

		// Example: %eventrsvp_1%
		if (params.matches("\\d+")) {
			int index = Integer.parseInt(params) - 1;

			List<String> names = new ArrayList<>();
			for (String uuidStr : EventRSVP.getInstance().getConfig().getConfigurationSection("RSVP").getKeys(false)) {
				UUID uuid = UUID.fromString(uuidStr);
				OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
				if (p.getName() != null) {
					names.add(p.getName());
				}
			}

			if (index >= 0 && index < names.size()) {
				return names.get(index);
			} else {
				return "";
			}
		}

		return null;
	}
}

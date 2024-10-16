package me.Jared.PlaceHolders;

import org.bukkit.entity.Player;

import me.Jared.Duels;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class DuelsExpansion extends PlaceholderExpansion
{
	@Override
	public String getAuthor()
	{
		return "Jared";
	}

	@Override
	public String getIdentifier()
	{
		return "Duels";
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
		if(player == null) return "";
		if(params.equals("wins"))
		{
			return Duels.getInstance().getConfig().getString(player.getUniqueId() + ".wins");
		}
		
		return null;
	}
}

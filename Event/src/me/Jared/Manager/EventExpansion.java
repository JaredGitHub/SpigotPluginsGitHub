package me.Jared.Manager;

import org.bukkit.entity.Player;

import me.Jared.Event;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class EventExpansion extends PlaceholderExpansion
{

	@Override
	public String getAuthor()
	{
		return "Jared";
	}

	@Override
	public String getIdentifier()
	{
		return "Event";
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
			return Event.getInstance().getConfig().getString(player.getUniqueId() + ".wins");
		}
		
		return null;
	}
}

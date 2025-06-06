package me.Jared.PlaceHolders;


import org.bukkit.entity.Player;

import me.Jared.Stats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class StatsExpansion extends PlaceholderExpansion
{

	@Override
	public String getAuthor()
	{
		return "Jared";
	}

	@Override
	public String getIdentifier()
	{
		return "Stats";
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
		if(params.equals("rank"))
		{
			return Stats.getInstance().getRankManager().getRank(player.getUniqueId()).getDisplay();
		}
		if(params.equals("kills"))
		{
			return Stats.getInstance().getConfig().getString(player.getUniqueId() + ".kills");
		}
		if(params.equals("deaths"))
		{
			return Stats.getInstance().getConfig().getString(player.getUniqueId() + ".deaths");
		}
		if(params.equals("kdr"))
		{
			
			double kills = Stats.getInstance().getConfig().getInt(player.getUniqueId() + ".kills");
			double deaths = Stats.getInstance().getConfig().getInt(player.getUniqueId() + ".deaths");

			double kdr;
			if (deaths < 1.0D) {
				kdr = kills;
			} else {
				kdr = kills / deaths;
			}

			kdr = Math.round(kdr * 100.0D) / 100.0D;
			return String.valueOf(kdr);
		}
		
		if(params.equals("gems"))
		{
			return Stats.getInstance().getConfig().getString(player.getUniqueId() + ".gems");
		}
		
		if(params.equals("zombiekills"))
		{
			return Stats.getInstance().getConfig().getString(player.getUniqueId() + ".zombiekills");
		}
		if(params.equals("highks"))
		{
			return Stats.getInstance().getConfig().getString(player.getUniqueId() + ".highks");
		}
		if(params.equals("elorank"))
		{
			return Stats.getInstance().getConfig().getString(player.getUniqueId() + ".elo");
		}
		
		return null;
	}
	
}

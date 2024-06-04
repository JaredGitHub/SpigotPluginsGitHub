package me.Jared.Ranks;

import org.bukkit.ChatColor;

public enum Rank
{
	DEFAULT(ChatColor.GRAY + "[DEFAULT] ", new String[] {"ranks.default"}),
	VIP(ChatColor.GREEN + "[VIP] ", new String[] {"ranks.vip"}),
	VIPPLUS(ChatColor.DARK_GREEN + "[VIP+] ", new String[] {"ranks.vipplus"}),
	MVP(ChatColor.AQUA + "[MVP] ", new String[] {"ranks.mvp"}),
	MVPPLUS(ChatColor.BLUE + "[MVP+] ", new String[] {"ranks.mvpplus"}),
	OWNER(ChatColor.DARK_RED + "[OWNER] ", new String[] {"ranks.owner"});

	private String display;
	private String[] permissions;
	
	Rank(String display, String[] permissions)
	{
		this.display = display;
		this.permissions = permissions;
	}
	
	public String getDisplay()
	{
		return display;
	}
	
	public String[] getPermissions()
	{
		return permissions;
	}
}

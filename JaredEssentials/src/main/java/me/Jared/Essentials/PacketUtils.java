package me.Jared.Essentials;

import org.bukkit.entity.Player;

public class PacketUtils
{
	public static void sendTabHF(Player player, String header, String footer)
	{
		player.setPlayerListFooter(footer);
		player.setPlayerListHeader(header);
	}
}
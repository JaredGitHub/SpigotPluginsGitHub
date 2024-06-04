package me.jared.grapple;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GrappleCooldownTask extends BukkitRunnable
{

	private int counter;
	private Player p;

	public GrappleCooldownTask(Player p, int counter)
	{
		this.p = p;
		this.counter = counter;
		if (counter <= 0) {
			throw new IllegalArgumentException("counter must be greater than 0");
		} else {
			this.counter = counter;
		}
	}
	@Override
	public void run()
	{
		if(counter > 0)
		{
			sendActionBar(p, getProgressBar());
		}
		else
		{
			sendActionBar(p, ChatColor.GREEN + "Grappling hook ready!");
			this.cancel();
		}

	}
	public String getProgressBar()
	{
		StringBuilder reload = new StringBuilder();
	      int scale = 40;
	      int bars = Math.round((scale - counter-- * scale / (3*20)));
	      for (int i = 0; i < bars; i++)
	        reload.append(ChatColor.GREEN + "|"); 
	      int left = scale - bars;
	      for (int ii = 0; ii < left; ii++)
	        reload.append(ChatColor.RED + "|"); 
	      if (!reload.toString().contains(ChatColor.RED +""))
	        return ChatColor.GREEN + ""; 
	      return reload.toString();
	}

	public void sendActionBar(Player player, String message)
	{
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}
}

package me.Jared.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KillstreakFiveEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	Player player;

	public KillstreakFiveEvent(Player p)
	{
		this.player = p;
	}

	public Player getPlayer()
	{
		return player;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
	
}

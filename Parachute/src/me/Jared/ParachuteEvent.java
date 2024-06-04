package me.Jared;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ParachuteEvent extends Event
{

	private static final HandlerList handlers = new HandlerList();

	Player parachuter;

	public ParachuteEvent(Player p)
	{
		this.parachuter = p;
	}

	public Player getPlayer()
	{
		return parachuter;
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

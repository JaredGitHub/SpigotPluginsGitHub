package me.Jared.warzPunishExpansion;

import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarzPunishExpansion extends JavaPlugin implements Listener
{

	@Override
	public void onEnable()
	{
		// Register the event listener
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable()
	{
		// Any cleanup logic if needed
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		// Check if the player is in combat using CombatLogX API
		player.sendMessage("YOU QUIT");
	}
}

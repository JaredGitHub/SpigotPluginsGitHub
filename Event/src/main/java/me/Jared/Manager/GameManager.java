package me.Jared.Manager;

import me.Jared.Commands.EventCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Event;
import me.Jared.GameState;
import me.Jared.Runnable.Countdown;

public class GameManager
{
	private final Event plugin;
	private final PlayerManager playerManager;
	private Countdown countdown;

	private GameState gameState = GameState.INACTIVE;

	public GameManager(Event plugin)
	{
		this.plugin = plugin;

		this.playerManager = new PlayerManager(this);
	}

	public Event getPlugin()
	{
		return plugin;
	}

	public void setGameState(GameState gameState)
	{
		if(this.gameState == gameState) return;

		this.gameState = gameState;

		switch(gameState)
		{

		case RECRUITING:

			ConfigManager.clearTeams();
			playerManager.teleportPlayers(ConfigManager.getLobbySpawn());

			for(Player player : Bukkit.getOnlinePlayers())
			{
				if(ConfigManager.getTeam(player) != null)
				{
					playerManager.setPlayerInGame(player);
				}
			}

			break;
		case INACTIVE:
			if(this.countdown != null) this.countdown.cancel();
			
			Bukkit.broadcastMessage(ChatColor.WHITE + "Event is now done!");
			ConfigManager.clearTeams();
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak t");

			for(Player player : playerManager.getPlayers())
			{
				player.getInventory().clear();
				player.teleport(player.getWorld().getSpawnLocation());
			}
			playerManager.getPlayers().clear();
			getPlayerManager().removePlayers();
			ConfigManager.clearTeams();
			break;
		case LIVE:
			if(this.countdown != null) this.countdown.cancel();
			break;
		case COUNTDOWN:
			
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak f");

			EventCommands.teleportPlayers(playerManager);

			var teams = ConfigManager.getTeams();
			Bukkit.broadcastMessage(ChatColor.WHITE + "" + ChatColor.BOLD 
					+ "IT IS " + ConfigManager.getTeamName(teams.get(0)) 
					+ " VS. " + ConfigManager.getTeamName(teams.get(1)) + "!!!!");

			this.countdown = new Countdown(this);
			this.countdown.runTaskTimerAsynchronously(plugin, 0, 20);

			break;
		case WINNING:

			ConfigManager.clearTeams();
			Bukkit.broadcastMessage(ChatColor.GREEN + "Team " + ChatColor.GOLD +  ConfigManager.getTeam(playerManager.getPlayers().get(0)) + ChatColor.GREEN + " has won the event!");
			for(Player player : playerManager.getPlayers())
			{
				player.setSneaking(true);
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givegems " + player.getName() + " 250");
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

				player.sendTitle(ChatColor.GREEN + "You won!", null, 20, 20, 20);
				
				//Add win to the config!!
				plugin.getConfig().set(player.getUniqueId() + ".wins", plugin.getConfig().getInt(player.getUniqueId() + ".wins") + 1);
				plugin.saveConfig();
			}

			Bukkit.getScheduler().runTaskLater(Event.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					for(Player player : Bukkit.getOnlinePlayers())
					{
						player.teleport(player.getWorld().getSpawnLocation());
						player.getInventory().clear();
					}
					setGameState(GameState.INACTIVE);
				}
			},100l);

			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kill @e[type=item]");
		default:
			break;
		}
	}

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	public GameState getGameState()
	{
		return gameState;
	}

}

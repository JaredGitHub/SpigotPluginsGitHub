package me.Jared.Manager;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Jared.Deathmatch;
import me.Jared.GameState;
import me.Jared.Kits.Main;
import me.Jared.Managers.KitManager;
import me.Jared.runnable.Countdown;
import me.Jared.runnable.GameTimer;

public class GameManager
{

	private final Deathmatch plugin;
	private final PlayerManager playerManager;
	private Countdown countdown;
	private GameTimer gameTimer;

	private GameState gameState = GameState.WAITING;

	public GameManager(Deathmatch plugin)
	{
		this.plugin = plugin;

		this.playerManager = new PlayerManager(this);

	}
	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}
	public Deathmatch getPlugin()
	{
		return plugin;
	}

	private Location spawnRadius(Location loc, int radius)
	{
		Location center = loc;
		Random rand = new Random();
		double angle = rand.nextDouble()*360; //Generate a random angle
		double x = center.getX() + (rand.nextDouble()*radius*Math.cos(Math.toRadians(angle))); // x
		double z = center.getZ() + (rand.nextDouble()*radius*Math.sin(Math.toRadians(angle))); // z
		Location newloc = new Location(Bukkit.getWorld("world"), x, center.getY(), z);
		newloc.setYaw(center.getYaw());
		newloc.setPitch(center.getPitch());
		return newloc;
	}

	public void setGameState(GameState gameState)
	{
		if((gameState == GameState.LIVE && gameState == GameState.COUNTDOWN) || (this.gameState == gameState))
		{
			return;
		}

		this.gameState = gameState;

		switch(gameState)
		{
		case LIVE:
			if(this.countdown != null)
			{
				this.countdown.cancel();
			}

			GameTimer gameTimer = new GameTimer(this);
			gameTimer.runTaskTimer(this.getPlugin(), 0, 20);

			playerManager.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Be the last one standing!");

			break;
		case COUNTDOWN:

			Bukkit.broadcastMessage(ChatColor.GREEN + "Team deathmatch has started!");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak f");
			for(Player player : playerManager.getPlayers())
			{

				Location location = ConfigManager.getGameSlotLocation(ConfigManager.getTeam(player));

				player.teleport(spawnRadius(location, 3));

				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.setHealth(20);

				if (Main.getInstance().getConfig().get("PlayerUniqueID." + player.getUniqueId()) != null)
				{
					KitManager.playerCustomHotBar(player);
					KitManager.diamondKit(player);
					KitManager.giveAmmo(player);
				} else
				{
					KitManager.diamondKit(player);
					for (int i = 0; i < 9; i++)
					{
						KitManager.defaultHotBar(player.getInventory());
					}
					player.getInventory().addItem(new ItemStack[]
							{ KitManager.sniperAmmo(128) });
					player.getInventory().addItem(new ItemStack[]
							{ KitManager.shotgunAmmo(128) });
					player.getInventory().addItem(new ItemStack[]
							{ KitManager.autoAmmo(128) });
				}
			}


			this.countdown = new Countdown(this);
			this.countdown.runTaskTimerAsynchronously(plugin, 0, 20);

			break;
		case RECRUITING:
			break;
		case WAITING:

			if(this.countdown != null)
			{
				this.countdown.cancel();
			}
			if(this.gameTimer != null)
			{
				this.gameTimer.cancel();
			}

			//If the winning team is 0 then it is a tie if not then it is the team with the most kills
			if(ConfigManager.getWinningTeam() == 0)
			{
				Bukkit.broadcastMessage(ChatColor.GREEN + "Tied with " + ConfigManager.getKills(1) + " kills!");
			}
			else
			{

				Bukkit.broadcastMessage(ChatColor.GREEN + "Team " + ConfigManager.getWinningTeam()
				+ " has won the game with " + ConfigManager.getKills(ConfigManager.getWinningTeam()) + " kills!");
			}

			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "boostpad clear");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ladder clear");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "web clear");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak t");

			//Remove all players from the game and send them to spawn
			for(Player player : this.getPlayerManager().getPlayers())
			{
				//Give every player on the winning team 250 gems

				player.getInventory().setArmorContents(null);
				player.getInventory().clear();
				player.teleport(player.getWorld().getSpawnLocation());

				player.sendTitle(ChatColor.GOLD + "Game Over", ChatColor.BLUE + "GG!", 1, 20, 1);
				player.setLevel(0);

				if(ConfigManager.getWinners(ConfigManager.getWinningTeam()).contains(player.getUniqueId().toString()))
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givegems " + player.getName() + " 250");

					//add wins to the config
					plugin.getConfig().set(player.getUniqueId() + ".wins", plugin.getConfig().getInt(player.getUniqueId() + ".wins") + 1);
					plugin.saveConfig();
				}
			}

			playerManager.getPlayers().clear();
			ConfigManager.tdmReset();
			break;
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

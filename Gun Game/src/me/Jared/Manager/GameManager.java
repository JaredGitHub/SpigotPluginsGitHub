package me.Jared.Manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Jared.GameState;
import me.Jared.GunGame;
import me.Jared.runnable.Countdown;

public class GameManager
{

	private final GunGame plugin;
	private final PlayerManager playerManager;
	private Countdown countdown;

	private GameState gameState = GameState.WAITING;

	public GameManager(GunGame plugin)
	{
		this.plugin = plugin;

		this.playerManager = new PlayerManager(this);

	}
	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}
	public GunGame getPlugin()
	{
		return plugin;
	}

	public void setGameState(GameState gameState)
	{
		if(gameState == GameState.LIVE && gameState == GameState.COUNTDOWN) return;
		if(this.gameState == gameState) return;

		this.gameState = gameState;

		switch(gameState)
		{
		case LIVE:
			if(this.countdown != null) this.countdown.cancel();

			playerManager.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Be the last one standing!");

			break;
		case COUNTDOWN:

			Bukkit.broadcastMessage(ChatColor.GREEN + "GunGame has started!");
			
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak f");
			
			for(Player player : playerManager.getPlayers())
			{
				player.teleport(ConfigManager.getGameSlotLocation(playerManager.getPlayerCount()));
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.setHealth(20);

				player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
				player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
				player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
				
				playerManager.giveGuns(player, 0);
				playerManager.getKills().put(player.getUniqueId(), 0);

			}

			this.countdown = new Countdown(this);
			this.countdown.runTaskTimerAsynchronously(plugin, 0, 20);

			break;
		case RECRUITING:
			break;
		case WAITING:
			if(this.countdown != null) this.countdown.cancel();

			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "killstreak t");
			
			for(Player player : playerManager.getPlayers())
			{
				player.getInventory().clear();
				player.teleport(player.getWorld().getSpawnLocation());
			}
			playerManager.getPlayers().clear();
			getPlayerManager().removePlayers();
			break;
		case WINNING:

			for(Player player : playerManager.getPlayers())
			{
				if(player !=null)
				{
					Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won GunGame!");
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givegems " + player.getName() + " 250");
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kill @e[type=item]");
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

					player.getInventory().setArmorContents(null);
					player.getInventory().clear();
					player.teleport(player.getWorld().getSpawnLocation());
					
					//Add win to config
					plugin.getConfig().set(player.getUniqueId() + ".wins", plugin.getConfig().getInt(player.getUniqueId() + ".wins") + 1);
					plugin.saveConfig();
					
				}
			}
			this.setGameState(GameState.WAITING);
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

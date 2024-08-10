package me.Jared.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;

public class GunGameCommand implements CommandExecutor
{
	private GameManager gameManager;

	public GunGameCommand(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("setmaxplayers"))
			{
				if(sender.hasPermission("gungame"))
				{
					if(gameManager.getGameState() == GameState.WAITING)
					{
						for(Player players : gameManager.getPlayerManager().getPlayers())
						{
							players.teleport(Bukkit.getWorld("world").getSpawnLocation());
						}
						gameManager.getPlayerManager().removePlayers();
						for(Player p : gameManager.getPlayerManager().getPlayers())
						{
							p.teleport(p.getWorld().getSpawnLocation());
						}
						try
						{
							int maxPlayers = Integer.parseInt(args[1]);

							sender.sendMessage(ChatColor.GREEN + "Set max players to " + maxPlayers);
							sender.sendMessage(
									ChatColor.DARK_GREEN + "Make sure to set your locations with /gg set [number]");

							ConfigManager.setPlayersNeeded(maxPlayers);
							return true;
						} catch(Exception e)
						{
							sender.sendMessage(ChatColor.RED + "Make sure you use an integer!");
						}
					} else
					{
						sender.sendMessage(ChatColor.RED + "Game in session!");
						return true;
					}
				}
			}

			if(args[0].equalsIgnoreCase("setcountdown"))
			{
				if(sender.hasPermission("gungame"))
				{
					if(gameManager.getGameState() == GameState.WAITING)
					{
						gameManager.getPlayerManager().removePlayers();
						try
						{
							int countdownTime = Integer.parseInt(args[1]);
							sender.sendMessage(ChatColor.GREEN + "Set countdown time to " + countdownTime);

							ConfigManager.setCountdown(countdownTime);
						} catch(Exception e)
						{
							sender.sendMessage(ChatColor.RED + "Make sure you use an integer!");
						}
					} else
					{
						sender.sendMessage(ChatColor.RED + "Game in session!");
						return true;
					}
				}
			}
		}

		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("gg") || cmd.getName().equalsIgnoreCase("gungame"))
			{
				if(args.length == 0)
				{
					player.sendMessage(ChatColor.GRAY + "Usage: /gg [join, leave]");

				}
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("join"))
					{

						Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

						if(block.getType() == Material.AIR)
						{
							player.sendMessage(ChatColor.RED + "Stay still");
							return true;
						}

						if(gameManager.getGameState() == GameState.LIVE)
						{
							player.sendMessage(ChatColor.RED + "Game is currently live so you can't join yet!");
							return true;
						}

						if(gameManager.getPlayerManager().getPlayers().contains(player))
						{
							player.sendMessage(ChatColor.RED + "You're already in!");
							return true;
						}

						// Allow the player to join the game
						gameManager.getPlayerManager().setPlayerInGame(player);
						gameManager.setGameState(GameState.WAITING);

						int playersNeeded = ConfigManager.getPlayersNeeded()
								- gameManager.getPlayerManager().getPlayerCount();

						gameManager.getPlayerManager().sendMessage(ChatColor.GRAY + "Waiting for " + ChatColor.UNDERLINE
								+ playersNeeded + ChatColor.RESET + ChatColor.GRAY + " more players to join the game!");

						Location location = ConfigManager.getLobbySpawn();
						player.teleport(location);
						player.setRespawnLocation(location);

						if(gameManager.getPlayerManager().getPlayers().size() > ConfigManager.getPlayersNeeded())
						{
							player.sendMessage(ChatColor.RED + "Too many players in game!");
							return true;
						}

						if(gameManager.getPlayerManager().getPlayers().size() == ConfigManager.getPlayersNeeded())
						{
							gameManager.setGameState(GameState.COUNTDOWN);
							gameManager.getPlayerManager().teleportPlayerInGame();
						}
					}

					if(args[0].equalsIgnoreCase("leave"))
					{
						if(gameManager.getPlayerManager().getPlayers().contains(player)
								&& gameManager.getGameState() != GameState.RECRUITING)
						{
							gameManager.getPlayerManager().removePlayer(player);
							Location loc = (Location) player.getWorld().getSpawnLocation();
							player.teleport(loc);
							player.getInventory().clear();
							player.getActivePotionEffects().clear();
							player.sendMessage(ChatColor.RED + "You are now out of the game!");

							if(gameManager.getPlayerManager().getPlayers().size() == 1
									&& gameManager.getGameState() == GameState.LIVE)
							{
								gameManager.setGameState(GameState.WINNING);
							}

						} else
						{
							player.sendMessage(ChatColor.RED + "You are not in game noob!");
						}
					}

					if(args[0].equalsIgnoreCase("setlobby"))
					{
						if(player.hasPermission("gungame"))
						{
							player.sendMessage(ChatColor.GREEN + "Lobby set to your location");

							ConfigManager.setLobbySpawn(player.getLocation());
							return true;
						}
					}
				}

				if(args.length == 2)
				{

					if(args[0].equalsIgnoreCase("set"))
					{
						if(!player.hasPermission("gungame"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission NOOOOB!!!");
							return true;
						}

						int id;
						try
						{
							id = Integer.parseInt(args[1]);
						} catch(NumberFormatException e)
						{

							Bukkit.broadcastMessage(ChatColor.GREEN + "Make sure your second argument is a number!");
							return false;
						}
						if(id == 0)
						{
							player.sendMessage(ChatColor.RED + "Zero is not a number noob!");
							return true;
						}

						player.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.RESET + id
								+ ChatColor.GREEN + " to your current location!");
						ConfigManager.setGameSlot(player.getLocation(), id);

						return true;
					}
				}
				return true;
			}
		}
		return true;
	}
}
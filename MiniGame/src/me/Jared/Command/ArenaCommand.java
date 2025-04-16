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

public class ArenaCommand implements CommandExecutor
{
	private GameManager gameManager;

	public ArenaCommand(GameManager gameManager)
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
				if(args.length == 2)
				{
					if(gameManager.getGameState() == GameState.WAITING)
					{
						try
						{
							int number = Integer.parseInt(args[1]);

							if(number <= 1)
							{
								sender.sendMessage(ChatColor.RED + "Must be 2 or more noob");
								return true;
							}
							ConfigManager.setPlayersNeeded(number);

							for(Player players : gameManager.getPlayerManager().getPlayers())
							{
								players.teleport(Bukkit.getWorld("world").getSpawnLocation());
							}
							gameManager.getPlayerManager().getPlayers().clear();
							Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully set max players to " + number);
						} catch(Exception e)
						{
							sender.sendMessage(ChatColor.RED + "Make sure your second argument is a number!");
							return true;
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "Game is live");
						return true;
					}
				} else
				{
					sender.sendMessage(ChatColor.GRAY + "Usage: /sg setmaxplayers [number]");
					return true;
				}
			}

			if(args[0].equalsIgnoreCase("setcountdown"))
			{
				if(args.length == 2)
				{
					try
					{
						int number = Integer.parseInt(args[1]);
						ConfigManager.setCountdown(number);

						Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully set countdown to " + number);
					} catch(Exception e)
					{
						sender.sendMessage(ChatColor.RED + "Make sure your second argument is a number!");
						return true;
					}
				} else
				{
					sender.sendMessage(ChatColor.GRAY + "Usage: /sg setmaxplayers [number]");
					return true;
				}
			}
		}

		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("sg") || cmd.getName().equalsIgnoreCase("survivalGames"))
			{
				if(args.length == 0)
				{
					player.sendMessage(ChatColor.GRAY + "Usage: /sg [join, leave]");

				}
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("join"))
					{
						if(gameManager.getGameState() == GameState.LIVE
								|| gameManager.getGameState() == GameState.WINNING)
						{
							player.sendMessage(ChatColor.RED + "Game is currently live so you can't join yet!");
							return true;
						}

						if(gameManager.getPlayerManager().getPlayers().contains(player))
						{
							player.sendMessage(ChatColor.RED + "You're already in!");
							return true;
						}

						if(gameManager.getPlayerManager().getPlayers().size() >= ConfigManager.getPlayersNeeded())
						{
							player.sendMessage(ChatColor.RED + "Too many players in game!");
							return true;
						}

						if(gameManager.getPlayerManager().getPlayers().size() == ConfigManager.getPlayersNeeded() - 1)
						{
							gameManager.getPlayerManager().setPlayerInGame(player);

							gameManager.getPlayerManager().teleportPlayerInGame();
							gameManager.setGameState(GameState.COUNTDOWN);
						} else
						{
							Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

							if(block.getType() == Material.AIR)
							{
								player.sendMessage(ChatColor.RED + "Stay still");
								return true;
							}

							// Allow the player to join the game
							gameManager.getPlayerManager().setPlayerInGame(player);
							gameManager.setGameState(GameState.RECRUITING);


							int playersNeeded =
									ConfigManager.getPlayersNeeded() - gameManager.getPlayerManager().getPlayerCount();

							gameManager.getPlayerManager().sendMessage(
									ChatColor.GRAY + "Waiting for " + ChatColor.UNDERLINE + playersNeeded
											+ ChatColor.RESET + ChatColor.GRAY + " more players to join the game!");

							Location location = ConfigManager.getLobbySpawn();
							player.teleport(location);
							player.setRespawnLocation(location);
						}
					}

					if(args[0].equalsIgnoreCase("leave"))
					{
						if(gameManager.getPlayerManager().getPlayers().contains(player))
						{
							gameManager.getPlayerManager().removePlayer(player);

							player.teleport(player.getWorld().getSpawnLocation());
							player.sendMessage(ChatColor.RED + "You are now out of the game!");

							if(gameManager.getPlayerManager().getPlayers().size() < 1)
							{
								gameManager.setGameState(GameState.WAITING);
							}
						} else
						{
							player.sendMessage(ChatColor.RED + "You are not in game noob!");
						}
					}

					if(args[0].equalsIgnoreCase("setlobbyspawn"))
					{
						if(player.hasPermission("hg"))
						{
							Location loc = player.getLocation();

							player.sendMessage(ChatColor.GREEN
									+ "Congratulations you set the lobby spawn to your current location!");
							ConfigManager.setLobbySpawn(loc);
							return true;
						} else
						{
							player.sendMessage(ChatColor.RED + "You are not op you big noob!");
							return true;
						}
					}
				}

				if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("set"))
					{
						if(!player.hasPermission("hg"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission!!!");
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

						if(id > ConfigManager.getPlayersNeeded())
						{
							player.sendMessage(
									ChatColor.RED + "Max players is " + ConfigManager.getPlayersNeeded() + "!");
							return true;
						}
						if(id == 0)
						{
							player.sendMessage(ChatColor.RED + "Zero is not a number noob!");
							return true;
						}

						player.sendMessage(
								ChatColor.GREEN + "Successfully set " + ChatColor.RESET + id + ChatColor.GREEN
										+ " to your current location!");
						ConfigManager.setGameSlot(player.getLocation(), id);
					}
				}
			}
		}

		return true;
	}

}
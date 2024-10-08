package me.Jared.Command;

import java.util.ArrayList;

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

import me.Jared.Deathmatch;
import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;

public class DeathmatchCommand implements CommandExecutor
{

	private GameManager gameManager;

	public DeathmatchCommand(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("tdm"))
			{
				if(args.length == 0)
				{
					player.sendMessage(ChatColor.GRAY + "Usage: /tdm [join, leave, setlobby, setspawn, set]");
					return true;
				}
				if (args.length == 1)
				{

					if (args[0].equalsIgnoreCase("leave"))
					{

						if (gameManager.getPlayerManager().getPlayers().contains(player))
						{
							ArrayList<String> list =
									new ArrayList<>(Deathmatch.getInstance().getConfig().getStringList
											("tdm.team." + ConfigManager.getTeam(player) + ".players"));
							ConfigManager.setKills(ConfigManager.getTeam(player), 0);

							for(int i = 0; i < list.size(); i++)
							{
								if(list.contains(player.getUniqueId().toString()))
								{
									list.remove(i);
								}
							}

							Deathmatch.getInstance().getConfig().set("tdm." +player.getUniqueId().toString(), null);

							Deathmatch.getInstance().getConfig().set("tdm.team." + ConfigManager.getTeam(player) + ".players", list);
							Deathmatch.getInstance().saveConfig();

							if(gameManager.getGameState() == GameState.LIVE)
							{
								if(gameManager.getPlayerManager().getPlayers().contains(player))
								{
									ConfigManager.setKills(ConfigManager.getTeam(player), 0);
									gameManager.setGameState(GameState.WAITING);
								}
							}

							gameManager.getPlayerManager().removePlayer(player);

							player.teleport(player.getWorld().getSpawnLocation());
							player.sendMessage(ChatColor.RED + "You are now out of the game!");

							if(gameManager.getPlayerManager().getPlayerCount() < 1)
							{
								gameManager.setGameState(GameState.WAITING);
								ConfigManager.tdmReset();
							}
						} else
						{
							player.sendMessage(ChatColor.RED + "You are not in game noob!");
						}
					}

					if(args[0].equalsIgnoreCase("setlobby"))
					{
						if(player.hasPermission("tdm"))
						{
							player.sendMessage(ChatColor.GREEN + "Lobby set to your location");

							ConfigManager.setLobbySpawn(player.getLocation());

						}
					}

					if(args[0].equalsIgnoreCase("setspawn"))
					{
						if(player.hasPermission("tdm"))
						{
							player.sendMessage(ChatColor.GREEN + "Spawn set to your location");

							ConfigManager.setSpawn(player.getLocation());

						}
					}
				}
				if (args[0].equalsIgnoreCase("join"))
				{
					Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

					if(block.getType() == Material.AIR)
					{
						player.sendMessage(ChatColor.RED + "Stay still");
						return true;
					}

					if(args.length == 2)
					{
						if (gameManager.getGameState() == GameState.LIVE)
						{
							player.sendMessage(ChatColor.RED + "Game is currently live so you can't join yet!");
							return true;
						}

						if(gameManager.getPlayerManager().getPlayers().contains(player))
						{
							player.sendMessage(ChatColor.RED + "You're already in!");
							return true;
						}

						if (gameManager.getPlayerManager().getPlayers().size() > ConfigManager.getPlayersNeeded())
						{
							player.sendMessage(ChatColor.RED + "Too many players in game!");
							return true;
						}


						try
						{
							int team = Integer.parseInt(args[1]);
							if(team > 2)
							{
								player.sendMessage(ChatColor.GRAY + "Usage: /tdm join 1 or 2");
								return true;
							}

							int slots = ConfigManager.getPlayersNeeded()/2;
							int team1Players = 0;
							int team2Players = 0;

							for(Player p : gameManager.getPlayerManager().getPlayers())
							{
								if(ConfigManager.getTeam(p) == 1)
								{
									team1Players++;
								}
								else if(ConfigManager.getTeam(p) == 2)
								{
									team2Players++;
								}
							}

							if(team1Players == slots && team == 1)
							{
								player.sendMessage(ChatColor.RED + "Team 1 is full!" + " [" + team1Players + "/" + slots + "]");
								return true;
							}
							if(team2Players == slots && team == 2)
							{
								player.sendMessage(ChatColor.RED + "Team 2 is full!" + " [" + team2Players + "/" + slots + "]");
								return true;
							}

							if(team == 1)
							{
								player.sendMessage(ChatColor.GREEN + "Joined team " + team + " [" + (team1Players+1) + "/" + slots + "]");
							}
							if(team == 2)
							{
								player.sendMessage(ChatColor.GREEN + "Joined team " + team + " [" + (team2Players+1) + "/" + slots + "]");
							}


							// Allow the player to join the game'
							gameManager.getPlayerManager().setPlayerInGame(player);
							Bukkit.getConsoleSender().sendMessage("Added player to the game " + player.getName());
							gameManager.setGameState(GameState.RECRUITING);

							ConfigManager.setTeam(team, player);
							ConfigManager.setTeam(player,team);

							if (gameManager.getPlayerManager().getPlayers().size() == ConfigManager.getPlayersNeeded())
							{
								gameManager.setGameState(GameState.COUNTDOWN);
								return true;
							}
							else
							{
								Location location = ConfigManager.getLobbySpawn();
								player.teleport(location);
								player.setRespawnLocation(location, false);
							}


						} catch (NumberFormatException e)
						{
							player.sendMessage(ChatColor.RED + "Make sure your second argument is either 1 or 2!");
							Bukkit.getConsoleSender().sendMessage("Make sure your second argument is either 1 or 2!");
						}
					}
					else
					{
						player.sendMessage(ChatColor.GRAY + "Usage: /tdm join 1 or 2");
					}
				}

				if (args.length == 2)
				{

					if(args[0].equalsIgnoreCase("setmaxplayers"))
					{
						if(player.hasPermission("tdm"))
						{
							if(gameManager.getGameState() == GameState.WAITING)
							{
								try
								{
									int maxPlayers = Integer.parseInt(args[1]);

									for(Player players : gameManager.getPlayerManager().getPlayers())
									{
										players.teleport(Bukkit.getWorld("world").getSpawnLocation());
									}
									gameManager.getPlayerManager().getPlayers().clear();

									if (maxPlayers % 2 == 0)
									{

										player.sendMessage(ChatColor.GREEN + "Set max players to " + maxPlayers);
										player.sendMessage(ChatColor.GREEN + "Make sure to set your locations with /tdm set [number]");
										ConfigManager.setPlayersNeeded(maxPlayers);
									}
									else
									{
										player.sendMessage(ChatColor.RED + "Make sure it is an even number!");
									}
								}
								catch (Exception e)
								{
									player.sendMessage(ChatColor.RED + "Make sure you use an integer!");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Game in session!");
								return true;
							}
						}
					}

					if(args[0].equalsIgnoreCase("setcountdown"))
					{
						if(player.hasPermission("tdm"))
						{
							if(gameManager.getGameState() == GameState.WAITING)
							{
								try
								{
									int countdownTime = Integer.parseInt(args[1]);
									player.sendMessage(ChatColor.GREEN + "Set countdown time to " + countdownTime);
									ConfigManager.setCountdown(countdownTime);
								}
								catch (Exception e)
								{
									player.sendMessage(ChatColor.RED + "Make sure you use an integer!");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Game in session!");
								return true;
							}
						}
					}

					if(args[0].equalsIgnoreCase("setGametime"))
					{
						if(player.hasPermission("tdm"))
						{
							if(gameManager.getGameState() == GameState.WAITING)
							{
								try
								{
									int gameTime = Integer.parseInt(args[1]);
									player.sendMessage(ChatColor.GREEN + "Set game time to " + gameTime);
									ConfigManager.setDeathmatchTime(gameTime);
								}
								catch (Exception e)
								{
									player.sendMessage(ChatColor.RED + "Make sure you use an integer!");
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Game in session!");
								return true;
							}
						}
					}

					if (args[0].equalsIgnoreCase("set"))
					{
						if (!player.hasPermission("tdm"))
						{
							player.sendMessage(ChatColor.RED + "You do not have permission NOOOOB!!!");
							return true;
						}

						if(args[1] == null)
						{
							player.sendMessage(ChatColor.RED + "Usage: /tdm set [number]");
						}

						int id;
						try
						{
							id = Integer.parseInt(args[1]);

							if (id > 2)
							{
								player.sendMessage(ChatColor.RED + "There are only two teams!");
								return true;
							}
							if (id == 0)
							{
								player.sendMessage(ChatColor.RED + "Zero is not a number noob!");
								return true;
							}

							player.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.RESET + id
									+ ChatColor.GREEN + " to your current location!");
							ConfigManager.setGameSlot(player.getLocation(), id);
						} catch (NumberFormatException e)
						{

							player.sendMessage(ChatColor.RED + "Make sure your second argument is a number!");
							return true;
						}
					}
				}
				return true;
			}
		}

		return true;
	}

}

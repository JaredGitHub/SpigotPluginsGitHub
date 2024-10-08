package me.Jared.Command;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Duels;
import me.Jared.Manager.ConfigManager;
import me.Jared.Managers.KitManager;
import me.Jared.Menus.MapMenu;
import me.Jared.Menus.PlayerListMenu;

public class DuelCommands implements CommandExecutor
{
	public static Player duelPlayer;

	public static HashMap<Player, Integer> betAmount = new HashMap<Player, Integer>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		// Have commands such as /duel, /duel accept, /duel deny

		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if(cmd.getName().equalsIgnoreCase("duel"))
			{
				if(block.getType() == Material.AIR)
				{
					player.sendMessage(ChatColor.RED + "Stay still");
					return true;
				}
				if(player.hasPermission("duels"))
				{
					if(args.length >= 1)
					{
						if(args[0].equalsIgnoreCase("list"))
						{
							ArrayList<String> list = new ArrayList<String>(
									Duels.getInstance().getConfig().getStringList("maps"));

							player.sendMessage(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "List of the duel maps:");
							for(String string : list)
							{
								player.sendMessage(ChatColor.GOLD + "-" + string);
							}
							return true;
						}

						if(args[0].equalsIgnoreCase("set"))
						{
							if(args.length == 3)
							{
								try
								{
									int number = Integer.parseInt(args[2]);

									if(number == 1 || number == 2)
									{
										String mapName = args[1];
										ConfigManager.setMapLocation(player.getLocation(), mapName, number);
										player.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.RESET
												+ number + ChatColor.GREEN + " to your current location");

									} else
									{
										player.sendMessage(
												ChatColor.RED + "Make sure that your second argument is 1 or 2!");
										return true;
									}

								} catch(NumberFormatException e)
								{
									player.sendMessage(
											ChatColor.RED + "Make sure that your second argument is 1 or 2!");
								}
								return true;
							} else
							{
								player.sendMessage(ChatColor.GRAY + "Usage: /duel set [mapName] [1 or 2]");
								return true;
							}
						}

						if(args[0].equalsIgnoreCase("remove"))
						{
							try
							{
								ArrayList<String> list = new ArrayList<String>(
										Duels.getInstance().getConfig().getStringList("maps"));

								String map = args[1];

								if(list.contains(map))
								{
									list.remove(map);

									Duels.getInstance().getConfig().set("maps", list);
									player.sendMessage(ChatColor.RED + "Successfully removed " + map
											+ " from the list of duel maps!");

									Duels.getInstance().getConfig().set("map." + map, null);
									Duels.getInstance().saveConfig();

									return true;
								}

							} catch(Exception e)
							{
								player.sendMessage(ChatColor.RED
										+ "Make sure your second argument is a map! \nType /duel list to see them all");
								return true;
							}
						}
					}
				}
				if(args.length == 0)
				{
					PlayerListMenu menu = new PlayerListMenu(Duels.getPlayerMenuUtility(player), player);
					menu.open();
					return true;
				}

				else if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("leave"))
					{
						if((MapMenu.playersInDuel.contains(player)) || (betAmount.containsKey(player)))
						{
							int indexMinusOne = (MapMenu.playersInDuel.indexOf(player) - 1);
							int indexPlusOne = (MapMenu.playersInDuel.indexOf(player) + 1);

							boolean inBoundsMinusOne = (indexMinusOne >= 0)
									&& (indexMinusOne < MapMenu.playersInDuel.size());

							boolean inBoundsPlusOne = (indexPlusOne >= 0)
									&& (indexPlusOne < MapMenu.playersInDuel.size());

							if(inBoundsPlusOne)
							{
								Player dueler = MapMenu.playersInDuel.get(indexPlusOne);
								dueler.teleport(dueler.getWorld().getSpawnLocation());
								dueler.getInventory().clear();
								MapMenu.playersInDuel.remove(dueler);
								DuelCommands.betAmount.remove(dueler);
							} else if(inBoundsMinusOne)
							{
								Player dueler = MapMenu.playersInDuel.get(indexMinusOne);
								dueler.teleport(dueler.getWorld().getSpawnLocation());
								dueler.getInventory().clear();
								MapMenu.playersInDuel.remove(dueler);
								DuelCommands.betAmount.remove(dueler);
							}

							// Remove player from hashmaps for dueling
							player.teleport(player.getWorld().getSpawnLocation());
							MapMenu.playersInDuel.remove(player);
							DuelCommands.betAmount.remove(player);

							return true;

						} else
						{
							player.sendMessage(ChatColor.RED + "You are not in a duel noob!");
							return true;
						}
					} else
					{
						try
						{
							Player argPlayer = Bukkit.getPlayer(args[0]);
							duelPlayer = argPlayer;

							if(!(argPlayer.equals(null)) && !(argPlayer.equals(player)))
							{

								if(!betAmount.containsKey(player))
								{
									// Open gui inventory
									MapMenu menu = new MapMenu(Duels.getPlayerMenuUtility(player));
									menu.open();
								} else
								{
									player.sendMessage(ChatColor.RED + "You are in a duel!");
									player.playSound(player, Sound.ENTITY_GHAST_DEATH, 1, 1);
									return true;
								}
							} else
							{
								player.sendMessage(ChatColor.GRAY + "Usage: /duel [player] [betAmount]");
								return true;
							}
						} catch(Exception e)
						{
							player.sendMessage(ChatColor.GRAY + "That player is not online!");
							if(args[0].contains("set"))
							{
								player.sendMessage(ChatColor.GRAY + "Usage: /duel set [mapName] [1 or 2]");
							}
							return true;
						}
					}
				} else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("accept"))
					{
						try
						{
							Player requester = Bukkit.getPlayer(args[1].toString());

							if(!MapMenu.playersInDuel.contains(player) || !betAmount.containsKey(player))
							{
								if(MapMenu.playersInDuel.contains(requester) || betAmount.containsKey(requester))
								{
									MapMenu.playersInDuel.add(player);
									player.getInventory().clear();
									player.getActivePotionEffects().clear();
									player.setHealth(20);
									requester.getInventory().clear();
									requester.getActivePotionEffects().clear();
									requester.setHealth(20);
									
									KitManager.diamondKit(player);
									KitManager.diamondKit(requester);

									// start the game
									player.teleport(ConfigManager
											.getMapLocation(ConfigManager.getMaps().get(MapMenu.getMapNumber()), 1));
									requester.teleport(ConfigManager
											.getMapLocation(ConfigManager.getMaps().get(MapMenu.getMapNumber()), 2));

									Bukkit.getScheduler().runTaskLater(Duels.getInstance(), new Runnable()
									{

										@Override
										public void run()
										{
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "boostpad clear");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "landmines clear");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "web clear");
										}
									}, 5);

									player.sendMessage(ChatColor.GREEN + "You are now in a duel against "
											+ requester.getName() + " with a bet of " + betAmount.get(requester) + "!");

									Bukkit.getServer().broadcastMessage(ChatColor.GREEN + player.getName()
											+ " is now in a duel with " + requester.getName() + "!");

									betAmount.put(player, betAmount.get(requester));
									player.getActivePotionEffects().clear();
									requester.getActivePotionEffects().clear();
								
								} else
								{
									player.sendMessage(ChatColor.RED + "You haven't been invited!");
									return true;
								}
							} else
							{
								player.sendMessage(ChatColor.RED + "You cannot duel while you are in a duel!");
								return true;
							}

						} catch(Exception e)
						{
							player.sendMessage(ChatColor.RED + "Make sure your second argument is a player!");
							return true;
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("deny"))
					{
						try
						{
							Player requester = Bukkit.getPlayer(args[1].toString());
							// Send message to player "Duel denied!"
							player.sendMessage(ChatColor.RED + "Duel denied!");
							requester.sendMessage(ChatColor.RED + "Duel denied by " + player.getName() + "!");

							player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
							requester.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

							requester.teleport(requester.getWorld().getSpawnLocation());

							MapMenu.playersInDuel.remove(requester);
							MapMenu.playersToDuel.remove(player);
							betAmount.remove(player);
							betAmount.remove(requester);

						} catch(Exception e)
						{
							player.sendMessage(ChatColor.RED + "Make sure your second argument is a player!");
							return true;
						}
					}

					/// duel [player] [betAmount]
					try
					{
						int bet = Integer.parseInt(args[1]);

						Player argPlayer = Bukkit.getPlayer(args[0]);
						duelPlayer = argPlayer;

						FileConfiguration statsConfig = Bukkit.getPluginManager().getPlugin("Stats").getConfig();

						if(statsConfig.getInt(player.getUniqueId() + ".gems") >= bet
								&& statsConfig.getInt(argPlayer.getUniqueId() + ".gems") >= bet)
						{
							if(!argPlayer.equals(null) && !argPlayer.equals(player))
							{
								if(!betAmount.containsKey(player) || !(MapMenu.playersInDuel.contains(player)))
								{
									// Put player in hashmap that specifies their bet amount
									betAmount.put(player, bet);
									// Open gui inventory
									MapMenu menu = new MapMenu(Duels.getPlayerMenuUtility(player));
									menu.open();
								} else
								{
									player.sendMessage(ChatColor.RED + "You are in a duel!");
									player.playSound(player, Sound.ENTITY_GHAST_DEATH, 1, 1);
									return true;
								}
							} else
							{
								player.sendMessage(ChatColor.RED + "That player is not online or it is you!!");
								player.playSound(player, Sound.ENTITY_GHAST_DEATH, 1, 1);
								return true;
							}
						} else
						{
							player.sendMessage(ChatColor.RED + "That bet is too much! " + argPlayer.getName()
									+ " only has " + ChatColor.GREEN
									+ statsConfig.getInt(argPlayer.getUniqueId() + ".gems") + ChatColor.RED + " gems! "
									+ "And you have " + ChatColor.GREEN
									+ statsConfig.getInt(player.getUniqueId() + ".gems") + ChatColor.RED + " gems!");

							player.playSound(player, Sound.ENTITY_GHAST_DEATH, 1, 1);
							return true;
						}

					} catch(Exception e)
					{
						e.printStackTrace();
						return true;
					}
				}
			}
			return true;
		} else
		{
			System.out.println("Be a player OKAY!");
		}
		return true;
	}

}

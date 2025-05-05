package me.Jared.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Jared.Duels;
import me.Jared.Manager.ConfigManager;
import me.Jared.Managers.KitManager;
import me.Jared.Menus.MapMenu;
import me.Jared.Menus.PlayerListMenu;

public class DuelCommands implements CommandExecutor, TabCompleter
{
	public static Player duelPlayer;

	public static HashMap<Player, Integer> betAmount = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> mapNumber = new HashMap<Player, Integer>();
	public static ArrayList<Player> playersInDuel = new ArrayList<Player>();
	public static ArrayList<Player> playersToDuel = new ArrayList<Player>();

	FileConfiguration config = Duels.getInstance().getConfig();

	public static int getMapNumber(String mapName)
	{
		var maps = ConfigManager.getMaps();
		for(String map : maps)
		{
			if(map.equals(mapName))
			{
				return maps.indexOf(map);
			}
		}
		return 0;
	}

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
										player.sendMessage(
												ChatColor.GREEN + "Successfully set " + ChatColor.RESET + number
														+ ChatColor.GREEN + " to your current location");

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
				} else if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("leave"))
					{
						if((playersInDuel.contains(player)) || (betAmount.containsKey(player)))
						{
							int indexMinusOne = (playersInDuel.indexOf(player) - 1);
							int indexPlusOne = (playersInDuel.indexOf(player) + 1);

							boolean inBoundsMinusOne = (indexMinusOne >= 0) && (indexMinusOne < playersInDuel.size());

							boolean inBoundsPlusOne = (indexPlusOne >= 0) && (indexPlusOne < playersInDuel.size());

							if(inBoundsPlusOne)
							{
								Player dueler = playersInDuel.get(indexPlusOne);
								dueler.teleport(dueler.getWorld().getSpawnLocation());
								dueler.getInventory().clear();
								playersInDuel.remove(dueler);
								DuelCommands.betAmount.remove(dueler);
							} else if(inBoundsMinusOne)
							{
								Player dueler = playersInDuel.get(indexMinusOne);
								dueler.teleport(dueler.getWorld().getSpawnLocation());
								dueler.getInventory().clear();
								playersInDuel.remove(dueler);
								DuelCommands.betAmount.remove(dueler);
							}

							// Remove player from hashmaps for dueling
							player.teleport(player.getWorld().getSpawnLocation());
							playersInDuel.remove(player);
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
									MapMenu menu = new MapMenu(Duels.getPlayerMenuUtility(player), argPlayer);
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

							if(!playersInDuel.contains(player) || !betAmount.containsKey(player))
							{
								if(playersInDuel.contains(requester) || betAmount.containsKey(requester))
								{
									playersInDuel.add(player);
									player.getInventory().clear();
									player.getActivePotionEffects().clear();
									player.setHealth(20);
									requester.getInventory().clear();
									requester.getActivePotionEffects().clear();
									requester.setHealth(20);

									KitManager.diamondKit(player);
									KitManager.diamondKit(requester);

									// start the game
									player.teleport(ConfigManager.getMapLocation(
											ConfigManager.getMaps().get(mapNumber.get(requester)), 1));

									//Sneak the requester so that if they are flying with a chicken they stop and they can actualy teleport
									requester.setSneaking(true);

									//TEleport the requester after 5 ticks and kill all ground items and boostpads and landmines and walls
									Bukkit.getScheduler().runTaskLater(Duels.getInstance(), new Runnable()
									{

										@Override
										public void run()
										{
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "boostpad clear");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "landmines clear");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "web clear");
											requester.teleport(ConfigManager.getMapLocation(
													ConfigManager.getMaps().get(mapNumber.get(requester)), 2));

										}
									}, 5);

									player.sendMessage(
											ChatColor.GREEN + "You are now in a duel against " + requester.getName()
													+ " with a bet of " + betAmount.get(requester) + "!");

									Bukkit.getServer().broadcastMessage(
											ChatColor.GREEN + player.getName() + " is now in a duel with "
													+ requester.getName() + "!");

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

							playersInDuel.remove(requester);
							playersToDuel.remove(player);
							betAmount.remove(player);
							betAmount.remove(requester);
							mapNumber.remove(player);
							mapNumber.remove(requester);

						} catch(Exception e)
						{
							player.sendMessage(ChatColor.RED + "Make sure your second argument is a player!");
							return true;
						}
					}
				} else if(args.length == 3)
				{

					/// duel [player] [betAmount] [map]
					try
					{
						int bet = Integer.parseInt(args[1]);
						String mapName = args[2];

						Player argPlayer = Bukkit.getPlayer(args[0]);
						duelPlayer = argPlayer;

						FileConfiguration statsConfig = Bukkit.getPluginManager().getPlugin("Stats").getConfig();

						if(statsConfig.getInt(player.getUniqueId() + ".gems") >= bet
								&& statsConfig.getInt(argPlayer.getUniqueId() + ".gems") >= bet)
						{
							if(!argPlayer.equals(null) && !argPlayer.equals(player))
							{
								if(!betAmount.containsKey(player) || !(playersInDuel.contains(player)))
								{
									if(config.getStringList("maps").size() == 0)
									{
										player.sendMessage(ChatColor.RED
												+ "There are no maps yet, add one with /duel set [mapName] [1 or 2]");
										player.playSound(player, Sound.ENTITY_GHAST_DEATH, 1, 1);
										return true;
									}

									// Put player in hashmap that specifies their bet amount and map number
									betAmount.put(player, bet);
									mapNumber.put(player, getMapNumber(mapName));

									//Logic for the map (args[3]

									for(String map : config.getStringList("maps"))
									{
										if(map.equals(mapName))
										{
											TextComponent text = new TextComponent(ChatColor.GREEN + player.getName()
													+ " is inviting you to a duel in map " + ChatColor.WHITE + "'"
													+ mapName + "'" + ChatColor.GREEN + " with a bet of "
													+ DuelCommands.betAmount.get(player));

											TextComponent accept = new TextComponent(ChatColor.GREEN + "ACCEPT");
											TextComponent deny = new TextComponent(ChatColor.RED + "DENY");

											accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/duel accept " + player.getName()));
											deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/duel deny " + player.getName()));
											accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new Text("Click to accept duel!")));
											deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new Text("Click to deny duel!")));

											text.addExtra(ChatColor.GRAY + " [");
											text.addExtra(accept);
											text.addExtra(ChatColor.GRAY + "/");
											text.addExtra(deny);
											text.addExtra(ChatColor.GRAY + "]");

											DuelCommands.duelPlayer.spigot().sendMessage(text);
											player.sendMessage(ChatColor.GREEN + "Invitation sent to "
													+ DuelCommands.duelPlayer.getName() + " for map " + mapName + "!");
											player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
											DuelCommands.duelPlayer.playSound(player,
													Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

											playersToDuel.add(DuelCommands.duelPlayer);
											playersInDuel.add(player);

										}
									}

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
							player.sendMessage(
									ChatColor.RED + "That bet is too much! " + argPlayer.getName() + " only has "
											+ ChatColor.GREEN + statsConfig.getInt(argPlayer.getUniqueId() + ".gems")
											+ ChatColor.RED + " gems! " + "And you have " + ChatColor.GREEN
											+ statsConfig.getInt(player.getUniqueId() + ".gems") + ChatColor.RED
											+ " gems!");

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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{

		if(args.length == 1)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
		else if(args.length == 3)
		{
			return ConfigManager.getMaps();
		}
		return List.of();
	}
}
package me.Jared.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.Jared.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Jared.GameState;
import me.Jared.Manager.ConfigManager;
import me.Jared.Manager.GameManager;
import me.Jared.Managers.KitManager;

public class EventCommands implements CommandExecutor, TabCompleter
{

	private GameManager gameManager;

	public EventCommands(GameManager gameManager)
	{
		this.gameManager = gameManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();

		if(cmd.getName().equalsIgnoreCase("event"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				if(player.hasPermission("event"))
				{
					list.add("start");
					list.add("setlobby");
					list.add("set");
					list.add("setcountdown");
					list.add("activate");
					list.add("deactivate");
					list.add("help");
					list.add("status");
				}
			}
		}
		return list;
	}

	private static Location spawnRadius(Location loc, int radius)
	{
		Location center = loc;
		Random rand = new Random();
		double angle = rand.nextDouble() * 360; // Generate a random angle
		double x = center.getX() + (rand.nextDouble() * radius * Math.cos(Math.toRadians(angle))); // x
		double z = center.getZ() + (rand.nextDouble() * radius * Math.sin(Math.toRadians(angle))); // z
		Location newloc = new Location(Bukkit.getWorld("world"), x, center.getY(), z);
		newloc.setYaw(center.getYaw());
		newloc.setPitch(center.getPitch());
		return newloc;
	}

	public static ArrayList<Player> noMovePlayers = new ArrayList<>();

	public static void teleportPlayers(PlayerManager playerManager)
	{
		noMovePlayers.clear();
		// Teleport players in game
		for(Player p : playerManager.getPlayers())
		{
			int teamIndex = ConfigManager.getTeamIndex(p);

			// Only include team 0 and 1
			if(teamIndex != 0 && teamIndex != 1)
				continue;

			// Normalize team index to 1 or 2 (for spawn)
			int spawnIndex = (teamIndex % 2 == 0) ? 2 : 1;

			Location location = ConfigManager.getEventSpawn(spawnIndex);
			p.teleport(spawnRadius(location, 3));
			noMovePlayers.add(p);
			p.getInventory().clear();
			p.getActivePotionEffects().clear();
			p.setHealth(20);

			if(me.Jared.Kits.Main.getInstance().getConfig().get("PlayerUniqueID." + p.getUniqueId()) != null)
			{
				KitManager.playerCustomHotBar(p);
				KitManager.diamondKit(p);
				KitManager.giveAmmo(p);
			} else
			{
				KitManager.diamondKit(p);
				KitManager.defaultHotBar(p.getUniqueId());
				for(int i = 0; i < 9; i++)
					p.getInventory().addItem(new ItemStack[] { KitManager.sniperAmmo(128) });
				p.getInventory().addItem(new ItemStack[] { KitManager.shotgunAmmo(128) });
				p.getInventory().addItem(new ItemStack[] { KitManager.autoAmmo(128) });
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("event"))
		{
			Player player = (Player) sender;
			if(!player.hasPermission("event"))
			{
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("leave"))
					{

						player.sendMessage(ChatColor.RED + "No Permission!");
						return true;
					}
				} else if(gameManager.getGameState() != GameState.INACTIVE)
				{
					player.teleport(ConfigManager.getLobbySpawn());
					gameManager.getPlayerManager().setPlayerInGame(player);
				}
			}

			if(sender.isOp() && sender.hasPermission("event"))
			{
				if(args.length == 0)
				{
					if(gameManager.getGameState() != GameState.INACTIVE)
					{
						player.sendMessage(ChatColor.RED + "Type /event help!");
					}
					player.sendMessage(ChatColor.RED + "Type /event activate to activate event mode!");
					return true;
				}

				if(args.length == 1)
				{
					if(gameManager.getGameState() == GameState.INACTIVE)
					{
						if(args[0].equalsIgnoreCase("activate"))
						{
							player.sendMessage(ChatColor.GREEN + "Set event mode to active!");
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
							gameManager.setGameState(GameState.RECRUITING);
							//							ConfigManager.clearTeams();
							return true;
						}
					} else if(gameManager.getGameState() == GameState.RECRUITING)
					{
						if(args[0].equalsIgnoreCase("start"))
						{
							if(ConfigManager.getTeams().size() <= 1)
							{
								player.sendMessage(ChatColor.RED + "Not enough teams yet!");
								return true;
							} else
							{
								player.sendMessage(ChatColor.GREEN + "Event is now starting!");
								player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

								gameManager.setGameState(GameState.COUNTDOWN);

								teleportPlayers(gameManager.getPlayerManager());

							}
						}
					}

					if(args[0].equalsIgnoreCase("status"))
					{
						player.sendMessage(ChatColor.WHITE + "Current status of the event is " + ChatColor.GOLD
								+ gameManager.getGameState().toString());
					}

					if(args[0].equalsIgnoreCase("deactivate"))
					{
						player.sendMessage(ChatColor.RED + "Set event mode to inactive!");
						for(Player online : Bukkit.getOnlinePlayers())
						{
							online.teleport(Bukkit.getWorld("world").getSpawnLocation());
						}
						player.playSound(player.getLocation(), Sound.ENTITY_GHAST_DEATH, 1, 1);
						gameManager.setGameState(GameState.INACTIVE);
						return true;
					}

					if(args[0].equalsIgnoreCase("help"))
					{
						player.sendMessage(
								ChatColor.GRAY + "------------" + ChatColor.GOLD + "Event Help" + ChatColor.GRAY
										+ "------------");
						player.sendMessage(ChatColor.GOLD + "- " + ChatColor.GRAY + "/event setlobby" + ChatColor.WHITE
								+ " - Sets the lobby of the event to where you are standing");
						player.sendMessage(
								ChatColor.GOLD + "- " + ChatColor.GRAY + "/event set <1 or 2>" + ChatColor.WHITE
										+ " - Sets the spawn for team 1 and team 2");
						return true;
					} else if(args[0].equalsIgnoreCase("setlobby"))
					{
						ConfigManager.setLobbySpawn(player.getLocation());
						player.sendMessage(ChatColor.GREEN + "You have successfully set event lobby to your location!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					} else if(args[0].equalsIgnoreCase("set"))
					{
						player.sendMessage(ChatColor.RED + "Usage: /event set <1 or 2>");
					}
				} else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("set"))
					{
						if(args[1].equalsIgnoreCase("1"))
						{
							ConfigManager.setEventSpawn(player.getLocation(), 1);
							player.sendMessage(
									ChatColor.GREEN + "You have successfully set event spawn 1 to your location!");
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						} else if(args[1].equalsIgnoreCase("2"))
						{
							ConfigManager.setEventSpawn(player.getLocation(), 2);
							player.sendMessage(
									ChatColor.GREEN + "You have successfully set event spawn 2 to your location!");
							player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						} else
						{
							player.sendMessage(ChatColor.RED + "Usage: /event set <1 or 2>");
						}
						return true;
					} else if(args[0].equalsIgnoreCase("setcountdown"))
					{
						int num = Integer.parseInt(args[1]);
						ConfigManager.setCountdown(num);
						player.sendMessage(
								ChatColor.GREEN + "You have successfully set countdown to " + num + " seconds!");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					}
				}
			} else
			{
				sender.sendMessage(ChatColor.RED + "No sir!");
			}
		}
		return true;
	}

}

package me.Jared.Essentials;

import me.Jared.Essentials.Runnables.TPARunnable;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin implements Listener
{
	private HashMap<Player, Player> requesters;
	private ArrayList<Player> playersInCooldown;

	private final Cooldown cooldown5min = new Cooldown(300);
	private final Cooldown cooldown4min = new Cooldown(240);
	private final Cooldown cooldown3min = new Cooldown(180);
	private final Cooldown cooldown2min = new Cooldown(120);
	private final Cooldown cooldown1min = new Cooldown(60);

	public void loadConfig()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private static Main instance;

	@Override
	public void onEnable()
	{
		requesters = new HashMap<>();
		playersInCooldown = new ArrayList<>();
		instance = this;

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "My plugin has been enabled!!");

		this.getServer().getPluginManager().registerEvents(new Listener1(this), this);
		this.getServer().getPluginManager().registerEvents(this, this);

		for(Player p : Bukkit.getOnlinePlayers())
		{
			PacketUtils.sendTabHF(p, ChatColor.GOLD + "JaredServer",
					ChatColor.GOLD + "store.jaredcoen.com\n" + ChatColor.GREEN + "Players Online: " + ChatColor.WHITE
							+ Bukkit.getOnlinePlayers().size());
		}

		loadConfig();
	}

	@Override
	public void onDisable()
	{
		for(Player online : Bukkit.getOnlinePlayers())
		{
			online.kickPlayer(ChatColor.RED + "Be back soon!!!");
		}

		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Bye bye birdie!");
	}

	public static Main getInstance()
	{
		return instance;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		requesters.remove(player);
		playersInCooldown.remove(player);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{

		if(cmd.getName().equalsIgnoreCase("r"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;

				if(requesters.containsKey(p))
				{
					if(args.length == 0)
					{
						p.sendMessage(ChatColor.RED + "Usage: /r <message>");
						return true;
					}
					if(args.length >= 1)
					{
						String playerName = requesters.get(p).getName();
						requesters.put(Bukkit.getPlayer(playerName), p);
						String message = "";
						for(String arg : args)
						{
							message += arg + " ";
						}

						if(Bukkit.getPlayer(playerName) != null)
						{
							Bukkit.getPlayer(playerName).sendMessage(
									ChatColor.WHITE + playerName + ChatColor.GRAY + " ---> You: " + ChatColor.WHITE
											+ message);
							p.sendMessage(ChatColor.GRAY + "You --> " + ChatColor.WHITE + playerName + ": " + message);
						} else
						{
							p.sendMessage(ChatColor.RED + "Player not online!");
							return true;
						}
					}
				} else
				{
					p.sendMessage(ChatColor.RED + "You have no one to reply to noob!");
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("msg"))
		{
			if(args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Usage: /msg <player>");
				return true;
			}
			if(args.length >= 1)
			{
				String playerName = args[0];

				if(sender instanceof Player)
				{
					Player p = (Player) sender;
					if(playerName.equals(p.getName()))
					{
						p.sendMessage(ChatColor.RED + "You cannot message yourself noob!");
						return true;
					}

					requesters.remove(Bukkit.getPlayer(playerName));
					requesters.put(Bukkit.getPlayer(playerName), p);
					String message = "";
					for(int i = 1; i < args.length; i++)
					{
						message += args[i] + " ";
					}

					if(Bukkit.getPlayer(playerName) != null)
					{
						Bukkit.getPlayer(playerName).sendMessage(
								ChatColor.WHITE + playerName + ChatColor.GRAY + " ---> You: " + ChatColor.WHITE
										+ message);
						p.sendMessage(ChatColor.GRAY + "You --> " + ChatColor.WHITE + playerName + ": " + message);
					} else
					{
						p.sendMessage(ChatColor.RED + "Player not online!");
						return true;
					}
				} else
				{
					String message = "";
					for(int i = 1; i < args.length; i++)
					{
						message += args[i] + " ";
					}

					if(Bukkit.getPlayer(playerName) != null)
					{
						Bukkit.getPlayer(playerName).sendMessage(
								ChatColor.WHITE + playerName + ChatColor.GRAY + " ---> You: " + ChatColor.WHITE
										+ message);
						sender.sendMessage(ChatColor.GRAY + "You --> " + ChatColor.WHITE + playerName + ": " + message);
					} else
					{
						sender.sendMessage(ChatColor.RED + "Player not online!");
						return true;
					}
				}
			}
		}

		// Teleport commands
		if(cmd.getName().equalsIgnoreCase("tpa"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				if(args.length == 0)
				{
					p.sendMessage(ChatColor.RED + "Usage: /tpa <playername>");
					return true;
				}

				if(args.length >= 1)
				{

					String playerName = args[0];
					if(playerName.equals(p.getName()))
					{
						p.sendMessage(ChatColor.RED + "You cannot teleport to yourself noob!");
						return true;
					}

					if(!(Bukkit.getPlayer(playerName).getWorld().equals(p.getWorld())))
					{
						p.sendMessage(ChatColor.RED + "You cannot teleport to someone in another world noob!!");
						return true;
					}

					if(Bukkit.getPlayer(playerName) != null)
					{
						if(!(cooldown5min.isOnCooldown(p)) && !(cooldown4min.isOnCooldown(p))
								&& !(cooldown3min.isOnCooldown(p)) && !(cooldown2min.isOnCooldown(p))
								&& !(cooldown1min.isOnCooldown(p)))
						{
							Bukkit.getPlayer(playerName).sendMessage(
									ChatColor.GRAY + p.getName() + " has sent you a TPA request. (/tpyes)");
							p.sendMessage(ChatColor.GRAY + "You have sent a TPA request to " + playerName);
							requesters.put(Bukkit.getPlayer(playerName), p);
							requesters.put(p, Bukkit.getPlayer(playerName));

							if(p.hasPermission("ranks.mvpplus"))
							{
								cooldown1min.putInCooldown(p);
							} else if(p.hasPermission("ranks.mvp"))
							{
								cooldown2min.putInCooldown(p);
							} else if(p.hasPermission("ranks.vipplus"))
							{
								cooldown3min.putInCooldown(p);
							} else if(p.hasPermission("ranks.vip"))
							{
								cooldown4min.putInCooldown(p);
							} else
							{
								cooldown5min.putInCooldown(p);
							}

						} else
						{
							// Here I want to add how many seconds left till they can teleport again

							if(p.hasPermission("ranks.mvpplus"))
							{
								int seconds = cooldown1min.getCooldownSeconds(p);
								int minutes = seconds / 60;
								p.sendMessage(
										ChatColor.RED + "You cannot teleport now! Wait " + minutes + "m " + seconds % 60
												+ "s");
							} else if(p.hasPermission("ranks.mvp"))
							{
								int seconds = cooldown2min.getCooldownSeconds(p);
								int minutes = seconds / 60;
								p.sendMessage(
										ChatColor.RED + "You cannot teleport now! Wait " + minutes + "m " + seconds % 60
												+ "s");
							} else if(p.hasPermission("ranks.vipplus"))
							{
								int seconds = cooldown3min.getCooldownSeconds(p);
								int minutes = seconds / 60;
								p.sendMessage(
										ChatColor.RED + "You cannot teleport now! Wait " + minutes + "m " + seconds % 60
												+ "s");
							} else if(p.hasPermission("ranks.vip"))
							{
								int seconds = cooldown4min.getCooldownSeconds(p);
								int minutes = seconds / 60;
								p.sendMessage(
										ChatColor.RED + "You cannot teleport now! Wait " + minutes + "m " + seconds % 60
												+ "s");
							} else
							{
								int seconds = cooldown5min.getCooldownSeconds(p);
								int minutes = seconds / 60;
								p.sendMessage(
										ChatColor.RED + "You cannot teleport now! Wait " + minutes + "m " + seconds % 60
												+ "s");
							}

							return true;
						}

					} else
					{
						p.sendMessage(ChatColor.RED + "Player not online!");
						return true;
					}
				}
			}
		}

		if(cmd.getName().equalsIgnoreCase("tpyes") || cmd.getName().equalsIgnoreCase("tpaccept"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				if(requesters.containsKey(requesters.get(p)))
				{
					requesters.remove(requesters.get(p));
					TPARunnable tpaRunnable = new TPARunnable(p, 10, requesters, playersInCooldown);
					tpaRunnable.runTaskTimer(instance, 0, 20);
				} else
				{
					p.sendMessage(ChatColor.RED + "No one is requesting to teleport noob!");
					return true;
				}
			}
		}

		if(cmd.getName().equalsIgnoreCase("texture"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;

				TextComponent text = new TextComponent(
						ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Click me for textures!");
				text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
						"https://www.dropbox.com/scl/fi/pioj8vx7o2vdih3ruuu4j/Jared-Server-Pack.zip?rlkey=h8o9s489a89r7ljn3nl6rpvry&dl=1"));
				p.spigot().sendMessage(text);
				p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}
		}

		if(cmd.getName().equalsIgnoreCase("discord"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				TextComponent text = new TextComponent(
						ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "Click me for the discord!");
				text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/PjKDsGf69U"));
				p.spigot().sendMessage(text);
				p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}
		}

		if(cmd.getName().equalsIgnoreCase("spawn"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);

				if(block.getType() == Material.AIR)
				{
					p.sendMessage(ChatColor.RED + "Stay still");
					return true;
				}


				if(p.getWorld().getName().equals("warz"))
				{
					p.sendTitle(ChatColor.YELLOW + "Teleporting to spawn...", "", 5, 5, 5);
					me.Jared.Loot.ConfigManager.savePlayerWarzData(p, p.getLocation(), p.getInventory());
					p.getInventory().clear();
					p.teleport(ConfigManager.getSpawn());
					me.Jared.Loot.ConfigManager.loadInventory(p, "world");
				}
				else
				{
					p.teleport(ConfigManager.getSpawn());
					for(PotionEffect effect : p.getActivePotionEffects())
					{
						p.removePotionEffect(effect.getType());
					}
					p.setLevel(0);
					p.sendMessage(ChatColor.GREEN + "Successfully teleported you to spawn!");
					p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				}


				return true;
			}

		}
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			if(p.hasPermission("jared"))
			{

				if(cmd.getName().equalsIgnoreCase("setspawn"))
				{
					Location location = p.getLocation();

					ConfigManager.setSpawn(location);
					p.sendMessage(ChatColor.GREEN + "Successfully set spawn to your location!");
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					return true;
				}

				if(cmd.getName().equalsIgnoreCase("tutorial"))
				{
					if(!(sender instanceof Player))
					{
						sender.sendMessage(ChatColor.RED + "Nope.");
						return true;
					}

					int x = (int) p.getLocation().getX();
					int y = (int) p.getLocation().getY();
					int z = (int) p.getLocation().getZ();

					this.getConfig().set("tutorial.x", x);
					this.getConfig().set("tutorial.y", y);
					this.getConfig().set("tutorial.z", z);
					this.saveConfig();

					p.sendMessage(ChatColor.GREEN + "Set the tutorial area to where you are standing!");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
				}

				if(cmd.getName().equalsIgnoreCase("sudo"))
				{
					if(!(sender instanceof Player))
					{
						sender.sendMessage(ChatColor.RED + "Nope.");
						return true;
					}

					if(args.length < 1)
					{
						p.sendMessage(ChatColor.RED + "You must type a player and a message!");
					}

					if(args.length >= 2)
					{

						Player target;
						try
						{

							StringBuilder x = new StringBuilder();
							for(int i = 1; i < args.length; i++)
							{
								x.append(args[i] + " ");
							}
							String message = x.toString().trim();

							target = Bukkit.getPlayer(args[0]);
							if(target == null)
							{
								p.sendMessage(ChatColor.RED + args[0].toString() + " is not online");
								return true;
							}

							target.chat(message);

						} catch(IllegalArgumentException e)
						{
							p.sendMessage(ChatColor.RED + "Usage: /sudo <name> <message>");
						}
						return true;
					}

				}
			}
		}
		return true;
	}
}
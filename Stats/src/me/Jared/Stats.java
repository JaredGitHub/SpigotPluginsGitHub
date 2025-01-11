package me.Jared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.MenuSystem.PlayerMenuUtility;
import me.Jared.PlaceHolders.StatsExpansion;
import me.Jared.Ranks.NameTagManager;
import me.Jared.Ranks.RankCommand;
import me.Jared.Ranks.RankManager;
import me.Jared.Shop.Commands;
import net.md_5.bungee.api.ChatColor;

public class Stats extends JavaPlugin
{
	public void loadConfig()
	{
		this.getConfig().options().copyDefaults();
		this.saveConfig();
	}

	private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

	private RankManager rankManager;
	private NameTagManager nametagManager;
	private StatScoreboard statScoreboard;

	private static Stats stats;

	@Override
	public void onEnable()
	{
		stats = this;

		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Stats plugin is here BOIII!");

		getCommand("rank").setExecutor(new RankCommand(this));
		this.getCommand("shop").setExecutor(new Commands());

		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new KillstreakListener(), this);

		rankManager = new RankManager(this);
		nametagManager = new NameTagManager(this);
		for(Player online : Bukkit.getOnlinePlayers())
		{
			statScoreboard = new StatScoreboard(this, online);
		}
		this.loadConfig();

		new StatsExpansion().register();
	}

	public static Stats getInstance()
	{
		return stats;
	}

	public RankManager getRankManager()
	{
		return this.rankManager;
	}

	public NameTagManager getNametagManager()
	{
		return this.nametagManager;
	}

	public StatScoreboard getStatScoreboard()
	{
		return this.statScoreboard;
	}

	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Stats plugin is gone! ;(");
	}

	public PlayerMenuUtility getPlayerMenuUtility(Player p)
	{
		PlayerMenuUtility playerMenuUtility;

		if(playerMenuUtilityMap.containsKey(p))
		{
			return playerMenuUtilityMap.get(p);
		} else
		{
			playerMenuUtility = new PlayerMenuUtility(p);
			playerMenuUtilityMap.put(p, playerMenuUtility);

			return playerMenuUtility;
		}
	}

	FileConfiguration config = this.getConfig();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("elo"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;

				if(args.length == 1)
				{
					Player otherPlayer = Bukkit.getPlayer(args[0]);

					if(otherPlayer != null)
					{
						player.sendMessage(ChatColor.GREEN + otherPlayer.getName() + "'s elo rank is: "
								+ ChatColor.translateAlternateColorCodes('&',
										config.getString(otherPlayer.getUniqueId() + ".rank"))
								+ " " + ChatColor.GREEN + config.getString(otherPlayer.getUniqueId() + ".elo"));

					}
					return true;
				}
				
				player.sendMessage(ChatColor.GRAY + "ELO Rankings in Order:");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&7Bambi: ") + ChatColor.WHITE + "< 1200");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&aScavenger: ") + ChatColor.WHITE + "1200 - 1399");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&bCitizen: ") + ChatColor.WHITE + "1400 - 1599");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cHunter: ") + ChatColor.WHITE + "1600 - 1799");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&9Survivor: ") + ChatColor.WHITE + "1800 - 1999");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&6Officer: ") + ChatColor.WHITE + "2000 - 2199");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&0Deputy: ") + ChatColor.WHITE + "2200 - 2399");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&1Sheriff: ") + ChatColor.WHITE + "2400 - 2599");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&bSoldier: ") + ChatColor.WHITE + "2600 - 2799");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&dWarrior: ") + ChatColor.WHITE + "2800 - 2999");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&bHero: ") + ChatColor.WHITE + "3000 - 3199");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cLegend: ") + ChatColor.WHITE + "3200 - 3499");
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&eImmortal: ") + ChatColor.WHITE + "3500+");

				player.sendMessage(ChatColor.GREEN + "\nYour elo rank is: "
						+ ChatColor.translateAlternateColorCodes('&', config.getString(player.getUniqueId() + ".rank"))
						+ " " + ChatColor.GREEN + config.getString(player.getUniqueId() + ".elo"));
			}
		}

		if(cmd.getName().equalsIgnoreCase("topelo"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;

				player.sendMessage(ChatColor.GRAY + "Top 10 Players by ELO:");

				// Convert the array to an ArrayList
				ArrayList<OfflinePlayer> offlinePlayers = new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayers()));

				// Sort players by ELO in descending order
				offlinePlayers.sort((p1, p2) ->
				{
					// Get ELO for p1
					String elo1String = config.getString(p1.getUniqueId() + ".elo", "1000");
					int elo1 = Integer.parseInt(elo1String);

					// Get ELO for p2
					String elo2String = config.getString(p2.getUniqueId() + ".elo", "1000");
					int elo2 = Integer.parseInt(elo2String);

					// Compare in descending order (highest first)
					return Integer.compare(elo2, elo1);
				});

				// Limit the list to the top 10 players
				int limit = Math.min(10, offlinePlayers.size()); // Ensure we don't exceed the list size
				List<OfflinePlayer> topPlayers = offlinePlayers.subList(0, limit);

				// Display the top players
				for(OfflinePlayer offline : topPlayers)
				{
					String rank = config.getString(offline.getUniqueId() + ".rank", "&7Bambi");
					String eloString = config.getString(offline.getUniqueId() + ".elo", "1000");
					int elo = Integer.parseInt(eloString);

					player.sendMessage(offline.getName() + ": " + ChatColor.translateAlternateColorCodes('&', rank)
							+ " " + ChatColor.GREEN + elo);
				}
			}
		}

		if(cmd.getName().equalsIgnoreCase("giveGems"))
		{
			if(!sender.hasPermission("stats"))
			{
				sender.sendMessage(ChatColor.RED + "You cannot give gems noob!");
				return true;
			}
			if(args.length >= 2)
			{

				if(Bukkit.getPlayer(args[0]) instanceof Player)
				{
					Player player = Bukkit.getPlayer(args[0]);
					try
					{
						int amount = Integer.parseInt(args[1]);
						int gems = config.getInt(player.getUniqueId() + ".gems");

						config.set(player.getUniqueId() + ".gems", gems + amount);

						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						player.sendMessage(ChatColor.GREEN + "Congratulions you have received " + amount + " gems!");

						sender.sendMessage(ChatColor.GREEN + "You gave " + player.getName() + " " + amount + " gems!");

						this.saveConfig();

						new StatScoreboard(this, player);

					} catch(NumberFormatException e)
					{
						sender.sendMessage(ChatColor.RED + "Please use number for second argument!");
					}
				}
			}
		}

		if(cmd.getName().equalsIgnoreCase("removegems"))
		{
			if(!sender.hasPermission("stats"))
			{
				sender.sendMessage(ChatColor.RED + "You cannot remove gems noob!");
				return true;
			}
			if(args.length >= 2)
			{
				if(Bukkit.getPlayer(args[0]) instanceof Player)
				{
					Player player = Bukkit.getPlayer(args[0]);
					try
					{
						int amount = Integer.parseInt(args[1]);
						int gems = config.getInt(player.getUniqueId() + ".gems");

						player.playSound(player.getLocation(), Sound.ENTITY_GHAST_DEATH, 0.5f, 1);
						player.sendMessage(ChatColor.RED + "Oh no " + amount + " gems has been taken away from you!");
						sender.sendMessage(
								ChatColor.RED + "You took " + amount + " gems from " + player.getName() + "!");

						if(!(gems < amount))
						{
							config.set(player.getUniqueId() + ".gems", gems - amount);
						} else
						{
							config.set(player.getUniqueId() + ".gems", 0);
						}

						this.saveConfig();

						new StatScoreboard(this, player);

					} catch(NumberFormatException e)
					{
						sender.sendMessage(ChatColor.RED + "Please use number for second argument!");
					}
				}
			}
		}

		if(cmd.getName().equalsIgnoreCase("killstreak"))
		{
			if(sender.hasPermission("stats"))
			{
				if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("t"))
					{
						config.set("killstreakRewards", true);
						this.saveConfig();
						sender.sendMessage(org.bukkit.ChatColor.GREEN + "Killstreak rewards are now on!");
					} else if(args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("f"))
					{
						config.set("killstreakRewards", false);
						this.saveConfig();
						sender.sendMessage(org.bukkit.ChatColor.RED + "Killstreak rewards are now off!");
					} else
					{
						sender.sendMessage(org.bukkit.ChatColor.RED + "Choose TRUE or FALSE!");
						return true;
					}
				} else if(args.length == 0)
				{
					sender.sendMessage(org.bukkit.ChatColor.GOLD + "Killstreak status is " + org.bukkit.ChatColor.WHITE
							+ config.getBoolean("killstreakRewards"));
				}
			} else
			{
				sender.sendMessage(org.bukkit.ChatColor.RED + "No Permission!");
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("buy"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				
				player.sendMessage("Click this URL to buy ---->  store.jaredcoen.com");
			}
		}
		
		return true;
	}
}
package me.Jared;

import java.util.HashMap;

import org.bukkit.Bukkit;
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
		return true;
	}
}
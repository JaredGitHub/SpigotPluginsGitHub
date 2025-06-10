package me.Jared.eventRSVP;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.Duration;

public final class EventRSVP extends JavaPlugin implements Listener, CommandExecutor, TabCompleter
{

	private static EventRSVP instance;

	@Override
	public void onEnable()
	{
		instance = this;
		saveDefaultConfig();
		getComponentLogger().info(Component.text("EventRSVP has been enabled!", NamedTextColor.GREEN));
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginCommand("eventrsvp").setExecutor(this);
		Bukkit.getPluginCommand("eventrsvp").setTabCompleter(this);

		new EventRSVPExpansion().register();
	}

	@Override
	public void onDisable()
	{
		getComponentLogger().info(Component.text("EventRSVP has been disabled!", NamedTextColor.RED));
	}

	public static EventRSVP getInstance()
	{
		return instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);
		showWelcomeTitle(player);
	}

	private void showWelcomeTitle(Player player)
	{
		MiniMessage miniMessage = MiniMessage.miniMessage();

		// Get title components
		String mainRaw = getConfig().getString("title.main", "<red>Missing Title</red>");
		String subRaw = getConfig().getString("title.subtitle", "<gray>Missing Subtitle</gray>");
		Component main = miniMessage.deserialize(mainRaw);
		Component sub = miniMessage.deserialize(subRaw);

		// Get timing values (with safe defaults)
		int fadeInSec = getConfig().getInt("title.fade-in", 1);
		int staySec = getConfig().getInt("title.stay", 3);
		int fadeOutSec = getConfig().getInt("title.fade-out", 1);

		// Create title with timing
		Title title = Title.title(main, sub,
				Title.Times.times(Duration.ofSeconds(fadeInSec), Duration.ofSeconds(staySec),
						Duration.ofSeconds(fadeOutSec)));

		player.showTitle(title);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("eventrsvp"))
		{
			if(args.length > 0)
			{
				if(args[0].equalsIgnoreCase("list"))
				{
					//Show all players who RSVP'd for the event
					showRSVPPlayers(sender);
				}

				if(sender.hasPermission("eventrsvp.use"))
				{
					if(args[0].equalsIgnoreCase("clear"))
					{

						//Clear all players who RSVP'd for the event
						clearRSVPPlayers();
						sender.sendMessage(Component.text("All players have been cleared!", NamedTextColor.GREEN));

					} else if(sender.hasPermission("eventrsvp.use"))
					{
						//Add the player to the config in eventRSVP
						Player player = Bukkit.getPlayer(args[0]);
						sender.sendMessage(
								Component.text(player.getName() + " has RSVP'd for the event!", NamedTextColor.GREEN));
						addPlayerRSVP(player);
						return true;
					}
				}
			}
		}
		return true;
	}

	private void showRSVPPlayers(CommandSender sender)
	{
		FileConfiguration config = getConfig();
		Component header = Component.text("Players who RSVP'd:", NamedTextColor.GOLD);

		sender.sendMessage(header);

		if(config.getConfigurationSection("RSVP") == null)
		{
			sender.sendMessage(Component.text("No players have RSVP'd!", NamedTextColor.RED));
			return;
		}

		for(String uuid : config.getConfigurationSection("RSVP").getKeys(false))
		{
			if(config.getBoolean("RSVP." + uuid))
			{
				String playerName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(uuid)).getName();
				if(playerName != null)
				{
					Component playerEntry = Component.text("- " + playerName, NamedTextColor.YELLOW);
					sender.sendMessage(playerEntry);
				}
			}
		}
	}

	private void clearRSVPPlayers()
	{
		FileConfiguration config = getConfig();
		config.set("RSVP", null);
		saveConfig();
	}

	private void addPlayerRSVP(Player player)
	{
		FileConfiguration config = getConfig();
		config.set("RSVP." + player.getUniqueId(), true);
		saveConfig();
	}

	@Override
	public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(command.getName().equalsIgnoreCase("eventrsvp") && args.length == 1)
		{
			java.util.List<String> suggestions = new java.util.ArrayList<>();
			suggestions.add("list");
			if(sender.hasPermission("eventrsvp.use"))
			{
				suggestions.add("clear");
			}
			return suggestions;
		}
		return java.util.Collections.emptyList();
	}
}

package me.Jared;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.permissions.PermissionAttachment;

import me.Jared.MenuSystem.StatsMenu;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class EventListener implements Listener
{
	private Stats stats = Stats.getPlugin(Stats.class);
	FileConfiguration config = stats.getConfig();

	private HashMap<UUID, Long> cooldown = new HashMap<UUID, Long>();
	private int cooldowntime = 3 * 1000;

	private void showHealthActionBar(Player player, Entity hitEntity)
	{
		if(hitEntity instanceof LivingEntity && !(hitEntity instanceof ArmorStand))
		{
			LivingEntity playerHit = (LivingEntity) hitEntity;
			String name = ChatColor.YELLOW + playerHit.getName() + " ";
			int health = (int) playerHit.getHealth();
			switch(health)
			{
			case 0:
				sendActionBar(player, name + ChatColor.BLACK + "❤❤❤❤❤❤❤❤❤❤");
				break;
			case 1:
				sendActionBar(player, name + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤❤❤❤❤❤❤");
				break;
			case 2:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤" + ChatColor.BLACK + "❤❤❤❤❤❤❤❤❤");
				break;
			case 3:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤❤❤❤❤❤");
				break;
			case 4:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤" + ChatColor.BLACK + "❤❤❤❤❤❤❤❤");
				break;
			case 5:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤❤❤❤❤");
				break;
			case 6:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤" + ChatColor.BLACK + "❤❤❤❤❤❤❤");
				break;
			case 7:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤❤❤❤");
				break;
			case 8:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤" + ChatColor.BLACK + "❤❤❤❤❤❤");
				break;
			case 9:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤❤❤");
				break;
			case 10:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤" + ChatColor.BLACK + "❤❤❤❤❤");
				break;
			case 11:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤❤❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤❤");
				break;
			case 12:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤❤" + ChatColor.BLACK + "❤❤❤❤");
				break;
			case 13:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤❤❤❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤❤");
				break;
			case 14:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤" + ChatColor.BLACK + "❤❤❤");
				break;
			case 15:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤❤");
				break;
			case 16:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤" + ChatColor.BLACK + "❤❤");
				break;
			case 17:
				sendActionBar(player,
						name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤" + ChatColor.RED + "❤" + ChatColor.BLACK + "❤");
				break;
			case 18:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤❤" + ChatColor.BLACK + "❤");
				break;
			case 19:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤❤" + ChatColor.RED + "❤");
				break;
			case 20:
				sendActionBar(player, name + ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤❤❤");
				break;
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if(e.getEntity().getShooter() instanceof Player)
		{
			Player player = (Player) e.getEntity().getShooter();

			showHealthActionBar(player, e.getHitEntity());
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		if(e.getEntity() instanceof Zombie)
		{
			if(e.getEntity().getKiller() != null)
			{
				Player player = e.getEntity().getKiller();

				// Setting the config to kills deaths killstreaks and gems
				int zombiekills = config.getInt(e.getEntity().getKiller().getUniqueId() + "." + "zombiekills");

				config.set(e.getEntity().getKiller().getUniqueId() + ".zombiekills", zombiekills + 1);

				stats.saveConfig();
				new StatScoreboard(stats, player);
			}
		}
	}

	private int getRankGems(Player killer)
	{
		int rankGems = 5;
		if(killer.hasPermission("ranks.default"))
		{
			if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
			{
				rankGems = 10;
			} else
			{
				rankGems = 5;
			}
		} else if(killer.hasPermission("ranks.vip"))
		{
			if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
			{
				rankGems = 14;
			} else
			{
				rankGems = 7;
			}
		} else if(killer.hasPermission("ranks.vipplus"))
		{
			if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
			{
				rankGems = 20;
			} else
			{
				rankGems = 10;
			}
		} else if(killer.hasPermission("ranks.mvp"))
		{
			if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
			{
				rankGems = 30;
			} else
			{
				rankGems = 15;
			}
		} else if(killer.hasPermission("ranks.mvpplus"))
		{
			if(Bukkit.getPluginManager().getPlugin("Boosters").getConfig().getBoolean("doublegems"))
			{
				rankGems = 40;
			} else
			{
				rankGems = 20;
			}
		}

		return rankGems;
	}

	// Calculate Elo change for both the killer and the victim
	private int[] calculateEloChange(Player killer, Player victim)
	{
		// Retrieve the current Elo ratings for the killer and victim
		int killerElo = config.getInt(killer.getUniqueId() + ".elo"); // Default Elo if not set
		int victimElo = config.getInt(victim.getUniqueId() + ".elo"); // Default Elo if not set

		// K-factor (the weight of the game outcome)
		int kFactor = 32;

		// Calculate the expected score for both players
		double expectedKillerScore = 1.0 / (1.0 + Math.pow(10, (victimElo - killerElo) / 400.0));
		double expectedVictimScore = 1.0 / (1.0 + Math.pow(10, (killerElo - victimElo) / 400.0));

		// Elo change for the killer (1 point for a win)
		double killerEloChange = kFactor * (1 - expectedKillerScore);
		// Elo change for the victim (0 points for a loss)
		double victimEloChange = kFactor * (0 - expectedVictimScore);

		// Update both Elo ratings
		int updatedKillerElo = (int) (killerElo + killerEloChange);
		int updatedVictimElo = (int) (victimElo + victimEloChange);

		// Return both Elo changes in an array: [killerEloChange, victimEloChange]
		// You could also return updated ratings instead of changes if preferred
		return new int[]
		{ updatedKillerElo, updatedVictimElo };
	}

	private void broadcastElimination(Player killer, String eloChangeKillerString, Player victim,
			String eloChangeVictimString)
	{
		// Create the main message component
		TextComponent message = new TextComponent(ChatColor.GOLD + "* ");

		// Create the hoverable text for the killer
		TextComponent killerName = new TextComponent(ChatColor.GRAY + killer.getName());
		killerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(eloChangeKillerString)));

		// Create the "has eliminated" text
		TextComponent middleText = new TextComponent(ChatColor.GRAY + " has eliminated ");

		// Create the hoverable text for the victim
		TextComponent victimName = new TextComponent(ChatColor.GRAY + victim.getName());
		victimName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(eloChangeVictimString)));

		// Append all components to the main message
		message.addExtra(killerName);
		message.addExtra(middleText);
		message.addExtra(victimName);

		// Broadcast the message
		Bukkit.spigot().broadcast(message);
	}

	private String getRankByELO(int elo)
	{
		if(elo < 1200)
		{
			return "&7Bambi";
		} else if(elo < 1400)
		{
			return "&aScavenger";
		} else if(elo < 1600)
		{
			return "&bCitizen";
		} else if(elo < 1800)
		{
			return "&cHunter";
		} else if(elo < 2000)
		{
			return "&9Survivor";
		} else if(elo < 2200)
		{
			return "&6Officer";
		} else if(elo < 2400)
		{
			return "&0Deputy";
		} else if(elo < 2600)
		{
			return "&1Sheriff";
		} else if(elo < 2800)
		{
			return "&bSoldier";
		} else if(elo < 3000)
		{
			return "&dWarrior";
		} else if(elo < 3200)
		{
			return "&bHero";
		} else if(elo < 3500)
		{
			return "&cLegend";
		} else
		{
			return "&eImmortal";
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		e.setDeathMessage(null);
		if(e.getEntity() instanceof Player)
		{
			Player victim = (Player) e.getEntity();

			// If the player died from getting killed by another player.
			if(victim.getKiller() != null)
			{
				Player killer = victim.getKiller();

				// If player is in cooldown STOP!!!
				if(cooldown.containsKey(victim.getUniqueId()))
				{
					long timeleft = ((cooldown.get(victim.getUniqueId())) + cooldowntime)
							- (System.currentTimeMillis());

					timeleft /= 1000.0;
					if(timeleft > 0)
					{
						return;
					}
				}

				// Get elo before kill
				int killerEloBefore = config.getInt(killer.getUniqueId() + ".elo");
				int victimEloBefore = config.getInt(victim.getUniqueId() + ".elo");

				int[] eloChange = calculateEloChange(killer, victim);

				// The new elo
				int newKillerElo = eloChange[0];
				int newVictimElo = eloChange[1];

				// Modified ELO change strings with ranks
				String eloChangeKillerString = "Elo Before: [" + ChatColor.RED + killerEloBefore + ChatColor.RESET
						+ "] " + "Rank Before: ["
						+ ChatColor.translateAlternateColorCodes('&', getRankByELO(killerEloBefore)) + ChatColor.RESET
						+ "]\n" + "Elo After: [" + ChatColor.GREEN + (newKillerElo) + ChatColor.RESET + "] "
						+ "Rank After: [" + ChatColor.translateAlternateColorCodes('&', getRankByELO(newKillerElo))
						+ ChatColor.RESET + "]";

				String eloChangeVictimString = "Elo Before: [" + ChatColor.GREEN + victimEloBefore + ChatColor.RESET
						+ "] " + "Rank Before: ["
						+ ChatColor.translateAlternateColorCodes('&', getRankByELO(victimEloBefore)) + ChatColor.RESET
						+ "]\n" + "Elo After: [" + ChatColor.RED + (newVictimElo) + ChatColor.RESET + "] "
						+ "Rank After: [" + ChatColor.translateAlternateColorCodes('&', getRankByELO(newVictimElo))
						+ ChatColor.RESET + "]";

				broadcastElimination(killer, eloChangeKillerString, victim, eloChangeVictimString);

				// sending action bars and messages in chat
				sendActionBar(victim, ChatColor.DARK_RED + "You were killed by " + e.getEntity().getKiller().getName());
				sendActionBar(killer, ChatColor.DARK_RED + "You killed " + e.getEntity().getName() + "!!");

				int rankGems = getRankGems(killer);
				killer.sendMessage(ChatColor.GREEN + "You killed " + ChatColor.WHITE + victim.getName()
						+ ChatColor.GREEN + " and received " + rankGems + " gems!");

				// Setting the config to kills deaths killstreaks and gems
				int deaths = config.getInt(e.getEntity().getUniqueId() + "." + "deaths");
				int kills = config.getInt(e.getEntity().getKiller().getUniqueId() + "." + "kills");
				int killStreak = config.getInt(e.getEntity().getKiller().getUniqueId() + ".killStreak");
				int highKS = config.getInt(victim.getKiller().getUniqueId() + ".highks");
				int gems = config.getInt(victim.getKiller().getUniqueId() + ".gems");

				if(killStreak >= highKS)
				{
					config.set(e.getEntity().getKiller().getUniqueId() + ".highks", killStreak + 1);
				}
				config.set(e.getEntity().getKiller().getUniqueId() + ".gems", gems + rankGems);
				config.set(e.getEntity().getUniqueId() + ".deaths", deaths + 1);
				config.set(e.getEntity().getKiller().getUniqueId() + ".kills", kills + 1);
				config.set(e.getEntity().getKiller().getUniqueId() + ".killStreak", killStreak + 1);
				config.set(e.getEntity().getUniqueId() + ".killStreak", killStreak == 0);

				// Set the elo of the killer and the victim
				config.set(killer.getUniqueId() + ".elo", newKillerElo);
				config.set(victim.getUniqueId() + ".elo", newVictimElo);

				// Setting the rank based on the new elo
				config.set(killer.getUniqueId() + ".rank", getRankByELO(newKillerElo));
				config.set(victim.getUniqueId() + ".rank", getRankByELO(newVictimElo));

				cooldown.put(victim.getUniqueId(), System.currentTimeMillis());

				stats.saveConfig();
				new StatScoreboard(stats, killer);
				new StatScoreboard(stats, victim);

			} else
			{
				EntityDamageEvent lastDamageEvent = e.getEntity().getLastDamageCause();
				if(lastDamageEvent != null)
				{
					if(lastDamageEvent.getCause().equals(DamageCause.ENTITY_ATTACK))
					{
						Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "* " + ChatColor.GRAY
								+ e.getEntity().getName() + " got mauled to death!");
					} else
					{
						Bukkit.getServer().broadcastMessage(
								ChatColor.GOLD + "* " + ChatColor.GRAY + e.getEntity().getName() + " has died");
					}

					int deaths = config.getInt(e.getEntity().getUniqueId() + "." + "deaths");
					int killStreak = config.getInt(e.getEntity().getUniqueId() + ".killStreak");

					config.set(e.getEntity().getUniqueId() + ".killStreak", killStreak == 0);
					config.set(e.getEntity().getUniqueId() + "." + "deaths", deaths + 1);

					stats.saveConfig();
				}

			}
			new StatScoreboard(stats, victim);
		}

	}

	public void sendActionBar(Player player, String message)
	{
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}

	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() == null)
			return;

		InventoryHolder holder = e.getInventory().getHolder();

		if(holder instanceof StatsMenu)
		{
			StatsMenu menu = (StatsMenu) holder;
			menu.handleMenu(e);

			if(e.getWhoClicked().getOpenInventory().getBottomInventory().getType().equals(InventoryType.PLAYER))
			{
				e.setCancelled(true);
			}
			if(e.getCurrentItem() == null)
				return;
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();

		if(config.getInt(player.getUniqueId() + ".elo") <= 0 || config.getString(player.getUniqueId() + ".rank") == null
				|| (!config.getString(player.getUniqueId() + ".rank").contains("&")))
		{
			stats.getConfig().set(player.getUniqueId() + ".elo", 1000);
			stats.getConfig().set(player.getUniqueId() + ".rank", "&7Bambi");
			stats.saveConfig();
		}

		if(config.getInt(player.getUniqueId() + ".elo") > 0)
		{
			stats.getNametagManager().setNametags(player);
			stats.getNametagManager().newTag(player);
		}

		PermissionAttachment attachment;
		if(stats.getRankManager().getPerms().containsKey(player.getUniqueId()))
		{
			attachment = stats.getRankManager().getPerms().get(player.getUniqueId());
		} else
		{
			attachment = player.addAttachment(stats);
			stats.getRankManager().getPerms().put(player.getUniqueId(), attachment);
		}

		for(String perm : stats.getRankManager().getRank(player.getUniqueId()).getPermissions())
		{
			attachment.setPermission(perm, true);
		}
		new StatScoreboard(stats, player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();

		stats.getNametagManager().removeTag(p);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		e.setCancelled(true);

		Player player = e.getPlayer();
		if(player.getWorld().equals(Bukkit.getWorld("world")))
		{
			Bukkit.broadcastMessage(stats.getRankManager().getRank(player.getUniqueId()).getDisplay() + ChatColor.RESET
					+ "[" + ChatColor.translateAlternateColorCodes('&', config.getString(player.getUniqueId() + ".rank")
							+ ChatColor.RESET + "] " + ChatColor.RESET + player.getName() + ": " + e.getMessage()));
		}

		else if(player.getWorld().equals(Bukkit.getWorld("warz")))
		{
			Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "[WARZ] "
					+ stats.getRankManager().getRank(player.getUniqueId()).getDisplay() + ChatColor.RESET + " " + "["
					+ ChatColor.translateAlternateColorCodes('&', config.getString(player.getUniqueId() + ".rank"))
					+ ChatColor.RESET + "] " + ChatColor.RESET + player.getName() + ": " + e.getMessage());
		}
	}
}
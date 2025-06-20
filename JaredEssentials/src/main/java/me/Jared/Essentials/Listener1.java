package me.Jared.Essentials;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Listener1 implements Listener
{
	FileConfiguration config = Main.getInstance().getConfig();

	Main plugin;

	public Listener1(Main pl)
	{
		this.plugin = pl;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		int size = Bukkit.getServer().getOnlinePlayers().size();
		if(size > 1)
		{
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "gg setmaxplayers " + size);
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "sg setmaxplayers " + size);
		}
		for(Player p : Bukkit.getOnlinePlayers())
		{
			PacketUtils.sendTabHF(p, ChatColor.GOLD + "JaredServer",
					ChatColor.GOLD + "store.jaredcoen.com\n" + ChatColor.GREEN + "Players Online: " + ChatColor.WHITE
							+ Bukkit.getOnlinePlayers().size());
		}

		Player p = e.getPlayer();
		p.setLevel(0);
		p.teleport(Bukkit.getWorld("world").getSpawnLocation());

		if(!(p.hasPermission("jared")))
		{
			e.setJoinMessage(ChatColor.GRAY + p.getName() + " is here :)");
		} else
		{
			e.setJoinMessage(
					ChatColor.DARK_RED + "" + ChatColor.BOLD + "[!!ALERT!!] " + ChatColor.GREEN + "" + ChatColor.BOLD
							+ "PLEASE WELCOME THE SERVER OPERATOR, " + p.getName() + "!");
		}

		if(!(p.hasPlayedBefore()))
		{
			int x = plugin.getConfig().getInt("tutorial.x");
			int y = plugin.getConfig().getInt("tutorial.y");
			int z = plugin.getConfig().getInt("tutorial.z");
			Location location = new Location(Bukkit.getWorld("world"), x, y, z);
			p.teleport(location);
			p.sendMessage(ChatColor.GREEN + "Welcome to the server!");
		}
	}

	// When a player who is not an operator quits it shows a different message than
	// the operator
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		int size = Bukkit.getOnlinePlayers().size() - 1;
		if(Bukkit.getServer().getOnlinePlayers().size() > 1)
		{
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "gg setmaxplayers " + size);
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "sg setmaxplayers " + size);
		}

		for(Player online : Bukkit.getOnlinePlayers())
		{
			PacketUtils.sendTabHF(online, ChatColor.GOLD + "JaredServer",
					ChatColor.GOLD + "store.jaredcoen.com\n" + ChatColor.GREEN + "Players Online: " + ChatColor.WHITE
							+ size);
		}

		Player p = e.getPlayer();
		p.setLevel(0);

		if(!(p.hasPermission("jared")))
		{
			e.setQuitMessage(ChatColor.GRAY + p.getName() + " has left!");
		} else
		{
			e.setQuitMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "SERVER OPERATOR HAS LEFT");
		}
	}

	// Makes the chat look better
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		if(e.getMessage().contains("chicken"))
		{
			p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_HURT, 3, 1);
		}
		if(e.getMessage().contains("cow"))
		{
			p.playSound(p.getLocation(), Sound.ENTITY_COW_DEATH, 3, 1);
		}
		if(e.getMessage().contains("pig"))
		{
			p.playSound(p.getLocation(), Sound.ENTITY_PIG_HURT, 3, 1);
		}
		if(e.getMessage().contains("horse"))
		{
			p.playSound(p.getLocation(), Sound.ENTITY_HORSE_ANGRY, 3, 1);
		}
		if(e.getMessage().contains("parrot"))
		{
			p.playSound(p.getLocation(), Sound.ENTITY_PARROT_AMBIENT, 3, 1);
		}
		if(e.getMessage().contains("cat"))
		{
			p.playSound(p.getLocation(), Sound.ENTITY_CAT_PURREOW, 3, 1);
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e)
	{
		// Don't let player use commands while in combat

		if(e.getMessage().startsWith("/pl") || e.getMessage().startsWith("/plugins"))
		{
			if(!(e.getPlayer().hasPermission("jared")))
			{
				e.setCancelled(true);
				e.getPlayer().sendMessage(
						ChatColor.WHITE + "Plugins (2): " + ChatColor.GREEN + "Die" + ChatColor.WHITE + ", "
								+ ChatColor.GREEN + "Noob");
			}
		}
	}

	@EventHandler
	public void onPlayerCommandSend(PlayerCommandSendEvent event)
	{
		if(!event.getPlayer().hasPermission("jared"))
		{
			Set<String> allowedCommands = new HashSet<>(
					Arrays.asList("eventrsvp","airdrop","shop","kit", "spawn", "warz", "duel", "gg", "sg", "vote", "discord", "texture", "warp"));

			event.getCommands().retainAll(allowedCommands);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();

		if(!p.hasPermission("jared"))
		{
			if(p.getWorld().getName().equals("warz"))
			{
				switch(e.getBlock().getType())
				{
				case LADDER:
					break;
				case GLASS:
					break;
				case GLASS_PANE:
					break;
				default:
					e.setCancelled(true);
					break;
				}
			} else
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();

		if(!p.hasPermission("jared"))
		{
			switch(e.getBlock().getType())
			{
			case COBWEB:
				break;
			case SPONGE:
				break;
			case LADDER:
				break;
			case BRICKS:
				break;
			case STONE_PRESSURE_PLATE:
				break;
			case RED_WOOL:
				break;
			default:
				e.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}

	@EventHandler
	public void onThrow(ProjectileLaunchEvent e)
	{
		if(e.getEntity() instanceof EnderPearl)
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onGrow(BlockFertilizeEvent e)
	{
		if(!(e.getPlayer().hasPermission("jared")))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void ping(ServerListPingEvent e)
	{
		e.setMaxPlayers(e.getNumPlayers() + 1);
	}

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent e)
	{
		if(e.getEntity().getWorld().getName().equals("world"))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onSignChange(PlayerSignOpenEvent e)
	{
		Player player = e.getPlayer();
		if(!(player.hasPermission("jared")))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(HangingBreakByEntityEvent e)
	{
		if(e.getRemover() instanceof Player)
		{
			Player player = (Player) e.getRemover();
			if(e.getEntity() != null)
			{
				if(!(player.hasPermission("jared")))
				{
					if(e.getEntity().getType() == EntityType.ITEM_FRAME
							|| e.getEntity().getType() == EntityType.GLOW_ITEM_FRAME)
					{
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e)
	{
		if(e.getRightClicked() instanceof ItemFrame)
		{
			if(!(e.getPlayer().hasPermission("jared")))
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent e)
	{
		e.setCancelled(true);
	}

	public void sendActionBar(Player player, String message)
	{
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}

	boolean isInRegion(Player player, String stringRegion)
	{
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		World world = WorldGuardPlugin.inst().wrapPlayer(player).getWorld();
		RegionManager regions = container.get(world);

		for(ProtectedRegion r : regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation())))
		{
			if(r.getId().contains(stringRegion))
			{
				return true;
			}
		}
		return false;
	}

	// Disable actionbar from showing when getting hurt
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();

			if(isInRegion(player, "spawn"))
			{
				player.setHealth(20);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();

			if(isInRegion(player, "spawn"))
			{
				player.setHealth(20);
				e.setCancelled(true);
			}
			sendActionBar(player, "");

			Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
			{
				int num = 0;

				@Override
				public void run()
				{
					if(num > 1)
					{
						return;
					}
					num++;
					sendActionBar(player, "");
				}
			}, 0, 20);

			if(e.getDamager() instanceof Player)
			{
				Player damager = (Player) e.getDamager();
				sendActionBar(damager, "");

				Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
				{
					int num = 0;

					@Override
					public void run()
					{
						if(num > 1)
						{
							return;
						}
						num++;
						sendActionBar(damager, "");
					}
				}, 0, 20);
			}
		}
	}

}

package me.Jared.Listeners;

import java.util.HashMap;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.Jared.GunsPlugin;
import me.Jared.Events.AllGunsReloadEvent;
import me.Jared.Events.GunsFireGunEvent;
import me.Jared.Events.PlayerChangeGunValuesEvent;
import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;
import me.Jared.Utils.UtilString;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener
{
	public static HashMap<String, String> lastFired = new HashMap<>();
	public static HashMap<String, Long> lastTime = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		GunsPlugin.getPlugin.onJoin(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		GunsPlugin.getPlugin.onQuit(event.getPlayer());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		Gun gun;
		Item dropped = e.getItemDrop();
		Player dropper = e.getPlayer();
		GunPlayer gp = GunsPlugin.getPlugin.getGunPlayer(dropper);
		if(gp != null && (gp.getLastItemHeld()) != null && (gun = gp.getGun(dropped.getItemStack().getType())) != null
				&& gun.hasClip && gun.changed && gun.reloadGunOnDrop)
		{
			e.setCancelled(true);
			if(!gun.reloading)
			{
				gun.reloadGun();
				gun.owner.toggleAim();
			}
		}
	}

	@EventHandler
	public void onCombo(GunsFireGunEvent e)
	{
		String name = e.getShooterAsPlayer().getName();
		String gunName = e.getGun().getName();
		Long timeNow = System.currentTimeMillis();
		if(!e.getShooter().checkAmmo(e.getGun(), 1) || e.getGun().bulletDelayTime < 5 || e.getGun().roundsPerBurst > 1)
		{
			return;
		}
		Long lastFiredTime;
		String lastGunName;
		if(!lastTime.containsKey(name))
		{
			lastTime.put(name, timeNow);
		}
		if(!lastFired.containsKey(name))
		{
			lastFired.put(name, gunName);
		}
		lastGunName = lastFired.get(name);
		lastFiredTime = lastTime.get(name);
		if(!gunName.equals(lastGunName) && timeNow - lastFiredTime < 400L && (timeNow - lastFiredTime) < 330L)
		{
			e.setCancelled(true);
		}
		lastFired.put(name, gunName);
		lastTime.put(name, timeNow);
	}

	public boolean isInRegion(Player player, String regionName)
	{
		if (WorldGuard.getInstance() == null) {
			Bukkit.getLogger().severe("WorldGuard is not initialized yet!");
			return false;
		}
		Location loc = player.getLocation();
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();

		ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));

		for(ProtectedRegion region : set)
		{
			if(region.getId().contains("spawn"))
			{
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack itm1 = event.getItem();
		GunPlayer gunPlayer = GunsPlugin.getPlugin.getGunPlayer(player);
		Gun holding;
		ItemStack hand = player.getInventory().getItemInMainHand();
		holding = GunsPlugin.getPlugin.getGun(hand.getType(), gunPlayer.getGunValue());
		if(hand.getType() == Material.ENDER_PEARL)
		{
			event.setCancelled(true);
			player.updateInventory();
		}
		if(holding == null)
		{
			return;
		}
		if(event.getAction() == Action.PHYSICAL)
		{
			return;
		}
		// If the player is in a protected region

		if(isInRegion(player, "spawn") || isInRegion(player, "spawn2"))
		{
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					new TextComponent(ChatColor.RED + "" + ChatColor.UNDERLINE + "Not here noob!"));
			event.setCancelled(true);
			return;
		}

		if(itm1 != null)
		{
			GunPlayer gp = GunsPlugin.getPlugin.getGunPlayer(player);
			switch(action)
			{
			case LEFT_CLICK_BLOCK:
			case LEFT_CLICK_AIR:
			{
				if(gp == null)
					break;
				if(GunsPlugin.getPlugin.getConfig().getBoolean(gp.getPlayer().getUniqueId() + ".triggerswap"))
				{
					gp.onClick(GunPlayer.ClickType.RIGHT);
				} else
				{
					gp.onClick(GunPlayer.ClickType.LEFT);
				}
				break;
			}
			case RIGHT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
			{
				if(event.getClickedBlock() != null)
				{
					Block clicked = event.getClickedBlock();
					if(player.getGameMode() != GameMode.CREATIVE)
					{
						if(clicked.getType() == Material.AIR)
						{
							event.setCancelled(true);
							player.sendMessage(UtilString.colour("&cCan't use it while holding a gun!"));
						}
					}
				}
				if(gp == null)
					break;

				if(GunsPlugin.getPlugin.getConfig().getBoolean(gp.getPlayer().getUniqueId() + ".triggerswap"))
				{
					gp.onClick(GunPlayer.ClickType.LEFT);
				} else
				{
					gp.onClick(GunPlayer.ClickType.RIGHT);
				}

				break;
			}
			default:
				break;
			}
		}
	}

	@EventHandler
	public void onAllGunsReload(AllGunsReloadEvent event)
	{
		Player player = event.getPlayer();
		GunPlayer gp = GunsPlugin.getPlugin.getGunPlayer(player);
		gp.reloadAllGuns();
	}

	@EventHandler
	public void onPlayerChangeGunValue(PlayerChangeGunValuesEvent event)
	{
		Player player = event.getPlayer();
		GunPlayer gp = GunsPlugin.getPlugin.getGunPlayer(player);
		gp.setGunValue(event.getGunValue());
	}
}
package me.Jared;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WallsMain extends JavaPlugin implements Listener
{

	public static WallsMain plugin;

	public static Plugin getInstance()
	{
		return plugin;
	}

	@Override
	public void onEnable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Placeable walls plugin is here!!!!!");
		getServer().getPluginManager().registerEvents(this, (Plugin) this);

		plugin = this;
	}

	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Placeable walls plugin is gone!!!!!");
		new PlaceableWall().removeAllWalls();
	}

	public ItemStack getWall()
	{
		ItemStack wall = new ItemStack(Material.BRICKS);
		ItemMeta im = wall.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Placeable Wall");
		wall.setItemMeta(im);
		return wall;
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

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{	
		Player p = e.getPlayer();

		for(ItemStack item : p.getInventory().getContents())
		{
			ItemMeta meta = getWall().getItemMeta();
			if(item == null)
			{
				continue;
			}

			if(item.getType().equals(Material.BRICKS))
			{
				item.setItemMeta(meta);
			}
		}
	}

	//Placing a wall
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{	
		PlaceableWall wall = new PlaceableWall();

		if (e.getBlock().getType() == Material.BRICKS)
		{
			if(!(isInRegion(e.getPlayer(), "spawn")))
			{
				wall.placeWall(e.getPlayer(), e.getBlock().getLocation());
			}
		}
	}

	//Break one block of the wall the whole wall is broken
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		PlaceableWall wall = new PlaceableWall();
		Location location = e.getBlock().getLocation();
		wall.removeWall(location);

	}

	//If there is no one online delete all the walls
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if(Bukkit.getServer().getOnlinePlayers().size() <= 1)
		{
			PlaceableWall wall = new PlaceableWall();
			wall.removeAllWalls();
		}
	}


	//If player shoots the wall delete that ho
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		BlockIterator iterator = new BlockIterator(e.getEntity().getWorld(), e.getEntity().getLocation().toVector(), e.getEntity().getVelocity().normalize(), 0.0D, 4);
		Block hitBlock = null;
		while(iterator.hasNext())
		{
			hitBlock = iterator.next();
			if(hitBlock.getType() != Material.AIR) break;
		}

		PlaceableWall wall = new PlaceableWall();
		if(e.getEntity().getShooter() instanceof Player)
		{
			if(e.getHitBlock() != null)
			{
				wall.hurtWall(e.getHitBlock().getLocation(), 1);
			}
		}

	}
}
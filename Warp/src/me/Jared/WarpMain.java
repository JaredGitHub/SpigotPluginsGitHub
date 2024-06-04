package me.Jared;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.MenuSystem.Menu;
import me.Jared.MenuSystem.PlayerMenuUtility;

public class WarpMain extends JavaPlugin implements Listener
{
	
	private static WarpMain instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Warps plugin is here!");
		
		Bukkit.getPluginCommand("warp").setExecutor(new WarpCommand());
		getServer().getPluginManager().registerEvents(this, (Plugin)this);
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Warps plugin is gone!");
	}
	
	public static WarpMain getInstance()
	{
		return instance;
	}
	
	private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
	
	public static PlayerMenuUtility getPlayerMenuUtility(Player p)
	{
		PlayerMenuUtility playerMenuUtility;
		
		if(playerMenuUtilityMap.containsKey(p))
		{
			return playerMenuUtilityMap.get(p);
		}
		else
		{
			playerMenuUtility = new PlayerMenuUtility(p);
			playerMenuUtilityMap.put(p, playerMenuUtility);
			
			return playerMenuUtility;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() == null) return;

		InventoryHolder holder = e.getInventory().getHolder();

		if(holder instanceof Menu)
		{
			if(e.getCurrentItem() == null) return;

			Menu menu = (Menu) holder;
			menu.handleMenu(e);

			if(e.getWhoClicked().getOpenInventory().getBottomInventory().getType().equals(InventoryType.PLAYER))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
}

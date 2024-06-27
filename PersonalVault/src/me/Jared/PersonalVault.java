package me.Jared;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.Listeners.VaultListener;
import me.Jared.MenuSystem.PlayerMenuUtility;
import me.Jared.VaultCommand.VaultCommand;

public class PersonalVault extends JavaPlugin
{
	
	private static PersonalVault personalVault;
	
	public static PersonalVault getInstance()
	{
		return personalVault;
	}
	private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
	
	@Override
	public void onEnable()
	{
		personalVault = this;
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Ranked Ender Chests plugin is here!!");
		
		Bukkit.getPluginCommand("pv").setExecutor(new VaultCommand());
		Bukkit.getPluginManager().registerEvents(new VaultListener(), this);
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Ranked Ender Chests plugin is gone OH NOOOOO!!");
	}
	
	public PlayerMenuUtility getPlayerMenuUtility(Player p)
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
}

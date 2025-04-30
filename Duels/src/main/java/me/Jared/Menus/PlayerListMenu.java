package me.Jared.Menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Manager.ConfigManager;

public class PlayerListMenu extends DuelsMenu
{
	protected PlayerMenuUtility playerMenuUtility;
	private Player playerViewer;

	public PlayerListMenu(PlayerMenuUtility playerMenuUtility, Player player)
	{
		super(playerMenuUtility);
		this.playerViewer = player;
	}

	@Override
	public String getMenuName()
	{
		return ChatColor.GREEN + "List of players to duel!";
	}

	@Override
	public int getSlots()
	{
		return 36;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();

		for(int i = 0; i < 27; i++)
		{
			if(e.getSlot() == i)
			{
				Bukkit.dispatchCommand(player, "duel " + e.getCurrentItem().getItemMeta().getDisplayName().toString());
			}
		}

		if(e.getSlot() == 35)
		{
			//go next page of players
		}
	}

	@Override
	public void setMenuItems()
	{
		ConfigManager.iconCreate(new ItemStack(Material.ARROW), ChatColor.GRAY + "Back", inventory, 27);
		ConfigManager.iconCreate(new ItemStack(Material.ARROW), ChatColor.GRAY + "Next", inventory, 35);

		if(Bukkit.getOnlinePlayers().size() <= 1)
		{
			for(int i = 0; i < 27; i++)
			{
				ConfigManager.iconCreate(new ItemStack(Material.PAPER), "You are the only player!", inventory, i);
			}
			return;
		}

		int numberOfPlayers = 0;
		for(Player player : Bukkit.getOnlinePlayers())
		{		
			if(player.equals(playerViewer))
			{
				continue;
			}
			ConfigManager.iconCreate(new ItemStack(Material.PAPER), player.getName(), inventory, numberOfPlayers);
			numberOfPlayers++;

		}
	}
}
package me.Jared.Menu;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.Jared.PersonalVault;
import me.Jared.VaultUtil;
import me.Jared.MenuSystem.PersonalVaultMenu;
import me.Jared.MenuSystem.PlayerMenuUtility;

public class PVMenu extends PersonalVaultMenu
{
	private PersonalVault pv = PersonalVault.getInstance();
	FileConfiguration config = pv.getConfig();

	public PVMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		if(playerMenuUtility.getOwner().getWorld().equals(Bukkit.getWorld("warz")))
		{
			return "Buy Rank at shop.jaredcoen.com for more slots!";
		}
		else
		{
			return "Personal Vault (/pv to view)";
		}
	}

	@Override
	public int getSlots()
	{
		Player player = super.playerMenuUtility.getOwner();

		int size;
		if(player.hasPermission("ranks.mvpplus"))
		{
			size = config.getInt("mvpplus") * 9;

		} else if(player.hasPermission("ranks.mvp"))
		{
			size = config.getInt("mvp") * 9;

		} else if(player.hasPermission("ranks.vipplus"))
		{
			size = config.getInt("vipplus") * 9;

		} else if(player.hasPermission("ranks.vip"))
		{
			size = config.getInt("vip") * 9;

		} else if(player.hasPermission("ranks.default"))
		{
			size = config.getInt("default") * 9;
		}
		else
		{
			size = 9;
		}
		return size;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{

	}

	@Override
	public void setMenuItems()
	{
		Player player = playerMenuUtility.getOwner();
		VaultUtil.loadInventory(player, inventory, player.getWorld().getName());
	}
}

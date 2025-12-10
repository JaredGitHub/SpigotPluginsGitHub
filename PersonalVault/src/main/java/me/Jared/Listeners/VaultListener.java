package me.Jared.Listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import me.Jared.PersonalVault;
import me.Jared.VaultUtil;
import me.Jared.Menu.PVMenu;

public class VaultListener implements Listener
{

	@EventHandler
	public void onChestOpen(PlayerInteractEvent e)
	{
		if(e.getClickedBlock() == null)
			return;
		if(e.getClickedBlock().getType() == Material.ENDER_CHEST && e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			e.setCancelled(true);

			Player player = e.getPlayer();

			player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1, 1);
			PVMenu pv = new PVMenu(PersonalVault.getInstance().getPlayerMenuUtility(player));
			pv.open();
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		if(e.getInventory() == null) return;

		InventoryHolder holder = e.getInventory().getHolder();

		if(holder instanceof PVMenu)
		{
			if(e.getPlayer() instanceof Player)
			{
				Player player = (Player) e.getPlayer();
				VaultUtil.saveInventory(player, e.getInventory(), player.getWorld().getName());
			}
		}

	}
}

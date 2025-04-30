package me.Jared.Menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.Jared.Kits.Main;
import me.Jared.Managers.KitManager;
import me.Jared.MenuSystem.KitsMenu;
import me.Jared.MenuSystem.PlayerMenuUtility;
import me.Jared.Util.Cooldown;
import net.md_5.bungee.api.ChatColor;

public class SelectKitMenu extends KitsMenu
{

	public SelectKitMenu(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
		setPlayer(playerMenuUtility.getOwner());
	}

	public String getMenuName()
	{
		return ChatColor.BLUE + "Select your kit!";
	}

	public int getSlots()
	{
		return 9;
	}

	private Player player;

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player p)
	{
		this.player = p;
	}

	public static Cooldown diamondCooldown = new Cooldown(60);

	public static Cooldown ironCooldown = new Cooldown(15);

	public void handleMenu(InventoryClickEvent e)
	{
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		switch (e.getCurrentItem().getType())
		{
		//If the player clicks on the diamond chestplate it will give them the diamond kit.
		case DIAMOND_CHESTPLATE:
			if (!diamondCooldown.isOnCooldown(p))
			{
				KitManager.diamondKit(p);
				diamondCooldown.putInCooldown(p);
				p.closeInventory();
				break;
			}
			p.sendMessage(ChatColor.RED + "There are " + ChatColor.DARK_RED + diamondCooldown.getCooldownSeconds(p)
			+ ChatColor.RED + " seconds left for your diamond kit!");
			break;

			//If the player clicks on the iron chestplate it will give them the iron kit.
		case IRON_CHESTPLATE:
			if (!ironCooldown.isOnCooldown(p))
			{
				KitManager.ironKit(p);
				ironCooldown.putInCooldown(p);
				p.closeInventory();
				break;
			}
			p.sendMessage(ChatColor.RED + "There are " + ChatColor.DARK_RED + ironCooldown.getCooldownSeconds(p)
			+ ChatColor.RED + " seconds left for your iron kit!");
			break;

			//Opens up the menu to select different items to save into the config for their hotbar
		case ENDER_EYE:
			p.closeInventory();

			if (Main.getInstance().getConfig().get("PlayerUniqueID." + p.getUniqueId()) == null)
			{
				KitManager.defaultHotBar(p.getUniqueId());
			}

			ItemSelectionMenu menu = new ItemSelectionMenu(Main.getPlayerMenuUtility(p));
			menu.open();
			break;
		default:
			break;
		}
	}

	public void setMenuItems()
	{
		KitManager.iconCreate(new ItemStack(Material.DIAMOND_CHESTPLATE), ChatColor.BLUE + "DIAMOND", this.inventory,
				5);
		KitManager.iconCreate(new ItemStack(Material.IRON_CHESTPLATE), ChatColor.WHITE + "IRON", this.inventory, 3);
		KitManager.iconCreate(new ItemStack(Material.ENDER_EYE), ChatColor.GOLD + "Hotbar Editor", this.inventory, 0);
	}
}

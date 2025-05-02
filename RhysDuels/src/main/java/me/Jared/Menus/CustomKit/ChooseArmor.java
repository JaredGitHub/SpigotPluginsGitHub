package me.Jared.Menus.CustomKit;

import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Duels;
import me.Jared.Manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChooseArmor extends DuelsMenu
{
	private final Material[] leatherArmor = new Material[] { Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS };

	private final Material[] chainmailArmor = new Material[] { Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS };

	private final Material[] ironArmor = new Material[] { Material.IRON_HELMET, Material.IRON_CHESTPLATE,
			Material.IRON_LEGGINGS, Material.IRON_BOOTS };

	private final Material[] goldArmor = new Material[] { Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
			Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS };

	private final Material[] diamondArmor = new Material[] { Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS };

	private final Material[] netheriteArmor = new Material[] { Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
			Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS };

	protected PlayerMenuUtility playerMenuUtility;

	public ChooseArmor(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return "Choose your armor!";
	}

	@Override
	public int getSlots()
	{
		return 9;
	}

	private static int armorNumber;

	public static int getArmorNumber()
	{
		return armorNumber;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		String uuid = player.getUniqueId().toString();

		//Setting the armor number to the slot number
		switch(e.getSlot())
		{
		case 2:
			ConfigManager.setArmorKit(uuid, leatherArmor);
			player.sendMessage(ChatColor.GREEN + "You have chosen Leather Armor!");
			break;
		case 3:
			ConfigManager.setArmorKit(uuid, chainmailArmor);
			player.sendMessage(ChatColor.GREEN + "You have chosen Chainmail Armor!");
			break;
		case 4:
			ConfigManager.setArmorKit(uuid, goldArmor);
			player.sendMessage(ChatColor.GREEN + "You have chosen Gold Armor!");
			break;
		case 5:
			ConfigManager.setArmorKit(uuid, ironArmor);
			player.sendMessage(ChatColor.GREEN + "You have chosen Iron Armor!");
			break;
		case 6:
			ConfigManager.setArmorKit(uuid, diamondArmor);
			player.sendMessage(ChatColor.GREEN + "You have chosen Diamond Armor!");
			break;

		default:
			break;
		}
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
		player.closeInventory();
	}

	@Override
	public void setMenuItems()
	{
		ConfigManager.iconCreate(new ItemStack(Material.LEATHER_CHESTPLATE), ChatColor.GRAY + "Leather", inventory, 2);
		ConfigManager.iconCreate(new ItemStack(Material.CHAINMAIL_CHESTPLATE), ChatColor.GRAY + "Chain", inventory, 3);
		ConfigManager.iconCreate(new ItemStack(Material.GOLDEN_CHESTPLATE), ChatColor.GRAY + "Gold", inventory, 4);
		ConfigManager.iconCreate(new ItemStack(Material.IRON_CHESTPLATE), ChatColor.WHITE + "Iron", inventory, 5);
		ConfigManager.iconCreate(new ItemStack(Material.DIAMOND_CHESTPLATE), ChatColor.BLUE + "Diamond", inventory, 6);
	}
}

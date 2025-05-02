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

public class ChooseWeapon extends DuelsMenu
{
	private final ItemStack[] weapons = new ItemStack[] { new ItemStack(Material.MACE, 1),
			new ItemStack(Material.TRIDENT, 1), new ItemStack(Material.NETHERITE_SWORD, 1),
			new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.IRON_SWORD, 1),
			new ItemStack(Material.STONE_SWORD, 1), new ItemStack(Material.GOLDEN_SWORD, 1),
			new ItemStack(Material.WOODEN_SWORD, 1), new ItemStack(Material.SHIELD, 1),
			new ItemStack(Material.NETHERITE_AXE, 1), new ItemStack(Material.DIAMOND_AXE, 1),
			new ItemStack(Material.IRON_AXE, 1), new ItemStack(Material.STONE_AXE, 1),
			new ItemStack(Material.GOLDEN_AXE, 1), new ItemStack(Material.WOODEN_AXE, 1),
			new ItemStack(Material.BOW, 1),
			new ItemStack(Material.CROSSBOW, 1),
			new ItemStack(Material.FISHING_ROD, 1)};

	protected PlayerMenuUtility playerMenuUtility;

	public ChooseWeapon(PlayerMenuUtility playerMenuUtility)
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
		return 18;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		String uuid = player.getUniqueId().toString();

		//Setting the armor number to the slot number
		int slot = e.getSlot();

		if(slot >= 0 && slot < weapons.length)
		{
			ItemStack selectedWeapon = weapons[slot];
			String displayName;

			if(selectedWeapon != null && selectedWeapon.hasItemMeta() && selectedWeapon.getItemMeta().hasDisplayName())
			{
				displayName = selectedWeapon.getItemMeta().getDisplayName();
			} else
			{
				displayName = formatMaterialName(selectedWeapon.getType());
			}

			ConfigManager.setHotbarKit(uuid, weapons[slot]);
			player.sendMessage(
					ChatColor.GREEN + "You have chosen " + displayName + " to add to your hotbar!");
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);
		}
	}

	private String formatMaterialName(Material material)
	{
		String formatted = material.toString().toLowerCase().replace("_", " ");
		return Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
	}

	@Override
	public void setMenuItems()
	{
		int i = 0;
		for(ItemStack weapon : weapons)
		{
			inventory.setItem(i, weapon);
			i++;
		}

	}
}

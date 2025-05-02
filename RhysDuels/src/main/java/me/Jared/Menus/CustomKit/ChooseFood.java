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

public class ChooseFood extends DuelsMenu
{
	// Each food item is paired with its realistic amount based on Minecraft mechanics
	private final ItemStack[] foods = new ItemStack[] { new ItemStack(Material.COOKED_BEEF, 64),
			// Standard stack size for cooked beef
			new ItemStack(Material.COOKED_CHICKEN, 16), // Reasonable stack size for cooked chicken
			new ItemStack(Material.COOKED_MUTTON, 16), // Reasonable stack size for cooked mutton
			new ItemStack(Material.COOKED_PORKCHOP, 64), // Standard stack size for cooked porkchop
			new ItemStack(Material.COOKED_RABBIT, 16), // Reasonable stack size for cooked rabbit
			new ItemStack(Material.BAKED_POTATO, 64), // Standard stack size for baked potatoes
			new ItemStack(Material.MUSHROOM_STEW, 1), // Mushroom stew is typically one item per slot
			new ItemStack(Material.RABBIT_STEW, 1), // Rabbit stew is also typically one item per slot
			new ItemStack(Material.GLOW_BERRIES, 64), // Glow berries can stack up to 64
			new ItemStack(Material.PUMPKIN_PIE, 16), // Pumpkin pie typically stacks to 16
			new ItemStack(Material.SWEET_BERRIES, 64), // Sweet berries stack to 64
			new ItemStack(Material.CAKE, 1), // Cake is a single item
			new ItemStack(Material.MELON_SLICE, 64), // Melon slices stack to 64
			new ItemStack(Material.HONEY_BOTTLE, 1), // Honey bottles are a single item
			new ItemStack(Material.APPLE, 64), // Apples stack to 64
			new ItemStack(Material.GOLDEN_APPLE, 3), // Golden apple is a single item
			new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1) // Enchanted golden apple is a single item
	};

	protected PlayerMenuUtility playerMenuUtility;

	public ChooseFood(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return "Choose your food!";
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

		// Setting the food item based on the slot clicked
		int slot = e.getSlot();

		if(slot >= 0 && slot < foods.length)
		{
			ItemStack selectedFood = foods[slot];
			String displayName;

			if(selectedFood != null && selectedFood.hasItemMeta() && selectedFood.getItemMeta().hasDisplayName())
			{
				displayName = selectedFood.getItemMeta().getDisplayName();
			} else
			{
				displayName = formatMaterialName(selectedFood.getType());
			}

			// Set the chosen food to the player's hotbar
			ConfigManager.setHotbarKit(uuid, foods[slot]);
			player.sendMessage(ChatColor.GREEN + "You have chosen " + displayName + " to add to your hotbar!");
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
		for(ItemStack food : foods)
		{
			inventory.setItem(i, food);
			i++;
		}
	}
}

package me.Jared.Menus.CustomKit;

import me.Jared.DuelMenuSystem.DuelsMenu;
import me.Jared.DuelMenuSystem.PlayerMenuUtility;
import me.Jared.Manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class ChooseMisc extends DuelsMenu
{
	private final List<ItemStack> miscItems = Arrays.asList(
			// Arrows
			new ItemStack(Material.ARROW, 64), createHarmArrow(), new ItemStack(Material.SPECTRAL_ARROW, 32),

			// Potions
			createHealingPotion(), createStrengthPotion(), createSpeedPotion(), createResistancePotion(),
			createJumpBoostPotion(), createFireResistancePotion(), createRegenerationPotion(),
			new ItemStack(Material.MILK_BUCKET, 1),

			// Utility Items
			new ItemStack(Material.TOTEM_OF_UNDYING, 1), new ItemStack(Material.FLINT_AND_STEEL, 1),
			new ItemStack(Material.ENDER_PEARL, 16), new ItemStack(Material.EXPERIENCE_BOTTLE, 16),
			new ItemStack(Material.GOLDEN_APPLE, 8), new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1),
			new ItemStack(Material.SHIELD, 1), new ItemStack(Material.BUCKET, 1), new ItemStack(Material.LEAD, 1),
			new ItemStack(Material.SADDLE, 1), new ItemStack(Material.MAP, 1), new ItemStack(Material.NAME_TAG, 1),

			// Building Blocks
			new ItemStack(Material.OAK_PLANKS, 64), new ItemStack(Material.COBBLESTONE, 64),
			new ItemStack(Material.STONE_BRICKS, 64), new ItemStack(Material.BRICKS, 64),
			new ItemStack(Material.GLASS, 64), new ItemStack(Material.SAND, 64), new ItemStack(Material.DIRT, 64),
			new ItemStack(Material.GRASS_BLOCK, 64), new ItemStack(Material.NETHERRACK, 64),
			new ItemStack(Material.OBSIDIAN, 32), new ItemStack(Material.SMOOTH_STONE, 64),
			new ItemStack(Material.TERRACOTTA, 64), new ItemStack(Material.MOSS_BLOCK, 64),

			// More Misc
			new ItemStack(Material.LANTERN, 4), new ItemStack(Material.TNT, 16), new ItemStack(Material.REDSTONE, 64),
			new ItemStack(Material.REPEATER, 4), new ItemStack(Material.SLIME_BLOCK, 16),
			new ItemStack(Material.HONEY_BLOCK, 16), new ItemStack(Material.BONE, 16),
			new ItemStack(Material.STRING, 16), new ItemStack(Material.FISHING_ROD, 1),
			new ItemStack(Material.SNOWBALL, 16), new ItemStack(Material.FIRE_CHARGE, 8),
			new ItemStack(Material.POWDER_SNOW_BUCKET, 1), new ItemStack(Material.WATER_BUCKET, 1),
			new ItemStack(Material.LAVA_BUCKET, 1), new ItemStack(Material.MUSIC_DISC_CAT, 1));

	public ChooseMisc(PlayerMenuUtility playerMenuUtility)
	{
		super(playerMenuUtility);
	}

	@Override
	public String getMenuName()
	{
		return "Choose your miscellaneous items!";
	}

	@Override
	public int getSlots()
	{
		return 54;
	}

	@Override
	public void handleMenu(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		String uuid = player.getUniqueId().toString();
		int slot = e.getSlot();

		if(slot >= 0 && slot < miscItems.size())
		{
			ItemStack selectedItem = miscItems.get(slot).clone();
			String displayName = selectedItem.getType().toString().toLowerCase().replace("_", " ");
			displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

			if(selectedItem.hasItemMeta() && selectedItem.getItemMeta().hasDisplayName())
			{
				displayName = selectedItem.getItemMeta().getDisplayName();
			}

			ConfigManager.setInventoryKit(uuid, selectedItem);
			player.sendMessage(ChatColor.GREEN + "You have added " + displayName + " to your kit inventory!");
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.5f);

		}

	}

	@Override
	public void setMenuItems()
	{
		for(int i = 0; i < miscItems.size(); i++)
		{
			inventory.setItem(i, miscItems.get(i));
		}
	}

	private ItemStack createHarmArrow()
	{
		ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 16);
		PotionMeta meta = (PotionMeta) arrow.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1), true);
		meta.setDisplayName(ChatColor.RESET + "Arrow of Harming");
		arrow.setItemMeta(meta);
		return arrow;
	}

	private ItemStack createHealingPotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1), true);
		meta.setDisplayName(ChatColor.RESET + "Healing Potion");
		potion.setItemMeta(meta);
		return potion;
	}

	private ItemStack createStrengthPotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 3600, 0), true);
		meta.setDisplayName(ChatColor.RESET + "Strength Potion");
		potion.setItemMeta(meta);
		return potion;
	}

	private ItemStack createSpeedPotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0), true);
		meta.setDisplayName(ChatColor.RESET + "Speed Potion");
		potion.setItemMeta(meta);
		return potion;
	}

	private ItemStack createResistancePotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, 3600, 0), true);
		meta.setDisplayName(ChatColor.RESET + "Resistance Potion");
		potion.setItemMeta(meta);
		return potion;
	}

	private ItemStack createJumpBoostPotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 3600, 0), true);
		meta.setDisplayName(ChatColor.RESET + "Jump Boost Potion");
		potion.setItemMeta(meta);
		return potion;
	}

	private ItemStack createFireResistancePotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 0), true);
		meta.setDisplayName(ChatColor.RESET + "Fire Resistance Potion");
		potion.setItemMeta(meta);
		return potion;
	}

	private ItemStack createRegenerationPotion()
	{
		ItemStack potion = new ItemStack(Material.POTION, 1);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 0), true);
		meta.setDisplayName(ChatColor.RESET + "Regeneration Potion");
		potion.setItemMeta(meta);
		return potion;
	}
}
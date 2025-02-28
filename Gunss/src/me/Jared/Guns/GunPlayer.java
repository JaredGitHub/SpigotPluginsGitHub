/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.Jared.Guns;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.Jared.GunsPlugin;
import me.Jared.Guns.constant.GunValue;
import me.Jared.Utils.InventoryUtils;

public class GunPlayer
{
	private Player controller;
	private ItemStack lastHeldItem;
	private ArrayList<Gun> m4guns;
	private Gun currentlyFiring;
	private GunValue gunValue = GunValue.M4;

	public GunPlayer(GunsPlugin plugin, Player player)
	{
		this.controller = player;
		this.m4guns = plugin.getLoadedGuns();
		for (Gun g : this.m4guns)
		{
			g.owner = this;
		}
	}

	public boolean isAimedIn()
	{
		if (this.controller == null)
		{
			return false;
		}
		if (!this.controller.isOnline())
		{
			return false;
		}
		return this.controller.hasPotionEffect(PotionEffectType.SLOWNESS);
	}

	public void onClick(ClickType t)
	{
		Gun holding = null;
		ItemStack hand = this.controller.getInventory().getItemInMainHand();
		if (hand != null)
		{
			holding = this.getGunByType(hand);
		}
		if (holding != null)
		{
			switch (t)
			{
			case LEFT:
			{
				this.toggleAim();
				break;
			}
			case RIGHT:
			{
				++holding.heldDownTicks;
				holding.lastFired = 0;
				if (this.currentlyFiring != null)
					break;
				this.fireGun(holding);
			}
			}
		}
	}

	public void toggleAim()
	{
		if (this.isAimedIn())
		{
			this.controller.removePotionEffect(PotionEffectType.SLOWNESS);
		} else
		{
			this.controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 4));
		}
	}

	private void fireGun(Gun gun)
	{
		if (gun.timer <= 0)
		{
			this.currentlyFiring = gun;
			gun.firing = true;
		}
	}

	public void tick()
	{
		if (this.controller != null)
		{
			ItemStack hand;
			this.lastHeldItem = hand = this.controller.getInventory().getItemInMainHand();
			Gun g2 =GunsPlugin.getPlugin.getGun(hand.getType(), this.gunValue);
			
			if (hand != null && (g2) == null)
			{
				this.controller.removePotionEffect(PotionEffectType.SLOWNESS);
			}
			for (Gun gun : this.m4guns)
			{
				if (gun == null)
					continue;
				gun.tick();
				if (this.controller.isDead())
				{
					gun.finishReloading();
				}
				if (this.currentlyFiring == null || gun.timer > 0 || !this.currentlyFiring.equals(gun))
					continue;
				this.currentlyFiring = null;
			}
			this.renameGuns();
		}
	}

	private void renameGuns()
	{
		ItemStack[] items;
		PlayerInventory playerInventory = this.controller.getInventory();
		ItemStack[] itemStackArray = items = playerInventory.getContents();
		int n = items.length;
		int n2 = 0;
		while (n2 < n)
		{
			String name;
			ItemStack is = itemStackArray[n2];
			if (is != null && (name = this.getGunName(is)) != null && name.length() > 0)
			{
				this.setName(is, name);
			}
			++n2;
		}
	}

	public Gun getGunByType(ItemStack item)
	{
		if (item == null)
		{
			return null;
		}
		Material mat = item.getType();
		for (Gun g : this.m4guns)
		{
			if (g.getGunType() != mat)
				continue;
			return g;
		}
		return null;
	}

	public String getGunName(ItemStack item)
	{
		return this.getGunName(this.getGunByType(item));
	}

	private String getGunName(Gun gun)
	{
		if (gun == null)
		{
			return null;
		}
		String adds = "";
		if (gun.hasClip)
		{
			int leftInClip = 0;
			int ammoLeft = 0;
			int maxInClip = gun.maxClipSize;
			int currentAmmo = Math
					.abs(InventoryUtils.amtItem((Inventory) this.controller.getInventory(), gun.getAmmoType())
							/ gun.getAmmoAmtNeeded());
			ammoLeft = currentAmmo - maxInClip + gun.roundsFired;
			if (ammoLeft < 0)
			{
				ammoLeft = 0;
			}
			leftInClip = currentAmmo - ammoLeft;
			adds = ChatColor.YELLOW + "   \u00ab" + leftInClip + " | " + ammoLeft + "\u00bb";
			if (gun.reloading)
			{
				return ChatColor.WHITE + "Reloading...";
			}
		}
		return ChatColor.YELLOW + gun.getName() + adds;
	}

	public ItemStack setName(ItemStack item, String name)
	{
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		return item;
	}

	public Player getPlayer()
	{
		return this.controller;
	}

	public void unload()
	{
		this.controller = null;
		this.currentlyFiring = null;
		for (Gun g : this.m4guns)
		{
			g.clear();
		}
	}

	public void reloadAllGuns()
	{
		for (Gun g : this.m4guns)
		{
			if (g == null)
				continue;
			g.reloadGun();
			g.finishReloading();
		}
	}

	public boolean checkAmmo(Gun gun, int amount)
	{
		return InventoryUtils.amtItem((Inventory) this.controller.getInventory(), gun.getAmmoType()) >= amount;
	}

	public void removeAmmo(Gun gun, int amount)
	{
		if (amount == 0)
		{
			return;
		}
		InventoryUtils.removeItem(this.controller, gun.getAmmoType(), amount);
	}

	public ItemStack getLastItemHeld()
	{
		return this.lastHeldItem;
	}

	public Gun getGun(Material mat)
	{
		for (Gun g : this.m4guns)
		{
			if (g.getGunType() != mat)
				continue;
			return g;
		}
		return null;
	}

	public GunValue getGunValue()
	{
		return this.gunValue;
	}

	public void setGunValue(GunValue gunValue)
	{
		this.gunValue = gunValue;
	}

	public static enum ClickType
	{
		LEFT, RIGHT;

	}
}

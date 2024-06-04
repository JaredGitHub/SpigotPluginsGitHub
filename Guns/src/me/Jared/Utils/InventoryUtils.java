/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package me.Jared.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {
    public static int amtItem(Inventory inventory, Material mat) {
        int ret = 0;
        if (inventory != null) {
            ItemStack[] items = inventory.getContents();
            int slot = 0;
            while (slot < items.length) {
                if (items[slot] != null) {
                    Material id = items[slot].getType();
                    int amt = items[slot].getAmount();
                    if (id == mat) {
                        ret += amt;
                    }
                }
                ++slot;
            }
        }
        return ret;
    }

    public static void removeItem(Player pl, Material mat, int amt) {
        PlayerInventory playerInventory = pl.getInventory();
        int start = amt;
        if (playerInventory != null) {
            ItemStack[] items = playerInventory.getContents();
            int slot = 0;
            while (slot < items.length) {
                if (items[slot] != null) {
                    Material id = items[slot].getType();
                    int itmAmt = items[slot].getAmount();
                    if (id == mat) {
                        if (amt > 0) {
                            if (itmAmt >= amt) {
                                itmAmt -= amt;
                                amt = 0;
                            } else {
                                amt = start - itmAmt;
                                itmAmt = 0;
                            }
                            if (itmAmt > 0) {
                                playerInventory.getItem(slot).setAmount(itmAmt);
                            } else {
                                playerInventory.setItem(slot, null);
                            }
                        }
                        if (amt <= 0) {
                            return;
                        }
                    }
                }
                ++slot;
            }
        }
    }
}


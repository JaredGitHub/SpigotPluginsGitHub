/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package me.Jared.Utils;

import org.bukkit.ChatColor;

public class UtilString {
    public static String colour(String msg) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)msg);
    }
}


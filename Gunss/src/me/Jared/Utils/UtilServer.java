/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.Jared.Utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.Jared.GunsPlugin;

public class UtilServer {
    private static GunsPlugin PLUGIN = (GunsPlugin)JavaPlugin.getPlugin(GunsPlugin.class);

    public static void runTask(Runnable runnable) {
        PLUGIN.getServer().getScheduler().runTask((Plugin)PLUGIN, runnable);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long period) {
        PLUGIN.getServer().getScheduler().runTaskTimer((Plugin)PLUGIN, runnable, delay, period);
    }

    public static void runTaskTimerAsync(Runnable runnable, long delay, long period) {
        PLUGIN.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)PLUGIN, runnable, delay, period);
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.Jared.Events;

import org.bukkit.entity.Player;

public class AllGunsReloadEvent
extends GunsEvent {
    private Player player;

    public AllGunsReloadEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}


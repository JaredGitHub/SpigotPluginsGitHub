/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.Jared.Events;

import org.bukkit.entity.Player;

import me.Jared.Guns.constant.GunValue;

public class PlayerChangeGunValuesEvent
extends GunsEvent {
    private Player player;
    private GunValue gunValue;

    public PlayerChangeGunValuesEvent(Player player, GunValue gunValue) {
        this.player = player;
        this.gunValue = gunValue;
    }

    public Player getPlayer() {
        return this.player;
    }

    public GunValue getGunValue() {
        return this.gunValue;
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package me.Jared.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;

public class GunsKillEntityEvent
extends GunsEvent {
    private Gun gun;
    private GunPlayer shooter;
    private Entity shot;

    public GunsKillEntityEvent(GunPlayer shooter, Gun gun, Entity killed) {
        this.gun = gun;
        this.shooter = shooter;
        this.shot = killed;
    }

    public GunPlayer getKiller() {
        return this.shooter;
    }

    public Player getKillerAsPlayer() {
        return this.shooter.getPlayer();
    }

    public Entity getKilled() {
        return this.shot;
    }

    public Gun getGun() {
        return this.gun;
    }
}


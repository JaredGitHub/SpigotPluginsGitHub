/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package me.Jared.Events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;

public class GunsBulletCollideEvent
extends GunsEvent {
    private Gun gun;
    private GunPlayer shooter;
    private Block blockHit;

    public GunsBulletCollideEvent(GunPlayer shooter, Gun gun, Block block) {
        this.gun = gun;
        this.shooter = shooter;
        this.blockHit = block;
    }

    public Gun getGun() {
        return this.gun;
    }

    public GunPlayer getShooter() {
        return this.shooter;
    }

    public Player getShooterAsPlayer() {
        return this.shooter.getPlayer();
    }

    public Block getBlockHit() {
        return this.blockHit;
    }
}


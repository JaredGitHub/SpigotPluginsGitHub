/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.Jared.Events;

import org.bukkit.entity.Player;

import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;

public class GunsFireGunEvent
extends GunsEvent {
    private Gun gun;
    private GunPlayer shooter;
    private int amountAmmoNeeded;
    private double accuracy;

    public GunsFireGunEvent(GunPlayer shooter, Gun gun) {
        this.gun = gun;
        this.shooter = shooter;
        this.amountAmmoNeeded = gun.getAmmoAmtNeeded();
        this.accuracy = gun.getAccuracy();
        if (shooter.isAimedIn() && gun.getAccuracy_aimed() > -1.0) {
            this.accuracy = gun.getAccuracy_aimed();
        }
    }

    public GunsEvent setAmountAmmoNeeded(int i) {
        this.amountAmmoNeeded = i;
        return this;
    }

    public int getAmountAmmoNeeded() {
        return this.amountAmmoNeeded;
    }

    public double getGunAccuracy() {
        return this.accuracy;
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

    public void setGunAccuracy(double d) {
        this.accuracy = d;
    }
}


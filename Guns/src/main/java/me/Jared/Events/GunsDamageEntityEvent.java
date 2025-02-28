/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 */
package me.Jared.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.Jared.Guns.Gun;
import me.Jared.Guns.GunPlayer;

public class GunsDamageEntityEvent
extends GunsEvent {
    private Gun gun;
    private GunPlayer shooter;
    private Entity shot;
    private boolean isHeadshot;
    private double damage;
    private EntityDamageByEntityEvent event;

    public GunsDamageEntityEvent(EntityDamageByEntityEvent event, GunPlayer shooter, Gun gun, Entity shot, boolean headshot) {
        this.gun = gun;
        this.shooter = shooter;
        this.shot = shot;
        this.isHeadshot = headshot;
        this.damage = gun.getGunDamage();
    }

    public EntityDamageByEntityEvent getEntityDamageEntityEvent() {
        return this.event;
    }

    public boolean isHeadshot() {
        return this.isHeadshot;
    }

    public void setHeadshot(boolean b) {
        this.isHeadshot = b;
    }

    public GunPlayer getShooter() {
        return this.shooter;
    }

    public Entity getEntityDamaged() {
        return this.shot;
    }

    public Player getKillerAsPlayer() {
        return this.shooter.getPlayer();
    }

    public Gun getGun() {
        return this.gun;
    }

    public double getDamage() {
        return this.damage;
    }
}


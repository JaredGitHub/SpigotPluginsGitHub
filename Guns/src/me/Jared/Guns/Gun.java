/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.util.Vector
 */
package me.Jared.Guns;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import me.Jared.GunsPlugin;
import me.Jared.Events.GunsFireGunEvent;

public class Gun {
    private boolean canHeadshot;
    private boolean isThrowable;
    private boolean hasSmokeTrail;
    private Material gunType;
    private Material ammoType;
    private boolean ray;
    private int ammoAmtNeeded;
    private double gunDamage;
    private int explosionDamage = -1;
    public int roundsPerBurst;
    private int reloadTime;
    private int maxDistance;
    private int bulletsPerClick;
    private int bulletsShot;
    private int bulletDelay = 2;
    private int armorPenetration;
    private int releaseTime = 0;
    private double bulletSpeed;
    private double accuracy;
    private double accuracy_aimed = 0.0;
    private double explodeRadius;
    private double fireRadius;
    private double flashRadius;
    private double knockback;
    private double recoil = 0.0;
    private double gunVolume = 2.0;
    private String gunName;
    private String fileName;
    public String projType = "SNOWBALL";
    public ArrayList<String> gunSound = new ArrayList<String>();
    public boolean hasClip = true;
    public boolean reloadGunOnDrop = true;
    public int maxClipSize = 30;
    public int bulletDelayTime = 10;
    public int roundsFired;
    public int gunReloadTimer;
    public int timer;
    public int lastFired;
    public int ticks;
    public int heldDownTicks;
    public boolean firing = false;
    public boolean reloading;
    public boolean changed = false;
    public GunPlayer owner;
    public String node;
    public String reloadType = "NORMAL";

    public Gun(String name) {
        this.gunName = name;
        this.fileName = name;
    }

    public void shoot() {
        if (this.owner != null && this.owner.getPlayer().isOnline() && !this.reloading) {
            GunsFireGunEvent event = new GunsFireGunEvent(this.owner, this);
            GunsPlugin.getPlugin.getServer().getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                if (this.owner.checkAmmo(this, event.getAmountAmmoNeeded()) && event.getAmountAmmoNeeded() > 0 || event.getAmountAmmoNeeded() == 0) {
                    this.owner.removeAmmo(this, event.getAmountAmmoNeeded());
                    if (this.roundsFired >= this.maxClipSize && this.hasClip) {
                        this.reloadGun();
                        return;
                    }
                    this.doRecoil(this.owner.getPlayer());
                    this.changed = true;
                    ++this.roundsFired;
                    int i = 0;
                    while (i < this.gunSound.size()) {
                        Sound sound = GunsPlugin.getSound(this.gunSound.get(i));
                        if (sound != null) {
                            this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), sound, (float)this.gunVolume, 2.0f);
                        }
                        ++i;
                    }
                    i = 0;
                    while (i < this.bulletsPerClick) {
                        int acc = (int)(event.getGunAccuracy() * 1000.0);
                        if (acc <= 0) {
                            acc = 1;
                        }
                        Location ploc = this.owner.getPlayer().getLocation();
                        double dir = -(ploc.getYaw() + 90.0f);
                        double pitch = -ploc.getPitch();
                        double xwep = ((double)(GunsPlugin.random.nextInt(acc) - GunsPlugin.random.nextInt(acc)) + 0.5) / 1000.0;
                        double ywep = ((double)(GunsPlugin.random.nextInt(acc) - GunsPlugin.random.nextInt(acc)) + 0.5) / 1000.0;
                        double zwep = ((double)(GunsPlugin.random.nextInt(acc) - GunsPlugin.random.nextInt(acc)) + 0.5) / 1000.0;
                        double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + xwep;
                        double yd = Math.sin(Math.toRadians(pitch)) + ywep;
                        double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + zwep;
                        Vector vec = new Vector(xd, yd, zd);
                        vec.multiply(this.bulletSpeed);
                        Bullet bullet = new Bullet(this.owner, vec, this);
                        GunsPlugin.getPlugin.addBullet(bullet);
                        ++i;
                    }
                    if (this.roundsFired >= this.maxClipSize && this.hasClip) {
                        this.reloadGun();
                    }
                } else {
                    String noammo = ChatColor.RED + "Out of ammo! " + ChatColor.GOLD;
                    this.owner.getPlayer().playSound(this.owner.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 60.0f, 60.0f);
                    if (this.ammoType == Material.CLAY_BALL) {
                        this.owner.getPlayer().sendMessage(String.valueOf(noammo) + "You need Sniper Bullets");
                    }
                    if (this.ammoType == Material.FLINT) {
                        this.owner.getPlayer().sendMessage(String.valueOf(noammo) + "You need Automatic Ammo");
                    }
                    if (this.ammoType == Material.WHEAT_SEEDS) {
                        this.owner.getPlayer().sendMessage(String.valueOf(noammo) + "You need Shotgun Shells");
                    }
                    if (this.ammoType == Material.ENDER_PEARL) {
                        this.owner.getPlayer().sendMessage(String.valueOf(noammo) + "You need Pistol Ammo");
                    }
                    if (this.ammoType == Material.PUMPKIN_SEEDS) {
                        this.owner.getPlayer().sendMessage(String.valueOf(noammo) + "You need SMG Ammo");
                    }
                    this.finishShooting();
                }
            }
        }
    }

    public void tick() {
        ++this.ticks;
        ++this.lastFired;
        --this.timer;
        --this.gunReloadTimer;
        if (this.gunReloadTimer < 0) {
            if (this.reloading) {
                this.finishReloading();
            }
            this.reloading = false;
        }
        this.gunSounds();
        if (this.lastFired > 6) {
            this.heldDownTicks = 0;
        }
        if (this.heldDownTicks >= 2 && this.timer <= 0 || this.firing && !this.reloading) {
            if (this.roundsPerBurst > 1) {
                if (this.ticks % this.bulletDelay == 0) {
                    ++this.bulletsShot;
                    if (this.bulletsShot <= this.roundsPerBurst) {
                        this.shoot();
                    } else {
                        this.finishShooting();
                    }
                }
            } else {
                this.shoot();
                this.finishShooting();
            }
        }
        if (this.reloading) {
            this.firing = false;
        }
    }

    public Gun copy() {
        Gun g = new Gun(this.gunName);
        g.gunName = this.gunName;
        g.gunType = this.gunType;
        g.ray = this.ray;
        g.ammoAmtNeeded = this.ammoAmtNeeded;
        g.ammoType = this.ammoType;
        g.roundsPerBurst = this.roundsPerBurst;
        g.bulletsPerClick = this.bulletsPerClick;
        g.bulletSpeed = this.bulletSpeed;
        g.accuracy = this.accuracy;
        g.accuracy_aimed = this.accuracy_aimed;
        g.maxDistance = this.maxDistance;
        g.gunVolume = this.gunVolume;
        g.gunDamage = this.gunDamage;
        g.explodeRadius = this.explodeRadius;
        g.fireRadius = this.fireRadius;
        g.flashRadius = this.flashRadius;
        g.canHeadshot = this.canHeadshot;
        g.reloadTime = this.reloadTime;
        g.hasSmokeTrail = this.hasSmokeTrail;
        g.armorPenetration = this.armorPenetration;
        g.isThrowable = this.isThrowable;
        g.projType = this.projType;
        g.node = this.node;
        g.gunSound = this.gunSound;
        g.bulletDelayTime = this.bulletDelayTime;
        g.hasClip = this.hasClip;
        g.maxClipSize = this.maxClipSize;
        g.reloadGunOnDrop = this.reloadGunOnDrop;
        g.fileName = this.fileName;
        g.explosionDamage = this.explosionDamage;
        g.recoil = this.recoil;
        g.knockback = this.knockback;
        g.reloadType = this.reloadType;
        g.releaseTime = this.releaseTime;
        return g;
    }

    public void reloadGun() {
        this.reloading = true;
        this.gunReloadTimer = this.reloadTime;
    }

    private void gunSounds() {
        if (this.reloading) {
            int amtReload = this.reloadTime - this.gunReloadTimer;
            if (this.reloadType.equalsIgnoreCase("bolt")) {
                if (amtReload == 6) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 2.0f, 1.5f);
                }
                if (amtReload == this.reloadTime - 4) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, 1.5f);
                }
            } else if (this.reloadType.equalsIgnoreCase("pump")) {
                int rep = (this.reloadTime - 10) / this.maxClipSize;
                if (amtReload >= 5 && amtReload <= this.reloadTime - 5 && amtReload % rep == 0) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 2.0f);
                }
                if (amtReload == this.reloadTime - 3) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_PISTON_EXTEND, 1.0f, 2.0f);
                }
                if (amtReload == this.reloadTime - 1) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1.0f, 2.0f);
                }
            } else {
                if (amtReload == 6) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_FIRE_AMBIENT, 2.0f, 2.0f);
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1.0f, 2.0f);
                }
                if (amtReload == this.reloadTime / 2) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.33f, 2.0f);
                }
                if (amtReload == this.reloadTime - 4) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_FIRE_AMBIENT, 2.0f, 2.0f);
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, 2.0f);
                }
            }
        } else {
            if (this.reloadType.equalsIgnoreCase("pump")) {
                if (this.timer == 8) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_PISTON_EXTEND, 1.0f, 2.0f);
                }
                if (this.timer == 6) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1.0f, 2.0f);
                }
            }
            if (this.reloadType.equalsIgnoreCase("bolt")) {
                if (this.timer == this.bulletDelayTime - 4) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 2.0f, 1.25f);
                }
                if (this.timer == 6) {
                    this.owner.getPlayer().getWorld().playSound(this.owner.getPlayer().getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, 1.25f);
                }
            }
        }
    }

    private void doRecoil(Player player) {
        if (this.recoil != 0.0) {
            Location ploc = player.getLocation();
            double dir = -ploc.getYaw() - 90.0f;
            double pitch = -ploc.getPitch() - 180.0f;
            double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch));
            double yd = Math.sin(Math.toRadians(pitch));
            double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch));
            Vector vec = new Vector(xd, yd, zd);
            vec.multiply(this.recoil / 2.0).setY(0);
            player.setVelocity(player.getVelocity().add(vec));
        }
    }

    public void doKnockback(Entity entity, Vector speed) {
        if (this.knockback != 0.0) {
            speed.normalize().setY(0.6).multiply(this.knockback / 4.0);
            entity.setVelocity(speed);
        }
    }

    public void finishReloading() {
        this.bulletsShot = 0;
        this.roundsFired = 0;
        this.changed = false;
        this.gunReloadTimer = 0;
    }

    private void finishShooting() {
        this.bulletsShot = 0;
        this.timer = this.bulletDelayTime;
        this.firing = false;
    }

    public String getName() {
        return this.gunName;
    }

    public Material getAmmoType() {
        return this.ammoType;
    }

    public int getAmmoAmtNeeded() {
        return this.ammoAmtNeeded;
    }

    public Material getGunType() {
        return this.gunType;
    }

    public double getExplodeRadius() {
        return this.explodeRadius;
    }

    public double getFireRadius() {
        return this.fireRadius;
    }

    public boolean isThrowable() {
        return this.isThrowable;
    }

    public void setName(String val) {
        this.gunName = val;
    }

    public Material getValueFromString(String str) {
        return Material.valueOf((String)str);
    }

    public void setGunType(String val) {
        this.gunType = this.getValueFromString(val);
    }

    public void setAmmoType(String val) {
        this.ammoType = this.getValueFromString(val);
    }

    public void setAmmoAmountNeeded(int parseInt) {
        this.ammoAmtNeeded = parseInt;
    }

    public void setRoundsPerBurst(int parseInt) {
        this.roundsPerBurst = parseInt;
    }

    public void setBulletsPerClick(int parseInt) {
        this.bulletsPerClick = parseInt;
    }

    public int getBulletsPerClick() {
        return this.bulletsPerClick;
    }

    public void setBulletSpeed(double parseDouble) {
        this.bulletSpeed = parseDouble;
    }

    public void setAccuracy(double parseDouble) {
        this.accuracy = parseDouble;
    }

    public void setAccuracyAimed(double parseDouble) {
        this.accuracy_aimed = parseDouble;
    }

    public void setRay(boolean parseBoolean) {
        this.ray = parseBoolean;
    }

    public void setExplodeRadius(double parseDouble) {
        this.explodeRadius = parseDouble;
    }

    public void setFireRadius(double parseDouble) {
        this.fireRadius = parseDouble;
    }

    public void setCanHeadshot(boolean parseBoolean) {
        this.canHeadshot = parseBoolean;
    }

    public void clear() {
        this.owner = null;
    }

    public void setReloadTime(int parseInt) {
        this.reloadTime = parseInt;
    }

    public int getReloadTime() {
        return this.reloadTime;
    }

    public boolean getRay() {
        return this.ray;
    }

    public double getGunDamage() {
        return this.gunDamage;
    }

    public void setGunDamage(double parseInt) {
        this.gunDamage = parseInt;
    }

    public double getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(int i) {
        this.maxDistance = i;
    }

    public void setFlashRadius(double parseDouble) {
        this.flashRadius = parseDouble;
    }

    public double getFlashRadius() {
        return this.flashRadius;
    }

    public void setIsThrowable(boolean b) {
        this.isThrowable = b;
    }

    public boolean canHeadShot() {
        return this.canHeadshot;
    }

    public boolean hasSmokeTrail() {
        return this.hasSmokeTrail;
    }

    public void setSmokeTrail(boolean b) {
        this.hasSmokeTrail = b;
    }

    public void setArmorPenetration(int parseInt) {
        this.armorPenetration = parseInt;
    }

    public int getArmorPenetration() {
        return this.armorPenetration;
    }

    public void setExplosionDamage(int i) {
        this.explosionDamage = i;
    }

    public int getExplosionDamage() {
        return this.explosionDamage;
    }

    public String getFilename() {
        return this.fileName;
    }

    public void setFilename(String string) {
        this.fileName = string;
    }

    public void setRecoil(double d) {
        this.recoil = d;
    }

    public double getRecoil() {
        return this.recoil;
    }

    public void setKnockback(double d) {
        this.knockback = d;
    }

    public double getKnockback() {
        return this.knockback;
    }

    public void addGunSounds(String val) {
        String[] sounds = val.split(",");
        int i = 0;
        while (i < sounds.length) {
            this.gunSound.add(sounds[i]);
            ++i;
        }
    }

    public int getReleaseTime() {
        return this.releaseTime;
    }

    public void setReleaseTime(int v) {
        this.releaseTime = v;
    }

    public void setGunVolume(double parseDouble) {
        this.gunVolume = parseDouble;
    }

    public double getGunVolume() {
        return this.gunVolume;
    }

    public double getAccuracy() {
        return this.accuracy;
    }

    public double getAccuracy_aimed() {
        return this.accuracy_aimed;
    }
}


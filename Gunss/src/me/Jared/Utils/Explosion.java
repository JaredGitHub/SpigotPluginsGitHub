/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package me.Jared.Utils;

import org.bukkit.Location;

public class Explosion {
    private Location location;

    public Explosion(Location location) {
        this.location = location;
    }

    public void explode() {
        this.location.getWorld().createExplosion(10.0, 10.0, 10.0, 1.0f, false, false);
    }
}


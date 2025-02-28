/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.Jared.Interfaces;

import org.bukkit.entity.Player;

public interface IUser {
    public boolean isHidden();

    public void setHidden(boolean var1);

    public boolean isVanished();

    public boolean isHidden(Player var1);

    public void setVanished(boolean var1);

    public boolean isIgnoreExempt();

    public boolean isMuted();

    public String getCustomName();

    public void sendMessage(String var1);

    public Player getBase();

    public String getName();

    public boolean canInteractVanished();
}


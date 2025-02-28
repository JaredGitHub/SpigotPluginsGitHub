/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package me.Jared.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GunsEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}


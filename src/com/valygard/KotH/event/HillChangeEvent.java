/**
 * HillChangeEvent.java is part of King of the Hill.
 * (c) 2014 Anand, All Rights Reserved.
 */
package com.valygard.KotH.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.valygard.KotH.framework.Arena;
import com.valygard.KotH.hill.HillManager;

/**
 * @author Anand
 *
 */
public class HillChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
    private Arena arena;
    private HillManager hill;
    private boolean cancelled;
    
    public HillChangeEvent(Arena arena) {
        this.arena = arena;
        this.hill  = new HillManager(arena);
        this.cancelled = false;
    }
    
    public Arena getArena() {
        return arena;
    }
    
    public HillManager getManager() {
    	return hill;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

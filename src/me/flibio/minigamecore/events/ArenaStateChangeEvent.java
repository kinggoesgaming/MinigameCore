package me.flibio.minigamecore.events;

import me.flibio.minigamecore.arena.Arena;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.impl.AbstractEvent;

public class ArenaStateChangeEvent extends AbstractEvent implements Cancellable {
	
	private boolean cancelled = false;
	private Arena arena;
	
	/**
	 * Called when an arena changes its state
	 * @param arena
	 * 	The arena whose state changed
	 */
	public ArenaStateChangeEvent(Arena arena) {
		this.arena = arena;
	}
	
	/**
	 * Gets whichever arena's state changed
	 * @return
	 * 	The arena whose state changed
	 */
	public Arena getArena() {
		return this.arena;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
}

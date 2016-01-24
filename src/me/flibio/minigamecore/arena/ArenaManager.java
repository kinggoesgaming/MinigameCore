package me.flibio.minigamecore.arena;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaManager {
	
	private CopyOnWriteArrayList<Arena> arenas = new CopyOnWriteArrayList<Arena>();
	
	/**
	 * Manages the arenas for a game
	 */
	public ArenaManager() {
		
	}
	
	/**
	 * Adds an arena to the minigame
	 * @param arena
	 * 	The arena to add
	 * @return
	 * 	If the arena was successfully added or not
	 */
	public boolean addArena(Arena arena) {
		if(arenaExists(arena.getData().getName())) {
			return false;
		}
		arenas.add(arena);
		return true;
	}
	
	/**
	 * Removes an arena
	 * @param name
	 * 	The name of the arena to remove
	 * @return
	 * 	If the arena was successfully removed or not
	 */
	public boolean removeArena(String name) {
		for(Arena arena : arenas) {
			if(arena.getData().getName().equalsIgnoreCase(name)) {
				arenas.remove(arena);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if an arena exists
	 * @param name
	 * 	The arena to check for
	 * @return
	 * 	Boolean based on if the arena was found or not
	 */
	public Optional<Arena> getArena(String name) {
		for(Arena arena : arenas) {
			if(arena.getData().getName().equalsIgnoreCase(name)) {
				return Optional.of(arena);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if an arena exists
	 * @param name
	 * 	The arena to check for
	 * @return
	 * 	Boolean based on if the arena was found or not
	 */
	public boolean arenaExists(String name) {
		for(Arena arena : arenas) {
			if(arena.getData().getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}

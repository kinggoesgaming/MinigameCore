package me.Flibio.MinigameCore.Arena;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaManager {
	
	private CopyOnWriteArrayList<Arena> arenas = new CopyOnWriteArrayList<Arena>();
	
	public ArenaManager() {
		
	}
	
	/**
	 * Creates a new arena
	 * @param name
	 * 	The name of the arena to be created
	 * @return
	 * 	Boolean based on if the arena was successfully created or not
	 */
	public boolean createArena(String name) {
		//TODO
		return false;
	}
	
	/**
	 * Deletes an arena
	 * @param name
	 * 	The name of the arena to delete
	 * @return
	 * 	Boolean based on if the arena was successfully deleted or not
	 */
	public boolean deleteArena(String name) {
		//TODO
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
			if(arena.getArenaName().equalsIgnoreCase(name)) {
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
		return getArena(name).isPresent();
	}
}

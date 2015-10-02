package me.flibio.minigamecore.arena;

public class ArenaStateBuilder {
	
	/**
	 * Creates a new arena state instance
	 * @param stateName
	 * 	The name of the arena state instance
	 * @return
	 * 	The new arena state instance
	 */
	public static ArenaState createBasicArenaState(String stateName) {
		return new ArenaState(stateName);
	}
}

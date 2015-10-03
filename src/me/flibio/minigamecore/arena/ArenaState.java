package me.flibio.minigamecore.arena;

public class ArenaState {
	
	private String stateName;
	
	/**
	 * An ArenaState is used to identify which state the arena is in
	 * @param stateName
	 * 	The name of the arena state
	 */
	public ArenaState(String stateName) {
		this.stateName = stateName;
	}
	
	/**
	 * Gets the name of the state
	 * @return
	 */
	public String getStateName() {
		return this.stateName;
	}
}

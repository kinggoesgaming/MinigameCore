package me.flibio.minigamecore.arena;

public class ArenaState {
	
	private String stateName;
	
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

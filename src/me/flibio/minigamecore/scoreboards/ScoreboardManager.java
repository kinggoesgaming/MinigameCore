package me.flibio.minigamecore.scoreboards;

import org.spongepowered.api.Game;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreboardManager {
	
	public enum ScoreboardType { LEADERBOARD }
	
	private CopyOnWriteArrayList<CustomScoreboard> scoreboards = new CopyOnWriteArrayList<CustomScoreboard>();
	
	public ScoreboardManager(Game game) {
		
	}
	
	/**
	 * Creates a new scoreboard
	 * @param type
	 * 	The type of scoreboard to create
	 * @return
	 * 	Optional of the created scoreboard
	 */
	public Optional<CustomScoreboard> createScoreboard(ScoreboardType type, String name) {
		//TODO
		return Optional.empty();
	}
	
	/**
	 * Gets a scoreboard
	 * @param name
	 * 	Name of the scoreboard to get
	 * @return
	 */
	public Optional<CustomScoreboard> getScoreboard(String name) {
		for(CustomScoreboard scoreboard : scoreboards) {
			if(scoreboard.getName().toLowerCase().equalsIgnoreCase(name)) {
				Optional.of(scoreboard);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if a scoreboard exists
	 * @param name
	 * 	Name of the scoreboard to check for
	 * @return
	 * 	Boolean based on if the scoreboard exists or not
	 */
	public boolean scoreboardExists(String name) {
		for(CustomScoreboard scoreboard : scoreboards) {
			if(scoreboard.getName().toLowerCase().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}

package me.flibio.minigamecore.scoreboards;

import org.spongepowered.api.Game;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreboardManager {
	
	public enum ScoreboardType { INFO,ABOVE_NAME,LEADERBOARD }
	
	private CopyOnWriteArrayList<MinigameCoreScoreboard> scoreboards = new CopyOnWriteArrayList<MinigameCoreScoreboard>();
	
	public ScoreboardManager(Game game) {
		
	}
	
	/**
	 * Gets a scoreboard
	 * @param name
	 * 	Name of the scoreboard to get
	 * @return
	 */
	public Optional<MinigameCoreScoreboard> getScoreboard(String name) {
		for(MinigameCoreScoreboard scoreboard : scoreboards) {
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
		for(MinigameCoreScoreboard scoreboard : scoreboards) {
			if(scoreboard.getName().toLowerCase().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}

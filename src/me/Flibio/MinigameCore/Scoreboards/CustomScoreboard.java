package me.flibio.minigamecore.scoreboards;

import me.flibio.minigamecore.scoreboards.ScoreboardManager.ScoreboardType;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;

public class CustomScoreboard {
	
	private ScoreboardType scoreboardType;
	private Scoreboard scoreboard;
	
	private String name;
	
	public CustomScoreboard(ScoreboardType type, String name) {
		this.scoreboardType = type;
		this.name = name;
	}
	
	/**
	 * Gets the name of the scoreboard
	 * @return
	 * 	The name of the scoreboard
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the type of the scoreboard
	 * @return
	 * 	The type of the scoreboard
	 */
	public ScoreboardType getType() {
		return this.scoreboardType;
	}
	
	/**
	 * Displays the scoreboard to a player
	 * @param player
	 * 	The player to display the scoreboard to
	 */
	public void displayToPlayer(Player player) {
		player.setScoreboard(scoreboard);
	}
}

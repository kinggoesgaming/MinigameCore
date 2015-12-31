package me.flibio.minigamecore.scoreboards;

import me.flibio.minigamecore.scoreboards.ScoreboardManager.ScoreboardType;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardScoreboard extends MinigameCoreScoreboard {
	
	private Text displayName;
	private TextColor nameColor = TextColors.GREEN;
	private Objective obj;
	
	private ConcurrentHashMap<String,Integer> leaders = new ConcurrentHashMap<String,Integer>();
	
	public LeaderboardScoreboard(String name, Game game) {
		super(ScoreboardType.LEADERBOARD, name);
		displayName = Texts.of(name);
		
		scoreboard = game.getRegistry().createBuilder(Scoreboard.Builder.class).build();
		obj = game.getRegistry().createBuilder(Objective.Builder.class).name(name).criterion(Criteria.DUMMY).displayName(displayName).build();
	}
	
	/**
	 * Adds a leader to the leaderboard
	 * @param player
	 * 	The name of the player to add
	 * @param score
	 * 	The score of the player
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean addLeader(String player, int score) {
		if(leaders.containsKey(player)) {
			return false;
		} else {
			leaders.put(player, score);
			return true;
		}
	}
	
	/**
	 * Updates a leader's score on the leaderboard
	 * @param player
	 * 	The name of the player to update
	 * @param score
	 * 	The score of the player
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean updateScore(String player, int score) {
		if(!leaders.containsKey(player)) {
			return false;
		} else {
			leaders.replace(player, score);
			return true;
		}
	}
	
	/**
	 * Removes a leader from the leaderboard
	 * @param player
	 * 	The name of the player to remove
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean removeLeader(String player) {
		if(!leaders.containsKey(player)) {
			return false;
		} else {
			leaders.remove(player);
			return true;
		}
	}
	
	/**
	 * Checks if a leader exists
	 * @param player
	 * 	The name of the player to check for
	 * @return
	 * 	If the leader was found or not
	 */
	public boolean leaderExists(String player) {
		return leaders.containsKey(player);
	}
	
	/**
	 * Gets the display name of the scoreboard
	 * @return
	 * 	The display name of the scoreboard
	 */
	public Text getDisplayName() {
		return displayName;
	}
	
	/**
	 * Sets the display name of the scoreboard
	 * @param text
	 * 	What to set the display name to
	 */
	public void setDisplayName(Text text) {
		displayName = text;
	}
	
	/**
	 * Gets the color of the player's names
	 * @return
	 * 	The color of the player's names
	 */
	public TextColor getNameColor() {
		return nameColor;
	}
	
	/**
	 * Sets the color of the player's names
	 * @param nameColor
	 * 	The color of the player's names
	 */
	public void setNameColor(TextColor nameColor) {
		this.nameColor = nameColor;
	}
	
	/**
	 * Displays the scoreboard to the player. Overrides any existing scoreboard.
	 * @param player
	 * 	The player to display the scoreboard to
	 */
	@Override
	public void displayToPlayer(Player player) {
		obj.setDisplayName(displayName);
		for(String leader : leaders.keySet()) {
			obj.getOrCreateScore(Texts.of(nameColor, leader)).setScore(leaders.get(leader));
		}
		scoreboard.updateDisplaySlot(obj, DisplaySlots.SIDEBAR);
		player.setScoreboard(scoreboard);
	}
}
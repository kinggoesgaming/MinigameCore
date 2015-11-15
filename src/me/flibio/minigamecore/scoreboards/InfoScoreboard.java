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

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InfoScoreboard extends MinigameCoreScoreboard {
	
	private ConcurrentHashMap<Integer,Text> lines = new ConcurrentHashMap<Integer,Text>();
	private Text displayName;
	private Objective obj;
	
	public InfoScoreboard(String name, Game game) {
		super(ScoreboardType.INFO, name);
		displayName = Texts.of(name);
		
		scoreboard = game.getRegistry().createBuilder(Scoreboard.Builder.class).build();
		obj = game.getRegistry().createBuilder(Objective.Builder.class).name(name).criterion(Criteria.DUMMY).displayName(displayName).build();
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
	 * Gets a line of text from the scoreboard
	 * @param lineNumber
	 * 	The line number to get text from
	 * @return
	 * 	The text if found
	 */
	public Optional<Text> getLine(int lineNumber) {
		if(!lines.containsKey(lineNumber)) {
			return Optional.empty();
		} else {
			return Optional.of(lines.get(lineNumber));
		}
	}
	
	/**
	 * Sets a line of text on the scoreboard
	 * @param lineNumber
	 * 	The line number to change (Must be greater than 0)
	 * @param text
	 * 	The text to set the line to
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean setLine(int lineNumber, Text text) {
		if(lineNumber<1) {
			return false;
		} else {
			lines.put(lineNumber, text);
			return true;
		}
	}
	
	/**
	 * Displays the scoreboard to the player. Overrides any existing scoreboard.
	 * @param player
	 * 	The player to display the scoreboard to
	 */
	@Override
	public void displayToPlayer(Player player) {
		obj.setDisplayName(displayName);
		for(Integer line : lines.keySet()) {
			obj.getScore(lines.get(line)).setScore(line);
		}
		scoreboard.addObjective(obj);
		scoreboard.addObjective(obj, DisplaySlots.SIDEBAR);
		player.setScoreboard(scoreboard);
	}
}

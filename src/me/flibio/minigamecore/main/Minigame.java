package me.flibio.minigamecore.main;

import me.flibio.minigamecore.arena.ArenaManager;
import me.flibio.minigamecore.economy.EconomyManager;
import me.flibio.minigamecore.file.FileManager;
import me.flibio.minigamecore.kits.KitManager;
import me.flibio.minigamecore.scoreboards.ScoreboardManager;
import me.flibio.minigamecore.teams.TeamManager;

import org.slf4j.Logger;
import org.spongepowered.api.Game;

public class Minigame {
	
	//Managers
	private ArenaManager arenaManager;
	private EconomyManager economyManager;
	private FileManager fileManager;
	private KitManager kitManager;
	private ScoreboardManager scoreboardManager;
	private TeamManager teamManager;

	private String name;

	private Game game;
	private Logger logger;
	
	/**
	 * Stores information about the game
	 * @param name
	 * 	The name of the game
	 * @param game
	 * 	Instance of the game object
	 * @param logger
	 * 	Instance of the logger object
	 */
	public Minigame(String name, Game game, Logger logger) {
		this.name = name;
		this.game = game;
		this.logger = logger;
		
		//Initialize Managers
		this.arenaManager = new ArenaManager();
		this.economyManager = new EconomyManager(this.game);
		this.fileManager = new FileManager(this.logger, this.game, this.name);
		this.kitManager = new KitManager();
		this.scoreboardManager = new ScoreboardManager(this.game);
		this.teamManager = new TeamManager();
	}
	
	/**
	 * Get the name of the minigame
	 * @return
	 * 	The name of the minigame
	 */
	public String getName() {
		return name;
	}
	
	//All manager getters
	/**
	 * Gets the arena manager for the game
	 * @return
	 * 	The arena manager
	 */
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	/**
	 * Gets the economy manager for the game
	 * @return
	 * 	The economy manager
	 */
	public EconomyManager getEconomyManager() {
		return economyManager;
	}
	
	/**
	 * Gets the file manager for the game
	 * @return
	 * 	The file manager
	 */
	public FileManager getFileManager() {
		return fileManager;
	}
	
	/**
	 * Gets the kit manager for the game
	 * @return
	 * 	The kit manager
	 */
	public KitManager getKitManager() {
		return kitManager;
	}
	
	/**
	 * Gets the scoreboard manager for the game
	 * @return
	 * 	The scoreboard manager
	 */
	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}
	
	/**
	 * Gets the team manager for the game
	 * @return
	 * 	The team manager
	 */
	public TeamManager getTeamManager() {
		return teamManager;
	}
}
